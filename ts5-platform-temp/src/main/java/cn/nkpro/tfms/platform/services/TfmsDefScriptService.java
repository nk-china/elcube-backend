package cn.nkpro.tfms.platform.services;


import cn.nkpro.tfms.platform.model.po.DefScript;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by bean on 2020/7/17.
 */
public interface TfmsDefScriptService {

    DefScript getScript(String scriptId);

    @Transactional
    DefScript update(DefScript script);

    List<DefScript> getAll();

    String getClassName(String beanName);

    DefScript getScriptByName(String scriptName);
}
