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
package cn.nkpro.elcard.task;

import cn.nkpro.elcard.basic.PageList;
import cn.nkpro.elcard.docengine.model.DocHV;
import cn.nkpro.elcard.task.model.BpmInstance;

public interface NkBpmTaskManager {

    PageList<BpmInstance> processInstancePage(Integer from, Integer rows);

    BpmInstance processInstanceDetail(String instanceId);

    void deleteProcessInstance(String instanceId, String deleteReason);



    Boolean taskExists(String taskId);

    void indexDocTask(DocHV doc);

    void revoke(String instanceId);
}
