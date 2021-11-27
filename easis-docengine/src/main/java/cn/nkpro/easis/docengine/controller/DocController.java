package cn.nkpro.easis.docengine.controller;

import cn.nkpro.easis.annotation.NkNote;
import cn.nkpro.easis.data.elasticearch.ESPageList;
import cn.nkpro.easis.docengine.NkDocSearchService;
import cn.nkpro.easis.docengine.gen.DocRecord;
import cn.nkpro.easis.docengine.model.DocDefFlowV;
import cn.nkpro.easis.docengine.model.DocHHistory;
import cn.nkpro.easis.docengine.model.DocHV;
import cn.nkpro.easis.docengine.model.SearchParams;
import cn.nkpro.easis.docengine.service.NkDocDefService;
import cn.nkpro.easis.docengine.service.NkDocEngineFrontService;
import cn.nkpro.easis.docengine.service.NkDocHistoryService;
import com.alibaba.fastjson.JSONObject;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bean on 2020/6/9.
 */
@PreAuthorize("authenticated")
@NkNote("3.单据")
@RestController
@RequestMapping("/doc")
public class DocController {

    @Autowired@SuppressWarnings("all")
    private NkDocDefService docDefService;
    @Autowired@SuppressWarnings("all")
    private NkDocEngineFrontService docEngine;
    @Autowired@SuppressWarnings("all")
    private NkDocSearchService searchService;
    @Autowired@SuppressWarnings("all")
    private NkDocHistoryService docHistoryService;

    @NkNote("1.拉取交易列表数据")
    @RequestMapping(value = "/list/{index}",method = RequestMethod.POST)
    public ESPageList<JSONObject> list(@RequestBody SearchParams params, @PathVariable String index) {
        return searchService.queryList(index, null, params);
    }

    @NkNote("1.拉取交易列表搜索提示")
    @RequestMapping(value = "/suggest/{index}",method = RequestMethod.POST)
    public ESPageList<JSONObject> suggest(@RequestBody SearchParams params, @PathVariable String index) {
        return searchService.querySuggest(index, null, params);
    }

    
    @NkNote("2.拉取入口单据列表")
    @RequestMapping(value = "/entrance",method = RequestMethod.GET)
    public List<DocDefFlowV> getEntrance(@RequestParam("classify") String classify){
        return docDefService.getEntrance(classify);
    }

    @NkNote("3.拉取交易详情")
    @RequestMapping(value = "/detail/{docId}",method = RequestMethod.GET)
    public DocHV get(@PathVariable("docId") String docId) {
        return docEngine.detailView(docId);
    }


    @NkNote("4、拉取交易历史版本")
    @RequestMapping(value = "/detail/snapshots/{docId}/{offset}",method = RequestMethod.GET)
    public List<DocRecord> getHistories(@PathVariable("docId") String docId, @PathVariable("offset") Integer offset) {
        return docHistoryService.getHistories(docId,offset);
    }

    @NkNote("5.拉取交易历史版本详情")
    @RequestMapping(value = "/detail/snapshot/{historyId}",method = RequestMethod.GET)
    public DocHHistory getHistory(@PathVariable("historyId") String historyId) {
        return docHistoryService.getDetail(historyId);
    }
    
    @NkNote("4、创建新单据")
    @RequestMapping(value = "/pre/create",method = RequestMethod.POST)
    public DocHV preCreate(
            @NkNote(value="前序ID") @RequestParam(value="preDocId",required = false) String preDocId,
            @NkNote(value="单据类型") @RequestParam(value="docType") String docType) {
        return docEngine.create(docType,preDocId);
    }


    @NkNote("7、计算")
    @RequestMapping(value = "/calculate",method = RequestMethod.POST)
    public DocHV calculate(
            @NkNote(value="单据JSON") @RequestBody CalcModel calcModel) {
        return docEngine.calculate(calcModel.getDoc(),calcModel.getFromCard(),calcModel.getOptions());
    }

    @NkNote("8、简单调用")
    @RequestMapping(value = "/call",method = RequestMethod.POST)
    public Object call(
            @NkNote(value="单据JSON") @RequestBody CallModel calcModel) {
        return docEngine.call(calcModel.getDoc(),calcModel.getFromCard(),calcModel.getMethod(), calcModel.getOptions());
    }
    
    @NkNote("A、修改单据")
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public DocHV update(
            HttpServletRequest request,
            @NkNote(value="单据JSON") @RequestBody DocHV doc) {

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

    @NkNote("B、新建单据后，检查单据索引的情况")
    @RequestMapping(value = "/exists/{docId}",method = RequestMethod.GET)
    public boolean exists(@PathVariable String docId) throws IOException {
        return searchService.exists(docId);
    }

    @Data
    public static class CalcModel{
        String fromCard;
        Object options;
        DocHV doc;
    }

    @Data
    public static class CallModel{
        String fromCard;
        String method;
        Object options;
        DocHV doc;
    }
}
