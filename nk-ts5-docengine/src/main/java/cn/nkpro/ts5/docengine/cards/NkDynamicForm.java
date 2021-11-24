package cn.nkpro.ts5.docengine.cards;

import cn.nkpro.ts5.annotation.NkNote;
import cn.nkpro.ts5.docengine.NkField;
import cn.nkpro.ts5.docengine.model.DocDefIV;
import cn.nkpro.ts5.docengine.model.DocHV;
import cn.nkpro.ts5.docengine.model.easy.EasySingle;
import cn.nkpro.ts5.docengine.utils.CopyUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

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

        if(defIV.getCopyFromRef()!=null&&defIV.getCopyFromRef()==1){
            CopyUtils.copy(
                    preDoc.getData().get(defIV.getCardKey()),
                    data,
                    d.getItems().stream().map(NkDynamicFormDefI::getKey).collect(Collectors.toList())
            );
        }

        this.execSpEL(EasySingle.from(data), doc, d.getItems(), defIV.getCardKey(), true, false, false, Collections.emptyMap());
        return super.afterCreate(doc, preDoc, data, defIV, d);
    }

    @Override
    public Map<String,Object> afterGetData(DocHV doc, Map<String,Object> data, DocDefIV defIV, NkDynamicFormDef d) {
        this.execSpEL(EasySingle.from(data), doc, d.getItems(), defIV.getCardKey(), false, false, false, Collections.emptyMap());
        return super.afterGetData(doc, data, defIV, d);
    }

    @Override
    public Map<String,Object> calculate(DocHV doc, Map<String,Object> data, DocDefIV defIV, NkDynamicFormDef d, boolean isTrigger, Object options) {
        this.execSpEL(EasySingle.from(data), doc, d.getItems(), defIV.getCardKey(), false, true, isTrigger, (Map) options);
        return super.calculate(doc, data, defIV, d, isTrigger, options);
    }

    @Override
    public Object callDef(NkDynamicFormDef def, Object options) {
        return customObjectManager.getCustomObjectDescriptionList(NkField.class, false, (entry)-> entry.getValue() instanceof NkDynamicFormField);
    }
}
