package cn.nkpro.ts5.docengine.cards;

import cn.nkpro.ts5.annotation.NkNote;
import cn.nkpro.ts5.co.NkCustomObjectManager;
import cn.nkpro.ts5.docengine.NkAbstractCard;
import cn.nkpro.ts5.docengine.NkField;
import cn.nkpro.ts5.docengine.model.DocDefHV;
import cn.nkpro.ts5.docengine.model.DocDefIV;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@NkNote("动态表单")
@Component("NkDynamicForm")
public class NkDynamicForm extends NkAbstractCard<Map, NkDynamicFormDef> {

    @Autowired
    private NkCustomObjectManager customObjectManager;

    @Override
    public String getDataComponentName() {
        return "NkDynamicForm";
    }

    @Override
    protected String[] getDefComponentNames() {
        return new String[]{"NkDynamicFormDef"};
    }

    @Override
    public NkDynamicFormDef afterGetDef(DocDefHV defHV, DocDefIV defIV, NkDynamicFormDef def) {

        def.getItems()
            .stream()
            .filter(field-> !StringUtils.equals(field.getInputType(),"divider"))
            .forEach(field->{
                NkField nkField = customObjectManager.getCustomObject(field.getInputType(), NkField.class);
                nkField.afterGetDef(field.getInputOptions());
            });

        return super.afterGetDef(defHV, defIV, def);
    }

    @Override
    public Object callDef(NkDynamicFormDef def, Object options) {
        return customObjectManager.getCustomObjectDescriptionList(NkField.class, false, null);
    }
}
