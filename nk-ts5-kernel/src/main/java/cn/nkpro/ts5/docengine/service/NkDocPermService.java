package cn.nkpro.ts5.docengine.service;

import cn.nkpro.ts5.docengine.model.DocHV;
import org.elasticsearch.index.query.BoolQueryBuilder;

public interface NkDocPermService {



    //String MODE_ADD =     "NEW";
    String MODE_READ =    "READ";
    String MODE_WRITE =   "WRITE";
    //String MODE_REMOVE =  "REMOVE";


    void filterDocCards(String mode, DocHV docHV);

    void assertHasDocPerm(String mode, String docType);

    boolean hasDocPerm(String mode, String docType);

    void assertHasDocPerm(String mode, String docId, String docType);

    boolean hasDocPerm(String mode, String docId, String docType);

    BoolQueryBuilder buildDocFilter(String mode, String docType, String typeKey, boolean ignoreLimit);
}
