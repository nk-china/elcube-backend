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
package cn.nkpro.easis.docengine.datasync;

import cn.nkpro.easis.co.NkAbstractCustomScriptObject;
import cn.nkpro.easis.co.spel.NkSpELManager;
import cn.nkpro.easis.docengine.gen.DocDefDataSync;
import cn.nkpro.easis.docengine.model.DocHV;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;

import java.util.Arrays;

@Slf4j
public abstract class NkAbstractDocDataSupport extends NkAbstractCustomScriptObject {

    @Autowired
    protected NkSpELManager spELManager;

    @SuppressWarnings({"unused"})
    public final void sync(DocHV doc, DocHV original, EvaluationContext context, EvaluationContext contextOriginal, DocDefDataSync config) {

        // data1 新数据
        Object dataUnmapping = spELManager.invoke(config.getDataSpEL(), context);
        // data2 原数据
        Object dataOriginalUnmapping = original!=null? spELManager.invoke(config.getDataSpEL(), original) :null;

        // 数组需要转换成List
        if(dataUnmapping!=null && dataUnmapping.getClass().isArray()){
            dataUnmapping = Arrays.asList((Object[])dataUnmapping);
        }
        if(dataOriginalUnmapping!=null && dataOriginalUnmapping.getClass().isArray()){
            dataOriginalUnmapping = Arrays.asList((Object[])dataOriginalUnmapping);
        }

        this.doSync(dataUnmapping, dataOriginalUnmapping, context, contextOriginal, config);
    }

    protected abstract void doSync(Object dataUnmapping, Object dataOriginalUnmapping, EvaluationContext context1, EvaluationContext context2, DocDefDataSync def);
}