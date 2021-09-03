package cn.nkpro.ts5.docengine.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public interface CurrencyUtils {

    static double add(Double target,Double... number){

        BigDecimal decimal = BigDecimal.valueOf(target);

        for(Double n : number){
            decimal = decimal.add(BigDecimal.valueOf(n));
        }

        return halfUp(decimal);
    }
    /*
     * ir - interest rate per month            月利率
     * np - number of periods (months)         还款次数
     * pv - present value                      限值
     * fv - future value (residual value)      终值
     * type - 0 or 1 need to implement that    期末
     */
    static double pmt(double ir, int np, double pv, double fv, int type ) {
        if(np==0||pv==0)return 0d;
        if(ir < 0.0000001)return pv/np;
        return halfUp(( ir * ( pv * Math.pow ( (ir+1), np ) + fv ) ) / ( ( ir * type + 1 ) * ( Math.pow ( (ir+1), np) -1 ) ));

    }

    static double halfUp(Double source){
        return halfUp(BigDecimal.valueOf(source));
    }

    static double halfUp(BigDecimal source){
        return source
                .setScale(2,RoundingMode.HALF_UP)
                .doubleValue();
    }
}
