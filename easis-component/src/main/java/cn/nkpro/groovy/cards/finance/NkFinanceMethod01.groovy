package cn.nkpro.groovy.cards.finance

import cn.nkpro.easis.co.NkAbstractApplyCSO
import cn.nkpro.easis.co.easy.EasySingle
import cn.nkpro.easis.co.spel.NkSpELManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.math.RoundingMode

@Component("NkFinanceMethod01")
class NkFinanceMethod01 extends NkAbstractApplyCSO {

    @Autowired
    NkSpELManager spELManager

    @Override
    Object apply(Object options) throws Exception {

        def single = EasySingle.from(options)
        def mfOptions = single.get("options")

        def context = spELManager.createContext(single.get("doc"))
        def params  = spELManager.invoke(mfOptions as String, context) as Map

        Double  ir = params.get("irSpEL") as Double
        Integer np = params.get("npSpEL") as Integer
        Integer pf = params.get("pfSpEL") as Integer
        Double  pv = params.get("pvSpEL") as Double
        Double  fv = params.get("fvSpEL") as Double
        Long    dt = params.get("dtSpEL") as Long


        if (ir != null && np != null && pf != null && pv != null && fv != null && dt != null){

            Calendar calendar = Calendar.getInstance()
            calendar.setTimeInMillis(dt*1000)

            // 期间利率 = 年利率 / 付款频率
            Double ip = ir / pf

            // 付款间隔 单位月
            int step = (int) (12 / pf)



            // 期间付款金额
            double pay = pmt(ip, np, pv, fv, 0)

            List<PaymentI> list = new ArrayList<>()
            PaymentI item
            for(int i=0;i<np;i++){


                calendar.add(Calendar.MONTH, step)
                item = new PaymentI()
                item.setPeriod(i+1)
                item.setExpireDate(calendar.getTimeInMillis()/1000 as Long)
                item.setPay(pay)
                item.setInterest(doubleValue(pv*ip))
                item.setPrincipal(doubleValue(pay-item.getInterest()))
                item.setResidual(doubleValue(pv-item.getPrincipal()))
                item.setFee(0d)
                item.setRemark(null)

                list.add(item)

                pv = item.getResidual()
            }

            if(item!=null){
                item.setPrincipal(doubleValue(item.getPrincipal() + item.getResidual()))
                item.setPay(doubleValue(item.getPrincipal() + item.getInterest()))
                item.setResidual(0d)
            }

            return list
        }



        return null
    }

    /*
    ir - interest rate per month
    np - number of periods (months)
    pv - present value
    fv - future value (residual value)
    type - 0 or 1 need to implement that
    */
    private static Double pmt(double ir, int np, double pv, double fv, int type ) {
        if(pv==0 || np==0)
            return 0
        if(ir<=0)
            return doubleValue(pv/np)
        return doubleValue(( ir * ( pv * Math.pow ( (ir+1), np ) + fv ) ) / ( ( ir * type + 1 ) * ( Math.pow ( (ir+1), np) -1 ) ));
    }
    protected static Double doubleValue(Double value){
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
    static class PaymentI{
        Integer period
        Long expireDate
        Double pay
        Double principal
        Double interest
        Double residual
        Double fee
        String remark
    }
}
