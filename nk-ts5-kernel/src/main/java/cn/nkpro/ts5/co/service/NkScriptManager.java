package cn.nkpro.ts5.co.service;


import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.co.PlatformScriptV;
import cn.nkpro.ts5.platform.gen.PlatformScript;
import cn.nkpro.ts5.platform.gen.PlatformScriptWithBLOBs;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by bean on 2020/7/17.
 */
public interface NkScriptManager extends ApplicationListener<ContextRefreshedEvent> {

    PageList<PlatformScript> getPage(String keyword,
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
    PlatformScript doRun(PlatformScriptV scriptDefH, boolean run);

    @Transactional
    void doDelete(PlatformScriptV scriptDefH);

    @Transactional
    PlatformScript doActive(PlatformScriptV scriptDefH, boolean force);

    @Transactional
    PlatformScript doBreach(PlatformScriptV scriptDefH);

    @Transactional
    PlatformScript doUpdate(PlatformScriptV scriptDefH, boolean force);
//
//    @Transactional
//    DefScript update(DefScript script);
//
//    String getClassName(String beanName);
//
//    DefScript getScriptByName(String scriptName);
}
