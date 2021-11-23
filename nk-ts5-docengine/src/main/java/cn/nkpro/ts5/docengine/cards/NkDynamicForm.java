package cn.nkpro.ts5.docengine.cards;

import cn.nkpro.ts5.annotation.NkNote;
import cn.nkpro.ts5.docengine.NkField;
import cn.nkpro.ts5.docengine.model.DocDefIV;
import cn.nkpro.ts5.docengine.model.DocHV;
import cn.nkpro.ts5.docengine.model.easy.EasySingle;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@NkNote("动态表单")
@Component("NkDynamicForm")
public class NkDynamicForm extends NkDynamicBase<Map, NkDynamicFormDef> {

    @Override
    public String getDataComponentName() {
        return "NkDynamicForm";
    }

    @Override
    protected String[] getDefComponentNames() {
        return new String[]{"NkDynamicFormDef"};
    }

    @Override
    public Map afterCreate(DocHV doc, DocHV preDoc, Map data, DocDefIV defIV, NkDynamicFormDef d) {
        this.execSpEL(EasySingle.from(data), doc, d.getItems(), defIV.getCardKey(), true, false, false, Collections.emptyMap());
        return super.afterCreate(doc, preDoc, data, defIV, d);
    }

    @Override
    public Map afterGetData(DocHV doc, Map data, DocDefIV defIV, NkDynamicFormDef d) {
        this.execSpEL(EasySingle.from(data), doc, d.getItems(), defIV.getCardKey(), false, false, false, Collections.emptyMap());
        return super.afterGetData(doc, data, defIV, d);
    }

    @Override
    public Map calculate(DocHV doc, Map data, DocDefIV defIV, NkDynamicFormDef d, boolean isTrigger, Object options) {
        this.execSpEL(EasySingle.from(data), doc, d.getItems(), defIV.getCardKey(), false, true, isTrigger, (Map) options);
        return super.calculate(doc, data, defIV, d, isTrigger, options);
    }

    @Override
    public Object callDef(NkDynamicFormDef def, Object options) {
        return customObjectManager.getCustomObjectDescriptionList(NkField.class, false, null);
    }
}
