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
package cn.nkpro.easis.platform.service;


import cn.nkpro.easis.basic.PageList;
import cn.nkpro.easis.co.NkScriptV;
import cn.nkpro.easis.platform.gen.PlatformScript;
import cn.nkpro.easis.platform.gen.PlatformScriptWithBLOBs;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by bean on 2020/7/17.
 */
public interface NkScriptManager extends ApplicationListener<ApplicationEvent> {

    PageList<PlatformScript> getPage(String keyword,
                                     String type,
                                     String version,
                                     String state,
                                     int from,
                                     int rows,
                                     String orderField,
                                     String order);

    List<PlatformScriptWithBLOBs> getActiveResources();

//    PlatformScriptWithBLOBs getLastVersion(String scriptName);

    PlatformScript getScript(String scriptName, String version);

    @Transactional
    PlatformScript doRun(NkScriptV scriptDefH, boolean run);

    @Transactional
    void doDelete(NkScriptV scriptDefH);

    @Transactional
    PlatformScript doActive(NkScriptV scriptDefH, boolean force);

    @Transactional
    PlatformScript doBreach(NkScriptV scriptDefH);

    @Transactional
    PlatformScript doUpdate(NkScriptV scriptDefH, boolean force);
//
//    @Transactional
//    DefScript update(DefScript script);
//
//    String getClassName(String beanName);
//
//    DefScript getScriptByName(String scriptName);
}
