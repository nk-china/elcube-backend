package cn.nkpro.tfms.platform.model.po;

import cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes;

import java.io.Serializable;

public class BizOrgPositionAreaKey implements Serializable {
    private String areaCode;

    private String docId;

    private String orgCode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table biz_org_position_area
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    @CodeFieldNotes("")
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    @CodeFieldNotes("")
    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    @CodeFieldNotes("")
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }
}