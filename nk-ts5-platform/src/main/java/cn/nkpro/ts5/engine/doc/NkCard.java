package cn.nkpro.ts5.engine.doc;

import cn.nkpro.ts5.engine.co.NkCustomObject;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.model.DocDefIV;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface NkCard<DT,DDT> extends NkCustomObject {

    String POSITION_DEFAULT = "default";
    String POSITION_HEADER  = "header";
    String POSITION_SIDEBAR = "sidebar";

    /**
     * 获取组件名称
     * @return string
     */
    String getCardName();

    String getPosition();

    String getDataComponentName();

    String[] getAutoDefComponentNames();

    Map<String,String> getVueTemplate();

    // 配置方法
    DDT deserializeDef(Object defContent);

    DDT afterGetDef(DocDefHV defHV, DocDefIV defIV, DDT def);

    // 解析数据
    DT deserialize(Object data);

    // 创建方法
    DT afterCreate(DocHV doc, DocHV preDoc, DT data, DocDefIV defIV, DDT def);

    // 查询方法
    DT afterGetData(DocHV doc, DT data, DocDefIV defIV, DDT def);

    // 计算方法
    DT calculate(DocHV doc, DT data, DocDefIV defIV, DDT def, boolean isTrigger, String options);

    // 调用方法
    DT call(DocHV doc, DT data, DocDefIV defIV, DDT def, String options);

    // 更新方法
    DT beforeUpdate(DocHV doc, DT data, DT original, DocDefIV defIV, DDT def);
    // 更新之后调用
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    DT afterUpdated(DocHV doc, DT data, DT original, DocDefIV defIV, DDT def);

    void stateChanged(DocHV doc, DocHV original, DT data, DocDefIV defIV, DDT def);

    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    default void updateCommitted(DocHV doc, DT data, DocDefIV defIV, DDT def){};

    boolean isDebug();

    default boolean enableDataDiff(){return true;}
}
