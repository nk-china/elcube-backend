package cn.nkpro.elcube.platform.gen;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlatformScript extends PlatformScriptKey implements Serializable {
    private String scriptType;

    private String scriptDesc;

    /**
     * 组件描述
     *
     * @mbggenerated
     */
    private String scriptDoc;

    /**
     * Active Version, boolean Value, 1 or 0
     *
     * @mbggenerated
     */
    private String state;

    /**
     * 服务类MD5值
     *
     * @mbggenerated
     */
    private String groovyMd5;

    /**
     * ref User ID
     *
     * @mbggenerated
     */
    private String owner;

    private Long createdTime;

    private Long updatedTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_platform_script
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public String getScriptType() {
        return scriptType;
    }

    public void setScriptType(String scriptType) {
        this.scriptType = scriptType;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public String getScriptDesc() {
        return scriptDesc;
    }

    public void setScriptDesc(String scriptDesc) {
        this.scriptDesc = scriptDesc;
    }

    /**
     * 获取 组件描述
     *
     * @return 组件描述
     *
     * @mbggenerated
     */
    @cn.nkpro.elcube.annotation.CodeFieldNotes("组件描述")
    public String getScriptDoc() {
        return scriptDoc;
    }

    /**
     * 设置 组件描述
     *
     * @return 组件描述
     *
     * @mbggenerated
     */
    public void setScriptDoc(String scriptDoc) {
        this.scriptDoc = scriptDoc;
    }

    /**
     * 获取 Active Version, boolean Value, 1 or 0
     *
     * @return Active Version, boolean Value, 1 or 0
     *
     * @mbggenerated
     */
    @cn.nkpro.elcube.annotation.CodeFieldNotes("Active Version, boolean Value, 1 or 0")
    public String getState() {
        return state;
    }

    /**
     * 设置 Active Version, boolean Value, 1 or 0
     *
     * @return Active Version, boolean Value, 1 or 0
     *
     * @mbggenerated
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * 获取 服务类MD5值
     *
     * @return 服务类MD5值
     *
     * @mbggenerated
     */
    @cn.nkpro.elcube.annotation.CodeFieldNotes("服务类MD5值")
    public String getGroovyMd5() {
        return groovyMd5;
    }

    /**
     * 设置 服务类MD5值
     *
     * @return 服务类MD5值
     *
     * @mbggenerated
     */
    public void setGroovyMd5(String groovyMd5) {
        this.groovyMd5 = groovyMd5;
    }

    /**
     * 获取 ref User ID
     *
     * @return ref User ID
     *
     * @mbggenerated
     */
    @cn.nkpro.elcube.annotation.CodeFieldNotes("ref User ID")
    public String getOwner() {
        return owner;
    }

    /**
     * 设置 ref User ID
     *
     * @return ref User ID
     *
     * @mbggenerated
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public Long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Long updatedTime) {
        this.updatedTime = updatedTime;
    }
}