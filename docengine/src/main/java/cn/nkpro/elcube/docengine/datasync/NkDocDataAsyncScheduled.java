/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.docengine.datasync;

import cn.nkpro.elcube.co.NkCustomObjectManager;
import cn.nkpro.elcube.docengine.gen.DocAsyncQueue;
import cn.nkpro.elcube.docengine.gen.DocAsyncQueueExample;
import cn.nkpro.elcube.docengine.gen.DocAsyncQueueMapper;
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
