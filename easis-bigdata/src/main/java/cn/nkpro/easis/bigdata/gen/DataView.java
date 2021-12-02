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
package cn.nkpro.easis.bigdata.gen;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataView implements Serializable {
    private String id;

    private String name;

    private Integer width;

    private Integer height;

    private String theme;

    private String accountId;

    private Integer shared;

    private Long updatedTime;

    private Integer orderBy;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_data_view
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    @cn.nkpro.easis.annotation.CodeFieldNotes("")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @cn.nkpro.easis.annotation.CodeFieldNotes("")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @cn.nkpro.easis.annotation.CodeFieldNotes("")
    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    @cn.nkpro.easis.annotation.CodeFieldNotes("")
    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    @cn.nkpro.easis.annotation.CodeFieldNotes("")
    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    @cn.nkpro.easis.annotation.CodeFieldNotes("")
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @cn.nkpro.easis.annotation.CodeFieldNotes("")
    public Integer getShared() {
        return shared;
    }

    public void setShared(Integer shared) {
        this.shared = shared;
    }

    @cn.nkpro.easis.annotation.CodeFieldNotes("")
    public Long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Long updatedTime) {
        this.updatedTime = updatedTime;
    }

    @cn.nkpro.easis.annotation.CodeFieldNotes("")
    public Integer getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Integer orderBy) {
        this.orderBy = orderBy;
    }
}