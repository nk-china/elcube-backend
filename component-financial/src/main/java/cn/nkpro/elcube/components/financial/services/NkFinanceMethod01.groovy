/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.components.financial.services

import cn.nkpro.elcube.co.NkAbstractApplyCSO
import cn.nkpro.elcube.co.spel.NkSpELManager
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.math.RoundingMode

@Component("NkFinanceMethod01")
class NkFinanceMethod01 extends NkAbstractApplyCSO{

    @Autowired
    NkSpELManager spELManager

    @Override
    Object apply(Object options) throws Exception {

        def params  = options as Map

        Double  ir = params.get("ir") as Double
        Integer np = params.get("np") as Integer
        Integer pf = params.get("pf") as Integer
        Double  pv = params.get("pv") as Double
        Double  fv = params.get("fv") as Double
        Long    dt = params.get("dt") as Long
        Integer tp = params.getOrDefault("tp", 0) as Integer

        // 不等额规则
        List customPayAmountRule = params.get("customPayAmountRule") as List


        // 还款金额精度，默认保留2位小数，四舍五入
        Integer payPrecision    = params.getOrDefault("payPrecision",2) as Integer
        Integer payRoundingMode = params.getOrDefault("payRoundingMode",BigDecimal.ROUND_HALF_UP) as Integer


        if (ir != null && np != null && pf != null && pv != null && fv != null && dt != null){

            Calendar calendar = Calendar.getInstance()

            // 期间利率 = 年利率 / 付款频率
            Double ip = ir / pf

            // 付款间隔 单位月
            int step = (int) (12 / pf)



            // 期间付款金额
            double pay = pmt(ip, np, pv, fv, tp)

            List<PaymentI> list = new ArrayList<>()
            PaymentI item
            Double prevResidual = pv
            for(int i=0;i<np;i++){

                calendar.setTimeInMillis(dt*1000)
                calendar.add(Calendar.MONTH, step*(i+1-tp))
                item = new PaymentI()
                item.setPeriod(i+1)
                item.setExpireDate(calendar.getTimeInMillis()/1000 as Long)
                item.setPay(pay)
                if(tp == 1){
                    item.setInterest(i==0?0:doubleValue(prevResidual*ip))
                }else{
                    item.setInterest(doubleValue(prevResidual*ip))
                }
                item.setPrincipal(doubleValue(pay-item.getInterest()))
                item.setResidual(doubleValue(prevResidual-item.getPrincipal()))
                item.setFee(0d)
                item.setRemark(null)

                list.add(item)

                prevResidual = item.getResidual()
            }

            CustomPayAmountRule r = new CustomPayAmountRule()
            r.fromDate = 1647100800
            r.endDate  = 1649779200
            r.pay      = 10000d

            //customPayAmountRule = new ArrayList()
            //customPayAmountRule.add(r)

            if(customPayAmountRule!=null){

                JSONArray jsonArray = JSONArray.toJSON(customPayAmountRule) as JSONArray
                List<CustomPayAmountRule> rules = jsonArray.toJavaList(CustomPayAmountRule.class)

                Double X = 0
                double minX = Double.MAX_VALUE
                int retry = 0
                while(true){
                    retry ++


                    prevResidual = pv
                    int count = 0

                    list.forEach({i->

                        CustomPayAmountRule rule = rules.stream()
                                .filter({rule->i.expireDate>=rule.fromDate && i.expireDate<=rule.endDate})
                                .findFirst()
                                .orElse(null)

                        i.interest = tp == 1 && i.period == 1 ? 0 : doubleValue(prevResidual*ip)
                        if(rule){
                            i.pay = Math.max(rule.pay, i.interest)
                        }else{
                            i.pay = doubleValue(i.pay + X)
                            count ++
                        }
                        i.principal = doubleValue(i.pay-i.interest)
                        i.residual  = doubleValue(prevResidual-i.principal)

                        prevResidual = i.residual
                    })

                    println "计算" + retry
                    println "X=" + X
                    println "prevResidual=" + prevResidual

                    if(minX == Math.abs(prevResidual))
                        break

                    X = prevResidual/count
                    minX = Math.min(minX,Math.abs(prevResidual))

                    // 最多重试50次
                    // 如果最后一期的剩余本金（单位分）【小于等于】等额期次数量 的一半 ！则无法继续缩小误差，退出计算
                    // count/2 等额期次数量 的一半
                    // * 100   将单位元转化为单位分
                    if(retry >= 50 || Math.abs(prevResidual * 100) <= count / 2)
                        break
                }

            }

            if(item!=null){
                item.setPrincipal(doubleValue(item.getPrincipal() + item.getResidual()))
                item.setPay(doubleValue(item.getPrincipal() + item.getInterest()))
                item.setResidual(0d)
            }

            if(payPrecision !=2){

                list.forEach({ i ->
                    double newPayValue = new BigDecimal(i.pay).setScale(payPrecision, RoundingMode.valueOf(payRoundingMode)).doubleValue()
                    double newIntValue = doubleValue(i.interest - i.pay + newPayValue)
                    i.pay       = newPayValue
                    i.interest  = newIntValue
                })
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
        return doubleValue(( ir * ( pv * Math.pow ( (ir+1), np ) + fv ) ) / ( ( ir * type + 1 ) * ( Math.pow ( (ir+1), np) -1 ) ))
    }
    protected static Double doubleValue(Double value){
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue()
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
    static class CustomPayAmountRule{
        Long fromDate
        Long endDate
        Double pay
    }

}
