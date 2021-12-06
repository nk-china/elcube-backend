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

import cn.nkpro.elcube.co.NkCustomObjectManager;
import cn.nkpro.elcube.docengine.NkAbstractCard;
import cn.nkpro.elcube.docengine.NkField;
import cn.nkpro.elcube.docengine.model.DocDefIV;
import cn.nkpro.elcube.docengine.model.DocHV;
import cn.nkpro.elcube.co.easy.EasySingle;
import cn.nkpro.elcube.docengine.service.NkDocEngineContext;
import cn.nkpro.elcube.docengine.utils.CopyUtils;
import cn.nkpro.elcube.exception.NkDefineException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class NkDynamicBase<DT, DDT> extends NkAbstractCard<DT, DDT> {

    @Autowired
    protected NkCustomObjectManager customObjectManager;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected void copyFromPre(DocHV preDoc, Map<String, Object> data, DocDefIV defIV, List<? extends NkDynamicFormDefI> fields){

        if(preDoc!=null && defIV.getCopyFromRef()!=null&&defIV.getCopyFromRef()==1){
            CopyUtils.copy(
                    preDoc.getData().get(defIV.getCardKey()),
                    data,
                    fields.stream().map(NkDynamicFormDefI::getKey).collect(Collectors.toList())
            );
        }
    }

    protected void processOptions(EasySingle data, DocHV doc, List<? extends NkDynamicFormDefI> fields){

        EvaluationContext context = spELManager.createContext(doc);

        // 创建一个计算上下文
        NkBaseContext calculateContext = new NkBaseContext();
        calculateContext.setDoc(doc);
        calculateContext.setFields(fields);

        // 处理字段的inputOptions
        fields.stream()
                .filter(field-> !(StringUtils.equalsAny(field.getInputType(),"divider","-","--")))
                .forEach(field -> {
                    NkField nkField = customObjectManager.getCustomObject(field.getInputType(), NkField.class);
                    nkField.processOptions(field, context, data, calculateContext);
                });
    }


    protected void execSpEL(EasySingle data, DocHV doc, List<? extends NkDynamicFormDefI> fields, String cardKey, boolean isTrigger, Map options, DocHV preDoc, boolean isNewCreate){
        EvaluationContext context = spELManager.createContext(doc);
        context.setVariable("pre", preDoc);

        Map<String,Object> original = new HashMap<>();

        // 按计算顺序排序
        List<? extends NkDynamicFormDefI> sortedFields = fields.stream()
                .filter(field-> !(StringUtils.equalsAny(field.getInputType(),"divider","-","--")))
                .sorted(Comparator.comparing(NkDynamicFormDefI::getCalcOrder))
                .collect(Collectors.toList());

        // 初始化上下文及保留原始值
        sortedFields.forEach(field -> {
            original.put(field.getKey(), data.get(field.getKey()));
            context.setVariable(field.getKey(), data.get(field.getKey()));
        });

        // 设置需要跳过SpEL计算的字段
        //List<String> skip = new ArrayList<>();
        //if(isTrigger && options !=null){
        //    skip.add((String) options.get("triggerKey"));
        //}

        // 创建一个计算上下文
        NkCalculateContext calculateContext = new NkCalculateContext();
        calculateContext.setDoc(doc);
        calculateContext.setFields(sortedFields);
        calculateContext.setSkip(new ArrayList<>());
        calculateContext.setOptions(options);
        calculateContext.setTrigger(isTrigger);
        calculateContext.setOriginal(original);

        // 处理字段的inputOptions
        sortedFields.forEach(field -> {
            NkField nkField = customObjectManager.getCustomObject(field.getInputType(), NkField.class);
            nkField.processOptions(field, context, data, calculateContext);
        });

        sortedFields.forEach( field -> {
            calculateContext.setFieldTrigger(isTrigger && options !=null && StringUtils.equals(field.getKey(), (String) options.get("triggerKey")));

            NkField nkField = customObjectManager.getCustomObject(field.getInputType(), NkField.class);
            nkField.beforeCalculate(field, context, data, calculateContext);
        });

        sortedFields.forEach( field -> {

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

            calculateContext.setFieldTrigger(
                    isTrigger
                            && options !=null
                            && StringUtils.equals(field.getKey(), (String) options.get("triggerKey"))
            );

            customObjectManager.getCustomObject(field.getInputType(), NkField.class)
                    .afterCalculate(field, context, data, calculateContext);

            context.setVariable(field.getKey(), data.get(field.getKey()));
        });
    }

    protected void execSpEL(EasySingle data, DocHV doc, List<? extends NkDynamicFormDefI> fields, String cardKey, boolean isTrigger, Map options){
        this.execSpEL(data, doc, fields, cardKey, isTrigger, options, null, false);

    }

    private static boolean isBlank(Object value){
        return value == null ||
                (value instanceof Array && Array.getLength(value)==0) ||
                (value instanceof Collection && CollectionUtils.isEmpty((Collection<?>) value)) ||
                StringUtils.isBlank(value.toString());
    }
}
