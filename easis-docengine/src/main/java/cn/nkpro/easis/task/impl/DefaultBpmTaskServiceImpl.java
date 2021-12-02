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
package cn.nkpro.easis.task.impl;

import cn.nkpro.easis.exception.NkSystemException;
import cn.nkpro.easis.task.NkBpmTaskService;
import cn.nkpro.easis.task.model.BpmTask;
import cn.nkpro.easis.task.model.BpmTaskComplete;


public class DefaultBpmTaskServiceImpl implements NkBpmTaskService {
    @Override
    public String start(String key, String docId) {
        throw new NkSystemException("操作不支持");
    }

    @Override
    public void complete(BpmTaskComplete bpmTask) {
        throw new NkSystemException("操作不支持");
    }

    @Override
    public BpmTask taskByBusinessAndAssignee(String businessKey, String assignee) {
        throw new NkSystemException("操作不支持");
    }
}
