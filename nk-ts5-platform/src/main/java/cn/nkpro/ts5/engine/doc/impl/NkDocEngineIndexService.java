package cn.nkpro.ts5.engine.doc.impl;

import cn.nkpro.ts5.config.redis.RedisSupport;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.engine.doc.service.NkDocEngineFrontService;
import cn.nkpro.ts5.engine.elasticearch.SearchService;
import cn.nkpro.ts5.engine.elasticearch.SearchEngine;
import cn.nkpro.ts5.engine.elasticearch.model.DocHES;
import cn.nkpro.ts5.engine.task.NkBpmTaskManager;
import cn.nkpro.ts5.orm.mb.gen.DocH;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class NkDocEngineIndexService {
    @Autowired
    private SearchService searchService;
    @Autowired
    private SearchEngine searchEngine;
    @Autowired
    private NkDocEngineFrontService docEngineFrontService;
    @Autowired@SuppressWarnings("all")
    private NkBpmTaskManager bpmTaskManager;
    @Autowired
    private RedisSupport<ReindexInfo> redisReindexInfo;

    @Async
    public void reindex(String asyncTaskId, Boolean dropFirst, String docType) throws IOException {
        if(dropFirst){
            searchService.dropAndInit();
        }

        int offset = 0;
        int rows   = 1000;
        int total  = 0;
        List<DocH> list;
        try{
            while((list = docEngineFrontService.list(docType, offset, rows, null)).size()>0){

                list.forEach(doc->{
                    // 获取详情
                    DocHV docHV = docEngineFrontService.detail(doc.getDocId());

                    // 创建单据索引
                    searchEngine.indexBeforeCommit(DocHES.from(docHV));

                    // 创建任务索引
                    bpmTaskManager.indexDocTask(docHV);

                    // 记录日志
                    redisReindexInfo.set(asyncTaskId,
                        new ReindexInfo(false,0,String.format("重建索引 docId = %s docDesc = %s",doc.getDocId(), doc.getDocDesc()),null)
                    );
                });
                offset += rows;
                total  += list.size();
            }
            redisReindexInfo.set(asyncTaskId,
                new ReindexInfo(false,0,String.format("重建索引完成，共 %d 条记录",total),null)
            );
        }catch (Exception e){
            e.printStackTrace();
            redisReindexInfo.set(asyncTaskId,
                new ReindexInfo(false,0,String.format("重建索引发生错误: %s",e.getMessage()),ExceptionUtils.getRootCauseStackTrace(e))
            );
        }
    }

    public ReindexInfo getReindexInfo(String asyncTaskId){
        return redisReindexInfo.getIfAbsent(asyncTaskId,()->null);
    }

    @Data
    @AllArgsConstructor
    static class ReindexInfo{
        boolean finished;
        int total;
        String message;
        String[] exceptions;
    }
}
