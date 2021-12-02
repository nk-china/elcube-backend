/*
 * This file is part of EAsis.
 *
 * EAsis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EAsis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with EAsis.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.easis.docengine.service.impl;

import cn.nkpro.easis.basic.PageList;
import cn.nkpro.easis.data.redis.RedisSupport;
import cn.nkpro.easis.docengine.NkDocSearchService;
import cn.nkpro.easis.docengine.gen.DocH;
import cn.nkpro.easis.docengine.model.DocHV;
import cn.nkpro.easis.docengine.service.NkDocEngineFrontService;
import cn.nkpro.easis.task.NkBpmTaskManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NkDocEngineIndexService {
    @Autowired
    private NkDocSearchService searchService;
    @Autowired
    private NkDocEngineFrontService docEngineFrontService;
    @Autowired@SuppressWarnings("all")
    private NkBpmTaskManager bpmTaskManager;
    @Autowired
    private RedisSupport<ReindexInfo> redisReindexInfo;

    @Async
    public void reindex(String asyncTaskId, Boolean dropFirst, String docType) throws IOException {
        redisReindexInfo.set(asyncTaskId,
            new ReindexInfo(false,0L, 0L, "加载索引任务", null)
        );

        if(dropFirst){
            searchService.dropAndInit();
        }


        int offset = 0;
        int rows   = 1000;
        long totalS = 0;
        long total  = 0;
        PageList<DocH> list;
        try{
            while((list = docEngineFrontService.list(docType, offset, rows, null)).getList().size()>0){

                total = list.getTotal();

                final long t1 = total;
                final long t2 = totalS;
                list.getList().forEach(doc->{
                    // 记录日志
                    redisReindexInfo.set(asyncTaskId,
                            new ReindexInfo(false,t1, t2, String.format("重建索引 docId = %s docDesc = %s",doc.getDocId(), doc.getDocDesc()),null)
                    );
                    // 获取详情
                    DocHV docHV = docEngineFrontService.detail(doc.getDocId());

                    // 创建单据索引
                    docEngineFrontService.reDataSync(docHV);

                    // 创建任务索引
                    bpmTaskManager.indexDocTask(docHV);
                });
                offset += rows;
                totalS  += list.getList().size();
            }
            redisReindexInfo.set(asyncTaskId,
                new ReindexInfo(true,total, totalS,"重建索引完成",null)
            );
        }catch (Exception e){
            e.printStackTrace();
            redisReindexInfo.set(asyncTaskId,
                new ReindexInfo(true,total, totalS,String.format("重建索引发生错误: %s",e.getMessage()),ExceptionUtils.getRootCauseStackTrace(e))
            );
        }finally {
            redisReindexInfo.expire(asyncTaskId, 10);
        }
    }

    public ReindexInfo getReindexInfo(String asyncTaskId){
        return redisReindexInfo.get(asyncTaskId);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class ReindexInfo{
        boolean finished;
        long total;
        long totalS;
        String message;
        String[] exceptions;
    }
}
