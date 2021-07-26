package cn.nkpro.ts5.engine.doc;

import cn.nkpro.ts5.engine.elasticearch.ESPageList;
import cn.nkpro.ts5.engine.elasticearch.SearchEngine;
import cn.nkpro.ts5.engine.elasticearch.model.ESDoc;
import cn.nkpro.ts5.exception.TfmsException;
import cn.nkpro.ts5.services.TfmsPermService;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NKDocIndexService {

    @Autowired
    private SearchEngine searchEngine;
    @Autowired
    protected TfmsPermService permService;

    public <T extends ESDoc> ESPageList<T> queryList(
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
            permService.buildDocFilter(TfmsPermService.MODE_READ, null,null,false)
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
            return searchEngine.searchPage(docType,sourceBuilder);
        } catch (IOException e) {
            throw new TfmsException(e);
        }
    }
}
