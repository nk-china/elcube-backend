package cn.nkpro.ts5.engine.doc.model;

import lombok.Data;

@Data
public class CardDescribe {
    private String cardHandler;
    private String cardName;
    private String dataComponentName;
    private String[] defComponentNames;
}
