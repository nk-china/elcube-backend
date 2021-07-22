package cn.nkpro.ts5.engine.doc;

import cn.nkpro.ts5.basic.NKCustomObject;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.model.mb.gen.DocDefI;

public interface NKCard<DT,DDT> extends NKCustomObject {

    /**
     * 获取组件名称
     * @return string
     */
    String getCardHandler();



    Object afterCreate(DocHV doc, DocHV preDoc, DT data, DDT def);

    @SuppressWarnings("all")
    DT getData(DocHV doc, DocDefI defI) throws Exception;

    DT afterGetData(DocHV doc, String data, String def);


    Object deserializeDef(Object def);

    Object deserialize(Object data);
}
