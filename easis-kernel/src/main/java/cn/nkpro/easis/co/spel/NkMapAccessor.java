/*
 * This file is part of EAsis.
 *
 * EAsis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EAsis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with EAsis.  If not, see <https://www.gnu.org/licenses/>.
 */
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
