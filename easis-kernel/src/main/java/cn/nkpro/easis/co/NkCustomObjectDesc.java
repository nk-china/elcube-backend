package cn.nkpro.easis.co;

import cn.nkpro.easis.annotation.Keep;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Keep
@Data
public class NkCustomObjectDesc {

    private String name;
    private String key;
    private int order;

    public NkCustomObjectDesc(String key, String name){
        this.key = key;
        this.name = StringUtils.defaultIfBlank(name,key);
    }

    public NkCustomObjectDesc(String key, String name, int order){
        this.key = key;
        this.name = StringUtils.defaultIfBlank(name,key);
        this.order = order;
    }

    public String getValue(){
        return this.key;
    }

    public String getLabel(){
        return this.name;
    }
}