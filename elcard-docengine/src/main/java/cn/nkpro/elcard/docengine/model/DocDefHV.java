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
package cn.nkpro.elcard.docengine.model;

import cn.nkpro.elcard.co.DebugAble;
import cn.nkpro.elcard.docengine.gen.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude()
public class DocDefHV extends DocDefH implements DebugAble {
    private List<DocDefStateV> status;
    private List<DocDefFlowV> flows;
    private List<DocDefFlowV> nextFlows;
    private List<DocDefBpm> bpms;
    private List<DocDefIV> cards;
    private List<DocDefCycle> lifeCycles;
    private List<DocDefIndexRule> indexRules;
    private List<DocDefIndexCustom> indexCustoms;
    private List<DocDefDataSync> dataSyncs;
    private boolean debug;

    public DocDefHV(){
        this.status       = new ArrayList<>();
        this.flows        = new ArrayList<>();
        this.nextFlows    = new ArrayList<>();
        this.bpms         = new ArrayList<>();
        this.lifeCycles   = new ArrayList<>();
        this.indexRules   = new ArrayList<>();
        this.indexCustoms = new ArrayList<>();
        this.dataSyncs    = new ArrayList<>();
    }
}
