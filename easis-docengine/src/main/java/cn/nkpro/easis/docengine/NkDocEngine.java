package cn.nkpro.easis.docengine;

import cn.nkpro.easis.docengine.model.DocHV;

public interface NkDocEngine {

    DocHV detail(String docId);

    DocHV create(String docType, String preDocId);

    DocHV calculate(DocHV doc, String fromCard, Object options);

    DocHV doUpdate(DocHV doc, String optSource);

    DocHV doUpdate(String docId, String optSource, Function function);

    DocHV random(DocHV doc);

    @FunctionalInterface
    interface Function{
        void apply(DocHV doc);
    }
}
