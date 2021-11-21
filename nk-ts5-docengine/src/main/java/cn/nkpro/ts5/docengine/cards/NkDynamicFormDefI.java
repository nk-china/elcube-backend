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
    private String calcTrigger;
    private Integer calcOrder;
    private Integer col;
    private Boolean required;
    private Integer control;
    private String spELControl;
    private String spELContent;
    private String[] spELTriggers;

    private Map<String,Object> inputOptions;
}