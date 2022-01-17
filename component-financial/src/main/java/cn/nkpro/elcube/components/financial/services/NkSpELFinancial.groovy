package cn.nkpro.elcube.components.financial.services;

import cn.nkpro.elcube.co.NkAbstractCustomScriptObject;
import cn.nkpro.elcube.co.spel.NkSpELInjection
import cn.nkpro.elcube.utils.xirr.Transaction
import cn.nkpro.elcube.utils.xirr.Xirr;
import org.springframework.stereotype.Component

import java.util.stream.Collector
import java.util.stream.Collectors;

@Component("SpELfinancial")
class NkSpELFinancial extends NkAbstractCustomScriptObject implements NkSpELInjection {

    @SuppressWarnings("unused")
    static Double xirr(Long loadDate, Double loanAmount, List<Map> income){

        if(loadDate && Double && income){

            try{
                def collect = income.stream()
                        .map({ item ->
                            return new Transaction(item.get("amount") as double, item.get("date") as long)
                        })
                        .collect(Collectors.toList())

                collect.add(0, new Transaction(-loanAmount, loadDate))

                Double rate = new Xirr(collect).xirr()

                return rate
            }catch(Exception ignored){

            }
        }

        return null
    }

    @SuppressWarnings("unused")
    static Double irr(Double loan, List<Double> income){
        if(loan && income){
            income.add(0, -loan)
            return _irr(income,0.1d)
        }
        return null
    }

    private static Double _irr(List<Double> income) {
        return _irr(income, 0.1D)
    }

    private static Double _irr(List<Double> values, double guess) {
        int maxIterationCount = 20
        double absoluteAccuracy = 1.0E-007D

        double x0 = guess

        int i = 0
        while (i < maxIterationCount) {
            double fValue = 0.0D
            double fDerivative = 0.0D
            for (int k = 0; k < values.size(); k++) {
                fValue += values[k] / Math.pow(1.0D + x0, k)
                fDerivative += -k * values[k] / Math.pow(1.0D + x0, k + 1)
            }
            double x1 = x0 - fValue / fDerivative
            if (Math.abs(x1 - x0) <= absoluteAccuracy) {
                return x1
            }
            x0 = x1
            i++
        }
        return null
    }
}
