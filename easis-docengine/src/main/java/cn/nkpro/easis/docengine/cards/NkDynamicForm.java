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
package cn.nkpro.easis.docengine.cards;

import cn.nkpro.easis.annotation.NkNote;
import cn.nkpro.easis.docengine.NkField;
import cn.nkpro.easis.docengine.model.DocDefIV;
import cn.nkpro.easis.docengine.model.DocHV;
import cn.nkpro.easis.co.easy.EasySingle;
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
        this.processOptions(EasySingle.from(data), doc, d.getItems());
        this.execSpEL(EasySingle.from(data), doc, d.getItems(), defIV.getCardKey(), false, null, preDoc, true);
        return super.afterCreate(doc, preDoc, data, defIV, d);
    }

    @Override
    public Map<String,Object> afterGetData(DocHV doc, Map<String,Object> data, DocDefIV defIV, NkDynamicFormDef d) {
        this.processOptions(EasySingle.from(data), doc, d.getItems());
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
        this.execSpEL(EasySingle.from(data), doc, d.getItems(), defIV.getCardKey(), isTrigger, (Map) options);
        return super.calculate(doc, data, defIV, d, isTrigger, options);
    }

    @Override
    public Object callDef(NkDynamicFormDef def, Object options) {
        return customObjectManager.getCustomObjectDescriptionList(NkField.class, false, (entry)-> entry.getValue() instanceof NkDynamicFormField);
    }
}
