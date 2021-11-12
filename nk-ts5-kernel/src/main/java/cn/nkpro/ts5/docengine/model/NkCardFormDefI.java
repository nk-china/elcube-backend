package cn.nkpro.ts5.docengine.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class NkCardFormDefI {
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
    private String format;
    private String options;
    private Object optionsObject;
    private Float min;
    private Float max;
    private Integer maxLength;
    private Integer digits;
    private Float step;
    private String selectMode;
    private String checked;
    private String unChecked;
    private String modal;
    private String pattern;
    private String message;
}