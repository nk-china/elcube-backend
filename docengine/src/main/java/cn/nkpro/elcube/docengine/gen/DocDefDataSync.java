package cn.nkpro.elcube.docengine.gen;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocDefDataSync extends DocDefDataSyncKey implements Serializable {
    private String targetArgs;

    private Integer reExecute;

    private String conditionSpEL;

    private String dataSpEL;

    private String keySpEL;

    private String mappingSpEL;

    private Long updatedTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_doc_def_data_sync
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public String getTargetArgs() {
        return targetArgs;
    }

    public void setTargetArgs(String targetArgs) {
        this.targetArgs = targetArgs;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public Integer getReExecute() {
        return reExecute;
    }

    public void setReExecute(Integer reExecute) {
        this.reExecute = reExecute;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public String getConditionSpEL() {
        return conditionSpEL;
    }

    public void setConditionSpEL(String conditionSpEL) {
        this.conditionSpEL = conditionSpEL;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public String getDataSpEL() {
        return dataSpEL;
    }

    public void setDataSpEL(String dataSpEL) {
        this.dataSpEL = dataSpEL;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public String getKeySpEL() {
        return keySpEL;
    }

    public void setKeySpEL(String keySpEL) {
        this.keySpEL = keySpEL;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public String getMappingSpEL() {
        return mappingSpEL;
    }

    public void setMappingSpEL(String mappingSpEL) {
        this.mappingSpEL = mappingSpEL;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public Long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Long updatedTime) {
        this.updatedTime = updatedTime;
    }
}