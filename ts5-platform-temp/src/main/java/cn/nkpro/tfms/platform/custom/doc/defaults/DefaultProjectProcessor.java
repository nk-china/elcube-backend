package cn.nkpro.tfms.platform.custom.doc.defaults;

import cn.nkpro.tfms.platform.basis.Constants;
import cn.nkpro.tfms.platform.custom.EnumDocClassify;
import cn.nkpro.tfms.platform.custom.doc.AbstractDocProcessor;
import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.index.IndexDoc;
import cn.nkpro.tfms.platform.services.TfmsProjectService;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
@Component("nkDefaultProjectProcessor")
public class DefaultProjectProcessor extends AbstractDocProcessor {

    @Autowired
    private TfmsProjectService projectService;

    @Override
    public String getDocHeaderComponentName() {
        return "nk-page-doc-header-project";
    }

    @Override
    public EnumDocClassify classify() {
        return EnumDocClassify.PROJECT;
    }
    @Override
    public Class<? extends BizProjectDoc> dataType() {
        return BizProjectDoc.class;
    }

    @Override
    public BizProjectDoc detailAfterComponent(BizDocBase doc) {
        BizProjectDoc projectDoc = (BizProjectDoc) super.detailAfterComponent(doc);
        projectDoc.setRefObjectId(doc.getDocId());
        projectDoc.setRefObject(projectService.getProject(projectDoc));

        return projectDoc;
    }

    @Override
    public BizProjectDoc createBeforeComponent(BizDocBase doc) {
        BizProjectDoc projectDoc = (BizProjectDoc) super.createBeforeComponent(doc);
        projectDoc.setRefObjectId(doc.getDocId());
        projectDoc.setRefObject(projectService.createProject(projectDoc.getDocId(), projectDoc.getDocType()));

        return projectDoc;
    }


    @Override
    public BizDocBase updateBeforeComponent(BizDocBase doc, boolean isCreate) {
        BizProjectDoc projectDoc = (BizProjectDoc) super.updateBeforeComponent(doc, isCreate);

        Assert.notNull(projectDoc.getRefObject(),"业务对象不能为空");

        projectDoc.setRefObjectId(doc.getDocId());
        projectDoc.getRefObject().setProjectNumber(doc.getDocNumber());
        projectDoc.getRefObject().setUpdatedTime(DateTimeUtilz.nowSeconds());
        projectDoc.getRefObject().setProjectName(doc.getDocName());
        projectDoc.getRefObject().setProjectId(doc.getDocId());
        projectService.doUpdateProject(projectDoc.getRefObject(),isCreate);

        return doc;
    }

    @Override
    public BizDocBase updateAfterComponent(BizDocBase doc, boolean isCreate) {
        super.updateAfterComponent(doc,isCreate);
        redisSupportObject.delete(String.format("%s%s", Constants.CACHE_PROJECT,doc.getDocId()));
        return doc;
    }

    @Override
    public IndexDoc buildIndex(BizDocBase doc,IndexDoc index) {

        BizProjectDoc projectDoc = (BizProjectDoc)doc;
        index.setProjectState(projectDoc.getRefObject().getProjectState());

        projectDoc.getRefObject().getDefined()
                .getStatus()
                .stream()
                .filter(state-> StringUtils.equals(state.getProjectState(),projectDoc.getRefObject().getProjectState()))
                .findFirst()
                .ifPresent(state-> index.setProjectStateDesc(String.format("%s | %s",projectDoc.getRefObject().getProjectState(), state.getProjectStateDesc())));
        return index;
    }
}
