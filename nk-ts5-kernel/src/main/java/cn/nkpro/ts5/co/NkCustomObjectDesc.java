package cn.nkpro.ts5.co;

import cn.nkpro.ts5.Keep;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Keep
@Data
public class NkCustomObjectDesc {

    private String name;
    private String key;

    public NkCustomObjectDesc(String key, String name){
        this.key = key;
        this.name = StringUtils.defaultIfBlank(name,key);
    }
}