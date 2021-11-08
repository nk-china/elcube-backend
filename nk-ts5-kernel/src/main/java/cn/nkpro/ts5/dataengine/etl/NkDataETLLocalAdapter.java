package cn.nkpro.ts5.dataengine.etl;

import cn.nkpro.ts5.basic.TransactionSync;
import cn.nkpro.ts5.data.elasticearch.SearchEngine;
import cn.nkpro.ts5.dataengine.cards.ReduceConfig;
import cn.nkpro.ts5.dataengine.cards.ReduceState;
import cn.nkpro.ts5.docengine.NkDocEngine;
import cn.nkpro.ts5.docengine.model.DocHV;
import cn.nkpro.ts5.docengine.model.es.DocExtES;
import cn.nkpro.ts5.docengine.service.NkDocEngineContext;
import cn.nkpro.ts5.security.NkSecurityRunner;
import cn.nkpro.ts5.spel.NkSpELManager;
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

        TransactionSync.runAfterCommitLast(()->{

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


                securityRunner.runAsUser("admin");
                docEngine.doUpdate(config.getDocId(),"by etl job",(doc)->{
                    doc.fetch(config.getCardKey())
                            .set("state","Complete")
                            .set("records", (long) data.size());
                });
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
