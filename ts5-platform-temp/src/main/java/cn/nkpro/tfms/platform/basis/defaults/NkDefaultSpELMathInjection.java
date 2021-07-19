package cn.nkpro.tfms.platform.basis.defaults;

import cn.nkpro.tfms.platform.basis.TfmsSpELInjection;
import cn.nkpro.ts5.utils.CurrencyUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;

@Component
public class NkDefaultSpELMathInjection implements TfmsSpELInjection {

    @Override
    public String getSpELName() {
        return "math";
    }

    public Number sum(Collection<Number> list){
        return list.stream().mapToDouble(Number::doubleValue).sum();
    }

    public Number avg(Collection<Number> list){
        return list.stream().mapToDouble(Number::doubleValue).average().orElse(0D);
    }

    public Number max(Collection<Number> list){
        return list.stream().max(Comparator.comparing(Number::doubleValue)).orElse(0D);
    }

    public Number min(Collection<Number> list){
        return list.stream().min(Comparator.comparing(Number::doubleValue)).orElse(0D);
    }

    public Number count(Collection<Number> list){
        return list.size();
    }

    public Number pmt(double ir, int np, double pv, double fv, int type ){
        return CurrencyUtils.pmt(ir,np,pv,fv,type);
    }
}
