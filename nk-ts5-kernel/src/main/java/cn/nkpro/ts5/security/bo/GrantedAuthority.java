package cn.nkpro.ts5.security.bo;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GrantedAuthority implements org.springframework.security.core.GrantedAuthority,Comparable<GrantedAuthority> {

    private static final long serialVersionUID  = 521L;

    public static final Integer LEVEL_SINGLE    = 0x020000;
    public static final Integer LEVEL_MULTIPLE  = 0x040000;
    public static final Integer LEVEL_ALL       = 0x080000;
    public static final Integer LEVEL_SUPER     = 0x100000;

    public static final Integer LEVEL_LIMIT     = 0x010000;

    private String authority;

    private String permResource;

    private String permOperate;

    private String subResource;

    private String[] limitIds;
    private String limitQuery;

    private String fromPermissionId;
    private String fromPermissionDesc;

    private String fromGroupId;
    private String fromGroupDesc;

    private String level;

    private Boolean disabled;


    public String getDocType(){
        return StringUtils.startsWith(getPermResource(),"@")
                ?getPermResource().substring(1)
                :(StringUtils.equals(getPermResource(),"*")?"*":null);
    }

    public void setDocType(String var0){}


    @Override
    public int compareTo(GrantedAuthority o) {
        return this.level.compareTo(o.level);
    }

    @Override
    public String toString(){
        return JSONObject.toJSONString(this);
    }
}
