package cn.nkpro.ts5.docengine.service.impl;

import cn.nkpro.ts5.co.NkCustomObjectManager;
import cn.nkpro.ts5.co.spel.NkSpELManager;
import cn.nkpro.ts5.docengine.datasync.NkDocDataAdapter;
import cn.nkpro.ts5.docengine.model.DocHV;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;

class AbstractNkDocEngine {

    @Autowired
    private NkSpELManager spELManager;
    @Autowired
    private NkCustomObjectManager customObjectManager;

    /**
     * todo 如果 doc 和 original是同一个对象，可以优化性能
     */
    void dataSync(DocHV doc, DocHV original, boolean reExecute){

        EvaluationContext context1 = spELManager.createContext(doc);
        EvaluationContext context2 = spELManager.createContext(original);

        if(doc.getDef().getDataSyncs()!=null){

            doc.getDef().getDataSyncs().forEach(config -> {
                if(!reExecute || config.getReExecute() == 1){
                    // 满足前置条件
                    if(StringUtils.isBlank(config.getConditionSpEL())||
                            (Boolean) spELManager.invoke(config.getConditionSpEL(),context1)){

                        customObjectManager.getCustomObject(config.getTargetSvr(), NkDocDataAdapter.class)
                                .sync(doc, original, context1, context2, config);
                    }
                }
            });
        }
    }
}
