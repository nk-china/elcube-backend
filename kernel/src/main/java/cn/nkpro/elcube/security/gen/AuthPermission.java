/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.security.gen;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthPermission implements Serializable {
    private String permId;

    private String permDesc;

    /**
     * 资源，如单据 D:ZR01 
     *
     * @mbggenerated
     */
    private String permResource;

    /**
     * 操作 CRUD
     *
     * @mbggenerated
     */
    private String permOperate;

    /**
     * 限制，主要针对数据权限控制
     *
     * @mbggenerated
     */
    private String limitId;

    private String permLevel;

    /**
     * 子资源，如卡片
     *
     * @mbggenerated
     */
    private String subResource;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_auth_permission
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public String getPermId() {
        return permId;
    }

    public void setPermId(String permId) {
        this.permId = permId;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public String getPermDesc() {
        return permDesc;
    }

    public void setPermDesc(String permDesc) {
        this.permDesc = permDesc;
    }

    /**
     * 获取 资源，如单据 D:ZR01 
     *
     * @return 资源，如单据 D:ZR01 
     *
     * @mbggenerated
     */
    @cn.nkpro.elcube.annotation.CodeFieldNotes("资源，如单据 D:ZR01 ")
    public String getPermResource() {
        return permResource;
    }

    /**
     * 设置 资源，如单据 D:ZR01 
     *
     * @return 资源，如单据 D:ZR01 
     *
     * @mbggenerated
     */
    public void setPermResource(String permResource) {
        this.permResource = permResource;
    }

    /**
     * 获取 操作 CRUD
     *
     * @return 操作 CRUD
     *
     * @mbggenerated
     */
    @cn.nkpro.elcube.annotation.CodeFieldNotes("操作 CRUD")
    public String getPermOperate() {
        return permOperate;
    }

    /**
     * 设置 操作 CRUD
     *
     * @return 操作 CRUD
     *
     * @mbggenerated
     */
    public void setPermOperate(String permOperate) {
        this.permOperate = permOperate;
    }

    /**
     * 获取 限制，主要针对数据权限控制
     *
     * @return 限制，主要针对数据权限控制
     *
     * @mbggenerated
     */
    @cn.nkpro.elcube.annotation.CodeFieldNotes("限制，主要针对数据权限控制")
    public String getLimitId() {
        return limitId;
    }

    /**
     * 设置 限制，主要针对数据权限控制
     *
     * @return 限制，主要针对数据权限控制
     *
     * @mbggenerated
     */
    public void setLimitId(String limitId) {
        this.limitId = limitId;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public String getPermLevel() {
        return permLevel;
    }

    public void setPermLevel(String permLevel) {
        this.permLevel = permLevel;
    }

    /**
     * 获取 子资源，如卡片
     *
     * @return 子资源，如卡片
     *
     * @mbggenerated
     */
    @cn.nkpro.elcube.annotation.CodeFieldNotes("子资源，如卡片")
    public String getSubResource() {
        return subResource;
    }

    /**
     * 设置 子资源，如卡片
     *
     * @return 子资源，如卡片
     *
     * @mbggenerated
     */
    public void setSubResource(String subResource) {
        this.subResource = subResource;
    }
}