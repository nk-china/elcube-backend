package cn.nkpro.ts5.docengine.cards;

import cn.nkpro.ts5.annotation.NkNote;
import cn.nkpro.ts5.co.NkCustomObjectManager;
import cn.nkpro.ts5.docengine.NkAbstractCard;
import cn.nkpro.ts5.docengine.NkField;
import cn.nkpro.ts5.docengine.model.DocDefHV;
import cn.nkpro.ts5.docengine.model.DocDefIV;
import cn.nkpro.ts5.docengine.model.DocHV;
import cn.nkpro.ts5.docengine.model.easy.EasySingle;
import cn.nkpro.ts5.docengine.service.NkDocEngineContext;
import cn.nkpro.ts5.exception.NkDefineException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
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
        return super.afterGetDef(defHV, defIV, def);
    }

    @Override
    public Map afterCreate(DocHV doc, DocHV preDoc, Map data, DocDefIV defIV, NkDynamicFormDef d) {
        this.execSpEL(EasySingle.from(data), doc, d.getItems(), defIV.getCardKey(), true, false);
        return super.afterCreate(doc, preDoc, data, defIV, d);
    }

    @Override
    public Map afterGetData(DocHV doc, Map data, DocDefIV defIV, NkDynamicFormDef d) {
        this.execSpEL(EasySingle.from(data), doc, d.getItems(), defIV.getCardKey(), false, false);
        return super.afterGetData(doc, data, defIV, d);
    }

    @Override
    public Map calculate(DocHV doc, Map data, DocDefIV defIV, NkDynamicFormDef d, boolean isTrigger, Object options) {
        this.execSpEL(EasySingle.from(data), doc, d.getItems(), defIV.getCardKey(), false, true);
        return super.calculate(doc, data, defIV, d, isTrigger, options);
    }

    private void execSpEL(EasySingle data, DocHV doc, List<NkDynamicFormDefI> fields, String cardKey, boolean isNewCreate, boolean calculate){

        EvaluationContext context = spELManager.createContext(doc);

        fields.stream()
                .sorted(Comparator.comparing(NkDynamicFormDefI::getCalcOrder))
                .filter(field-> !StringUtils.equals(field.getInputType(),"divider"))
                .peek( field -> {
                    if(calculate){
                        NkField nkField = customObjectManager.getCustomObject(field.getInputType(), NkField.class);
                        nkField.beforeCalculate(field, context, doc, data);
                    }
                })
                .peek( field -> {

                    if(StringUtils.isNotBlank(field.getSpELControl())){

                        if(log.isInfoEnabled())
                            log.info("{}\t\t{} 执行表达式 KEY={} T=CONTROL EL={}",
                                    NkDocEngineContext.currLog(),
                                    cardKey,
                                    field.getKey(),
                                    field.getSpELControl()
                            );
                        try{
                            field.setControl((Integer) spELManager.invoke(field.getSpELControl(),context));
                        }catch(Exception e){
                            throw new NkDefineException(
                                    String.format("KEY=%s T=CONTROL %s",
                                            field.getKey(),
                                            e.getMessage()
                                    )
                            );
                        }
                    }

                    if (StringUtils.isNotBlank(field.getSpELContent())) {

                        String trigger = null;
                        if (ArrayUtils.contains(field.getSpELTriggers(), "ALWAYS")) {
                            trigger = "ALWAYS";
                        } else if (ArrayUtils.contains(field.getSpELTriggers(), "INIT") && isNewCreate) {
                            trigger = "INIT";
                        } else if (ArrayUtils.contains(field.getSpELTriggers(), "BLANK") && isBlank(data.get(field.getKey()))) {
                            trigger = "BLANK";
                        }

                        if (trigger != null) {

                            if (log.isInfoEnabled())
                                log.info("{}\t\t{} 执行表达式 KEY={} T={} EL={}",
                                        NkDocEngineContext.currLog(),
                                        cardKey,
                                        field.getKey(),
                                        trigger,
                                        field.getSpELContent()
                                );

                            try {
                                data.set(field.getKey(), spELManager.invoke(field.getSpELContent(), context));
                            } catch (Exception e) {
                                throw new NkDefineException(
                                        String.format("KEY=%s T=%s %s",
                                                field.getKey(),
                                                trigger,
                                                e.getMessage()
                                        )
                                );
                            }
                        }
                    }
                    context.setVariable(field.getKey(), data.get(field.getKey()));
                }).forEach(field->{
                    NkField nkField = customObjectManager.getCustomObject(field.getInputType(), NkField.class);
                    nkField.processOptions(field, context, doc, data);

                    if(calculate){
                        nkField.afterCalculate(field, context, doc, data);
                    }
                });
    }

    private static boolean isBlank(Object value){
        return value == null ||
                (value instanceof Array && Array.getLength(value)==0) ||
                (value instanceof Collection && CollectionUtils.isEmpty((Collection<?>) value)) ||
                StringUtils.isBlank(value.toString());
    }

    @Override
    public Object callDef(NkDynamicFormDef def, Object options) {
        return customObjectManager.getCustomObjectDescriptionList(NkField.class, false, null);
    }
}
