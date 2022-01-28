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
package cn.nkpro.elcube.docengine.service.impl;

import cn.nkpro.elcube.co.easy.EasySingle;
import cn.nkpro.elcube.docengine.NkDocEngine;
import cn.nkpro.elcube.docengine.gen.DocH;
import cn.nkpro.elcube.docengine.model.DocHV;
import cn.nkpro.elcube.platform.service.NkAbstractDocOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: wf
 * @Description 单据操作实现类
 * @date 2022/1/14 10:32
 */
@Service
public class NkDocOperationServiceImpl extends NkAbstractDocOperation {

    @Autowired@SuppressWarnings("all")
    private NkDocEngine docEngine;

    /**
     * 创建客户单据，补充手机号、openId、appleId等信息
     * @param obj
     * @return
     */
    @Override
    public Object createDoc(Object obj) {
        String docType = "TP02";
        Map<String,String> map = (HashMap) obj;
        DocHV doc = docEngine.create(docType,null,"移动端创建客户", (docHv) ->{
            docHv.setDocName(map.get("realname"));
            EasySingle cusinfoSingle = docHv.fetch("cusBaseInfo");
            cusinfoSingle.set("cusPhone",map.get("phone"));

            EasySingle accountSingle = docHv.fetch("cusAccount");
            accountSingle.set("openId",map.get("openId"));
            accountSingle.set("appleId",map.get("appleId"));
        });
        docEngine.doUpdate(doc.getDocId(),"移动端创建客户", (d)->{});
        return doc;
    }

    @Override
    public List<Object> getDocByDocContent(Map map,String... docType) {
        List<DocH> re = docEngine.find(docType).dynamicEquals(map).listResult();
        List<Object> list = new ArrayList<>();
        list.addAll(re);
        return list;
    }
}
