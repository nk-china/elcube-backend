package cn.nkpro.tfms.platform.services;

import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.DefProjectTypeBO;
import cn.nkpro.tfms.platform.model.po.BizProject;
import cn.nkpro.tfms.platform.model.po.DefProjectType;
import cn.nkpro.tfms.platform.model.util.PageList;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by bean on 2020/7/13.
 */
public interface TfmsDefProjectTypeService {


    PageList<DefProjectType> getPage(String projectType, String keyword, int from, int rows, String orderField, String order);

    List<DefProjectType> getAllTypes();

    DefProjectTypeBO getProjectDefined(String projectType);

    DefProjectTypeBO getProjectDefined(String projectType, Integer version);

    Map<String, Object> options();

    @Transactional
    void doUpdate(DefProjectTypeBO defProjectType, boolean create, boolean force);
}
