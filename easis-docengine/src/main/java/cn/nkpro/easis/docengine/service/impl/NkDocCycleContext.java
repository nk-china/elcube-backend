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
package cn.nkpro.easis.docengine.service.impl;

import cn.nkpro.easis.docengine.NkDocCycle;
import cn.nkpro.easis.docengine.model.DocHV;
import lombok.Getter;

public class NkDocCycleContext {

    static NkDocCycleContext build(NkDocCycle cycle){
        NkDocCycleContext context = new NkDocCycleContext();
        context.cycle = cycle;
        return context;
    }

    @Getter
    private NkDocCycle cycle;
    @Getter
    private DocHV prev;
    @Getter
    private DocHV clip;
    @Getter
    private DocHV original;


    NkDocCycleContext cycle(NkDocCycle cycle) {
        this.cycle = cycle;
        return this;
    }
    NkDocCycleContext prev(DocHV prev) {
        this.prev = prev;
        return this;
    }

    NkDocCycleContext clip(DocHV clip) {
        this.clip = clip;
        return this;
    }

    NkDocCycleContext original(DocHV original) {
        this.original = original;
        return this;
    }
}
