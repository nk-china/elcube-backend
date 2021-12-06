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
package cn.nkpro.elcard.docengine;

import cn.nkpro.elcard.annotation.NkScriptType;
import cn.nkpro.elcard.co.NkScriptComponent;
import cn.nkpro.elcard.docengine.cards.NkBaseContext;
import cn.nkpro.elcard.docengine.cards.NkCalculateContext;
import cn.nkpro.elcard.docengine.cards.NkDynamicFormDefI;
import cn.nkpro.elcard.co.easy.EasySingle;
import org.springframework.expression.EvaluationContext;
@NkScriptType("Field")
public interface NkField extends NkScriptComponent {

    void beforeCalculate(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkCalculateContext calculateContext);

    void processOptions(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkBaseContext baseContext);

    void afterCalculate(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkCalculateContext calculateContext);
}
