package cn.nkpro.ts5.dataengine.cards;

import cn.nkpro.ts5.annotation.NkNote;
import cn.nkpro.ts5.co.NkCustomObjectManager;
import cn.nkpro.ts5.dataengine.etl.NkDataETLAdapter;
import cn.nkpro.ts5.docengine.NkAbstractCard;
import cn.nkpro.ts5.docengine.model.DocDefIV;
import cn.nkpro.ts5.docengine.model.DocHV;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@NkNote("数据归集")
@Component("NkCardDataMapReduce")
public class NkCardDataMapReduce extends NkAbstractCard<ReduceState, ReduceConfig> {

    @Autowired
    private NkCustomObjectManager customObjectManager;

    @Override
    public String getDataComponentName() {
        return getBeanName();
    }

    @Override
    protected String[] getDefComponentNames() {
        return new String[]{
            "NkCardDataMapReduceDef"
        };
    }

    @Override
    public ReduceState beforeUpdate(DocHV doc, ReduceState data, ReduceState original, DocDefIV defIV, ReduceConfig d) {

        if ("S001".equals(doc.getDocState())) {
            data.setState("NotActive");
        } else if("NotActive".equals(data.getState())){
            data.setState("Waiting");
        }else{
            data.setState(StringUtils.defaultIfBlank(data.getState(), "Waiting"));
        }

        return super.beforeUpdate(doc, data, original, defIV, d);
    }

    @Override
    public void stateChanged(DocHV doc, DocHV original, ReduceState data, DocDefIV defIV, ReduceConfig d) {

        if(doc.getDocState().equals("S002")){

            d.setDocId(doc.getDocId());
            d.setCardKey(defIV.getCardKey());

            customObjectManager.getCustomObject("NkDataETLLocalAdapter",NkDataETLAdapter.class)
                .execute(d);
        }

        super.stateChanged(doc, original, data, defIV, d);
    }
}
