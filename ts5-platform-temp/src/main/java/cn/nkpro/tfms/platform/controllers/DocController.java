package cn.nkpro.tfms.platform.controllers;

import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.index.IndexDoc;
import cn.nkpro.tfms.platform.model.index.IndexDocItem;
import cn.nkpro.tfms.platform.model.po.BizDoc;
import cn.nkpro.tfms.platform.model.po.DefDocType;
import cn.nkpro.tfms.platform.model.po.SysLogDocRecord;
import cn.nkpro.ts5.config.mvc.CompressResponse;
import cn.nkpro.tfms.platform.services.TfmsDocEngineWithPerm;
import cn.nkpro.tfms.platform.services.TfmsDocService;
import cn.nkpro.tfms.platform.services.TfmsDocHistoryService;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.tfms.platform.elasticearch.ESPageList;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by bean on 2020/6/9.
 */
@WsDocNote("13.单据")
@Controller
@RequestMapping("/doc")
public class DocController {
    @Autowired
    private TfmsDocService docService;
    @Autowired
    private TfmsDocEngineWithPerm docEngineWithPerm;
    @Autowired
    private TfmsDocHistoryService historyService;

    @CompressResponse
    @WsDocNote("1、拉取交易列表数据")
    @RequestMapping(value = "/list",method = RequestMethod.POST)
    public ESPageList<IndexDoc> list(@RequestBody JSONObject params) {
        return docService.query(IndexDoc.class, params,null);
    }

    @CompressResponse
    @WsDocNote("1、拉取交易列表数据")
    @RequestMapping(value = "/items/list",method = RequestMethod.POST)
    public ESPageList<IndexDocItem> itemList(@RequestBody JSONObject params) {
        return docService.query(IndexDocItem.class, params,null);
    }

    @CompressResponse
    @WsDocNote("2、拉取业务类型")
    @RequestMapping(value = "/types/{classify}",method = RequestMethod.GET)
    public List<DefDocType> typesList(@PathVariable("classify") String classify){
        return docService.getDocTypes(classify);
    }

    @CompressResponse
    @WsDocNote("3、拉取交易详情")
    @RequestMapping(value = "/detail/{docId}",method = RequestMethod.GET)
    public BizDoc get(@PathVariable("docId") String docId) {
        return docEngineWithPerm.getDetailHasDocPermForController(docId);
    }

    @CompressResponse
    @WsDocNote("3、拉取交易历史版本")
    @RequestMapping(value = "/detail/histories/{docId}",method = RequestMethod.GET)
    public List<SysLogDocRecord> getHistorys(@PathVariable("docId") String docId) {
        return historyService.getHistorys(docId);
    }

    @CompressResponse
    @WsDocNote("3、拉取交易历史版本")
    @RequestMapping(value = "/detail/history/{id}",method = RequestMethod.GET)
    public SysLogDocRecord getHistory(@PathVariable("id") String id) {
        return historyService.getDetail(id);
    }

    @CompressResponse
    @WsDocNote("4、创建新单据")
    @RequestMapping(value = "/pre/create",method = RequestMethod.POST)
    public BizDocBase preCreate(
            @WsDocNote(value="容器ID") @RequestParam(value="refObjectId",required = false) String refObjectId,
            @WsDocNote(value="前序ID") @RequestParam(value="preDocId",required = false) String preDocId,
            @WsDocNote(value="单据类型") @RequestParam(value="docType") String docType) {
        return docEngineWithPerm.toCreateForController(refObjectId,preDocId,docType);
    }

    @CompressResponse
    @WsDocNote("7、计算")
    @RequestMapping(value = "/calculate/{docType}",method = RequestMethod.POST)
    public BizDocBase calculate(
            @WsDocNote(value="单据类型") @PathVariable(value="docType") String docType,
            @WsDocNote(value="卡片名称") @RequestParam(value="component",required = false) String componentName,
            @WsDocNote(value="计算类型") @RequestParam(value="calculate",required = false) String calculate,
            @WsDocNote(value="单据JSON") @RequestBody String json) {
        return docEngineWithPerm.calculateForController(docType,componentName,calculate,json);
    }

    @ResponseBody
    @CompressResponse
    @WsDocNote("8、调用")
    @RequestMapping(value = "/call/{docType}",method = RequestMethod.POST)
    public Object call(
            @WsDocNote(value="单据类型") @PathVariable(value="docType") String docType,
            @WsDocNote(value="卡片名称") @RequestParam(value="component",required = false) String componentName,
            @WsDocNote(value="计算类型") @RequestParam(value="event",required = false) String event,
            @WsDocNote(value="单据JSON") @RequestBody String json) {
        return docEngineWithPerm.callForController(docType,componentName,event,json);
    }

    @CompressResponse
    @WsDocNote("9、检查修改单据权限")
    @RequestMapping(value = "/pre/update/{docType}/{docId}",method = RequestMethod.POST)
    public boolean preUpdate(
            @WsDocNote(value="单据类型") @PathVariable(value="docType") String docType,
            @WsDocNote(value="单据JSON") @PathVariable(value="docId") String docId) {
        return docEngineWithPerm.preUpdateForController(docType,docId);
    }

    @CompressResponse
    @WsDocNote("A、修改单据")
    @RequestMapping(value = "/update/{docType}",method = RequestMethod.POST)
    public BizDocBase update(
            HttpServletRequest request,
            @WsDocNote(value="单据类型") @PathVariable(value="docType") String docType,
            @WsDocNote(value="单据JSON") @RequestBody String json) {

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
        return docEngineWithPerm.doUpdateForController(docType, json, info);
    }

    @ResponseBody
    @CompressResponse
    @WsDocNote("B、执行单据函数")
    @RequestMapping(value = "/exec/processor/{docType}",method = RequestMethod.POST)
    public Object execDocFunc(
            @WsDocNote(value="单据类型") @PathVariable(value="docType") String docType,
            @WsDocNote(value="单据JSON") @RequestBody String json) {
        return docService.execDocProcessorFunc(docType,json);
    }

    @ResponseBody
    @CompressResponse
    @WsDocNote("C、执行组件函数")
    @RequestMapping(value = {
            "/exec/card/{card}/{func}",
            "/exec/component/{card}/{func}"},method = RequestMethod.POST)
    public Object execComponentFunc(
            @PathVariable("card")       String card,
            @PathVariable("func")       String func,
            @RequestBody                String body) throws Throwable {
        return docService.execCardFunc(card,func,body);
    }
}
