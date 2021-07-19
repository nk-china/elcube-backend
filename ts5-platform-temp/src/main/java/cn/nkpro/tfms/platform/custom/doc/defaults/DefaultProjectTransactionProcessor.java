package cn.nkpro.tfms.platform.custom.doc.defaults;

import cn.nkpro.tfms.platform.custom.EnumDocClassify;
import cn.nkpro.tfms.platform.custom.doc.AbstractDocProcessor;
import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.services.TfmsProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
@Component("nkDefaultProjectTransactionProcessor")
public class DefaultProjectTransactionProcessor extends AbstractDocProcessor {

    @Autowired
    private TfmsProjectService projectService;


    @Override
    public EnumDocClassify classify() {
        return EnumDocClassify.TRANSACTION;
    }

    @Override
    public Class<? extends BizDocBase> dataType() {
        return BizProjectTranDoc.class;
    }

    @Override
    public BizDocBase detailAfterComponent(BizDocBase doc) {

        BizProjectTranDoc tranDoc = (BizProjectTranDoc) super.detailAfterComponent(doc);

        Assert.notNull(tranDoc.getRefObjectId(),"业务ID不存在");
        tranDoc.setRefObject(projectService.getProject(tranDoc));

        return tranDoc;
    }

    @Override
    public  BizDocBase updateBeforeComponent(BizDocBase doc, boolean isCreate) {
        if(isCreate) {
            projectService.toCreateProjectDoc((BizProjectTranDoc) doc);
        }
        return super.updateBeforeComponent(doc, isCreate);
    }

    @Override
    public  BizDocBase createBeforeComponent(BizDocBase doc) {
        ((BizProjectTranDoc) super.createBeforeComponent(doc))
                .setRefObject(projectService.toCreateProjectDoc((BizProjectTranDoc) doc));
        return super.createBeforeComponent(doc);
    }
//
//
//    private void execInterceptorPreCondition(BizProjectBO projectBO, BizProjectTranDoc doc){
//        DefProjectDocLI defProjectDoc = getDefProjectDoc(doc);
//        if(StringUtils.isNotBlank(defProjectDoc.getRefObjectType())){
//            TfmsProjectDocInterceptor interceptor = customObjectManager.getCustomObject(defProjectDoc.getRefObjectType(),TfmsProjectDocInterceptor.class);
//            if(!interceptor.preCondition(projectBO,doc.getPreDoc(),defProjectDoc)){
//                throw new TfmsOperatNotAllowedException("当前操作已不满足业务流规则");
//            }
//        }
//    }
//
//    private DefProjectDocLI getDefProjectDoc(BizProjectTranDoc doc){
//        String prevType = StringUtils.EMPTY;
//        String prevState = StringUtils.EMPTY;
//        if(doc.getPreDocId()!=null && !StringUtils.equalsAny(doc.getPreDocId(), Constants.BIZ_DEFAULT_EMPTY,StringUtils.EMPTY)){
//            BizDocBase prev = docService.getDocDetail(doc.getPreDocId(),false);
//            prevType  = prev.getDocType();
//            prevState = prev.getDocState();
//        }
//
//        String fPrevType = prevType;
//        String fPrevState = prevState;
//
//        // 获取业务流配置
//        return doc.getRefObject().getDefined()
//                .getDocs()
//                .stream()
//                .filter(def -> StringUtils.equals(def.getDocType(), doc.getDocType()))
//                .filter(def -> StringUtils.equals(def.getPreDocType(), fPrevType))
//                .filter(def -> StringUtils.isBlank(def.getPreProjectStatus()) || ArrayUtils.contains(StringUtils.split(def.getPreProjectStatus(),','),doc.getRefObject().getProjectState()))
//                .filter(def -> StringUtils.isBlank(def.getPreDocStatus()) || ArrayUtils.contains(StringUtils.split(def.getPreDocStatus(),','),fPrevState))
//                .findFirst()
//                .orElseThrow(()->new TfmsDefineException("没有找到业务流配置"));
//    }
}
