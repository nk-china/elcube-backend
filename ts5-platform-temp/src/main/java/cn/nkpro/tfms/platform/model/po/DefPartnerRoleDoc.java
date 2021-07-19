package cn.nkpro.tfms.platform.model.po;

import cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DefPartnerRoleDoc extends DefPartnerRoleDocKey implements Serializable {
    private Long updatedTime;

    private Integer orderby;

    private String preDocStatus;

    private String refObjectType;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table def_partner_role_doc
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    @CodeFieldNotes("")
    public Long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Long updatedTime) {
        this.updatedTime = updatedTime;
    }

    @CodeFieldNotes("")
    public Integer getOrderby() {
        return orderby;
    }

    public void setOrderby(Integer orderby) {
        this.orderby = orderby;
    }

    @CodeFieldNotes("")
    public String getPreDocStatus() {
        return preDocStatus;
    }

    public void setPreDocStatus(String preDocStatus) {
        this.preDocStatus = preDocStatus;
    }

    @CodeFieldNotes("")
    public String getRefObjectType() {
        return refObjectType;
    }

    public void setRefObjectType(String refObjectType) {
        this.refObjectType = refObjectType;
    }
}