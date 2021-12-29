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
package cn.nkpro.elcube.components.defaults.cards.bpm

import cn.nkpro.elcube.annotation.NkNote
import cn.nkpro.elcube.docengine.NkAbstractCard
import org.springframework.stereotype.Component

/*
 * 暂时没用到，待作废
 */
@Deprecated
@SuppressWarnings("unused")
@NkNote("工作流")
@Component("NkCardBpm")
class NkCardBpm extends NkAbstractCard<BpmData,BpmDef> {

    @SuppressWarnings("unused")
    static class BpmData{
        private String processKey

        String getProcessKey() {
            return processKey
        }

        void setProcessKey(String processKey) {
            this.processKey = processKey
        }
    }

    @SuppressWarnings("unused")
    static class BpmDef{

        private String processKey
        private String startBy
        private String rollbackTo

        String getProcessKey() {
            return processKey
        }

        void setProcessKey(String processKey) {
            this.processKey = processKey
        }

        String getStartBy() {
            return startBy
        }

        void setStartBy(String startBy) {
            this.startBy = startBy
        }

        String getRollbackTo() {
            return rollbackTo
        }

        void setRollbackTo(String rollbackTo) {
            this.rollbackTo = rollbackTo
        }
    }
}

