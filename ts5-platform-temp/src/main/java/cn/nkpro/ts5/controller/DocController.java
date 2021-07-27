package cn.nkpro.ts5.controller;

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.engine.doc.NKDocIndexService;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.engine.doc.service.NKDocDefService;
import cn.nkpro.ts5.engine.doc.service.NkDocEngineFrontService;
import cn.nkpro.ts5.engine.elasticearch.ESPageList;
import cn.nkpro.ts5.engine.elasticearch.model.DocHES;
import cn.nkpro.ts5.orm.mb.gen.DocDefH;
import com.alibaba.fastjson.JSONObject;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by bean on 2020/6/9.
 */
@PreAuthorize("authenticated")
@WsDocNote("13.单据")
@RestController
@RequestMapping("/doc")
public class DocController {

    @Autowired@SuppressWarnings("all")
    private NKDocDefService docDefService;

    @Autowired@SuppressWarnings("all")
    private NkDocEngineFrontService docEngine;
    @Autowired@SuppressWarnings("all")
    private NKDocIndexService docService;
//    @Autowired
//    private TfmsDocEngineWithPerm docEngineWithPerm;
//    @Autowired
//    private TfmsDocHistoryService historyService;

    
    @WsDocNote("1.拉取交易列表数据")
    @RequestMapping(value = "/list",method = RequestMethod.POST)
    public ESPageList<DocHES> list(@RequestBody JSONObject params) {
        return docService.queryList(DocHES.class, null, params);
    }

    
    @WsDocNote("2.拉取入口单据列表")
    @RequestMapping(value = "/entrance",method = RequestMethod.GET)
    public List<DocDefH> getEntrance(@RequestParam("classify") String classify){
        return docDefService.getEntrance(classify);
    }

    
    @WsDocNote("3.拉取交易详情")
    @RequestMapping(value = "/detail/{docId}",method = RequestMethod.GET)
    public DocHV get(@PathVariable("docId") String docId) throws Exception {
        return docEngine.detail(docId);
    }
//
//    @ResponseCompress
//    @WsDocNote("1、拉取交易列表数据")
//    @RequestMapping(value = "/items/list",method = RequestMethod.POST)
//    public ESPageList<IndexDocItem> itemList(@RequestBody JSONObject params) {
//        return docService.query(IndexDocItem.class, params,null);
//    }
//
//    @ResponseCompress
//    @WsDocNote("3、拉取交易历史版本")
//    @RequestMapping(value = "/detail/histories/{docId}",method = RequestMethod.GET)
//    public List<SysLogDocRecord> getHistorys(@PathVariable("docId") String docId) {
//        return historyService.getHistorys(docId);
//    }
//
//    @ResponseCompress
//    @WsDocNote("3、拉取交易历史版本")
//    @RequestMapping(value = "/detail/history/{id}",method = RequestMethod.GET)
//    public SysLogDocRecord getHistory(@PathVariable("id") String id) {
//        return historyService.getDetail(id);
//    }
//
    
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
            @WsDocNote(value="单据JSON") @RequestBody CalcModel calcModel) throws Exception {
        return docEngine.calculate(calcModel.getDoc(),calcModel.getFromCard(),calcModel.getOptions());
    }

    @Data
    static class CalcModel extends DocHV{
        String fromCard;
        String options;
        DocHV doc;
    }
//
//    
//    @ResponseCompress
//    @WsDocNote("8、调用")
//    @RequestMapping(value = "/call/{docType}",method = RequestMethod.POST)
//    public Object call(
//            @WsDocNote(value="单据类型") @PathVariable(value="docType") String docType,
//            @WsDocNote(value="卡片名称") @RequestParam(value="component",required = false) String componentName,
//            @WsDocNote(value="计算类型") @RequestParam(value="event",required = false) String event,
//            @WsDocNote(value="单据JSON") @RequestBody String json) {
//        return docEngineWithPerm.callForController(docType,componentName,event,json);
//    }
//
//    @ResponseCompress
//    @WsDocNote("9、检查修改单据权限")
//    @RequestMapping(value = "/pre/update/{docType}/{docId}",method = RequestMethod.POST)
//    public boolean preUpdate(
//            @WsDocNote(value="单据类型") @PathVariable(value="docType") String docType,
//            @WsDocNote(value="单据JSON") @PathVariable(value="docId") String docId) {
//        return docEngineWithPerm.preUpdateForController(docType,docId);
//    }
//
    
    @WsDocNote("A、修改单据")
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public DocHV update(
            HttpServletRequest request,
            @WsDocNote(value="单据JSON") @RequestBody DocHV doc) throws Exception {

        Browser browser = UserAgent.parseUserAgentString(request.getHeader("User-Agent")).getBrowser();
        Version version = browser.getVersion(request.getHeader("User-Agent"));
        String info = null;//browser.getName() + "/" + version.getVersion();
        //微信小程序请求
        if(request.getHeader("User-Agent").contains("MicroMessenger"))
        {
            String str = request.getHeader("User-Agent");
            String cut = " ";
            String[] newStr = str.split(cut);
            for(String s:newStr){
                if(s.contains("MicroMessenger"))
                {
                    info= s;
                    break;
                }
            }
        }else {
            info = browser.getName() + "/" + version.getVersion();
        }
        return docEngine.doUpdate(doc);
    }
//
//    
//    @ResponseCompress
//    @WsDocNote("B、执行单据函数")
//    @RequestMapping(value = "/exec/processor/{docType}",method = RequestMethod.POST)
//    public Object execDocFunc(
//            @WsDocNote(value="单据类型") @PathVariable(value="docType") String docType,
//            @WsDocNote(value="单据JSON") @RequestBody String json) {
//        return docService.execDocProcessorFunc(docType,json);
//    }
//
//    
//    @ResponseCompress
//    @WsDocNote("C、执行组件函数")
//    @RequestMapping(value = {
//            "/exec/card/{card}/{func}",
//            "/exec/component/{card}/{func}"},method = RequestMethod.POST)
//    public Object execComponentFunc(
//            @PathVariable("card")       String card,
//            @PathVariable("func")       String func,
//            @RequestBody                String body) throws Throwable {
//        return docService.execCardFunc(card,func,body);
//    }
}
