/*
 * This file is part of ELCard.
 *
 * ELCard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcard.platform.service;

import cn.nkpro.elcard.platform.gen.PlatformRegistry;
import cn.nkpro.elcard.platform.service.impl.PlatformRegistryServiceImpl;

import java.util.List;

public interface PlatformRegistryService {

    Object getJSON(String regType, String regKey);

    String getString(String regType, String regKey);

    List<Object> getList(String regType, String regKeyPrefix);

    PlatformRegistry getValue(String key);

    void updateValue(PlatformRegistry registry);

    void deleteValue(PlatformRegistry registry);

    List<PlatformRegistry> getAllByType(String type);

    List<PlatformRegistryServiceImpl.TreeNode> getTree();

    void doUpdate(List<PlatformRegistry> list);
}
