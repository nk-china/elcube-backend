package cn.nkpro.tfms.platform.services;

import cn.nkpro.tfms.platform.custom.doc.defaults.BizProjectDoc;
import cn.nkpro.tfms.platform.custom.doc.defaults.BizProjectTranDoc;
import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.BizProjectBO;
import cn.nkpro.tfms.platform.model.DefProjectTypeBO;
import cn.nkpro.tfms.platform.model.po.BizProject;

public interface TfmsProjectService {

    BizProjectBO createProject(String projectId, String projectType);

    void doUpdateProject(BizProjectBO projectBO, boolean isCreate);

    BizProjectBO getProject(BizProjectDoc doc);

    BizProjectBO toCreateProjectDoc(BizProjectTranDoc doc);
}
