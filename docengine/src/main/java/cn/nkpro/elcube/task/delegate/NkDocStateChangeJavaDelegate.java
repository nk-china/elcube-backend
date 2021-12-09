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
package cn.nkpro.elcube.task.delegate;

import cn.nkpro.elcube.docengine.NkDocEngineThreadLocal;
import cn.nkpro.elcube.exception.NkDefineException;
import lombok.Setter;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.el.FixedValue;
import org.springframework.stereotype.Component;

@Component("NkDocStateChangeJavaDelegate")
public class NkDocStateChangeJavaDelegate implements JavaDelegate {

    @Setter
    private FixedValue state;

    @Override
    public void execute(DelegateExecution delegateExecution) {

        if(NkDocEngineThreadLocal.getCurr()==null)
            throw new NkDefineException("NkDocStateChangeJavaDelegate 仅支持工作流启动时配置");

        NkDocEngineThreadLocal.getCurr().setDocState(
                (String) state.getValue(delegateExecution)
        );
    }
}
