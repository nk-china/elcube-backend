package cn.nkpro.ts5.controller;

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.engine.doc.model.DocDefFlowV;
import cn.nkpro.ts5.engine.doc.model.DocHHistory;
import cn.nkpro.ts5.engine.doc.service.NkDocHistoryService;
import cn.nkpro.ts5.engine.elasticearch.SearchService;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.engine.doc.service.NkDocDefService;
import cn.nkpro.ts5.engine.doc.service.NkDocEngineFrontService;
import cn.nkpro.ts5.engine.elasticearch.ESPageList;
import cn.nkpro.ts5.engine.elasticearch.model.DocHES;
import cn.nkpro.ts5.orm.mb.gen.SysLogDocRecord;
import com.alibaba.fastjson.JSONObject;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bean on 2020/6/9.
 */
@PreAuthorize("authenticated")
@WsDocNote("3.单据")
@RestController
@RequestMapping("/doc")
public class DocController {

    @Autowired@SuppressWarnings("all")
    private NkDocDefService docDefService;

    @Autowired@SuppressWarnings("all")
    private NkDocEngineFrontService docEngine;
    @Autowired@SuppressWarnings("all")
    private SearchService searchService;
    @Autowired
    private NkDocHistoryService docHistoryService;
    
    @WsDocNote("1.拉取交易列表数据")
    @RequestMapping(value = "/list",method = RequestMethod.POST)
    public ESPageList<DocHES> list(@RequestBody JSONObject params) {
        return searchService.queryList(DocHES.class, null, params);
    }

    
    @WsDocNote("2.拉取入口单据列表")
    @RequestMapping(value = "/entrance",method = RequestMethod.GET)
    public List<DocDefFlowV> getEntrance(@RequestParam("classify") String classify){
        return docDefService.getEntrance(classify);
    }

    @WsDocNote("3.拉取交易详情")
    @RequestMapping(value = "/detail/{docId}",method = RequestMethod.GET)
    public DocHV get(@PathVariable("docId") String docId) {
        return docEngine.detailView(docId);
    }


    @WsDocNote("4、拉取交易历史版本")
    @RequestMapping(value = "/detail/snapshots/{docId}/{offset}",method = RequestMethod.GET)
    public List<SysLogDocRecord> getHistories(@PathVariable("docId") String docId,@PathVariable("offset") Integer offset) {
        return docHistoryService.getHistories(docId,offset);
    }

    @WsDocNote("5.拉取交易历史版本详情")
    @RequestMapping(value = "/detail/snapshot/{historyId}",method = RequestMethod.GET)
    public DocHHistory getHistory(@PathVariable("historyId") String historyId) {
        return docHistoryService.getDetail(historyId);
    }
    
    @WsDocNote("4、创建新单据")
    @RequestMapping(value = "/pre/create",method = RequestMethod.POST)
    public DocHV preCreate(
            @WsDocNote(value="前序ID") @RequestParam(value="preDocId",required = false) String preDocId,
            @WsDocNote(value="单据类型") @RequestParam(value="docType") String docType) throws Exception {
        return docEngine.create(docType,preDocId);
    }


    @WsDocNote("7、计算")
    @RequestMapping(value = "/calculate",method = RequestMethod.POST)
    public DocHV calculate(
            @WsDocNote(value="单据JSON") @RequestBody CalcModel calcModel) {
        return docEngine.calculate(calcModel.getDoc(),calcModel.getFromCard(),calcModel.getOptions());
    }

    @WsDocNote("8、简单调用")
    @RequestMapping(value = "/call",method = RequestMethod.POST)
    public Object call(
            @WsDocNote(value="单据JSON") @RequestBody CallModel calcModel) {
        return docEngine.call(calcModel.getDoc(),calcModel.getFromCard(),calcModel.getMethod(), calcModel.getOptions());
    }
    
    @WsDocNote("A、修改单据")
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public DocHV update(
            HttpServletRequest request,
            @WsDocNote(value="单据JSON") @RequestBody DocHV doc) {

        String userAgent = request.getHeader("User-Agent");
        String info = userAgent != null ? Arrays.stream(userAgent.split(" "))
            .filter(s->s.contains("MicroMessenger"))
            .findFirst()
            .orElseGet(()->{
                Browser browser = UserAgent.parseUserAgentString(userAgent).getBrowser();
                Version version = browser.getVersion(userAgent);
                return browser.getName() + "/" + version.getVersion();
            }) : "Unknown Browser";

        return docEngine.doUpdateView(doc, info);
    }

    @Data
    static class CalcModel extends DocHV{
        String fromCard;
        String options;
        DocHV doc;
    }

    @Data
    static class CallModel extends DocHV{
        String fromCard;
        String method;
        String options;
        DocHV doc;
    }
}
