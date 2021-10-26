package cn.nkpro.ts5.docengine.gen;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocDefH extends DocDefHKey implements Serializable {
    /**
     * 暂时不启用
     *
     * @mbggenerated
     */
    private Integer versionHead;

    private String docClassify;

    /**
     * 单据名称
     *
     * @mbggenerated
     */
    private String docName;

    private String refObjectType;

    private String businessKeySpEL;

    private Integer docEntrance;

    /**
     * 暂时不启用
     *
     * @mbggenerated
     */
    private String validFrom;

    /**
     * 暂时不启用
     *
     * @mbggenerated
     */
    private String validTo;

    private String state;

    private Integer markdownFlag;

    /**
     * 最后修改时间秒数
     *
     * @mbggenerated
     */
    private Long updatedTime;

    private String markdown;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_doc_def_h
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取 暂时不启用
     *
     * @return 暂时不启用
     *
     * @mbggenerated
     */
    @cn.nkpro.ts5.annotation.CodeFieldNotes("暂时不启用")
    public Integer getVersionHead() {
        return versionHead;
    }

    /**
     * 设置 暂时不启用
     *
     * @return 暂时不启用
     *
     * @mbggenerated
     */
    public void setVersionHead(Integer versionHead) {
        this.versionHead = versionHead;
    }

    @cn.nkpro.ts5.annotation.CodeFieldNotes("")
    public String getDocClassify() {
        return docClassify;
    }

    public void setDocClassify(String docClassify) {
        this.docClassify = docClassify;
    }

    /**
     * 获取 单据名称
     *
     * @return 单据名称
     *
     * @mbggenerated
     */
    @cn.nkpro.ts5.annotation.CodeFieldNotes("单据名称")
    public String getDocName() {
        return docName;
    }

    /**
     * 设置 单据名称
     *
     * @return 单据名称
     *
     * @mbggenerated
     */
    public void setDocName(String docName) {
        this.docName = docName;
    }

    @cn.nkpro.ts5.annotation.CodeFieldNotes("")
    public String getRefObjectType() {
        return refObjectType;
    }

    public void setRefObjectType(String refObjectType) {
        this.refObjectType = refObjectType;
    }

    @cn.nkpro.ts5.annotation.CodeFieldNotes("")
    public String getBusinessKeySpEL() {
        return businessKeySpEL;
    }

    public void setBusinessKeySpEL(String businessKeySpEL) {
        this.businessKeySpEL = businessKeySpEL;
    }

    @cn.nkpro.ts5.annotation.CodeFieldNotes("")
    public Integer getDocEntrance() {
        return docEntrance;
    }

    public void setDocEntrance(Integer docEntrance) {
        this.docEntrance = docEntrance;
    }

    /**
     * 获取 暂时不启用
     *
     * @return 暂时不启用
     *
     * @mbggenerated
     */
    @cn.nkpro.ts5.annotation.CodeFieldNotes("暂时不启用")
    public String getValidFrom() {
        return validFrom;
    }

    /**
     * 设置 暂时不启用
     *
     * @return 暂时不启用
     *
     * @mbggenerated
     */
    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * 获取 暂时不启用
     *
     * @return 暂时不启用
     *
     * @mbggenerated
     */
    @cn.nkpro.ts5.annotation.CodeFieldNotes("暂时不启用")
    public String getValidTo() {
        return validTo;
    }

    /**
     * 设置 暂时不启用
     *
     * @return 暂时不启用
     *
     * @mbggenerated
     */
    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }

    @cn.nkpro.ts5.annotation.CodeFieldNotes("")
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @cn.nkpro.ts5.annotation.CodeFieldNotes("")
    public Integer getMarkdownFlag() {
        return markdownFlag;
    }

    public void setMarkdownFlag(Integer markdownFlag) {
        this.markdownFlag = markdownFlag;
    }

    /**
     * 获取 最后修改时间秒数
     *
     * @return 最后修改时间秒数
     *
     * @mbggenerated
     */
    @cn.nkpro.ts5.annotation.CodeFieldNotes("最后修改时间秒数")
    public Long getUpdatedTime() {
        return updatedTime;
    }

    /**
     * 设置 最后修改时间秒数
     *
     * @return 最后修改时间秒数
     *
     * @mbggenerated
     */
    public void setUpdatedTime(Long updatedTime) {
        this.updatedTime = updatedTime;
    }

    @cn.nkpro.ts5.annotation.CodeFieldNotes("")
    public String getMarkdown() {
        return markdown;
    }

    public void setMarkdown(String markdown) {
        this.markdown = markdown;
    }
}