package cn.nkpro.ts5.model.mb.gen;

import java.io.Serializable;

public class DocIKey implements Serializable {
    private String cardKey;

    private String docId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_doc_i
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    @cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes("")
    public String getCardKey() {
        return cardKey;
    }

    public void setCardKey(String cardKey) {
        this.cardKey = cardKey;
    }

    @cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes("")
    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
}