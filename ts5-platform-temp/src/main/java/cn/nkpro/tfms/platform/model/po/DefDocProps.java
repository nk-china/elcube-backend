package cn.nkpro.tfms.platform.model.po;

import cn.nkpro.ts5.basic.wsdoc.annotation.CodeFieldNotes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DefDocProps implements Serializable {
    private String id;

    private String key;

    private String value;

    private String docId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table def_doc_props
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
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @CodeFieldNotes("")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @CodeFieldNotes("")
    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
}