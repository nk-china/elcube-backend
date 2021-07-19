package cn.nkpro.tfms.platform.model.po;

import cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DefProjectStatus extends DefProjectStatusKey implements Serializable {
    private String projectStateDesc;

    /**
     * 业务逻辑处理类
     *
     * @mbggenerated
     */
    private String refObjectType;

    private Long updatedTime;

    private Integer orderby;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table def_project_status
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    @CodeFieldNotes("")
    public String getProjectStateDesc() {
        return projectStateDesc;
    }

    public void setProjectStateDesc(String projectStateDesc) {
        this.projectStateDesc = projectStateDesc;
    }

    /**
     * 获取 业务逻辑处理类
     *
     * @return 业务逻辑处理类
     *
     * @mbggenerated
     */
    @CodeFieldNotes("业务逻辑处理类")
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
}