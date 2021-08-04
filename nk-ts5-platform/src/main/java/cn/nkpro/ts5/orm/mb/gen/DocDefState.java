package cn.nkpro.ts5.orm.mb.gen;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocDefState extends DocDefStateKey implements Serializable {
    private String docStateDesc;

    private String sysState;

    /**
     * 业务逻辑处理类
     *
     * @mbggenerated
     */
    private String refObjectType;

    /**
     * 0 不可编辑，1 可编辑，2 流程中可编辑
     *
     * @mbggenerated
     */
    private Integer editPerm;

    private Integer displayPrimary;

    private Long updatedTime;

    /**
     * ACTIVATE, CANCEL
     *
     * @mbggenerated
     */
    private String action;

    private Integer orderBy;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    @cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes("")
    public String getDocStateDesc() {
        return docStateDesc;
    }

    public void setDocStateDesc(String docStateDesc) {
        this.docStateDesc = docStateDesc;
    }

    @cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes("")
    public String getSysState() {
        return sysState;
    }

    public void setSysState(String sysState) {
        this.sysState = sysState;
    }

    /**
     * 获取 业务逻辑处理类
     *
     * @return 业务逻辑处理类
     *
     * @mbggenerated
     */
    @cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes("业务逻辑处理类")
    public String getRefObjectType() {
        return refObjectType;
    }

    /**
     * 设置 业务逻辑处理类
     *
     * @return 业务逻辑处理类
     *
     * @mbggenerated
     */
    public void setRefObjectType(String refObjectType) {
        this.refObjectType = refObjectType;
    }

    /**
     * 获取 0 不可编辑，1 可编辑，2 流程中可编辑
     *
     * @return 0 不可编辑，1 可编辑，2 流程中可编辑
     *
     * @mbggenerated
     */
    @cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes("0 不可编辑，1 可编辑，2 流程中可编辑")
    public Integer getEditPerm() {
        return editPerm;
    }

    /**
     * 设置 0 不可编辑，1 可编辑，2 流程中可编辑
     *
     * @return 0 不可编辑，1 可编辑，2 流程中可编辑
     *
     * @mbggenerated
     */
    public void setEditPerm(Integer editPerm) {
        this.editPerm = editPerm;
    }

    @cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes("")
    public Integer getDisplayPrimary() {
        return displayPrimary;
    }

    public void setDisplayPrimary(Integer displayPrimary) {
        this.displayPrimary = displayPrimary;
    }

    @cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes("")
    public Long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Long updatedTime) {
        this.updatedTime = updatedTime;
    }

    /**
     * 获取 ACTIVATE, CANCEL
     *
     * @return ACTIVATE, CANCEL
     *
     * @mbggenerated
     */
    @cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes("ACTIVATE, CANCEL")
    public String getAction() {
        return action;
    }

    /**
     * 设置 ACTIVATE, CANCEL
     *
     * @return ACTIVATE, CANCEL
     *
     * @mbggenerated
     */
    public void setAction(String action) {
        this.action = action;
    }

    @cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes("")
    public Integer getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Integer orderBy) {
        this.orderBy = orderBy;
    }
}