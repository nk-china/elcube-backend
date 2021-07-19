package cn.nkpro.tfms.platform.elasticearch.annotation;

import lombok.Getter;

public enum ESFieldType {
    Text("text"),
    Integer("integer"),
    Long("long"),
    Date("date"),
    Float("float"),
    Double("double"),
    Boolean("boolean"),
    Object("object"),
    Auto(""),
    Nested("nested"),
//    Ip("ip"),
//    Attachment("attachment"),
    Keyword("keyword");

    @Getter
    private String value;

    private ESFieldType(String value) {
        this.value = value;
    }


}