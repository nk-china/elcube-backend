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

import cn.nkpro.elcube.docengine.NkDocEngine;
import lombok.Setter;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.el.FixedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component("NkDocStateUpdateJavaDelegate")
public class NkDocStateUpdateJavaDelegate implements JavaDelegate {

    @Autowired
    private NkDocEngine docEngine;
    @Setter
    private FixedValue state;

    @Override
    public void execute(DelegateExecution delegateExecution) {

        docEngine.doUpdate(
                delegateExecution.getBusinessKey(),
                "BPM:"+delegateExecution.getEventName(),
                (doc)-> doc.setDocState((String) state.getValue(delegateExecution))
        );

        delegateExecution.setVariable("NK$COUNTERSIGNATURE_USERS", Arrays.asList("nk-default-admin","nk-default-test"));
    }
}
