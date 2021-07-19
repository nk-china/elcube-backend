package cn.nkpro.tfms.platform.model.po;

import cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes;

import java.io.Serializable;

public class DefPartnerRoleDocKey implements Serializable {
    private String docType;

    private String partnerRole;

    private String preDocType;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table def_partner_role_doc
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    @CodeFieldNotes("")
    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    @CodeFieldNotes("")
    public String getPartnerRole() {
        return partnerRole;
    }

    public void setPartnerRole(String partnerRole) {
        this.partnerRole = partnerRole;
    }

    @CodeFieldNotes("")
    public String getPreDocType() {
        return preDocType;
    }

    public void setPreDocType(String preDocType) {
        this.preDocType = preDocType;
    }
}