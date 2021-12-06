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
package cn.nkpro.elcard.docengine.controller;

import cn.nkpro.elcard.docengine.NkDocSearchService;
import cn.nkpro.elcard.docengine.service.impl.NkDocEngineIndexService;
import cn.nkpro.elcard.data.elasticearch.SearchEngine;
import cn.nkpro.elcard.annotation.NkNote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by bean on 2020/7/17.
 */
@NkNote("22.[DevOps]索引管理")
@RestController
@RequestMapping("/ops/datasync")
@PreAuthorize("hasAnyAuthority('*:*','DEVOPS:*','DEVOPS:DATASYNC')")
public class DevOpsDataSyncController {

    @Autowired@SuppressWarnings("all")
    private SearchEngine searchEngine;

    @Autowired
    private NkDocSearchService searchService;
    @Autowired
    private NkDocEngineIndexService docEngineIndexService;

    @NkNote("1.初始化索引")
    @RequestMapping(value = "/init")
    public void init() throws IOException {
        searchService.dropAndInit();
    }

    @NkNote("2.立即执行同步")
    @RequestMapping(value = "/redo")
    public String reIndex(Boolean dropFirst, String docType) throws IOException {
        String asyncTaskId = UUID.randomUUID().toString();
        docEngineIndexService.reindex(asyncTaskId, dropFirst, docType);
        return asyncTaskId;
    }

    @NkNote("3.获取同步信息")
    @RequestMapping(value = "/redo/{asyncTaskId}")
    public Object reIndex(@PathVariable String asyncTaskId) {
        return docEngineIndexService.getReindexInfo(asyncTaskId);
    }
}
