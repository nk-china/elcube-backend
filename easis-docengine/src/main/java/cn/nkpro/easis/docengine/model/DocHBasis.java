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

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * 单据数据对象，基础格式
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude()
public class DocHBasis extends DocHPersistent {

    private String partnerName;

    private String docTypeDesc;

    private String docStateDesc;

    private DocDefHV def;

    private Map<String,Object> data;

    DocHBasis() {
        super();
        this.data       = new HashMap<>();
    }

//    @Override
//    protected Object clone() throws CloneNotSupportedException {
//        DocHBasis clone = (DocHBasis) super.clone();
//        clone.setPartnerName(partnerName);
//        clone.setDocTypeDesc(docTypeDesc);
//        clone.setDocStateDesc(docStateDesc);
//        clone.setDef(def);
//        clone.data     = new HashMap<>();
//        data.forEach((k,v)-> clone.data.put(k,BeanUtilz.cloneWithFastjson(v)));
//
//        return clone;
//    }
}
