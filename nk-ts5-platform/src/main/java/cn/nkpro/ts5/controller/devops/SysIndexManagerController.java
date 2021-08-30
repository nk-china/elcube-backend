package cn.nkpro.ts5.controller.devops;

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.engine.doc.impl.NkDocEngineIndexService;
import cn.nkpro.ts5.engine.elasticearch.SearchService;
import cn.nkpro.ts5.engine.elasticearch.SearchEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;
import java.util.Map;

/**
 * Created by bean on 2020/7/17.
 */
@WsDocNote("22.[DevOps]索引管理")
@RestController
@RequestMapping("/ops/index")
@PreAuthorize("hasAnyAuthority('*:*','DEVOPS:*','DEVOPS:ES')")
public class SysIndexManagerController {

    @Autowired@SuppressWarnings("all")
    private SearchEngine searchEngine;

    @Autowired
    private SearchService searchService;
    @Autowired
    private NkDocEngineIndexService docEngineIndexService;

    @WsDocNote("1.初始化索引")
    @RequestMapping(value = "/init")
    public void init() throws IOException {
        searchService.dropAndInit();
    }

    @WsDocNote("2.重建单据索引")
    @RequestMapping(value = "/docs/reindex")
    public String reIndex(Boolean dropFirst, String docType) throws IOException {
        String asyncTaskId = UUID.randomUUID().toString();
        docEngineIndexService.reindex(asyncTaskId, dropFirst, docType);
        return asyncTaskId;
    }

    @WsDocNote("3.获取重建单据索引信息")
    @RequestMapping(value = "/docs/reindex/{asyncTaskId}")
    public Map<String,String> reIndex(@PathVariable String asyncTaskId) {
        return docEngineIndexService.getReindexInfo(asyncTaskId);
    }
}
