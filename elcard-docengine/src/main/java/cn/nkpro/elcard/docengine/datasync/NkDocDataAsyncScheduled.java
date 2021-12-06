/*
 * This file is part of ELCard.
 *
 * ELCard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcard.docengine.datasync;

import cn.nkpro.elcard.co.NkCustomObjectManager;
import cn.nkpro.elcard.docengine.gen.DocAsyncQueue;
import cn.nkpro.elcard.docengine.gen.DocAsyncQueueExample;
import cn.nkpro.elcard.docengine.gen.DocAsyncQueueMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class NkDocDataAsyncScheduled {

    @Autowired@SuppressWarnings("all")
    private NkCustomObjectManager customObjectManager;

    @Autowired@SuppressWarnings("all")
    private DocAsyncQueueMapper asyncQueueMapper;

    @Scheduled(cron = "0 * * * * ?")
    public void cron(){

        DocAsyncQueueExample example = new DocAsyncQueueExample();
        example.createCriteria()
                .andAsyncStateEqualTo("WAITING");
        example.setOrderByClause("UPDATED_TIME");

        List<DocAsyncQueue> asyncQueues = asyncQueueMapper.selectByExample(example);

        if(log.isInfoEnabled())
            log.info("数据同步重试 任务数量：{}",asyncQueues.size());

        asyncQueues.forEach(asyncQueue->
            customObjectManager.getCustomObject(asyncQueue.getAsyncObjectRef(), NkDocDataAsyncAdapter.class)
                .run(asyncQueue)
        );

        if(log.isInfoEnabled())
            log.info("数据同步重试完成 任务数量：{}",asyncQueues.size());
    }
}
