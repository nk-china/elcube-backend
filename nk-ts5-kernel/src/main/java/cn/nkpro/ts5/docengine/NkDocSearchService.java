package cn.nkpro.ts5.docengine;

import cn.nkpro.ts5.data.elasticearch.*;
import cn.nkpro.ts5.docengine.model.SearchParams;
import cn.nkpro.ts5.docengine.model.es.DocExtES;
import cn.nkpro.ts5.docengine.model.es.DocHES;
import cn.nkpro.ts5.docengine.service.NkDocPermService;
import cn.nkpro.ts5.task.model.BpmTaskES;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
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
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NkDocSearchService {

    @Autowired@SuppressWarnings("all")
    private SearchEngine searchEngine;
    @Autowired@SuppressWarnings("all")
    protected NkDocPermService docPermService;
    @Autowired@SuppressWarnings("all")
    private NkDocProperties docProperties;


    @SuppressWarnings("all")
    void init() throws IOException {

        searchEngine.createIndices(DocHES.class);
        searchEngine.createIndices(BpmTaskES.class);
        searchEngine.createIndices(DocExtES.class);

        if(docProperties.getIndices()!=null){
            for(Map.Entry<String, Class> e : docProperties.getIndices().entrySet()){
                searchEngine.createIndices(e.getValue(),e.getKey());
            }
        }
    }

    public void dropAndInit() throws IOException {

        searchEngine.deleteIndices(DocHES.class);
        searchEngine.deleteIndices(BpmTaskES.class);
        searchEngine.deleteIndices(DocExtES.class);

        this.init();
    }

    public boolean exists(String docId) throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("docId",docId));
        return searchEngine.exists(DocHES.class,sourceBuilder);
    }


    public ESPageList<JSONObject> queryList(
            QueryBuilder preQueryBuilder,
            SearchParams params
    ){
        return this.queryList("document", preQueryBuilder, params);
    }

    public ESPageList<JSONObject> queryList(
            String indexName,
            QueryBuilder preQueryBuilder,
            SearchParams params
    ){

        BoolQueryBuilder postQueryBuilder = QueryBuilders.boolQuery();

        if(preQueryBuilder!=null){
            postQueryBuilder.must(preQueryBuilder);
        }
        // 功能前置条件
        if(params.getPostCondition()!=null) {
            postQueryBuilder.must(
                new LimitQueryBuilder(params.getPostCondition())
                //QueryBuilders.wrapperQuery()
            );
        }

        QueryBuilder permQuery = docPermService.buildDocFilter(NkDocPermService.MODE_READ, null, null, false);
        if(permQuery!=null)
            postQueryBuilder.must(permQuery);

        // 构造检索语句
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .from(params.getFrom())
                .size(params.getRows());
        // 过滤权限
        if(!postQueryBuilder.must().isEmpty())
            sourceBuilder.postFilter(postQueryBuilder);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if(params.getConditions()!=null){
            JSONObject filter = params.getConditions();
            if(filter!=null){
                filter.forEach((k,v)-> boolQueryBuilder.must(new LimitQueryBuilder(filter.getJSONObject(k))));
            }
        }

        if(!boolQueryBuilder.must().isEmpty())
            sourceBuilder.query(boolQueryBuilder);

        // 高亮
        List<String> highlightField = params.getHighlight();
        if(highlightField!=null && !highlightField.isEmpty()){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.preTags("<span class='highlight'>");
            highlightBuilder.postTags("</span>");
            for(String field : highlightField){
                highlightBuilder.field(new HighlightBuilder.Field(field));
            }
            sourceBuilder.highlighter(highlightBuilder);
        }

        // 排序
        if(StringUtils.isNotBlank(params.getOrderField())){
            sourceBuilder.sort(SortBuilders
                .fieldSort(params.getOrderField())
                .order(SortOrder.fromString(StringUtils.defaultIfBlank(params.getOrder(),"desc")))
            );
            sourceBuilder.sort(SortBuilders.scoreSort());
        }else{
            sourceBuilder.sort(SortBuilders.scoreSort());
            sourceBuilder.sort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC));
        }

        // 过滤字段
        if(params.getSource()!=null){
            sourceBuilder.fetchSource(params.getSource(),null);
        }

        // 汇总数据
        if(params.getAggs()!=null) {
            FilterAggregationBuilder $aggs = AggregationBuilders
                    .filter("$aggs", postQueryBuilder);
            params.getAggs()
                .forEach(agg ->
                    $aggs.subAggregation(
                        AggregationBuilders.terms(agg)
                            .field(agg)
                            .order(BucketOrder.key(true))
                            .size(100)
                    )
                );
            sourceBuilder.aggregation($aggs);
        }

        return searchPage(indexName,sourceBuilder);
    }

    private ESPageList<JSONObject> searchPage(String indexName, SearchSourceBuilder builder) {

        SearchResponse response = searchEngine.search(indexName, builder);

        List<JSONObject> collect = Arrays.stream(response.getHits().getHits())
                .map(hit -> {
                    JSONObject jsonObject = new JSONObject(hit.getSourceAsMap());

                    hit.getHighlightFields().forEach((k,v)-> {
                        if(v.getFragments()!=null && v.getFragments().length>0){
                            jsonObject.put(k,v.getFragments()[0].string());
                        }
                    });

                    return jsonObject;
                })
                .collect(Collectors.toList());

        Map<String, ESAgg> aggs = null;
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
