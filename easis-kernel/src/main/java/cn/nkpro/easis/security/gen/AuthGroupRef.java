package cn.nkpro.easis.security.gen;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthGroupRef implements Serializable {
    private String groupId;

    private String refId;

    private Integer refType;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_auth_group_ref
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    @cn.nkpro.easis.annotation.CodeFieldNotes("")
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @cn.nkpro.easis.annotation.CodeFieldNotes("")
    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    @cn.nkpro.easis.annotation.CodeFieldNotes("")
    public Integer getRefType() {
        return refType;
    }

    public void setRefType(Integer refType) {
        this.refType = refType;
    }
}