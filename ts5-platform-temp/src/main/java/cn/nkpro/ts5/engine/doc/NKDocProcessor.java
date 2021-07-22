package cn.nkpro.ts5.engine.doc;

import cn.nkpro.ts5.basic.NKCustomObject;
import lombok.Getter;

public interface NKDocProcessor extends NKCustomObject {
    enum EnumDocClassify {

        PARTNER     ("伙伴"),
        TRANSACTION ("交易")
        ;

        @Getter
        private String desc;

        EnumDocClassify(String desc){
            this.desc = desc;
        }

        public boolean is(String classify) {
            return this.name().equals(classify);
        }
    }

    EnumDocClassify classify();
}
