package cn.nkpro.ts5.platform.service;


import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.co.NkScriptV;
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
