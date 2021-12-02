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

import cn.nkpro.easis.platform.gen.PlatformMenu;
import cn.nkpro.easis.platform.model.WebMenuBO;

import java.util.List;

/**
 * Created by bean on 2020/1/3.
 */
public interface WebMenuService {
    List<WebMenuBO> getMenus(boolean filterAuth);

    PlatformMenu getDetail(String id);

    void doUpdate(List<WebMenuBO> menus);
}
