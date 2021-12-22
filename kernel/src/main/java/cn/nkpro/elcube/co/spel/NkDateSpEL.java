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

import cn.nkpro.elcube.utils.DateTimeUtilz;
import org.springframework.stereotype.Component;

/**
 * @date:2021/12/22
 * @author :zxc
 * @description: 1、可以获取当前日期返回秒
 */
@Component("SpELdate")
public class NkDateSpEL implements NkSpELInjection {

    /**
     * @description：获取当前日期返回秒
     * @return
     */
    @SuppressWarnings("unused")
    public Object now(){
        return DateTimeUtilz.todaySeconds();
    }

}
