package cn.nkpro.tfms.platform.custom;

import lombok.Getter;

public enum EnumDocClassify {

    PROJECT     ("业务",       true),
    TRANSACTION ("单据",       false),
    PARTNER     ("伙伴角色",    true),
    PARTNER_T   ("伙伴单据",    false),


//    FUND        ("资金业务",    true),
//    ASSET       ("资产业务",    true),
//    PRODUCT     ("金融产品",    false),
    ;

    @Getter
    private String desc;
    @Getter
    private boolean isContainer;

    EnumDocClassify(String desc, boolean isContainer){
        this.desc = desc;
        this.isContainer = isContainer;
    }

    public boolean is(String classify) {
        return this.name().equals(classify);
    }
}