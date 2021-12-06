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
package cn.nkpro.elcube.platform.model;

import cn.nkpro.elcube.platform.gen.PlatformMenu;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by bean on 2020/1/3.
 */

public class WebMenuBO extends PlatformMenu {

    @Getter@Setter
    private List<PlatformMenu> children;
}
