package cn.nkpro.tfms.platform.custom.doc;

import cn.nkpro.tfms.platform.basis.TfmsCustomObject;
import cn.nkpro.tfms.platform.custom.EnumDocClassify;
import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.DefDocTypeBO;
import cn.nkpro.tfms.platform.model.index.IndexDoc;

import java.util.Map;

/**
 * Created by bean on 2020/7/9.
 */
public interface TfmsDocProcessor extends TfmsCustomObject {

    EnumDocClassify classify(); //单据的枚举类型

    boolean standalone();

    Class<? extends BizDocBase> dataType();

    String getProcessorName();

    default String getDocHeaderComponentName(){return null;};

    default String[] getDocDefComponentNames() {return new String[0];}

    Map<String,Object> getDocProcessDef(DefDocTypeBO def);

    BizDocBase detail(BizDocBase doc);

    BizDocBase create(DefDocTypeBO def, String refObjectId, String preDocId);

    BizDocBase calculate(String component, String calculate, BizDocBase doc);

    Object call(String component, String event, BizDocBase doc);

    BizDocBase update(BizDocBase doc, BizDocBase original, String source);

    void updateBeforeCommit(BizDocBase doc);

    default void stateChanged(BizDocBase doc, String oldDocState){}

    IndexDoc buildIndex(BizDocBase docBO);

    void index(BizDocBase docBO);

    Object exec(String json);
}
