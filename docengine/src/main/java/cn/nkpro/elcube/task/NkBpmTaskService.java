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
package cn.nkpro.elcube.task;

import cn.nkpro.elcube.task.model.BpmInstance;
import cn.nkpro.elcube.task.model.BpmTask;
import cn.nkpro.elcube.task.model.BpmTaskComplete;
import cn.nkpro.elcube.task.model.BpmTaskForward;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NkBpmTaskService {
    @Transactional
    String start(String key, String docId);

    @Transactional
    void complete(BpmTaskComplete bpmTask);

    @Transactional
    void forward(BpmTaskForward taskForward);

    @Transactional
    void delegate(BpmTaskForward bpmTask);

    BpmTask taskByBusinessAndAssignee(String businessKey, String assignee);

    boolean isDocAssignee(String businessKey, String assignee);

    List<BpmInstance> instanceHistories(String businessKey);

    List<BpmTask> instanceTaskHistories(String processInstanceId);
}
