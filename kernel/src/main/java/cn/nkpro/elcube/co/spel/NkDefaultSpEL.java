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
package cn.nkpro.elcube.co.spel;

import cn.nkpro.elcube.platform.service.PlatformRegistryService;
import cn.nkpro.elcube.security.SecurityUtilz;
import cn.nkpro.elcube.security.UserBusinessAdapter;
import cn.nkpro.elcube.security.bo.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("SpELnk")
public class NkDefaultSpEL implements NkSpELInjection {

    @Autowired@SuppressWarnings("all")
    private PlatformRegistryService registryService;
    @Autowired@SuppressWarnings("all")
    private UserBusinessAdapter userBusinessAdapter;

    /**
     * 推荐使用 @dict.json()
     * @param key key
     * @return json
     */
    @SuppressWarnings("unused")
    @Deprecated
    public Object dict(String key){
        return registryService.getJSON("@DICT",key);
    }

    @SuppressWarnings("unused")
    public String account(){
        return SecurityUtilz.getUser().getObjectId();
    }

    @SuppressWarnings("unused")
    public String realname(){
        return SecurityUtilz.getUser().getRealname();
    }

    @SuppressWarnings("unused")
    public UserDetails user(){
        return SecurityUtilz.getUser();
    }

    @SuppressWarnings("unused")
    public Object me(){
        return userBusinessAdapter.getUser(SecurityUtilz.getUser());
    }
}
