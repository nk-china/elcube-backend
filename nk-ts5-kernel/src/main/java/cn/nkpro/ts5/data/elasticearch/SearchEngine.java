package cn.nkpro.ts5.data.elasticearch;

import cn.nkpro.ts5.basic.TransactionSync;
import cn.nkpro.ts5.data.elasticearch.annotation.ESDocument;
import cn.nkpro.ts5.exception.NkDefineException;
import cn.nkpro.ts5.exception.NkSystemException;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.fieldcaps.FieldCapabilitiesRequest;
import org.elasticsearch.action.fieldcaps.FieldCapabilitiesResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.*;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bean on 2020/6/15.
 */
@Slf4j
@Component
public class SearchEngine extends ESContentBuilder{

    private static Pattern p_repl = Pattern.compile("\\s[Ff][Rr][Oo][Mm]\\s*[\"]?([^\\s\"]*)[\"]?");
    private static Pattern p_find = Pattern.compile("^[\\w\\W]*\\s[Ff][Rr][Oo][Mm]\\s*[\"]?([^\\s\"]*)[\"]?[\\w\\W]*$");

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

    public FieldCapabilitiesResponse getFieldCaps(String index){
        try {
            return client.fieldCaps(
                    new FieldCapabilitiesRequest().indices(getIndexPrefix()+index).fields("*"),
                    RequestOptions.DEFAULT
            );
        } catch (IOException e) {
            throw new NkSystemException("搜索引擎发生错误："+e.getMessage(), e);
        }
    }

    public String parseSqlIndex(String query){

        Matcher matcher = p_find.matcher(query);

        if(matcher.matches()){
            return matcher.group(1);
        }

        throw new NkDefineException("没有匹配到正确的索引名称");
    }

    public ESSqlResponse sql(ESSqlRequest sqlRequest){

        sqlRequest.setQuery(
            p_repl.matcher(sqlRequest.getQuery()).replaceFirst(" FROM \""+getIndexPrefix()+"$1\"")
        );

        Request request = new Request("GET","/_sql");
        request.setJsonEntity(sqlRequest.toString());

        try {

            if(log.isDebugEnabled()){
                log.debug("ES._sql : " + sqlRequest.toString());
            }

            Response response = client.getLowLevelClient()
                    .performRequest(request);

            return JSONObject.parseObject(EntityUtils.toString(response.getEntity()),ESSqlResponse.class);

        } catch (IOException e) {
            if(e instanceof ResponseException){
                try{
                    throw new NkSystemException("搜索引擎发生错误："+EntityUtils.toString(((ResponseException) e).getResponse().getEntity()), e);
                }catch (IOException ee){
                    throw new NkSystemException("搜索引擎发生错误："+e.getMessage(), e);
                }
            }
            throw new NkSystemException("搜索引擎发生错误："+e.getMessage(), e);
        }
    }


    public SearchResponse search(String indexName, SearchSourceBuilder builder) {
        try {

            SearchSourceBuilder sourceBuilder = builder.trackTotalHits(true).timeout(new TimeValue(10, TimeUnit.SECONDS));

            if(log.isDebugEnabled()){
                log.debug("ES._search : " + sourceBuilder);
            }

            SearchRequest searchRequest = new SearchRequest()
                    .indices(documentIndex(indexName))
                    //track_total_hits : true 解决查询列表最大total限制10000的问题
                    .source(sourceBuilder);

            return client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new NkSystemException("搜索引擎发生错误："+e.getMessage(), e);
        }
    }

    public <T extends AbstractESModel> SearchResponse search(Class<T> docType, SearchSourceBuilder builder) {
        return search(parseDocument(docType),builder);
    }

    public <T extends AbstractESModel> boolean exists(Class<T> docType, SearchSourceBuilder builder) throws IOException {
        try {
            ESDocument document = docType.getAnnotation(ESDocument.class);
            if(document==null) {
                throw new RuntimeException(String.format("类型 %s 的 ESDocument 注解不存在",docType.getName()));
            }

            SearchRequest searchRequest = new SearchRequest()
                    .indices(documentIndex(document.value()))
                    .source(
                            builder
                                    .timeout(new TimeValue(10, TimeUnit.SECONDS))
                                    .fetchSource(false)
                    );

            return client.search(searchRequest, RequestOptions.DEFAULT).getHits().getTotalHits().value > 0;
        } catch (IOException e) {
            throw new NkSystemException("搜索引擎发生错误："+e.getMessage(), e);
        }
    }

