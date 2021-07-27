package cn.nkpro.ts5.engine.doc;

import cn.nkpro.ts5.basic.NKCustomObject;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.orm.mb.gen.DocDefI;
import cn.nkpro.ts5.orm.mb.gen.DocDefIWithBLOBs;

public interface NKCard<DT,DDT> extends NKCustomObject {

    String POSITION_DEFAULT = "default";
    String POSITION_HEADER  = "header";
    String POSITION_SIDEBAR = "sidebar";

    /**
     * 获取组件名称
     * @return string
     */
    String getCardHandler();

    String getPosition();

    String getDataComponentName();

    String[] getDefComponentNames();

    String getCardName();


    DDT def(DocDefIWithBLOBs defI);

    DT create(DocHV doc, DocHV preDoc, DocDefI defI) throws Exception;

    Object afterCreate(DocHV doc, DocHV preDoc, DT data, DDT def);

    DT calculate(DocHV doc, DocDefI defI,String options) throws Exception;

    @SuppressWarnings("all")
    DT getData(DocHV doc, DocDefI defI) throws Exception;

    DT afterGetData(DocHV doc, String data, String def);


    Object deserializeDef(Object def);

    Object deserialize(Object data);
}
