package cn.nkpro.ts5.docengine.gen;

import java.io.Serializable;

public class DocDefHKey implements Serializable {
    private String docType;

    private String version;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_doc_def_h
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    @cn.nkpro.ts5.annotation.CodeFieldNotes("")
    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    @cn.nkpro.ts5.annotation.CodeFieldNotes("")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}