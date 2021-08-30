package cn.nkpro.ts5.engine.elasticearch;

import cn.nkpro.ts5.config.redis.RedisSupport;
import cn.nkpro.ts5.engine.doc.service.NkDocEngineFrontService;
import cn.nkpro.ts5.engine.doc.service.NkDocPermService;
import cn.nkpro.ts5.engine.elasticearch.model.BpmTaskES;
import cn.nkpro.ts5.engine.elasticearch.model.DocHES;
import cn.nkpro.ts5.engine.elasticearch.model.ESDoc;
import cn.nkpro.ts5.exception.TfmsSystemException;
import cn.nkpro.ts5.orm.mb.gen.DocH;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class NkIndexService {

    @Autowired@SuppressWarnings("all")
    private SearchEngine searchEngine;
    @Autowired@SuppressWarnings("all")
    protected NkDocPermService docPermService;
    @Autowired
    private NkDocEngineFrontService docEngineFrontService;
    @Autowired
    private RedisSupport<Map<String,String>> redisMapStringString;


    public void init() throws IOException {

        searchEngine.createIndices(DocHES.class);
        searchEngine.createIndices(BpmTaskES.class);
    }

    @Async
    public void reindex(String asyncTaskId, Boolean dropFirst, String docType) throws IOException {
        if(dropFirst){
            this.dropAndInit();
        }

        int offset = 0;
        int rows   = 1000;
        int total  = 0;
        List<DocH> list;
        try{
            while((list = docEngineFrontService.list(docType, offset, rows, null)).size()>0){
                redisMapStringString.set(asyncTaskId, Collections.singletonMap(
                        "message",
                        String.format("%s 等 %d 条记录",list.get(0).getDocDesc(),list.size())
                ));
                searchEngine.indexBeforeCommit(list.stream()
                        .map(doc->DocHES.from(docEngineFrontService.detail(doc.getDocId())))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()));
                offset += rows;
                total  += list.size();
            }
        }catch (Exception e){
            redisMapStringString.set(asyncTaskId, Collections.singletonMap("error",String.format("重建索引发生错误: %s",e.getMessage())));
        }finally {
            redisMapStringString.set(asyncTaskId, Collections.singletonMap("success",String.format("重建索引完成，共 %d 条记录",total)));
        }
    }

    public Map<String,String> getReindexInfo(String asyncTaskId){
        return redisMapStringString.getIfAbsent(asyncTaskId,()->null);
    }

    public void dropAndInit() throws IOException {

        searchEngine.deleteIndices(DocHES.class);
        searchEngine.deleteIndices(BpmTaskES.class);

        this.init();
    }

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
            return searchEngine.searchPage(docType,sourceBuilder);
        } catch (IOException e) {
            throw new TfmsSystemException(e);
        }
    }
}
