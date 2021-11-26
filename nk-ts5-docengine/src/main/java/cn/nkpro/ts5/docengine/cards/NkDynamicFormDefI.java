package cn.nkpro.ts5.docengine.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class NkDynamicFormDefI {
    private String key;
    private String name;
    private String inputType;
    private Integer col;
    private Boolean calcTrigger;

    private Integer control;
    private String spELControl;

    private Integer calcOrder;
    private String[] spELTriggers;
    private String spELContent;

    private Boolean required;
    private String message;

    private String indexName;

    private Map<String,Object> inputOptions;
}