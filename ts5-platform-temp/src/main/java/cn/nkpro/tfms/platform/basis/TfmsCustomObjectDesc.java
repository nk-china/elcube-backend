package cn.nkpro.tfms.platform.basis;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class TfmsCustomObjectDesc {

    private String name;
    private String key;

    public TfmsCustomObjectDesc(String key, String name){
        this.key = key;
        this.name = StringUtils.defaultIfBlank(name,key);
    }
}