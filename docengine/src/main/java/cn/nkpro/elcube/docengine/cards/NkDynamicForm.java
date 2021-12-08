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
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

@Order(1001)
@NkNote("动态表单")
@Component("NkDynamicForm")
public class NkDynamicForm extends NkDynamicBase<Map<String,Object>, NkDynamicFormDef> {

    @Override
    public String getDataComponentName() {
        return "NkDynamicForm";
    }

    @Override
    protected String[] getDefComponentNames() {
        return new String[]{"NkDynamicFormDef"};
    }

    @Override
    public Map<String,Object> afterCreate(DocHV doc, DocHV preDoc, Map<String,Object> data, DocDefIV defIV, NkDynamicFormDef d) {
        this.copyFromPre(preDoc, data, defIV, d.getItems());

        EasySingle single = EasySingle.from(data);

        this.processOptions(single, doc, d.getItems());
        this.execSpEL(single, doc, d.getItems(), defIV.getCardKey(), false, null, preDoc, true);
        this.processControl(single,doc,d.getItems(),defIV.getCardKey());
        return super.afterCreate(doc, preDoc, data, defIV, d);
    }

    @Override
    public Map<String,Object> afterGetData(DocHV doc, Map<String,Object> data, DocDefIV defIV, NkDynamicFormDef d) {

        EasySingle single = EasySingle.from(data);
        this.processOptions(single, doc, d.getItems());
        this.processControl(single,doc,d.getItems(),defIV.getCardKey());
        return super.afterGetData(doc, data, defIV, d);
    }

    @Override
    public Map<String, Object> beforeUpdate(DocHV doc, Map<String, Object> data, Map<String, Object> original, DocDefIV defIV, NkDynamicFormDef d) {

        d.getItems().forEach(item->{
            if(StringUtils.isNotBlank(item.getIndexName())){
                doc.getDynamics().put(item.getIndexName(), data.get(item.getKey()));
            }
        });

        return super.beforeUpdate(doc, data, original, defIV, d);
    }

    @Override
    public Map<String, Object> afterUpdated(DocHV doc, Map<String, Object> data, Map<String, Object> original, DocDefIV defIV, NkDynamicFormDef d) {
        return super.afterUpdated(doc, data, original, defIV, d);
    }

    @Override
    public Map<String,Object> calculate(DocHV doc, Map<String,Object> data, DocDefIV defIV, NkDynamicFormDef d, boolean isTrigger, Object options) {
        EasySingle single = EasySingle.from(data);
        this.execSpEL(single, doc, d.getItems(), defIV.getCardKey(), isTrigger, (Map) options);
        this.processControl(single,doc,d.getItems(),defIV.getCardKey());
        return super.calculate(doc, data, defIV, d, isTrigger, options);
    }

    @Override
    public Object callDef(NkDynamicFormDef def, Object options) {
        return customObjectManager.getCustomObjectDescriptionList(NkField.class, false, (entry)-> entry.getValue() instanceof NkDynamicFormField);
    }
}
