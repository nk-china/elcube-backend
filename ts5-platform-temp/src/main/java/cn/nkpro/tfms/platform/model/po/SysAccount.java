package cn.nkpro.tfms.platform.model.po;

import cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysAccount implements Serializable {
    private String id;

    private String username;

    private String password;

    private Integer locked;

    private String validFrom;

    private String validTo;

    private String realname;

    private Long createdTime;

    private Long updatedTime;

    private Long lastActived;

    private String objectId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table sys_account
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    @CodeFieldNotes("")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @CodeFieldNotes("")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @CodeFieldNotes("")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @CodeFieldNotes("")
    public Integer getLocked() {
        return locked;
    }

    public void setLocked(Integer locked) {
        this.locked = locked;
    }

    @CodeFieldNotes("")
    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    @CodeFieldNotes("")
    public String getValidTo() {
        return validTo;
    }

    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }

    @CodeFieldNotes("")
    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
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
    public Long getLastActived() {
        return lastActived;
    }

    public void setLastActived(Long lastActived) {
        this.lastActived = lastActived;
    }

    @CodeFieldNotes("")
    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}