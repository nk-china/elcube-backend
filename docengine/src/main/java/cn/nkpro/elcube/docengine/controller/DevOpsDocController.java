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
package cn.nkpro.elcube.docengine.controller;

import cn.nkpro.elcube.annotation.NkNote;
import cn.nkpro.elcube.docengine.NkEqlEngine;
import cn.nkpro.elcube.docengine.gen.DocH;
import cn.nkpro.elcube.docengine.model.DocHQL;
import cn.nkpro.elcube.docengine.model.DocHV;
import cn.nkpro.elcube.docengine.service.NkDocEngineFrontService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by bean on 2020/7/17.
 */
@NkNote("22.[DevOps]单据管理")
@RestController
@RequestMapping("/ops/doc")
@PreAuthorize("hasAnyAuthority('*:*','DEVOPS:*','DEVOPS:DOC')")
public class DevOpsDocController {

    @Autowired@SuppressWarnings("all")
    private NkDocEngineFrontService docEngine;

    @Autowired@SuppressWarnings("all")
    private NkEqlEngine eqlEngine;

    @NkNote("1.执行eql")
    @RequestMapping(value = "/eql/execute",method = RequestMethod.POST)
    public List<? extends DocH> execute(@NkNote(value="EQL") @RequestParam("sql") String eql) {
        return eqlEngine.executeEql(eql);
    }

    @NkNote("2.执行select eql")
    @RequestMapping(value = "/eql/find",method = RequestMethod.POST)
    public List<DocHV> find(@NkNote(value="EQL") @RequestParam("sql") String eql) {
        return eqlEngine.execUpdateEql(eql);
    }

    @NkNote("3.执行update eql")
    @RequestMapping(value = "/eql/update",method = RequestMethod.POST)
    public List<DocHV> update(@NkNote(value="EQL") @RequestParam("sql") String eql) {
        return eqlEngine.execUpdateEql(eql);
    }

    @NkNote("4.执行doUpdate eql 并保存单据")
    @RequestMapping(value = "/eql/doUpdate",method = RequestMethod.POST)
    public List<DocHV> doUpdate(
            @NkNote(value="EQL") @RequestParam("sql") String eql,
            @NkNote(value="修改原因") @RequestParam("source") String source) {
        return eqlEngine.doUpdateByEql(eql, source);
    }

    @NkNote("5.批量修改单据")
    @RequestMapping(value = "/update/batch",method = RequestMethod.POST)
    public DocHV update(
            @NkNote(value="单据JSON") @RequestParam("remarks") String remarks,
            @NkNote(value="单据JSON") @RequestBody List<DocHV> docs) {
        throw new RuntimeException("暂不支持");
    }

    @NkNote("6.修改单据")
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public DocHV update(
            @NkNote(value="单据JSON") @RequestParam("remarks") String remarks,
            @NkNote(value="单据JSON") @RequestBody DocHV doc) {
        return docEngine.doUpdateView(doc,remarks);
    }
}
