package cn.nkpro.ts5.docengine.cards;

import cn.nkpro.ts5.annotation.NkNote;
import cn.nkpro.ts5.co.NkCustomObjectManager;
import cn.nkpro.ts5.docengine.NkAbstractCard;
import cn.nkpro.ts5.docengine.NkField;
import cn.nkpro.ts5.docengine.model.DocDefIV;
import cn.nkpro.ts5.docengine.model.DocHV;
import cn.nkpro.ts5.docengine.model.easy.EasySingle;
import cn.nkpro.ts5.docengine.service.NkDocEngineContext;
import cn.nkpro.ts5.docengine.utils.CopyUtils;
import cn.nkpro.ts5.exception.NkDefineException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 联动表单模式下
 * <li>仅支持部分字段组件
 * <li>由于字段关联的特殊性，SpEL表达式的计算时点强制为Always
 * <li>通过NkFieldRef配置dataMappings   可来设置其他字段的默认值
 * <li>通过NkFieldRef配置optionMappings 可来设置其他字段的input选项
 */
@Order(1002)
@NkNote("联动表单")
@Component("NkLinkageForm")
public class NkLinkageForm extends NkAbstractCard<Map<String,Object>, NkLinkageFormDef> {

    @Autowired
    protected NkCustomObjectManager customObjectManager;

    @Override
    public String getDataComponentName() {
        return "NkLinkageForm";
    }

    @Override
    protected String[] getDefComponentNames() {
        return new String[]{"NkLinkageFormDef"};
    }


    @Override
    public Map<String,Object> afterCreate(DocHV doc, DocHV preDoc, Map<String,Object> data, DocDefIV defIV, NkLinkageFormDef d) {

        if(defIV.getCopyFromRef()!=null&&defIV.getCopyFromRef()==1){
            CopyUtils.copy(
                    preDoc.getData().get(defIV.getCardKey()),
                    data,
                    d.getItems().stream().map(NkLinkageFormDefI::getKey).collect(Collectors.toList())
            );
        }

        return super.afterCreate(doc, preDoc, data, defIV, d);
    }

    @Override
    public Map<String,Object> afterGetData(DocHV doc, Map<String,Object> data, DocDefIV defIV, NkLinkageFormDef d) {
        return super.afterGetData(doc, data, defIV, d);
    }

    @Override
    public Map<String, Object> calculate(DocHV doc, Map<String, Object> data, DocDefIV defIV, NkLinkageFormDef d, boolean isTrigger, Object options) {
        this.execSpEL(EasySingle.from(data), doc, d.getItems(), defIV.getCardKey(), isTrigger, (Map) options);
        return super.calculate(doc, data, defIV, d, isTrigger, options);
    }


    private void execSpEL(EasySingle data, DocHV doc, List<? extends NkLinkageFormDefI> fields, String cardKey, boolean isTrigger, Map options){

        EvaluationContext context = spELManager.createContext(doc);

        Map<String,Object> original = new HashMap<>();

        List<? extends NkLinkageFormDefI> sortedFields = fields.stream()
                .sorted(Comparator.comparing(NkDynamicFormDefI::getCalcOrder)).collect(Collectors.toList());

        sortedFields.forEach(field -> {
            original.put(field.getKey(), data.get(field.getKey()));
            context.setVariable(field.getKey(), data.get(field.getKey()));
        });

        List<String> skip = new ArrayList<>();
        if(isTrigger && options !=null){
            skip.add((String) options.get("triggerKey"));
        }


        NkDynamicCalculateContext calculateContext = new NkDynamicCalculateContext();
        calculateContext.setDoc(doc);
        calculateContext.setFields(sortedFields);
        calculateContext.setSkip(skip);
        calculateContext.setOptions(options);
        calculateContext.setTrigger(isTrigger);
        calculateContext.setOriginal(original);

        sortedFields.forEach(field -> {

            if(!skip.contains(field.getKey()) && StringUtils.isNotBlank(field.getSpELContent())){
                if (log.isInfoEnabled())
                    log.info("{}\t\t{} 执行表达式 KEY={} EL={}",
                            NkDocEngineContext.currLog(),
                            cardKey,
                            field.getKey(),
                            field.getSpELContent()
                    );

                try {
                    data.set(field.getKey(), spELManager.invoke(field.getSpELContent(), context));
                } catch (Exception e) {
                    throw new NkDefineException(
                            String.format("KEY=%s %s",
                                    field.getKey(),
                                    e.getMessage()
                            )
                    );
                }
            }

            calculateContext.setFieldTrigger(isTrigger && options !=null && StringUtils.equals(field.getKey(), (String) options.get("triggerKey")));

            NkField nkField = customObjectManager.getCustomObject(field.getInputType(), NkField.class);
            nkField.afterCalculate(field, context, data, calculateContext);

            context.setVariable(field.getKey(), data.get(field.getKey()));
        });
    }

    @Override
    public Object callDef(NkLinkageFormDef def, Object options) {
        return customObjectManager.getCustomObjectDescriptionList(NkField.class, false, (entry)-> entry.getValue() instanceof NkLinkageFormField);
    }
}
