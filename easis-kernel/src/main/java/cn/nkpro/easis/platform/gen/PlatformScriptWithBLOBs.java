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
package cn.nkpro.easis.platform.gen;

import java.io.Serializable;

public class PlatformScriptWithBLOBs extends PlatformScript implements Serializable {
    /**
     * 组件的服务类入口
     *
     * @mbggenerated
     */
    private String groovyMain;

    /**
     * 组件的依赖类集合
     *
     * @mbggenerated
     */
    private String groovyRefs;

    /**
     * 组件的数据界面入口
     *
     * @mbggenerated
     */
    private String vueMain;

    /**
     * 组件的配置界面入口
     *
     * @mbggenerated
     */
    private String vueDefs;

    /**
     * 组件的依赖界面组件集合
     *
     * @mbggenerated
     */
    private String vueComponents;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_platform_script
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取 组件的服务类入口
     *
     * @return 组件的服务类入口
     *
     * @mbggenerated
     */
    @cn.nkpro.easis.annotation.CodeFieldNotes("组件的服务类入口")
    public String getGroovyMain() {
        return groovyMain;
    }

    /**
     * 设置 组件的服务类入口
     *
     * @return 组件的服务类入口
     *
     * @mbggenerated
     */
    public void setGroovyMain(String groovyMain) {
        this.groovyMain = groovyMain;
    }

    /**
     * 获取 组件的依赖类集合
     *
     * @return 组件的依赖类集合
     *
     * @mbggenerated
     */
    @cn.nkpro.easis.annotation.CodeFieldNotes("组件的依赖类集合")
    public String getGroovyRefs() {
        return groovyRefs;
    }

    /**
     * 设置 组件的依赖类集合
     *
     * @return 组件的依赖类集合
     *
     * @mbggenerated
     */
    public void setGroovyRefs(String groovyRefs) {
        this.groovyRefs = groovyRefs;
    }

    /**
     * 获取 组件的数据界面入口
     *
     * @return 组件的数据界面入口
     *
     * @mbggenerated
     */
    @cn.nkpro.easis.annotation.CodeFieldNotes("组件的数据界面入口")
    public String getVueMain() {
        return vueMain;
    }

    /**
     * 设置 组件的数据界面入口
     *
     * @return 组件的数据界面入口
     *
     * @mbggenerated
     */
    public void setVueMain(String vueMain) {
        this.vueMain = vueMain;
    }

    /**
     * 获取 组件的配置界面入口
     *
     * @return 组件的配置界面入口
     *
     * @mbggenerated
     */
    @cn.nkpro.easis.annotation.CodeFieldNotes("组件的配置界面入口")
    public String getVueDefs() {
        return vueDefs;
    }

    /**
     * 设置 组件的配置界面入口
     *
     * @return 组件的配置界面入口
     *
     * @mbggenerated
     */
    public void setVueDefs(String vueDefs) {
        this.vueDefs = vueDefs;
    }

    /**
     * 获取 组件的依赖界面组件集合
     *
     * @return 组件的依赖界面组件集合
     *
     * @mbggenerated
     */
    @cn.nkpro.easis.annotation.CodeFieldNotes("组件的依赖界面组件集合")
    public String getVueComponents() {
        return vueComponents;
    }

    /**
     * 设置 组件的依赖界面组件集合
     *
     * @return 组件的依赖界面组件集合
     *
     * @mbggenerated
     */
    public void setVueComponents(String vueComponents) {
        this.vueComponents = vueComponents;
    }
}