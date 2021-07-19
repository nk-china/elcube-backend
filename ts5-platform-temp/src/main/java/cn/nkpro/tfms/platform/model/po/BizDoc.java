package cn.nkpro.tfms.platform.model.po;

import cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BizDoc implements Serializable {
    private String docId;

    private String docType;

    private Integer defVersion;

    private Long createdTime;

    private Long updatedTime;

    private String classify;

    private String docName;

    private String docDesc;

    private String refObjectId;

    private String docNumber;

    private String docState;

    private String docTags;

    private String preDocId;

    /**
     * 交易伙伴ID（注意是roleID）
     *
     * @mbggenerated
     */
    private String partnerId;

    private String identification;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table biz_doc
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
    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    @CodeFieldNotes("")
    public Integer getDefVersion() {
        return defVersion;
    }

    public void setDefVersion(Integer defVersion) {
        this.defVersion = defVersion;
    }

    @CodeFieldNotes("")
    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    @CodeFieldNotes("")
    public Long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Long updatedTime) {
        this.updatedTime = updatedTime;
    }

    @CodeFieldNotes("")
    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    @CodeFieldNotes("")
    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    @CodeFieldNotes("")
    public String getDocDesc() {
        return docDesc;
    }

    public void setDocDesc(String docDesc) {
        this.docDesc = docDesc;
    }

    @CodeFieldNotes("")
    public String getRefObjectId() {
        return refObjectId;
    }

    public void setRefObjectId(String refObjectId) {
        this.refObjectId = refObjectId;
    }

    @CodeFieldNotes("")
    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    @CodeFieldNotes("")
    public String getDocState() {
        return docState;
    }

    public void setDocState(String docState) {
        this.docState = docState;
    }

    @CodeFieldNotes("")
    public String getDocTags() {
        return docTags;
    }

    public void setDocTags(String docTags) {
        this.docTags = docTags;
    }

    @CodeFieldNotes("")
    public String getPreDocId() {
        return preDocId;
    }

    public void setPreDocId(String preDocId) {
        this.preDocId = preDocId;
    }

    /**
     * 获取 交易伙伴ID（注意是roleID）
     *
     * @return 交易伙伴ID（注意是roleID）
     *
     * @mbggenerated
     */
    @CodeFieldNotes("交易伙伴ID（注意是roleID）")
    public String getPartnerId() {
        return partnerId;
    }

    /**
     * 设置 交易伙伴ID（注意是roleID）
     *
     * @return 交易伙伴ID（注意是roleID）
     *
     * @mbggenerated
     */
    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    @CodeFieldNotes("")
    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }
}