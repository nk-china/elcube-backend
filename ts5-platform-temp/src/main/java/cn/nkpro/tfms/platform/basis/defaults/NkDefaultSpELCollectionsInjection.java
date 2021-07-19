package cn.nkpro.tfms.platform.basis.defaults;

import cn.nkpro.tfms.platform.basis.TfmsSpELInjection;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;

@Deprecated
@Component
public class NkDefaultSpELCollectionsInjection implements TfmsSpELInjection {

    @Override
    public String getSpELName() {
        return "collections";
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
}
