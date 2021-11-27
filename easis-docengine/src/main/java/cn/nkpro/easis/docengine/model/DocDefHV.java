package cn.nkpro.easis.docengine.model;

import cn.nkpro.easis.co.DebugAble;
import cn.nkpro.easis.docengine.gen.*;
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
