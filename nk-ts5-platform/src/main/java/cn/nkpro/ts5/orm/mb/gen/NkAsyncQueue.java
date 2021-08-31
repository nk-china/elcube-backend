package cn.nkpro.ts5.orm.mb.gen;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NkAsyncQueue implements Serializable {
    private String asyncId;

    private String asyncObjectRef;

    private Integer asyncRetry;

    private Integer asyncLimit;

    private String asyncRule;

    private String asyncState;

    private String asyncNext;

    private Long createdTime;

    private Long updatedTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_async_queue
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    @cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes("")
    public String getAsyncId() {
        return asyncId;
    }

    public void setAsyncId(String asyncId) {
        this.asyncId = asyncId;
    }

    @cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes("")
    public String getAsyncObjectRef() {
        return asyncObjectRef;
    }

    public void setAsyncObjectRef(String asyncObjectRef) {
        this.asyncObjectRef = asyncObjectRef;
    }

    @cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes("")
    public Integer getAsyncRetry() {
        return asyncRetry;
    }

    public void setAsyncRetry(Integer asyncRetry) {
        this.asyncRetry = asyncRetry;
    }

    @cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes("")
    public Integer getAsyncLimit() {
        return asyncLimit;
    }

    public void setAsyncLimit(Integer asyncLimit) {
        this.asyncLimit = asyncLimit;
    }

    @cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes("")
    public String getAsyncRule() {
        return asyncRule;
    }

    public void setAsyncRule(String asyncRule) {
        this.asyncRule = asyncRule;
    }

    @cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes("")
    public String getAsyncState() {
        return asyncState;
    }

    public void setAsyncState(String asyncState) {
        this.asyncState = asyncState;
    }

    @cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes("")
    public String getAsyncNext() {
        return asyncNext;
    }

    public void setAsyncNext(String asyncNext) {
        this.asyncNext = asyncNext;
    }

    @cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes("")
    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    @cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes("")
    public Long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Long updatedTime) {
        this.updatedTime = updatedTime;
    }
}