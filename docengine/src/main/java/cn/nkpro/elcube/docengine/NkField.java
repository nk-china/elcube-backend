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
package cn.nkpro.elcube.docengine;

import cn.nkpro.elcube.annotation.NkScriptType;
import cn.nkpro.elcube.co.NkScriptComponent;
import cn.nkpro.elcube.docengine.cards.NkBaseContext;
import cn.nkpro.elcube.docengine.cards.NkCalculateContext;
import cn.nkpro.elcube.docengine.cards.NkDynamicFormDefI;
import cn.nkpro.elcube.co.easy.EasySingle;
import org.springframework.expression.EvaluationContext;
@NkScriptType("Field")
public interface NkField extends NkScriptComponent {

    void beforeCalculate(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkCalculateContext calculateContext);

    void processOptions(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkBaseContext baseContext);

    void afterCalculate(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkCalculateContext calculateContext);
}
