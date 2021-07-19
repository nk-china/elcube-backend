package cn.nkpro.tfms.platform.model.po;

import cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes;

import java.io.Serializable;

public class BizDocSubjectKey implements Serializable {
    private String docId;

    private String subjectRefDocId;

    private String subjectRefItemId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table biz_doc_subject
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    @CodeFieldNotes("")
    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    @CodeFieldNotes("")
    public String getSubjectRefDocId() {
        return subjectRefDocId;
    }

    public void setSubjectRefDocId(String subjectRefDocId) {
        this.subjectRefDocId = subjectRefDocId;
    }

    @CodeFieldNotes("")
    public String getSubjectRefItemId() {
        return subjectRefItemId;
    }

    public void setSubjectRefItemId(String subjectRefItemId) {
        this.subjectRefItemId = subjectRefItemId;
    }
}