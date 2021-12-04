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
package cn.nkpro.easis.docengine.model;

import cn.nkpro.easis.co.DebugAble;
import cn.nkpro.easis.docengine.gen.DocH;
import cn.nkpro.easis.docengine.gen.DocI;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * 单据数据对象，缓存到redis的单据数据格式
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude()
public class DocHPersistent extends DocH implements DebugAble {

    private Map<String, DocI> items;

    private Map<String,Object> dynamics;

    private boolean debug;

    DocHPersistent() {
        this.items      = new HashMap<>();
        this.dynamics   = new HashMap<>();
    }

//    @Override
//    protected Object clone() throws CloneNotSupportedException {
//        DocHPersistent clone = (DocHPersistent) super.clone();
//        clone.items    = items;
//        clone.dynamics = new HashMap<>(dynamics);
//        return clone;
//    }
}
