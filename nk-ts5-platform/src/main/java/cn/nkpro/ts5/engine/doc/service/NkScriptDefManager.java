package cn.nkpro.ts5.engine.doc.service;


import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.engine.doc.model.ScriptDefHV;
import cn.nkpro.ts5.orm.mb.gen.ScriptDefH;
import cn.nkpro.ts5.orm.mb.gen.ScriptDefHWithBLOBs;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by bean on 2020/7/17.
 */
public interface NkScriptDefManager extends ApplicationListener<ContextRefreshedEvent> {

    PageList<ScriptDefH> getPage(String keyword,
                                 String version,
                                 String state,
                                 int from,
                                 int rows,
                                 String orderField,
                                 String order);

    List<ScriptDefHWithBLOBs> getActiveResources();

    ScriptDefHWithBLOBs getLastVersion(String scriptName);

    ScriptDefH getScript(String scriptName, String version);

    @Transactional
    ScriptDefH doRun(ScriptDefHV scriptDefH, boolean run);

    @Transactional
    void doDelete(ScriptDefHV scriptDefH);

    @Transactional
    ScriptDefH doActive(ScriptDefHV scriptDefH);

    @Transactional
    ScriptDefH doBreach(ScriptDefHV scriptDefH);

    @Transactional
    ScriptDefH doUpdate(ScriptDefHV scriptDefH, boolean force);
//
//    @Transactional
//    DefScript update(DefScript script);
//
//    String getClassName(String beanName);
//
//    DefScript getScriptByName(String scriptName);
}
