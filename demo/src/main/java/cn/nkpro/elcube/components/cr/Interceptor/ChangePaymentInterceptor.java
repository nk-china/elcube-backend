package cn.nkpro.elcube.components.cr.Interceptor;

import cn.nkpro.elcube.co.easy.EasyCollection;
import cn.nkpro.elcube.co.easy.EasySingle;
import cn.nkpro.elcube.docengine.NkDocCycle;
import cn.nkpro.elcube.docengine.NkDocEngine;
import cn.nkpro.elcube.docengine.gen.DocHMapper;
import cn.nkpro.elcube.docengine.interceptor.abstracts.NkAbstractDocCycleInterceptor;
import cn.nkpro.elcube.docengine.model.DocHV;
import cn.nkpro.elcube.docengine.model.event.AbstractDocCycleEvent;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @Author: 纽扣互联-刘赢泽
 * @Date: 2022/1/17 10:34
 */
@Component("ChangePaymentInterceptor")
public class ChangePaymentInterceptor extends NkAbstractDocCycleInterceptor {

    @SuppressWarnings("all")
    @Autowired
    private NkDocEngine docEngine;

    @SuppressWarnings("all")
    @Autowired
    private DocHMapper docHMapper;

    public Lock lock = new ReentrantLock();
    private String s;
    @Override
    public void apply(DocHV doc, AbstractDocCycleEvent event) {

       if(NkDocCycle.afterUpdated.equals(event.getCycle())) {
           EasyCollection paymentList=null;
           EasySingle repaymentMap=null;
            paymentList = doc.fetchList("payment");
            repaymentMap = doc.fetch("repayment");
            Integer payDate = repaymentMap.get("payDate");
            Integer paidPeriod = repaymentMap.get("paidPeriod");
            List<EasySingle> paymentDate = paymentList.stream().filter(l -> (Integer)l.get("expireDate") < payDate&&(Integer)l.get("period") > paidPeriod).collect(Collectors.toList());
           BigDecimal firstPay = paymentDate.stream().filter(l -> (Integer)l.get("period") > paidPeriod).map(l -> {return new BigDecimal(l.get("pay").toString());}).reduce(BigDecimal.ZERO, BigDecimal::add);
           BigDecimal firstPrincipal = paymentDate.stream().filter(l -> (Integer)l.get("period") > paidPeriod).map(l -> {return new BigDecimal(l.get("principal").toString());}).reduce(BigDecimal.ZERO, BigDecimal::add);
           BigDecimal firstInterest = paymentDate.stream().filter(l -> (Integer)l.get("period") > paidPeriod).map(l -> {return new BigDecimal(l.get("interest").toString());}).reduce(BigDecimal.ZERO, BigDecimal::add);
           AtomicReference<Double> lastResidual = new AtomicReference<>();
           lastResidual.set(-1.00);
           System.out.println("表哥我又出来了！");
//            BigDecimal sum = paymentDate.stream() .map(EasySingle::new Bigdecimal(get("pay").toString())).reduce(BigDecimal.ZERO,BigDecimal::add);
            paymentDate.stream().forEach(l->{
                if(lastResidual.get()==-1.00||Double.parseDouble(l.get("residual").toString())<=lastResidual.get())
                lastResidual.set(Double.parseDouble(l.get("residual").toString()));
           });
           System.out.println("表哥我又出来了！");

           for(int i=0;i<paymentDate.size();i++) {
               EasySingle paymentDateExample = paymentList.get(Integer.parseInt(paymentDate.get(i).get("period").toString())-1);
               paymentDateExample.set("pay","0.00");
               paymentDateExample.set("principal","0.00");
               paymentDateExample.set("interest","0.00");
               paymentDateExample.set("residual","0.00");
           }
           EasySingle paymentDateExample = paymentList.get(paidPeriod);
           paymentDateExample.set("pay",firstPay);
           paymentDateExample.set("principal",firstPrincipal);
           paymentDateExample.set("interest",firstInterest);
           paymentDateExample.set("residual",lastResidual);

            List<HashMap<String,Object>> paymentResult = new ArrayList<>();
           paymentList.forEach(easySingle -> {
                    HashMap<String,Object> map = new HashMap();
               final val target = (Map<String, Object>) easySingle.target();
               target.forEach((k, v) -> {
                   map.put(k, v);
               });
               paymentResult.add(map);
                   });

               docEngine.doUpdate(doc.getDocId(),"",(ind -> {
                   ind.getData().put("payment", paymentResult);
               }));


           }
           }
       }




