package cn.nkpro.easis.co.spel;

import org.jetbrains.annotations.NotNull;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.Map;

public class NkMapAccessor implements PropertyAccessor {

    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return new Class[]{Map.class};
    }

    @Override
    public boolean canRead(@NotNull EvaluationContext evaluationContext, @Nullable Object target, @NotNull String name) {
        return target instanceof Map;
    }

    @NotNull
    @Override
    public TypedValue read(@NotNull EvaluationContext evaluationContext, Object target, @NotNull String name) {
        Assert.state(target instanceof Map, "Target must be of type Map");
        Map<?, ?> map = (Map)target;
        Object value = map.get(name);
        if (value == null && !map.containsKey(name)) {
            return new TypedValue(null);
        } else {
            return new TypedValue(value);
        }
    }

    @Override
    public boolean canWrite(@NotNull EvaluationContext evaluationContext, Object target, @NotNull String name) {
        return target instanceof Map;
    }

    @Override
    public void write(@NotNull EvaluationContext evaluationContext, Object target, @NotNull String name, Object newValue) {
        Assert.state(target instanceof Map, "Target must be of type Map");
        @SuppressWarnings("unchecked")
        Map<Object, Object> map = (Map<Object, Object>)target;
        map.put(name, newValue);
    }
}
