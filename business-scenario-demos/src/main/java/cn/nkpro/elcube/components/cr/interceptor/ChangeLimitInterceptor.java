package cn.nkpro.elcube.components.cr.interceptor;

import cn.nkpro.elcube.docengine.NkDocCycle;
import cn.nkpro.elcube.docengine.NkDocEngine;
import cn.nkpro.elcube.docengine.interceptor.abstracts.NkAbstractDocCycleInterceptor;
import cn.nkpro.elcube.docengine.model.DocHV;
import cn.nkpro.elcube.docengine.model.event.AbstractDocCycleEvent;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.print.Doc;
import java.math.BigDecimal;
import java.util.LinkedHashMap;

/**
 * @program: cn.nkpro.elcube.components.CR.interceptor
 * @description:
 * @author: mab
 * @create: 2022/1/24 16:29
 * @version: 1.0
 **/
@Component("ChangeLimitInterceptor")
public class ChangeLimitInterceptor extends NkAbstractDocCycleInterceptor {

    @SuppressWarnings("all")
    @Autowired
    private NkDocEngine docEngine;

    @Override
    public void apply(DocHV doc, AbstractDocCycleEvent event) {
        if (NkDocCycle.beforeUpdate.equals(event.getCycle()) && "S003".equals(doc.getDocState())){
            Object o = doc.fetch("customer").get("customer");
            Object docId = null;
            if (o instanceof LinkedHashMap){
                docId = ((LinkedHashMap<String, Object>) o).get("docId");
            }else if (o instanceof JSONObject){
                docId = ((JSONObject) o).get("docId");
            }
            Object availableCredit = docEngine.detail((String) docId).fetch("cusUsableQuota").get("availableCredit");
            Object loanAmount = doc.fetch("financialPlan").get("loanAmount");

            if(new BigDecimal( loanAmount.toString()).compareTo(new BigDecimal( availableCredit.toString())) > 0){
                throw new RuntimeException("申请金额:"+new BigDecimal( loanAmount.toString())+"元,大于可用额度"+new BigDecimal( availableCredit.toString())+"元");
            }else {
                docEngine.doUpdate((String) docId,"",l->{
                    l.fetch("cusUsableQuota").set("availableCredit",new BigDecimal( availableCredit.toString()).subtract(new BigDecimal(loanAmount.toString())).setScale(2));
                });
            }

//            throw new RuntimeException("123");
        }
    }
}
