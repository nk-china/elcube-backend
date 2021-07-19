package cn.nkpro.tfms.platform.controllers.sys;

import cn.nkpro.tfms.platform.services.TfmsDocService;
import cn.nkpro.tfms.platform.services.TfmsTaskService;
import cn.nkpro.ts5.supports.RedisSupport;
import cn.nkpro.tfms.platform.tasks.TfmsBreachTaskTimer;
import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Created by bean on 2020/7/23.
 */
@WsDocNote("Z.系统工具")
@RestController
@RequestMapping("/sys/utils")
@PreAuthorize("hasAnyAuthority('*:*')")
public class SysUtilsController implements ApplicationContextAware {

    @Autowired
    private RedisSupport redisSupport;
    @Autowired
    private TfmsDocService docService;
    @Autowired
    private TfmsTaskService taskService;
    @Autowired
    private TfmsBreachTaskTimer breachTask;

    @WsDocNote("1、清空缓存")
    @RequestMapping("/redis/flush")
    public void flushAll(){
        redisSupport.clear();
    }

    @WsDocNote("2、重建索引")
    @RequestMapping("/search/engine/reindex")
    public void reindex(@RequestParam(value = "docType",required = false) String docType) throws IOException {
        docService.reindex(docType);
    }

    @WsDocNote("3、重建任务索引")
    @RequestMapping("/search/engine/reindexTask")
    public void reindexTask() {
        taskService.reIndexTask();
    }

    @WsDocNote("4、计算罚息")
    @RequestMapping("/payment/breach")
    public void paymentBreach() {
        breachTask.createBreachTasks();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    }
}
