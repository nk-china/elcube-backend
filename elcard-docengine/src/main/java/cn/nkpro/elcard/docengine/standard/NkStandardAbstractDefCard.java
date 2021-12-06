/*
 * This file is part of ELCard.
 *
 * ELCard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcard.docengine.standard;

import cn.nkpro.elcard.docengine.NkAbstractCard;
import cn.nkpro.elcard.docengine.model.DocDefHV;
import cn.nkpro.elcard.docengine.model.DocDefIV;
import cn.nkpro.elcard.docengine.model.DocHV;
import cn.nkpro.elcard.co.easy.EasySingle;
import cn.nkpro.elcard.docengine.service.NkDocEngineContext;
import cn.nkpro.elcard.exception.NkDefineException;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.EvaluationContext;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class NkStandardAbstractDefCard<DT,DDT extends NkStandardDef> extends NkAbstractCard<DT,DDT> {

    @Override
    public DDT afterGetDef(DocDefHV defHV, DocDefIV defIV, DDT def) {

        def.getFields()
                .stream()
                .filter(e -> StringUtils.equals(e.getType(),"select"))
                .filter(e -> StringUtils.isNotBlank((CharSequence) e.getOptions()))
                .forEach(e -> e.setOptions(JSON.parseArray(spELManager.convert((String) e.getOptions(), null))));

        return super.afterGetDef(defHV, defIV, def);
    }

    @Override
    public DT afterCreate(DocHV doc, DocHV preDoc, DT data, DocDefIV defIV, DDT d) {

        this.execSpEL(EasySingle.from(data), doc, d.getFields(), defIV.getCardKey(), true);

        return super.afterCreate(doc, preDoc, data, defIV, d);
    }

    @Override
    public DT calculate(DocHV doc, DT data, DocDefIV defIV, DDT d, boolean isTrigger, Object options) {

        this.execSpEL(EasySingle.from(data), doc, d.getFields(), defIV.getCardKey(), false);

        return super.calculate(doc, data, defIV, d, isTrigger, options);
    }

    protected void execSpEL(EasySingle data, DocHV doc, List<NkStandardDefField> fields, String cardKey, boolean isNewCreate){

        EvaluationContext context = spELManager.createContext(doc);

        fields.stream()
                .sorted(Comparator.comparing(NkStandardDefField::getCalcOrder))
                .forEach( field -> {
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
                });
    }

    private static boolean isBlank(Object value){
        return value == null ||
                (value instanceof Array && Array.getLength(value)==0) ||
                (value instanceof Collection && CollectionUtils.isEmpty((Collection<?>) value)) ||
                StringUtils.isBlank(value.toString());
    }
}
