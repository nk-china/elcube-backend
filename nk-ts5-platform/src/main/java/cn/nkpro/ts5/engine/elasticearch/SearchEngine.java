package cn.nkpro.ts5.engine.elasticearch;

import cn.nkpro.ts5.engine.LocalSyncUtilz;
import cn.nkpro.ts5.engine.elasticearch.annotation.ESDocument;
import cn.nkpro.ts5.engine.elasticearch.model.AbstractESDoc;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
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

/**
 * Created by bean on 2020/6/15.
 */
@Slf4j
@Component
public class SearchEngine extends ESContentBuilder{

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

    public <T extends AbstractESDoc> SearchResponse search(Class<T> docType, SearchSourceBuilder builder) throws IOException {
        return client.search(new SearchRequest()
                .indices(documentIndex(parseDocument(docType)))
                .source(builder.timeout(new TimeValue(10, TimeUnit.SECONDS))), RequestOptions.DEFAULT);
    }

    public <T extends AbstractESDoc> boolean exists(Class<T> docType, SearchSourceBuilder builder) throws IOException {

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

    public void deleteBeforeCommit(Class<? extends AbstractESDoc> docType, QueryBuilder query){
        LocalSyncUtilz.runBeforeCommit(()-> client.deleteByQuery(
                new DeleteByQueryRequest(
                        documentIndex(parseDocument(docType))
                ).setQuery(query),
                RequestOptions.DEFAULT
        ));
    }

    public void deleteBeforeCommit(Class<? extends AbstractESDoc> esType, String... id){
        deleteBeforeCommit(esType, Arrays.asList(id));
    }

    public void deleteBeforeCommit(Class<? extends AbstractESDoc> esType, Collection<String> keys){
        LocalSyncUtilz.runBeforeCommit(()-> {
            for(String id : keys) {
                client.delete(
                        new DeleteRequest(documentIndex(parseDocument(esType)), id),
                        RequestOptions.DEFAULT
                );
            }
        });
    }

    public void updateBeforeCommit(AbstractESDoc... docs){
        updateBeforeCommit(Arrays.asList(docs));
    }

    public void updateBeforeCommit(Collection<AbstractESDoc> docs){

        LocalSyncUtilz.runBeforeCommit(()-> {


            for(AbstractESDoc doc : docs){
                doc.validateDynamic();
            }
            for(AbstractESDoc doc : docs){
                client.update(
                        new UpdateRequest(documentIndex(parseDocument(doc.getClass())), parseDocId(doc))
                                .docAsUpsert(true)
                                .upsert()
                                .doc(doc.toSource()),
                        RequestOptions.DEFAULT
                );
            }
        });
    }

    public void indexBeforeCommit(AbstractESDoc... docs){
        indexBeforeCommit(Arrays.asList(docs));
    }

    public void indexBeforeCommit(Collection<AbstractESDoc> docs){

        LocalSyncUtilz.runBeforeCommit(()-> {

            if(log.isInfoEnabled()){
                log.info("重建索引开始 数量 = {}", docs.size());
            }

            for(AbstractESDoc doc : docs){
                doc.validateDynamic();
            }

            for(AbstractESDoc doc : docs){
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
        });
    }

    private boolean existsIndices(Class<? extends AbstractESDoc> docType) throws IOException {
        ESDocument document = parseDocument(docType);
        return client.indices()
                .exists(new GetIndexRequest(documentIndex(document)), RequestOptions.DEFAULT);
    }


    void deleteIndices(Class<? extends AbstractESDoc> docType) throws IOException {
        if(existsIndices(docType)){
            ESDocument document = parseDocument(docType);
            client.indices()
                    .delete(new DeleteIndexRequest(documentIndex(document)), RequestOptions.DEFAULT);
        }
    }

    void createIndices(Class<? extends AbstractESDoc> docType) throws IOException {

        if(existsIndices(docType))
            return;

        ESDocument document = parseDocument(docType);

        CreateIndexRequest request = new CreateIndexRequest(documentIndex(document));

        request.settings(buildNgramTokenizer());
        request.mapping(buildMapping(docType));

        client.indices().create(request, RequestOptions.DEFAULT);
    }
}
