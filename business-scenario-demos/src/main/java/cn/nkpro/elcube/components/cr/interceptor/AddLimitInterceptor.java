package cn.nkpro.elcube.components.cr.interceptor;

import cn.nkpro.elcube.co.easy.EasyCollection;
import cn.nkpro.elcube.docengine.NkDocCycle;
import cn.nkpro.elcube.docengine.NkDocEngine;
import cn.nkpro.elcube.docengine.gen.DocIReceived;
import cn.nkpro.elcube.docengine.interceptor.abstracts.NkAbstractDocCycleInterceptor;
import cn.nkpro.elcube.docengine.model.DocHV;
import cn.nkpro.elcube.docengine.model.event.AbstractDocCycleEvent;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

/**
* @Description: 还款完成增加客户可用额度
* @Param: 
* @return: 
* @Author: mab
* @Date: 2022/1/26
**/
@Component("AddLimitInterceptor")
public class AddLimitInterceptor extends NkAbstractDocCycleInterceptor {

    @SuppressWarnings("all")
    @Autowired
    private NkDocEngine docEngine;

    @Override
    public void apply(DocHV doc, AbstractDocCycleEvent event) {
        if (NkDocCycle.afterUpdated.equals(event.getCycle()) && "S003".equals(doc.getDocState()) ){
            EasyCollection nkCardRepayment = doc.fetchList("NkCardRepayment");
            BigDecimal currReceived = nkCardRepayment.stream().filter(l -> {
                boolean flag = false;
                if (((DocIReceived) l.target()).getBillType().equals("本金"))
                    flag = true;
                return flag;
            }).map(l -> {
                return new BigDecimal(((DocIReceived) l.target()).getCurrReceived());
            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2,BigDecimal.ROUND_HALF_UP);

            Object o = doc.fetch("customer").get("customer");
            Object docId = null;
            if (o instanceof LinkedHashMap){
                docId = ((LinkedHashMap<String, Object>) o).get("docId");
            }else if (o instanceof JSONObject){
                docId = ((JSONObject) o).get("docId");
            }

            BigDecimal zero = BigDecimal.ZERO;
            Object availableCredit = docEngine.detail((String) docId).fetch("cusUsableQuota").get("availableCredit");
            if (availableCredit != null)
                zero = new BigDecimal(availableCredit.toString());
            BigDecimal bigDecimal = zero.add(currReceived).setScale(2, BigDecimal.ROUND_HALF_UP);
            docEngine.doUpdate((String) docId,"",l->{
                l.fetch("cusUsableQuota").set("availableCredit",bigDecimal);
            });
        }
    }
}
