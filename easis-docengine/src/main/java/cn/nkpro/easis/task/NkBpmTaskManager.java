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
package cn.nkpro.easis.task;

import cn.nkpro.easis.basic.PageList;
import cn.nkpro.easis.docengine.model.DocHV;
import cn.nkpro.easis.task.model.BpmInstance;

public interface NkBpmTaskManager {

    PageList<BpmInstance> processInstancePage(Integer from, Integer rows);

    BpmInstance processInstanceDetail(String instanceId);

    void deleteProcessInstance(String instanceId, String deleteReason);



    Boolean taskExists(String taskId);

    void indexDocTask(DocHV doc);

    void revoke(String instanceId);
}
