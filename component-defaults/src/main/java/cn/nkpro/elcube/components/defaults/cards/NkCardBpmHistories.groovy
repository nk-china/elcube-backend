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
package cn.nkpro.elcube.components.defaults.cards

import cn.nkpro.elcube.annotation.NkNote
import cn.nkpro.elcube.docengine.NkAbstractCard
import cn.nkpro.elcube.docengine.NkDocEngine
import cn.nkpro.elcube.docengine.model.DocDefIV
import cn.nkpro.elcube.docengine.model.DocHV
import cn.nkpro.elcube.task.NkBpmTaskService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@NkNote("业务流程历史")
@Component("NkCardBpmHistories")
class NkCardBpmHistories extends NkAbstractCard<Map,Map> {

    @Autowired
    private NkDocEngine docEngine

    @Autowired
    private NkBpmTaskService bpmTaskService

    @Override
    Object call(DocHV doc, Map data, DocDefIV defIV, Map d, Object options) {

        if(options){
            return bpmTaskService.instanceTaskHistories(options as String)
        }else{
            return bpmTaskService.instanceHistories(doc.docId)
        }

    }
}
