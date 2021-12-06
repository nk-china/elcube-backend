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
package cn.nkpro.elcube.docengine.cards;

import cn.nkpro.elcube.annotation.NkNote;
import cn.nkpro.elcube.docengine.NkField;
import cn.nkpro.elcube.docengine.model.DocDefIV;
import cn.nkpro.elcube.docengine.model.DocHV;
import cn.nkpro.elcube.co.easy.EasySingle;
import cn.nkpro.elcube.docengine.utils.CopyUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Order(1002)
@NkNote("动态表格")
@Component("NkDynamicGrid")
public class NkDynamicGrid extends NkDynamicBase<List<Map>, NkDynamicGridDef> {

    @Override
    public String getDataComponentName() {
        return "NkDynamicGrid";
    }

    @Override
    protected String[] getDefComponentNames() {
        return new String[]{"NkDynamicGridDef"};
    }

    @Override
    public List<Map> afterCreate(DocHV doc, DocHV preDoc, List<Map> data, DocDefIV defIV, NkDynamicGridDef d) {

        if(preDoc != null && defIV.getCopyFromRef()!=null&&defIV.getCopyFromRef()==1){
            CopyUtils.copy(
                    preDoc.getData().get(defIV.getCardKey()),
                    data,
                    HashMap.class,
                    d.getItems().stream().map(NkDynamicFormDefI::getKey).collect(Collectors.toList())
            );
        }

        this.processOptions(EasySingle.from(Collections.emptyMap()), doc, d.getItems());
        return super.afterCreate(doc, preDoc, data, defIV, d);
    }

    @Override
    public List<Map> afterGetData(DocHV doc, List<Map> data, DocDefIV defIV, NkDynamicGridDef d) {
        this.processOptions(EasySingle.from(Collections.emptyMap()), doc, d.getItems());
        return super.afterGetData(doc, data, defIV, d);
    }

    @Override
    public List<Map> calculate(DocHV doc, List<Map> data, DocDefIV defIV, NkDynamicGridDef d, boolean isTrigger, Object options) {
        this.execSpEL(data, doc, d.getItems(), defIV.getCardKey(), isTrigger, (Map) options);
        return super.calculate(doc, data, defIV, d, isTrigger, options);
    }

    private void execSpEL(List<Map> data, DocHV doc, List<NkDynamicGridDefI> fields, String cardKey, boolean isTrigger, Map options){
        if(data.isEmpty())
            execSpEL(EasySingle.from(new HashMap()), doc, fields,cardKey, isTrigger, options);
        else
            data.forEach(item-> execSpEL(EasySingle.from(item), doc, fields, cardKey, isTrigger, options));
    }

    @Override
    public Object callDef(NkDynamicGridDef def, Object options) {
        return customObjectManager.getCustomObjectDescriptionList(NkField.class, false, (entry)-> entry.getValue() instanceof NkDynamicGridField);
    }
}
