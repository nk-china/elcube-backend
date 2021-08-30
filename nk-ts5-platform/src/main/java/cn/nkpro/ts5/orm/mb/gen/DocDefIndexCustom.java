package cn.nkpro.ts5.orm.mb.gen;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocDefIndexCustom extends DocDefIndexCustomKey implements Serializable {
    private String conditionSpEL;

    private String dataSpEL;

    private String keySpEL;

    private String mappingSpEL;

    private Long updatedTime;

    private Integer orderBy;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_doc_def_index_custom
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    @cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes("")
    public String getConditionSpEL() {
        return conditionSpEL;
    }

    public void setConditionSpEL(String conditionSpEL) {
        this.conditionSpEL = conditionSpEL;
    }

    @cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes("")
    public String getDataSpEL() {
        return dataSpEL;
    }

    public void setDataSpEL(String dataSpEL) {
        this.dataSpEL = dataSpEL;
    }

    @cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes("")
    public String getKeySpEL() {
        return keySpEL;
    }

    public void setKeySpEL(String keySpEL) {
        this.keySpEL = keySpEL;
    }

    @cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes("")
    public String getMappingSpEL() {
        return mappingSpEL;
    }

    public void setMappingSpEL(String mappingSpEL) {
        this.mappingSpEL = mappingSpEL;
    }

    @cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes("")
    public Long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Long updatedTime) {
        this.updatedTime = updatedTime;
    }

    @cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes("")
    public Integer getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Integer orderBy) {
        this.orderBy = orderBy;
    }
}