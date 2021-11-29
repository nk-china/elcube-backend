package cn.nkpro.easis.platform.service;


import cn.nkpro.easis.basic.PageList;
import cn.nkpro.easis.co.NkScriptV;
import cn.nkpro.easis.platform.gen.PlatformScript;
import cn.nkpro.easis.platform.gen.PlatformScriptWithBLOBs;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by bean on 2020/7/17.
 */
public interface NkScriptManager extends ApplicationListener<ApplicationEvent> {

    PageList<PlatformScript> getPage(String keyword,
                                     String type,
                                     String version,
                                     String state,
                                     int from,
                                     int rows,
                                     String orderField,
                                     String order);

    List<PlatformScriptWithBLOBs> getActiveResources();

//    PlatformScriptWithBLOBs getLastVersion(String scriptName);

    PlatformScript getScript(String scriptName, String version);

    @Transactional
    PlatformScript doRun(NkScriptV scriptDefH, boolean run);

    @Transactional
    void doDelete(NkScriptV scriptDefH);

    @Transactional
    PlatformScript doActive(NkScriptV scriptDefH, boolean force);

    @Transactional
    PlatformScript doBreach(NkScriptV scriptDefH);

    @Transactional
    PlatformScript doUpdate(NkScriptV scriptDefH, boolean force);
//
//    @Transactional
//    DefScript update(DefScript script);
//
//    String getClassName(String beanName);
//
//    DefScript getScriptByName(String scriptName);
}
