package cn.nkpro.ts5.engine.elasticearch;

import cn.nkpro.ts5.config.NkProperties;
import cn.nkpro.ts5.engine.elasticearch.annotation.*;
import cn.nkpro.ts5.engine.elasticearch.model.ESDoc;
import cn.nkpro.ts5.engine.LocalSyncUtilz;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilter;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by bean on 2020/6/15.
 */
@Slf4j
@Component
public class SearchEngine {

    @Autowired
    private NkProperties properties;
    @Autowired
    private RestHighLevelClient client;

    @Scheduled(cron = "0 * * * * ?")
    public void heartbeat(){
        try {
            client.ping(RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("indices heartbeat error",e);
        }
    }

    public void deleteAfterCommit(Class<? extends ESDoc> docType, QueryBuilder query){
        LocalSyncUtilz.runAfterCommit(()-> client.deleteByQuery(
                new DeleteByQueryRequest(
                        documentIndex(parseESDocument(docType))
                ).setQuery(query),
                RequestOptions.DEFAULT
        ));
    }

    public void indexBeforeCommit(ESDoc... docs){
        indexBeforeCommit(Arrays.asList(docs));
    }

    public void indexBeforeCommit(Collection<ESDoc> docs){

        LocalSyncUtilz.runBeforeCommit(()-> {

            if(log.isInfoEnabled()){
                log.info("重建索引开始 数量 = {}", docs.size());
            }

            for(ESDoc doc : docs){

                Class<? extends ESDoc> esType = doc.getClass();
                ESDocument document = esType.getAnnotation(ESDocument.class);
                if(document==null) {
                    throw new RuntimeException(String.format("类型 %s 的 ESDocument 注解不存在",esType.getName()));
                }
                // 获取ID的值，如果ID没有定义，则使用es默认的规则生成ID
                String id = Arrays.stream(esType.getDeclaredFields())
                        .filter(field -> field.getAnnotation(ESId.class) != null)
                        .sorted(Comparator.comparing(Field::getName))
                        .map(field -> {
                            try {
                                field.setAccessible(true);
                                Object value = field.get(doc);
                                return value==null? StringUtils.EMPTY:value.toString();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                                return StringUtils.EMPTY;
                            }
                        }).collect(Collectors.joining("$"));

                JSONObject json = (JSONObject) JSON.toJSON(doc);

                client.index(
                        new IndexRequest(documentIndex(document))
                                .id(id)
                                .source(json),
                        RequestOptions.DEFAULT);

                if(log.isInfoEnabled()) {
                    log.info("重建索引 DOC = {}", doc);
                }
            }

            if(log.isInfoEnabled()){
                log.info("重建索引完成 数量 = {}", docs.size());
            }
        });
    }

    public <T extends ESDoc> boolean exists(Class<T> docType, SearchSourceBuilder builder) throws IOException {

        ESDocument document = docType.getAnnotation(ESDocument.class);
        if(document==null) {
            throw new RuntimeException(String.format("类型 %s 的 ESDocument 注解不存在",docType.getName()));
        }

        SearchRequest searchRequest = new SearchRequest()
            .indices(documentIndex(document))
            .source(
                builder
                    .timeout(new TimeValue(10, TimeUnit.SECONDS))
                    .fetchSource(false)
            );

        return client.search(searchRequest, RequestOptions.DEFAULT).getHits().getTotalHits().value > 0;
    }

    public <T extends ESDoc> ESPageList<T> searchPage(Class<T> docType, SearchSourceBuilder builder) throws IOException {

        ESDocument document = docType.getAnnotation(ESDocument.class);
        if(document==null) {
            throw new RuntimeException(String.format("类型 %s 的 ESDocument 注解不存在",docType.getName()));
        }

        SearchRequest searchRequest = new SearchRequest()
            .indices(documentIndex(document))
            .source(
                builder
                    .timeout(new TimeValue(10, TimeUnit.SECONDS))
            );

        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

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


//            aggs = response.getAggregations().asList().stream()
//                .map(aggregation -> {
//                    ESAgg agg = new ESAgg();
//                    agg.setName(aggregation.getName());
//
//                    switch (aggregation.getName()){
//                        case "$aggs":
//
//
//
//                            break;
//                    }
//
//                    switch (aggregation.getType()){
//                        case "$aggs":
//                            agg.setBuckets(
//                                ((ParsedStringTerms) aggregation)
//                                    .getBuckets()
//                                    .stream()
//                                    .map(bucket->{
//                                        ESBucket bt = new ESBucket();
//                                        bt.setKey(bucket.getKeyAsString());
//                                        bt.setDocCount(bucket.getDocCount());
//
//                                        bucket.getAggregations().asList()
//                                            .forEach(subAggregation->{
//                                                switch (subAggregation.getType()){
//                                                    case "max":
//                                                        bt.setMax(((ParsedSingleValueNumericMetricsAggregation)subAggregation).value());
//                                                        break;
//                                                    case "min":
//                                                        bt.setMin(((ParsedSingleValueNumericMetricsAggregation)subAggregation).value());
//                                                        break;
//                                                    case "avg":
//                                                        bt.setAvg(((ParsedSingleValueNumericMetricsAggregation)subAggregation).value());
//                                                        break;
//                                                    case "sum":
//                                                        bt.setSum(((ParsedSingleValueNumericMetricsAggregation)subAggregation).value());
//                                                        break;
//                                                }
//                                            });
//
//                                        return bt;
//                                    })
//                                    .collect(Collectors.toList())
//                            );
//                            break;
//                        case "max":
//                        case "min":
//                        case "avg":
//                        case "sum":
//                            agg.setValue(((ParsedSingleValueNumericMetricsAggregation)aggregation).value());
//                            break;
//                        default:
//                            break;
//                    }
//                    return agg;
//                })
//                .collect(Collectors.toMap(ESAgg::getName, Function.identity()));
        }

        return new ESPageList<>(
            collect,
            aggs,
            builder.from(),
            builder.size(),
            response.getHits().getTotalHits().value
        );
    }

