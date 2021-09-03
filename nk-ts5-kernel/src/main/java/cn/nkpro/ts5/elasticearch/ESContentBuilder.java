package cn.nkpro.ts5.elasticearch;

import cn.nkpro.ts5.NkProperties;
import cn.nkpro.ts5.elasticearch.annotation.*;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

class ESContentBuilder {

    @Autowired
    private NkProperties properties;

    ESDocument parseDocument(Class<? extends AbstractESModel> esType){
        ESDocument document = esType.getAnnotation(ESDocument.class);
        Assert.notNull(document,String.format("类型 %s 的 ESDocument 注解不存在",esType.getName()));
        return document;
    }

    String parseDocId(AbstractESModel doc){
        Class<? extends AbstractESModel> esType = doc.getClass();
        ESDocument document = parseDocument(esType);
        // 获取ID的值，如果ID没有定义，则使用es默认的规则生成ID
        return Arrays.stream(esType.getDeclaredFields())
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
    }

    XContentBuilder buildNgramTokenizer() throws IOException {
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
        return settings;
    }

    XContentBuilder buildMapping(Class<? extends AbstractESModel> esType) throws IOException {

        final XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            ESDynamicTemplate[] array = null;
            ESDynamicTemplates dynamicTemplates = esType.getAnnotation(ESDynamicTemplates.class);
            ESDynamicTemplate dynamicTemplate = esType.getAnnotation(ESDynamicTemplate.class);
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
                Class<?> type = esType;
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
                                //if (esField.fieldData()) {
                                //    builder.field("fielddata", true);
                                //}
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
        return builder;
    }

    String documentIndex(ESDocument document){

        String prefix = properties.getEnvKey();
        if(StringUtils.isNotBlank(prefix)){
            return String.format("%s-%s",prefix,document.value());
        }

        return document.value();
    }


// 临时内容，待删除
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
