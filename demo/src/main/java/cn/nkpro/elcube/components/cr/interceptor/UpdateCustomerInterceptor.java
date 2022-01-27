package cn.nkpro.elcube.components.cr.interceptor;

import cn.nkpro.elcube.docengine.NkDocCycle;
import cn.nkpro.elcube.docengine.NkDocEngine;
import cn.nkpro.elcube.docengine.interceptor.abstracts.NkAbstractDocCycleInterceptor;
import cn.nkpro.elcube.docengine.model.DocHV;
import cn.nkpro.elcube.docengine.model.event.AbstractDocCycleEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @program: cn.nkpro.elcube.components.cr.interceptor
 * @description: 客户拜访审核通过回更客户信息
 * @author: mab
 * @create: 2022/1/27 9:59
 * @version: 1.0
 **/
@Component("UpdateCustomerInterceptor")
public class UpdateCustomerInterceptor extends NkAbstractDocCycleInterceptor {

    @SuppressWarnings("all")
    @Autowired
    private NkDocEngine docEngine;

    @Override
    public void apply(DocHV doc, AbstractDocCycleEvent event) {
        if (NkDocCycle.afterUpdated.equals(event.getCycle()) && "S003".equals(doc.getDocState()) ) {
            String preDocId = doc.getPreDocId();
            Map<String, Object> thisData = doc.getData();
            Map<String, Object> preData = docEngine.detail(preDocId).getData();
            thisData.remove("NkCardHeaderDefault");
            thisData.remove("cusVisitInfo");
            preData.putAll(thisData);
            docEngine.doUpdate(preDocId,"",l->{
               l.setData(preData);
            });
        }
    }
}
