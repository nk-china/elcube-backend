package cn.nkpro.ts5.engine.doc;

import cn.nkpro.ts5.engine.co.NKCustomObject;
import cn.nkpro.ts5.engine.co.NKCustomScriptObject;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.model.DocDefIV;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.orm.mb.gen.ScriptDefH;
import cn.nkpro.ts5.orm.mb.gen.ScriptDefHWithBLOBs;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface NKCard<DT,DDT> extends NKCustomObject{

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

    String[] getDefComponentNames();

    // 配置方法
    DDT deserializeDef(DocDefIV defI);

    DDT afterGetDef(DocDefHV defHV, DocDefIV defIV, DDT def);

    // 解析数据
    DT deserialize(Object data);

    // 创建方法
    DT afterCreate(DocHV doc, DocHV preDoc, DT data, DDT def);

    // 查询方法
    DT afterGetData(DocHV doc, DT data, DDT def);

    // 计算方法
    DT calculate(DocHV doc, DT data, DDT def, boolean isTrigger, String options);

    // 更新方法
    DT beforeUpdate(DocHV doc, DT data, DDT def, DT original);

    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    default void afterUpdated(DocHV doc, DT data, DDT def){};
}
