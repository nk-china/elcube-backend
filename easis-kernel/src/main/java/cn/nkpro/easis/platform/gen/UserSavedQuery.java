package cn.nkpro.easis.platform.gen;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSavedQuery implements Serializable {
    private String id;

    private String name;

    private String userId;

    private String source;

    private String json;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_user_saved_query
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    @cn.nkpro.easis.annotation.CodeFieldNotes("")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @cn.nkpro.easis.annotation.CodeFieldNotes("")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @cn.nkpro.easis.annotation.CodeFieldNotes("")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @cn.nkpro.easis.annotation.CodeFieldNotes("")
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @cn.nkpro.easis.annotation.CodeFieldNotes("")
    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}