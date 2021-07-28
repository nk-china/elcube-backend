package cn.nkpro.ts5.engine.script;


import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.orm.mb.gen.ScriptDefH;
import cn.nkpro.ts5.orm.mb.gen.ScriptDefHWithBLOBs;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by bean on 2020/7/17.
 */
public interface ScriptDefManager {

    PageList<ScriptDefH> getPage(String keyword,
                                 String version,
                                 String state,
                                 int from,
                                 int rows,
                                 String orderField,
                                 String order);

    List<ScriptDefHWithBLOBs> getActiveResources();

    ScriptDefH getScript(String scriptName, String version);

    @Transactional
    ScriptDefH doEdit(ScriptDefHWithBLOBs scriptDefH);

    @Transactional
    ScriptDefH doUpdate(ScriptDefHWithBLOBs scriptDefH, boolean force);
//
//    @Transactional
//    DefScript update(DefScript script);
//
//    String getClassName(String beanName);
//
//    DefScript getScriptByName(String scriptName);
}
