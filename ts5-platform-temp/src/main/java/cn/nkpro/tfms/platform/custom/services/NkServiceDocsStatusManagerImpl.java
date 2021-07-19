package cn.nkpro.tfms.platform.custom.services;

import cn.nkpro.tfms.platform.custom.EnumDocClassify;
import cn.nkpro.tfms.platform.custom.TfmsAbstractService;
import cn.nkpro.tfms.platform.custom.doc.defaults.BizProjectDoc;
import cn.nkpro.tfms.platform.mappers.gen.DefServiceStatusManagerMapper;
import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.DefDocTypeBO;
import cn.nkpro.tfms.platform.model.po.DefServiceStatusManager;
import cn.nkpro.tfms.platform.model.po.DefServiceStatusManagerExample;
import cn.nkpro.tfms.platform.services.TfmsDocEngine;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import io.jsonwebtoken.lang.Assert;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("nk-service-docs-status-manager")
public class NkServiceDocsStatusManagerImpl extends TfmsAbstractService<List<DefServiceStatusManager>> {

    private TfmsDocEngine docEngine;

    private DefServiceStatusManagerMapper serviceStatusManagerMapper;

    @Autowired
    public NkServiceDocsStatusManagerImpl(
            DefServiceStatusManagerMapper serviceStatusManagerMapper,
            TfmsDocEngine docEngine
    ) {
        this.serviceStatusManagerMapper = serviceStatusManagerMapper;
        this.docEngine = docEngine;
    }

    @Override
    public String getComponentDesc() {
        return "单据状态管理";
    }

    @Override
    public String[] getDefComponentNames() {
        return new String[]{getComponentName()};
    }

    @Override
    protected List<DefServiceStatusManager> doGetDef(DefDocTypeBO defDocType) {

        DefServiceStatusManagerExample example = new DefServiceStatusManagerExample();
        example.createCriteria().andDocTypeEqualTo(defDocType.getDocType());
        example.setOrderByClause("ORDERBY");

        return serviceStatusManagerMapper.selectByExample(example);
    }

    @Override
    protected void doUpdateDef(DefDocTypeBO defDocType, List<DefServiceStatusManager> def) throws Exception {
        super.doUpdateDef(defDocType, def);

        DefServiceStatusManagerExample example = new DefServiceStatusManagerExample();
        example.createCriteria().andDocTypeEqualTo(defDocType.getDocType());
        serviceStatusManagerMapper.deleteByExample(example);

        def.forEach(item->{
            item.setDocType(defDocType.getDocType());
            item.setUpdatedTime(DateTimeUtilz.nowSeconds());
            item.setOrderby(def.indexOf(item));
            serviceStatusManagerMapper.insert(item);
        });

    }

    @Override
    protected void doUpdate(BizDocBase doc, BizDocBase original, DefDocTypeBO docDef, List<DefServiceStatusManager> def) {

        if(original==null||!StringUtils.equals(original.getDocState(),doc.getDocState())){
            String source = "Updated By "+doc.getDocType()+" ["+doc.getDocId()+"]";
            def.stream()
                .filter(item-> StringUtils.equals(item.getCondState(),doc.getDocState()))
                .forEach(item->{
                    switch (item.getTargetType()){
                        case "PROJECT":
                            updateProject(doc,item,source);
                            break;
                        case "PREV":
                            updatePrev(doc,item,source);
                            break;
                        case "SIBLING":
                            updateSibling(doc,item,source);
                            break;
                        case "NEXT":
                            updateNext(doc,item,source);
                            break;
                    }
                });
        }
    }

    private void updateProject(BizDocBase doc,DefServiceStatusManager def,String source){
        if(StringUtils.isNotBlank(doc.getRefObjectId())){
            BizDocBase targetDoc = docEngine.getDocDetail(doc.getRefObjectId());
            Assert.notNull(targetDoc,"项目单据没有找到");
            Assert.isTrue(targetDoc instanceof BizProjectDoc,"项目单据必须为PROJECT类型");

            ((BizProjectDoc)targetDoc).getRefObject().setProjectState(def.getTargetState());
            docEngine.doUpdate(targetDoc,source);
        }
    }

    private void updatePrev(BizDocBase doc,DefServiceStatusManager def,String source){
        if(StringUtils.isNotBlank(doc.getPreDocId())) {
            BizDocBase targetDoc = docEngine.getDocDetail(doc.getPreDocId());
            Assert.notNull(targetDoc,"前序单据没有找到");
            targetDoc.setDocState(def.getTargetState());
            docEngine.doUpdate(targetDoc,source);
        }
    }

    private void updateSibling(BizDocBase doc,DefServiceStatusManager def,String source){
        docEngine.getListByRefObject(doc.getRefObjectId(), doc.getPreDocId(), EnumDocClassify.valueOf(doc.getClassify()))
            .stream()
                .filter(sibling->StringUtils.equals(sibling.getDocType(),def.getTargetDocType()))
                .filter(sibling->!StringUtils.equals(sibling.getDocId(),doc.getDocId()))
                .forEach(sibling->{
                    BizDocBase targetDoc = docEngine.getDocDetail(sibling.getDocId());
                    targetDoc.setDocState(def.getTargetState());
                    docEngine.doUpdate(targetDoc,source);
                });
    }

    private void updateNext(BizDocBase doc,DefServiceStatusManager def,String source){
        docEngine.getListByRefObject(doc.getRefObjectId(), doc.getDocId(), EnumDocClassify.valueOf(doc.getClassify()))
                .stream()
                .filter(sibling->StringUtils.equals(sibling.getDocType(),def.getTargetDocType()))
                .filter(sibling->!StringUtils.equals(sibling.getDocId(),doc.getDocId()))
                .forEach(sibling->{
                    BizDocBase targetDoc = docEngine.getDocDetail(sibling.getDocId());
                    targetDoc.setDocState(def.getTargetState());
                    docEngine.doUpdate(targetDoc,source);
                });
    }
}
