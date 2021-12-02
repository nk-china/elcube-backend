/*
 * This file is part of EAsis.
 *
 * EAsis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EAsis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with EAsis.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.easis.security.bo;

import com.alibaba.fastjson.JSON;
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

    private String[] limitIds;
    private String limitQuery;

    private String fromPermissionId;
    private String fromPermissionDesc;

    private String fromGroupId;
    private String fromGroupDesc;

    private String level;

    private Boolean disabled = false;

    private GrantedAuthoritySub subPerm = null;


    public String getDocType(){
        return StringUtils.startsWith(getPermResource(),"@")
                ?getPermResource().substring(1)
                :(StringUtils.equals(getPermResource(),"*")?"*":null);
    }

    public void setDocType(String var0){}

    public void parseSubPerm(String subResource) {
        if(StringUtils.isNotBlank(subResource))
            this.subPerm = JSON.parseObject(subResource, GrantedAuthoritySub.class);
    }

    @Override
    public int compareTo(GrantedAuthority o) {
        return this.level.compareTo(o.level);
    }

    @Override
    public String toString(){
        return JSONObject.toJSONString(this);
    }
}
