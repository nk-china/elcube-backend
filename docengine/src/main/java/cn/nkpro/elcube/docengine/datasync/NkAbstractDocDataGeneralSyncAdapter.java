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
package cn.nkpro.elcube.docengine.datasync;

import cn.nkpro.elcube.co.spel.NkSpELManager;
import cn.nkpro.elcube.docengine.gen.DocDefDataSync;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"all"})
@Slf4j
public abstract class NkAbstractDocDataGeneralSyncAdapter<K> extends NkAbstractDocDataSupport implements NkDocDataSyncAdapter<K> {

    @Autowired
    protected NkSpELManager spELManager;

    protected void doSync(Object dataUnmapping, Object dataOriginalUnmapping, EvaluationContext context1, EvaluationContext context2, DocDefDataSync def){
        if(dataUnmapping instanceof List){
            doSyncMultiple((List<Map<String, Object>>) ((List) dataUnmapping).stream()
                    .map(value -> {
                        context1.setVariable("row", value);
                        return (Map<String, Object>) (spELManager.invoke(def.getMappingSpEL(), context1));
                    }).collect(Collectors.toList()), def);
        }else{
            context1.setVariable("row", dataUnmapping);
            doSyncSingle((Map)(spELManager.invoke(def.getMappingSpEL(), context1)), def);
        }
    }

    protected abstract void doSyncSingle(Map<String,Object> singleData, DocDefDataSync def);

    protected abstract void doSyncMultiple(List<Map<String,Object>> multipleData, DocDefDataSync def);
}