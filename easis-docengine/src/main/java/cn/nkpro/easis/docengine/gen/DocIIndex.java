package cn.nkpro.easis.docengine.gen;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocIIndex extends DocIIndexKey implements Serializable {
    private String value;

    private String dataType;

    private Integer orderBy;

    private Long updatedTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_doc_i_index
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    @cn.nkpro.easis.annotation.CodeFieldNotes("")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @cn.nkpro.easis.annotation.CodeFieldNotes("")
    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @cn.nkpro.easis.annotation.CodeFieldNotes("")
    public Integer getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Integer orderBy) {
        this.orderBy = orderBy;
    }

    @cn.nkpro.easis.annotation.CodeFieldNotes("")
    public Long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Long updatedTime) {
        this.updatedTime = updatedTime;
    }
}