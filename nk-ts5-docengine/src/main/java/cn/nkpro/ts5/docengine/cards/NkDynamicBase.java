package cn.nkpro.ts5.docengine.cards;

import cn.nkpro.ts5.co.NkCustomObjectManager;
import cn.nkpro.ts5.docengine.NkAbstractCard;
import cn.nkpro.ts5.docengine.NkField;
import cn.nkpro.ts5.docengine.model.DocHV;
import cn.nkpro.ts5.docengine.model.easy.EasySingle;
import cn.nkpro.ts5.docengine.service.NkDocEngineContext;
import cn.nkpro.ts5.exception.NkDefineException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NkDynamicBase<DT, DDT> extends NkAbstractCard<DT, DDT> {

    @Autowired
    protected NkCustomObjectManager customObjectManager;

    void execSpEL(EasySingle data, DocHV doc, List<? extends NkDynamicFormDefI> fields, String cardKey, boolean isNewCreate, boolean calculate, boolean isTrigger, Map options){

        EvaluationContext context = spELManager.createContext(doc);

        Map<String,Object> original = new HashMap<>();

        fields.stream()
                .sorted(Comparator.comparing(NkDynamicFormDefI::getCalcOrder))
                .filter(field-> !(StringUtils.equalsAny(field.getInputType(),"divider","-","--")))
                .peek( field -> {
                    if(calculate){
                        NkDynamicCalculateContext calculateContext = new NkDynamicCalculateContext();
                        calculateContext.setDoc(doc);
                        calculateContext.setOptions(options);
                        calculateContext.setTrigger(isTrigger);
                        calculateContext.setFieldTrigger(isTrigger && options !=null && StringUtils.equals(field.getKey(), (String) options.get("triggerKey")));

                        NkField nkField = customObjectManager.getCustomObject(field.getInputType(), NkField.class);
                        nkField.beforeCalculate(field, context, data, calculateContext);
                    }
                })
                .peek( field -> {
                    original.put(field.getKey(), data.get(field.getKey()));

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
                    NkDynamicCalculateContext calculateContext = new NkDynamicCalculateContext();
                    calculateContext.setDoc(doc);
                    calculateContext.setOptions(options);

                    NkField nkField = customObjectManager.getCustomObject(field.getInputType(), NkField.class);
                    nkField.processOptions(field, context, data, calculateContext);

                    if(calculate){

                        calculateContext.setTrigger(isTrigger);
                        calculateContext.setOriginal(original);
                        calculateContext.setFieldTrigger(isTrigger && options !=null && StringUtils.equals(field.getKey(), (String) options.get("triggerKey")));

                        nkField.afterCalculate(field, context, data, calculateContext);
                    }
                });
    }

    private static boolean isBlank(Object value){
        return value == null ||
                (value instanceof Array && Array.getLength(value)==0) ||
                (value instanceof Collection && CollectionUtils.isEmpty((Collection<?>) value)) ||
                StringUtils.isBlank(value.toString());
    }
}