    public void deleteBeforeCommit(Class<? extends AbstractESModel> esType, QueryBuilder query){
        TransactionSync.runBeforeCommit(()-> {
            try{
                client.deleteByQuery(
                        new DeleteByQueryRequest(
                                documentIndex(parseDocument(esType))
                        ).setQuery(query),
                        RequestOptions.DEFAULT
                );
            } catch (IOException e) {
                throw new NkSystemException("搜索引擎发生错误："+e.getMessage(), e);
            }
        });
    }

    public void deleteBeforeCommit(Class<? extends AbstractESModel> esType, String... id){
        deleteBeforeCommit(esType, Arrays.asList(id));
    }

    public void deleteBeforeCommit(Class<? extends AbstractESModel> esType, Collection<String> keys){
        TransactionSync.runBeforeCommit(()-> {
            try{
                for(String id : keys) {
                    client.delete(
                            new DeleteRequest(documentIndex(parseDocument(esType)), id),
                            RequestOptions.DEFAULT
                    );
                }
            } catch (IOException e) {
                throw new NkSystemException("搜索引擎发生错误："+e.getMessage(), e);
            }
        });
    }

    public void updateBeforeCommit(AbstractESModel... docs){
        updateBeforeCommit(Arrays.asList(docs));
    }

    public void updateBeforeCommit(Collection<AbstractESModel> docs){

        TransactionSync.runBeforeCommit(()-> {
            for(AbstractESModel doc : docs){
                doc.validateDynamic();
            }
            try{
                for(AbstractESModel doc : docs){
                    client.update(
                            new UpdateRequest(documentIndex(parseDocument(doc.getClass())), parseDocId(doc))
                                    .docAsUpsert(true)
                                    .upsert()
                                    .doc(doc.toSource()),
                            RequestOptions.DEFAULT
                    );
                }
            } catch (IOException e) {
                throw new NkSystemException("搜索引擎发生错误："+e.getMessage(), e);
            }
        });
    }

    public void indexBeforeCommit(AbstractESModel... docs){
        indexBeforeCommit(Arrays.asList(docs));
    }

    public void indexBeforeCommit(Collection<AbstractESModel> docs){

        TransactionSync.runBeforeCommit(()-> {

            if(log.isInfoEnabled()){
                log.info("重建索引开始 数量 = {}", docs.size());
            }
            for(AbstractESModel doc : docs){
                doc.validateDynamic();
            }
            try{
                for(AbstractESModel doc : docs){
                    client.index(
                            new IndexRequest(documentIndex(parseDocument(doc.getClass())))
                                    .id(parseDocId(doc))
                                    .source(doc.toSource()),
                            RequestOptions.DEFAULT);

                    if(log.isInfoEnabled()) {
                        log.info("重建索引 DOC = {}", doc);
                    }
                }

                if(log.isInfoEnabled()){
                    log.info("重建索引完成 数量 = {}", docs.size());
                }
            } catch (IOException e) {
                throw new NkSystemException("搜索引擎发生错误："+e.getMessage(), e);
            }
        });
    }

    private boolean existsIndices(Class<? extends AbstractESModel> docType) throws IOException {
        try{
            return client.indices()
                    .exists(new GetIndexRequest(documentIndex(parseDocument(docType))), RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new NkSystemException("搜索引擎发生错误："+e.getMessage(), e);
        }
    }


    public void deleteIndices(Class<? extends AbstractESModel> docType) throws IOException {
        if(existsIndices(docType)){
            try{
                client.indices()
                        .delete(new DeleteIndexRequest(documentIndex(parseDocument(docType))), RequestOptions.DEFAULT);
            } catch (IOException e) {
                throw new NkSystemException("搜索引擎发生错误："+e.getMessage(), e);
            }
        }
    }

    public void createIndices(Class<? extends AbstractESModel> docType) throws IOException {

        if(existsIndices(docType))
            return;

        try{
            CreateIndexRequest request = new CreateIndexRequest(documentIndex(parseDocument(docType)));

            request.settings(buildNgramTokenizer());
            request.mapping(buildMapping(docType));

            client.indices().create(request, RequestOptions.DEFAULT);

        } catch (IOException e) {
            throw new NkSystemException("搜索引擎发生错误："+e.getMessage(), e);
        }
    }
}
