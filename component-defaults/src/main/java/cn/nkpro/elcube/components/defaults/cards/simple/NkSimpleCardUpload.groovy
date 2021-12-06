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
package cn.nkpro.elcube.components.defaults.cards.simple

import cn.nkpro.elcube.annotation.NkNote
import cn.nkpro.elcube.docengine.NkAbstractCard
import cn.nkpro.elcube.docengine.NkDocEngine
import cn.nkpro.elcube.docengine.model.DocDefHV
import cn.nkpro.elcube.docengine.model.DocDefIV
import cn.nkpro.elcube.docengine.model.DocHV
import com.alibaba.fastjson.JSON
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@NkNote("文件上传演示")
@Component("NkSimpleCardUpload")
class NkSimpleCardUpload extends NkAbstractCard<Map,Map> {

    @Autowired
    NkDocEngine docEngine;

    @Override
    Map afterGetDef(DocDefHV defHV, DocDefIV defIV, Map cardDef) {
        log.debug("DEF:"+JSON.toJSONString(cardDef))
        return cardDef
    }

    @Override
    Map afterCreate(DocHV doc, DocHV preDoc, Map data, DocDefIV defIV, Map cardDef) {
        log.debug("DATA:"+JSON.toJSONString(data))
        return data
    }

    @Override
    Map afterGetData(DocHV doc, Map data, DocDefIV defIV, Map cardDef) {
        log.debug("DATA:"+JSON.toJSONString(data))
        return data
    }

    @Override
    Map beforeUpdate(DocHV doc, Map data, Map original, DocDefIV defIV, Map cardDef) {
        log.debug("DATA:"+JSON.toJSONString(data))
        return data
    }
}
