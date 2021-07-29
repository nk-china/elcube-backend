package cn.nkpro.ts5.basic;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class NKCustomObjectDesc {

    private String name;
    private String key;

    public NKCustomObjectDesc(String key, String name){
        this.key = key;
        this.name = StringUtils.defaultIfBlank(name,key);
    }
}