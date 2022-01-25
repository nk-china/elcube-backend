package cn.nkpro.elcube.components.cr.Interceptor;

import cn.nkpro.elcube.docengine.NkDocCycle;
import cn.nkpro.elcube.docengine.NkDocEngine;
import cn.nkpro.elcube.docengine.interceptor.abstracts.NkAbstractDocCycleInterceptor;
import cn.nkpro.elcube.docengine.model.DocHV;
import cn.nkpro.elcube.docengine.model.event.AbstractDocCycleEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @program: cn.nkpro.elcube.components.CR.interceptor
 * @description:
 * @author: mab
 * @create: 2022/1/24 16:29
 * @version: 1.0
 **/
@Component("SaveCustomerInterceptor")
public class SaveCustomerInterceptor extends NkAbstractDocCycleInterceptor {

    @SuppressWarnings("all")
    @Autowired
    private NkDocEngine docEngine;

    @Override
    public void apply(DocHV doc, AbstractDocCycleEvent event) {
        if (NkDocCycle.beforeUpdate.equals(event.getCycle())){
            WeightCategory weightCategory1 = new WeightCategory("校验成功",7);
            WeightCategory weightCategory2 = new WeightCategory("校验失败",3);
            List<WeightCategory> categoryList=new ArrayList<>();
            categoryList.add(weightCategory1);
            categoryList.add(weightCategory2);
            if (doc.fetch("cusOtherBigdata").get("phoneCheck") == null)
                doc.fetch("cusOtherBigdata").set("phoneCheck",getWeight(categoryList));
            if (doc.fetch("cusOtherBigdata").get("bankAccountCheck") == null)
                doc.fetch("cusOtherBigdata").set("bankAccountCheck",getWeight(categoryList));
            if (doc.fetch("cusOtherBigdata").get("idCardCheck") == null)
                doc.fetch("cusOtherBigdata").set("idCardCheck",getWeight(categoryList));

            WeightCategory weightCategory3 = new WeightCategory("极高",1);
            WeightCategory weightCategory4 = new WeightCategory("高",2);
            WeightCategory weightCategory5 = new WeightCategory("一般",5);
            WeightCategory weightCategory6 = new WeightCategory("低",2);
            List<WeightCategory> categoryList1=new ArrayList<>();
            categoryList1.add(weightCategory3);
            categoryList1.add(weightCategory4);
            categoryList1.add(weightCategory5);
            categoryList1.add(weightCategory6);
            String weight = getWeight(categoryList1);
            if (doc.fetch("cusCreditRating").get("creditRating") == null)
                doc.fetch("cusCreditRating").set("creditRating",weight);

            Object o = doc.fetch("cusUsableQuota").get("availableCredit");
            if (o == null){
                if (doc.fetch("cusCreditRating").get("creditRating") != null)
                    weight = doc.fetch("cusCreditRating").get("creditRating");
                int max = 0;
                int min = 0;
                switch (weight){
                    case "极高":
                        max = 10000;
                        min = 8000;
                        break;
                    case "高":
                        max = 8000;
                        min = 5000;
                        break;
                    case "一般":
                        max = 5000;
                        min = 3000;
                        break;
                    case "低":
                        max = 3000;
                        min = 1000;
                        break;
                }
                doc.fetch("cusUsableQuota").set("availableCredit",new Random().nextInt(max - min) + min + 1);
            }
//            throw new RuntimeException("滴滴滴滴");
        }
    }

    /**
     * @Description: 获取权重
     * @Param: [categorys]
     * @return: java.lang.String
     * @Author: mab
     * @Date: 2022/1/24
     **/
    public static String getWeight(List<WeightCategory> categorys) {
        Integer weightSum = 0;
        String result=null;
        for (WeightCategory wc : categorys) {
            weightSum += wc.getWeight();
        }

        if (weightSum <= 0) {
            System.err.println("Error: weightSum=" + weightSum.toString());
            return result;
        }
        Random random = new Random();
        Integer n = random.nextInt(weightSum); // n in [0, weightSum)
        Integer m = 0;
        for (WeightCategory wc : categorys) {
            if (m <= n && n < m + wc.getWeight()) {
                result=wc.getCategory();
                break;
            }
            m += wc.getWeight();
        }
        return result;
    }

}
