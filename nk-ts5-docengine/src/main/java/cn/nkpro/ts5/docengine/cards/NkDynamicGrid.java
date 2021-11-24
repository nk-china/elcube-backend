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

        if(defIV.getCopyFromRef()!=null&&defIV.getCopyFromRef()==1){
            CopyUtils.copy(
                    preDoc.getData().get(defIV.getCardKey()),
                    data,
                    HashMap.class,
                    d.getItems().stream().map(NkDynamicFormDefI::getKey).collect(Collectors.toList())
            );
        }

        this.execSpEL(data, doc, d.getItems(), defIV.getCardKey(), true, false, false, Collections.emptyMap());
        return super.afterCreate(doc, preDoc, data, defIV, d);
    }

    @Override
    public List<Map> afterGetData(DocHV doc, List<Map> data, DocDefIV defIV, NkDynamicGridDef d) {
        this.execSpEL(data, doc, d.getItems(), defIV.getCardKey(), false, false, false, Collections.emptyMap());
        return super.afterGetData(doc, data, defIV, d);
    }

    @Override
    public List<Map> calculate(DocHV doc, List<Map> data, DocDefIV defIV, NkDynamicGridDef d, boolean isTrigger, Object options) {
        this.execSpEL(data, doc, d.getItems(), defIV.getCardKey(), false, true, true, (Map) options);
        return super.calculate(doc, data, defIV, d, isTrigger, options);
    }

    private void execSpEL(List<Map> data, DocHV doc, List<NkDynamicGridDefI> fields, String cardKey, boolean isNewCreate, boolean calculate, boolean isTrigger, Map options){
        if(data.isEmpty())
            execSpEL(EasySingle.from(new HashMap()), doc, fields,cardKey, isNewCreate, calculate, isTrigger, options);
        else
            data.forEach(item-> execSpEL(EasySingle.from(item), doc, fields, cardKey, isNewCreate, calculate, isTrigger, options));
    }

    @Override
    public Object callDef(NkDynamicGridDef def, Object options) {
        return customObjectManager.getCustomObjectDescriptionList(NkField.class, false, (entry)-> entry.getValue() instanceof NkDynamicGridField);
    }
}
