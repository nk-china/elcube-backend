package cn.nkpro.tfms.platform.tasks;

import cn.nkpro.tfms.platform.mappers.CustomMapper;
import cn.nkpro.tfms.platform.services.TfmsStreamTaskService;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TfmsBreachTaskTimer {

    @Autowired
    private CustomMapper customMapper;
    @Autowired
    private TfmsStreamTaskService streamTaskService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void createBreachTasks(){

        streamTaskService.doInLock(this,()->{

            Map<String,Object> params = new HashMap<>();
            params.put("expireDate", DateTimeUtilz.todaySeconds());
            params.put("billAvailable",1);
            List<String> strings = customMapper.selectAllBreachPaymentDocId(params);


            strings.forEach(docId->{
                streamTaskService.createTask("TfmsBreachTask",docId);
            });
        });
    }
}
