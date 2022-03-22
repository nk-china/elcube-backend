package cn.nkpro.elcube.platform.gen;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlatformMenu implements Serializable {
    private String menuId;

    private String title;

    private String subTitle;

    private String parentId;

    private Integer orderBy;

    private String url;

    private String icon;

    private String authorityOptions;

    private Long updatedTime;

    /**
     * 徽标参数
     *
     * @mbggenerated
     */
    private String badgeOption;

    private String menuOptions;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_platform_menu
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public Integer getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Integer orderBy) {
        this.orderBy = orderBy;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public String getAuthorityOptions() {
        return authorityOptions;
    }

    public void setAuthorityOptions(String authorityOptions) {
        this.authorityOptions = authorityOptions;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public Long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Long updatedTime) {
        this.updatedTime = updatedTime;
    }

    /**
     * 获取 徽标参数
     *
     * @return 徽标参数
     *
     * @mbggenerated
     */
    @cn.nkpro.elcube.annotation.CodeFieldNotes("徽标参数")
    public String getBadgeOption() {
        return badgeOption;
    }

    /**
     * 设置 徽标参数
     *
     * @return 徽标参数
     *
     * @mbggenerated
     */
    public void setBadgeOption(String badgeOption) {
        this.badgeOption = badgeOption;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public String getMenuOptions() {
        return menuOptions;
    }

    public void setMenuOptions(String menuOptions) {
        this.menuOptions = menuOptions;
    }
}