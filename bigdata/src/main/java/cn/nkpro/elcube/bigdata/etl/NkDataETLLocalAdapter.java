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
package cn.nkpro.elcube.bigdata.etl;

import cn.nkpro.elcube.basic.TransactionSync;
import cn.nkpro.elcube.bigdata.cards.ReduceConfig;
import cn.nkpro.elcube.bigdata.cards.ReduceState;
import cn.nkpro.elcube.data.elasticearch.SearchEngine;
import cn.nkpro.elcube.docengine.NkDocEngine;
import cn.nkpro.elcube.docengine.model.DocHV;
import cn.nkpro.elcube.docengine.model.es.DocExtES;
import cn.nkpro.elcube.docengine.service.NkDocEngineContext;
import cn.nkpro.elcube.security.NkSecurityRunner;
import cn.nkpro.elcube.co.spel.NkSpELManager;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;


@Component("NkDataETLLocalAdapter")
public class NkDataETLLocalAdapter implements NkDataETLAdapter {

    @Autowired
    private NkDocEngine docEngine;

    @Autowired
    @Qualifier("nkTaskExecutor")
    private TaskExecutor taskExecutor;

    @Autowired
    private NkSecurityRunner securityRunner;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    protected NkSpELManager spELManager;
    @Autowired@SuppressWarnings("all")
    private SearchEngine searchEngine;

    @Override
    public void execute(ReduceConfig config) {

        TransactionSync.runAfterCommitLast("执行数据同步任务",()->{

            taskExecutor.execute(()->{

                int retry = 0;
                do{

                    if(StringUtils.isBlank(config.getPreTask())){
                        break;
                    }else{
                        NkDocEngineContext.clear();
                        DocHV docHV = docEngine.detail(config.getDocId());
                        ReduceState reduceState = (ReduceState) docHV.getData().get(config.getPreTask());
                        if(reduceState==null){
                            return;
                        }
                        if(reduceState.getState().equals("Complete")){
                           break;
                        }else{
                            retry ++;
                            try {
                                Thread.sleep(1000 * 5);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                return;
                            }
                        }
                    }
                }while (retry<10);


                List<Map<String, Object>> data = jdbcTemplate.queryForList(config.getDataCommand());

                data.forEach(item->{

                    String dataKey = (String) spELManager.invoke(config.getDataKey(), item);
                    @SuppressWarnings({"all"})
                    Map<String,Object> dataMapping = (Map<String, Object>) spELManager.invoke(config.getDataMapping(), item);


                    searchEngine.updateBeforeCommit(buildCustomES(dataKey, dataMapping));
                });

                securityRunner.runAsUser("admin",()->
                    docEngine.doUpdate(config.getDocId(),"by etl job",(doc)->
                        doc.fetch(config.getCardKey())
                                .set("state","Complete")
                                .set("records", (long) data.size())
                    )
                );
            });
        });
    }

    private DocExtES buildCustomES(String customId, Map<String,Object> value){
        Assert.isTrue(StringUtils.isNotBlank(customId),"数据同步服务["+getBeanName() + "] 主键id值不能为空");
        DocExtES customES = new JSONObject(value).toJavaObject(DocExtES.class);
        customES.setCustomType("etl-test");
        customES.setCustomId(customId);
        return customES;
    }
}
