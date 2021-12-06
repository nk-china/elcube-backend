/*
 * This file is part of ELCard.
 *
 * ELCard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcard.docengine.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public interface CurrencyUtils {

    static double add(Double target, Double... number){

        BigDecimal decimal = BigDecimal.valueOf(target);

        for(Double n : number){
            decimal = decimal.add(BigDecimal.valueOf(n));
        }

        return halfUp(decimal);
    }
    /*
     * ir - interest rate per month            月利率
     * np - number of periods (months)         还款次数
     * pv - present value                      现值
     * fv - future value (residual value)      终值
     * type - 0 or 1 need to implement that    期末
     */
    static double pmt(double ir, int np, double pv, double fv, int type) {
        if(np==0||pv<0.0000001)return 0d;
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
