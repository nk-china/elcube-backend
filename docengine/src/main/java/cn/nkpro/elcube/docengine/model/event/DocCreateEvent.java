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
package cn.nkpro.elcube.docengine.model.event;

import cn.nkpro.elcube.docengine.NkDocCycle;
import cn.nkpro.elcube.docengine.model.DocHV;
import lombok.Getter;

public class DocCreateEvent extends AbstractDocCycleEvent {

    public static DocCreateEvent build(NkDocCycle cycle, DocHV prev){
        DocCreateEvent context = new DocCreateEvent();
        context.cycle = cycle;
        context.prev = prev;
        return context;
    }

    @Getter
    private DocHV prev;
}
