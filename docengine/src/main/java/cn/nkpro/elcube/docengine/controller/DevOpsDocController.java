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
import cn.nkpro.elcube.docengine.model.DocHV;
import cn.nkpro.elcube.docengine.service.NkDocEngineFrontService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @NkNote("A、修改单据")
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public DocHV update(
            @NkNote(value="单据JSON") @RequestParam("remarks") String remarks,
            @NkNote(value="单据JSON") @RequestBody DocHV doc) {
        return docEngine.doUpdateView(doc,remarks);
    }
}
