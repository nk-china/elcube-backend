package cn.nkpro.ts5.engine.elasticearch;

import cn.nkpro.ts5.engine.doc.service.NkDocPermService;
import cn.nkpro.ts5.engine.elasticearch.annotation.ESDocument;
import cn.nkpro.ts5.engine.elasticearch.model.BpmTaskES;
import cn.nkpro.ts5.engine.elasticearch.model.CustomES;
import cn.nkpro.ts5.engine.elasticearch.model.DocHES;
import cn.nkpro.ts5.engine.elasticearch.model.AbstractESDoc;
import cn.nkpro.ts5.exception.TfmsSystemException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilter;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SearchService {

    @Autowired@SuppressWarnings("all")
    private SearchEngine searchEngine;
    @Autowired@SuppressWarnings("all")
    protected NkDocPermService docPermService;


    public void init() throws IOException {

        searchEngine.createIndices(DocHES.class);
        searchEngine.createIndices(BpmTaskES.class);
        searchEngine.createIndices(CustomES.class);
    }

    public void dropAndInit() throws IOException {

        searchEngine.deleteIndices(DocHES.class);
        searchEngine.deleteIndices(BpmTaskES.class);
        searchEngine.deleteIndices(CustomES.class);

        this.init();
    }

    public <T extends AbstractESDoc> ESPageList<T> queryList(
            Class<T> docType,
            QueryBuilder preQueryBuilder,
            JSONObject params
    ){

        BoolQueryBuilder postQueryBuilder = QueryBuilders.boolQuery();

        if(preQueryBuilder!=null){
            postQueryBuilder.must(preQueryBuilder);
        }
        if(params.containsKey("preCondition")) {
            postQueryBuilder.must(
                    QueryBuilders.wrapperQuery(params.getString("preCondition"))
            );
        }
        postQueryBuilder.must(
                docPermService.buildDocFilter(NkDocPermService.MODE_READ, null,null,false)
        );

        // 处理查询条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(
                        QueryBuilders.queryStringQuery(
                                StringUtils.defaultIfBlank(params.getString("keyword"),"*")
                        ).defaultField("$keyword")
                );

        if(params.containsKey("condition")) {
            boolQueryBuilder.must(
                    QueryBuilders.wrapperQuery(params.getString("condition"))
            );
        }

        FieldSortBuilder sortBuilder = SortBuilders
                .fieldSort(
                        StringUtils.defaultIfBlank(params.getString("orderField"),"updatedTime")
                )
                .order(
                        SortOrder.fromString(
                                StringUtils.defaultIfBlank(params.getString("order"),"desc")
                        )
                );

        // 构造检索语句
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                // 过滤权限
                .postFilter(postQueryBuilder)
                .query(boolQueryBuilder)
                .sort(sortBuilder)
                .sort(SortBuilders.scoreSort())
                .sort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC))
                .from(params.getInteger("from"))
                .size(params.getInteger("rows"));

        if(params.containsKey("_source")){
            @SuppressWarnings("all")
            String[] fields = params.getJSONArray("_source").toArray(new String[0]);
            sourceBuilder.fetchSource(fields,null);
        }


        // 汇总数据
        if(params.containsKey("$aggs")) {
            FilterAggregationBuilder $aggs = AggregationBuilders
                    .filter("$aggs", postQueryBuilder);
            params.getJSONArray("$aggs")
                    .forEach(agg ->
                            $aggs.subAggregation(
                                    AggregationBuilders.terms((String) agg)
                                            .field((String) agg)
                                            .order(BucketOrder.key(true))
                                            .size(100)
                            )
                    );
            sourceBuilder.aggregation($aggs);
        }

        try {
            return searchPage(docType,sourceBuilder);
        } catch (IOException e) {
            throw new TfmsSystemException(e);
        }
    }

    private <T extends AbstractESDoc> ESPageList<T> searchPage(Class<T> docType, SearchSourceBuilder builder) throws IOException {

        SearchResponse response = searchEngine.search(docType, builder);

        List<T> collect = Arrays.stream(response.getHits().getHits())
                .map(hit -> new JSONObject(hit.getSourceAsMap()).toJavaObject(docType))
                .collect(Collectors.toList());

        Map<String,ESAgg> aggs = null;
        if(response.getAggregations()!=null){

            Map<String, Aggregation> aggregationMap = response.getAggregations()
                    .asMap();

            ParsedFilter $aggs = (ParsedFilter) aggregationMap.get("$aggs");
            aggs = $aggs.getAggregations()
                    .asList()
                    .stream()
                    .map(aggregation -> {
                        ESAgg agg = new ESAgg();
                        ParsedStringTerms parsedStringTerms = (ParsedStringTerms) aggregation;
                        agg.setName(parsedStringTerms.getName());
                        agg.setBuckets(
                                parsedStringTerms.getBuckets()
                                        .stream()
                                        .map(bucket->{
                                            ESBucket bt = new ESBucket();
                                            bt.setKey(bucket.getKeyAsString());
                                            bt.setDocCount(bucket.getDocCount());
                                            return bt;
                                        })
                                        .collect(Collectors.toList())
                        );
                        return agg;
                    })
                    .collect(Collectors.toMap(ESAgg::getName, Function.identity()));
        }

        return new ESPageList<>(
                collect,
                aggs,
                builder.from(),
                builder.size(),
                response.getHits().getTotalHits().value
        );
    }
}
