package cn.nkpro.ts5.docengine.standard;

import lombok.Data;

@Data
public class NkStandardDefField {
    private String key;
    private String label;
    private String type;
    private Boolean visible;
    private Boolean readonly;
    private String[] spELTriggers;
    private String spELContent;
    private int calcOrder;
    private String calcTrigger;
    private Object options;
}