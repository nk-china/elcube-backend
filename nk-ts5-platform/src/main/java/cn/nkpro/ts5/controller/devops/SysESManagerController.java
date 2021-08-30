package cn.nkpro.ts5.controller.devops;

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.engine.doc.service.NkDocEngineFrontService;
import cn.nkpro.ts5.engine.elasticearch.ESManager;
import cn.nkpro.ts5.engine.elasticearch.SearchEngine;
import cn.nkpro.ts5.engine.elasticearch.model.DocHES;
import cn.nkpro.ts5.orm.mb.gen.DocH;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by bean on 2020/7/17.
 */
@WsDocNote("22.[DevOps]索引管理")
@RestController
@RequestMapping("/ops/es")
@PreAuthorize("hasAnyAuthority('*:*','DEVOPS:*','DEVOPS:ES')")
public class SysESManagerController {

    @Autowired@SuppressWarnings("all")
    private SearchEngine searchEngine;

    @Autowired
    private ESManager esManager;
    @Autowired
    private NkDocEngineFrontService docEngineFrontService;

    @WsDocNote("1.初始化索引")
    @RequestMapping(value = "/init")
    public void init() throws IOException {
        esManager.dropAndInit();
    }

    @WsDocNote("2.重建单据索引")
    @RequestMapping(value = "/docs/reindex")
    public void reIndex(Boolean dropFirst, String docType) throws IOException {

        if(dropFirst){
            esManager.dropAndInit();
        }

        int offset = 0;
        int rows   = 1000;
        List<DocH> list;
        while((list = docEngineFrontService.list(docType, offset, rows, null)).size()>0){
            searchEngine.indexBeforeCommit(list.stream()
                    .map(doc->DocHES.from(docEngineFrontService.detail(doc.getDocId())))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
            offset += rows;
        }
    }

    @WsDocNote("2.重建任务索引")
    @RequestMapping(value = "/tasks/reindex")
    public void reIndexTasks(Boolean dropFirst, String docType) throws IOException {

    }
}
