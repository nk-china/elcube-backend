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

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

/**
 * 会签结果清零
 */
@Component("NkCountersignatureCleanListener")
public class NkCountersignatureCleanListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        //通过数量 var key
        String passCountingKey = "NK$COUNTERSIGNATURE_PASS_COUNT";
        // 设置通过计数
        delegateTask.setVariable(passCountingKey,0);
    }
}
