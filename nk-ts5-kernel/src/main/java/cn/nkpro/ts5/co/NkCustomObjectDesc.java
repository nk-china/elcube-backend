package cn.nkpro.ts5.co;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class NkCustomObjectDesc {

    private String name;
    private String key;

    public NkCustomObjectDesc(String key, String name){
        this.key = key;
        this.name = StringUtils.defaultIfBlank(name,key);
    }
}