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
package cn.nkpro.elcube.components.defaults.services

import cn.nkpro.elcube.annotation.NkNote
import cn.nkpro.elcube.co.remote.NkAbstractRemoteAdapter
import org.springframework.stereotype.Component

@NkNote("演示远程调用")
@Component("NkSimpleRemoteAdapterImpl")
class NkSimpleRemoteAdapterImpl extends NkAbstractRemoteAdapter<Map,Map>{
    @Override
    Map<String,Object> doApply(Map options) {

        if(options!=null){

            Map clone = new HashMap(options)
            clone.put("age",30)
            return clone
        }else{

            return Collections.singletonMap("age", 31)

        }
    }
}
