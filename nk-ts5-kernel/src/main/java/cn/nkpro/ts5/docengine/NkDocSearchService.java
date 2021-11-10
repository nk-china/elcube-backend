package cn.nkpro.ts5.docengine;

import cn.nkpro.ts5.basic.Keep;
import cn.nkpro.ts5.data.elasticearch.annotation.ESDocument;
import cn.nkpro.ts5.docengine.model.BpmTaskES;
import cn.nkpro.ts5.docengine.model.es.DocExtES;
import cn.nkpro.ts5.docengine.service.NkDocPermService;
import cn.nkpro.ts5.data.elasticearch.*;
import cn.nkpro.ts5.docengine.model.es.DocHES;
import cn.nkpro.ts5.exception.NkAccessDeniedException;
import cn.nkpro.ts5.exception.NkDefineException;
import cn.nkpro.ts5.security.SecurityUtilz;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.fieldcaps.FieldCapabilities;
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

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NkDocSearchService {

    @Autowired@SuppressWarnings("all")
    private SearchEngine searchEngine;
    @Autowired@SuppressWarnings("all")
    protected NkDocPermService docPermService;


    void init() throws IOException {

        searchEngine.createIndices(DocHES.class);
        searchEngine.createIndices(BpmTaskES.class);
        searchEngine.createIndices(DocExtES.class);
    }

    public void dropAndInit() throws IOException {

        searchEngine.deleteIndices(DocHES.class);
        searchEngine.deleteIndices(BpmTaskES.class);
        searchEngine.deleteIndices(DocExtES.class);

        this.init();
    }

    public Map<String, Map<String, FieldCapabilities>> getFieldCaps(String index){

        if(!SecurityUtilz.hasAnyAuthority("*:*","#*:READ","#"+index+":READ")){
            throw new NkAccessDeniedException(String.format("没有索引[%s]的访问权限", index));
        }

        return searchEngine.getFieldCaps(index).get();
    }

    @Keep
    @Data
    public static class SqlSearchRequest{
        private List<String> sqls;
        private JSONObject conditions;
        private SqlSearchRequestDrill drill;

        public void setSql(String sql){
            this.sqls = Collections.singletonList(sql);
        }

        public static SqlSearchRequest fromSql(String sql){
            SqlSearchRequest sqlSearchRequest = new SqlSearchRequest();
            sqlSearchRequest.setSqls(Collections.singletonList(sql));
            return sqlSearchRequest;
        }
    }

    @Keep
    @Data
    public static class SqlSearchRequestDrill{
        private String from;
        private Object fromValue;
        private String to;
    }

    public ESSqlResponse searchBySql(SqlSearchRequest params){

        ESSqlResponse response = null;

        for(String sql : params.getSqls()) {


            if(params.getDrill()!=null&&StringUtils.isNotBlank(params.getDrill().getFrom())){
                try {
                    Select select = (Select) CCJSqlParserUtil.parse(sql);
                    PlainSelect selectBody = (PlainSelect) select.getSelectBody();
                    SelectExpressionItem selectItem = (SelectExpressionItem) selectBody.getSelectItems()
                            .stream()
                            .filter(s -> {
                                if(s instanceof SelectExpressionItem){
                                    SelectExpressionItem expressionItem = (SelectExpressionItem) s;
                                    String alias =
                                            expressionItem.getAlias() !=null ?
                                                    expressionItem.getAlias().getName():
                                                    expressionItem.getExpression().toString();

                                    alias = alias.replaceAll("(^\")|(\"$)","");
                                    return StringUtils.equals(alias,params.getDrill().getFrom());
                                }
                                return false;
                            }).findFirst().orElse(null);

                    if(selectItem==null){
                        throw new NkDefineException(String.format("下钻列[%s]不存在",params.getDrill().getFrom()));
                    }

                    if(!(selectItem.getExpression() instanceof Column)){
                        throw new NkDefineException(String.format("下钻列[%s]不支持",params.getDrill().getFrom()));
                    }


                    String alias =
                            selectItem.getAlias() !=null ?
                                    selectItem.getAlias().getName():
                                    selectItem.getExpression().toString();

                    // 移除select字段
                    selectBody.getSelectItems().remove(selectItem);

                    // 移除group by字段
                    selectBody.getGroupBy().getGroupByExpressionList().getExpressions()
                            .removeIf(expression -> expression instanceof Column &&
                                    StringUtils.equals(((Column) expression).getColumnName(),alias));

                    Column columnTo = new Column(params.getDrill().getTo());
                    // 添加select字段
                    selectBody.getSelectItems().add(new SelectExpressionItem(columnTo).withAlias(new Alias(alias)));

                    // 添加group by字段
                    selectBody.getGroupBy().addGroupByExpressions(columnTo);

                    // 添加fromValue 条件
                    EqualsTo equalsTo = new EqualsTo(selectItem.getExpression(), new StringValue((String) params.getDrill().getFromValue()));
                    if(selectBody.getWhere()!=null){
                        selectBody.setWhere(new AndExpression(equalsTo,selectBody.getWhere()));
                    }else{
                        selectBody.setWhere(equalsTo);
                    }

                    if(selectBody.getGroupBy().getGroupByExpressionList().getExpressions().isEmpty()){
                        selectBody.setGroupByElement(null);
                    }
                    sql = selectBody.toString();

                } catch (JSQLParserException e) {
                    e.printStackTrace();
                }
            }

            String index = searchEngine.parseSqlIndex(sql);

            QueryBuilder filterBuilder;
            if (DocHES.class.getAnnotation(ESDocument.class).value().equals(index)) {
                filterBuilder = docPermService.buildDocFilter(NkDocPermService.MODE_READ, null, null, false);
            } else {
                filterBuilder = docPermService.buildIndexFilter(index);
            }


            if (params.getConditions() != null) {
                BoolQueryBuilder conditionsFilter = QueryBuilders.boolQuery();
                JSONObject filter = params.getConditions();
                if (filter != null) {
                    filter.forEach((k, v) -> conditionsFilter.must(new LimitQueryBuilder(filter.getJSONObject(k))));
                }
                if (filterBuilder != null)
                    conditionsFilter.must(filterBuilder);

                if (!conditionsFilter.must().isEmpty())
                    filterBuilder = conditionsFilter;
            }

            if(response==null)
                response = searchEngine.sql(new ESSqlRequest(sql, filterBuilder));
            else{
                response.getRows().addAll(searchEngine.sql(new ESSqlRequest(sql, filterBuilder)).getRows());
            }
            if(response.getSqls()==null){
                response.setSqls(new ArrayList<>());
            }
            response.getSqls().add(sql);
        }

        return response;
    }

    public ESPageList<JSONObject> queryList(
            String indexName,
            QueryBuilder preQueryBuilder,
            JSONObject params
    ){

        BoolQueryBuilder postQueryBuilder = QueryBuilders.boolQuery();

        if(preQueryBuilder!=null){
            postQueryBuilder.must(preQueryBuilder);
        }
        // 功能前置条件
        if(params.containsKey("postCondition")) {
            postQueryBuilder.must(
                new LimitQueryBuilder(params.getJSONObject("postCondition"))
                //QueryBuilders.wrapperQuery()
            );
        }

        QueryBuilder permQuery = docPermService.buildDocFilter(NkDocPermService.MODE_READ, null, null, false);
        if(permQuery!=null)
            postQueryBuilder.must(permQuery);

        // 构造检索语句
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .from(params.getInteger("from"))
                .size(params.getInteger("rows"));
        // 过滤权限
        if(!postQueryBuilder.must().isEmpty())
            sourceBuilder.postFilter(postQueryBuilder);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if(params.containsKey("conditions")){
            JSONObject filter = params.getJSONObject("conditions");
            if(filter!=null){
                filter.forEach((k,v)-> boolQueryBuilder.must(new LimitQueryBuilder(filter.getJSONObject(k))));
            }
        }

        if(!boolQueryBuilder.must().isEmpty())
            sourceBuilder.query(boolQueryBuilder);

        // 高亮
        JSONArray highlightField = params.getJSONArray("_highlight");
        if(highlightField!=null && !highlightField.isEmpty()){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.preTags("<span class='highlight'>");
            highlightBuilder.postTags("</span>");
            for(Object field : highlightField){
                highlightBuilder.field(new HighlightBuilder.Field((String) field));
            }
            sourceBuilder.highlighter(highlightBuilder);
        }

        // 排序
        if(StringUtils.isNotBlank(params.getString("orderField"))){
            sourceBuilder.sort(SortBuilders
                .fieldSort(params.getString("orderField"))
                .order(SortOrder.fromString(StringUtils.defaultIfBlank(params.getString("order"),"desc")))
            );
            sourceBuilder.sort(SortBuilders.scoreSort());
        }else{
            sourceBuilder.sort(SortBuilders.scoreSort());
            sourceBuilder.sort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC));
        }

        // 过滤字段
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
