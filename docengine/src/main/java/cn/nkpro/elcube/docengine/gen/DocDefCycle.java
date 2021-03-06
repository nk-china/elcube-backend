package cn.nkpro.elcube.docengine.gen;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocDefCycle extends DocDefCycleKey implements Serializable {
    private Integer orderBy;

    private String refObjectType;

    private Long updatedTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_doc_def_cycle
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public Integer getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Integer orderBy) {
        this.orderBy = orderBy;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public String getRefObjectType() {
        return refObjectType;
    }

    public void setRefObjectType(String refObjectType) {
        this.refObjectType = refObjectType;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public Long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Long updatedTime) {
        this.updatedTime = updatedTime;
    }
}