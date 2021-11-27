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