    @SuppressWarnings("all")
    public boolean existsIndices(Class<? extends ESDoc> docType) throws IOException {
        ESDocument document = parseESDocument(docType);
        return client.indices()
                .exists(new GetIndexRequest(documentIndex(document)), RequestOptions.DEFAULT);
    }


    public void deleteIndices(Class<? extends ESDoc> docType) throws IOException {
        if(existsIndices(docType)){
            ESDocument document = parseESDocument(docType);
            client.indices()
                    .delete(new DeleteIndexRequest(documentIndex(document)), RequestOptions.DEFAULT);
        }
    }

    @SuppressWarnings("deprecation")
    void createIndices(Class<? extends ESDoc> docType) throws IOException {

        if(existsIndices(docType))
            return;

        ESDocument document = parseESDocument(docType);

        CreateIndexRequest request = new CreateIndexRequest(documentIndex(document));

        final XContentBuilder settings = XContentFactory.jsonBuilder();
        settings.startObject();
        {
            settings.startObject("analysis");
            {
                settings.startObject("analyzer");
                {
                    settings.startObject("ngram_analyzer");
                    settings.field("tokenizer","ngram_tokenizer");
                    settings.endObject();
                }
                settings.endObject();

                settings.startObject("tokenizer");
                {
                    settings.startObject("ngram_tokenizer");
                    settings.field("type","ngram");
                    settings.field("min_gram",4);
                    settings.field("max_gram",4);
                    settings.array("token_chars","letter","digit");
                    settings.endObject();
                }
                settings.endObject();
            }
            settings.endObject();
        }
        settings.endObject();
        request.settings(settings);


        final XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            ESDynamicTemplate[] array = null;
            ESDynamicTemplates dynamicTemplates = docType.getAnnotation(ESDynamicTemplates.class);
            ESDynamicTemplate dynamicTemplate = docType.getAnnotation(ESDynamicTemplate.class);
            if(dynamicTemplates!=null){
                array = dynamicTemplates.value();
            }else if(dynamicTemplate!=null){
                array = new ESDynamicTemplate[]{dynamicTemplate};
            }

            if(array!=null){
                builder.startArray("dynamic_templates");
                for(ESDynamicTemplate template : array){
                    builder.startObject();
                    builder.startObject(template.value());
                    {
                        if(StringUtils.isNotBlank(template.matchMappingType()))
                            builder.field("match_mapping_type",template.matchMappingType());
                        if(StringUtils.isNotBlank(template.match()))
                            builder.field("match",template.match());
                        if(StringUtils.isNotBlank(template.unmatch()))
                            builder.field("unmatch",template.unmatch());

                        builder.startObject("mapping");
                        {
                            builder.field("type",template.mappingType().getValue());
                            if(template.analyzer()!=ESAnalyzerType.none)
                                builder.field("analyzer",template.analyzer());
                            if(template.searchAnalyzer()!=ESAnalyzerType.none)
                                builder.field("search_analyzer",template.searchAnalyzer());
                            if (template.copyToKeyword()) {
                                builder.field("copy_to", "$keyword");
                            }
                            if (template.original()) {
                                builder.startObject("fields");
                                builder.startObject("original");
                                builder.field("type", "keyword");
                                builder.endObject();
                                builder.endObject();
                            }
                            if(StringUtils.isNotBlank(template.format())){
                                builder.field("format",template.format());
                            }
                            for(@SuppressWarnings("unused") ESField field : template.fields()){
                                throw new RuntimeException("暂不支持");
                            }
                        }
                        builder.endObject();
                    }
                    builder.endObject();
                    builder.endObject();
                }
                builder.endArray();
            }

            builder.startObject("properties");
            {
                Class<?> type = docType;
                do{
                    Field[] fields = type.getDeclaredFields();
                    for (Field field : fields) {
                        ESField esField = field.getAnnotation(ESField.class);
                        if (esField != null) {
                            builder.startObject(field.getName());
                            {
                                builder.field("type", esField.type().getValue());
                                if (esField.analyzer() != ESAnalyzerType.none) {
                                    builder.field("analyzer", esField.analyzer());
                                }
                                if (esField.searchAnalyzer() != ESAnalyzerType.none) {
                                    builder.field("search_analyzer", esField.searchAnalyzer());
                                }
                                if (esField.fieldData()) {
                                    builder.field("fielddata", true);
                                }
                                if (esField.original()) {
                                    builder.startObject("fields");
                                    builder.startObject("original");
                                    builder.field("type", "keyword");
                                    builder.endObject();
                                    builder.endObject();
                                }
                                if (esField.copyToKeyword()) {
                                    builder.field("copy_to", "$keyword");
                                }
                                if(StringUtils.isNotBlank(esField.format())){
                                    builder.field("format",esField.format());
                                }
                            }
                            builder.endObject();
                        }
                    }
                }while ((type = type.getSuperclass())!=Object.class);
            }
            builder.endObject();
        }
        builder.endObject();
        request.mapping(builder);

        client.indices().create(request, RequestOptions.DEFAULT);

    }

    private ESDocument parseESDocument(Class<? extends ESDoc> docType){
        ESDocument document = docType.getAnnotation(ESDocument.class);
        if(document==null) {
            throw new RuntimeException(String.format("类型 %s 的 ESDocument 注解不存在",docType.getName()));
        }
        return document;
    }

    private String documentIndex(ESDocument document){

        String prefix = properties.getEnvKey();
        if(StringUtils.isNotBlank(prefix)){
            return String.format("%s-%s",prefix,document.value());
        }

        return document.value();
    }
}
