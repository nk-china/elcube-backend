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
import cn.nkpro.elcube.data.elasticearch.ESPageList;
import cn.nkpro.elcube.docengine.NkDocSearchService;
import cn.nkpro.elcube.docengine.gen.DocRecord;
import cn.nkpro.elcube.docengine.model.*;
import cn.nkpro.elcube.docengine.service.NkDocDefService;
import cn.nkpro.elcube.docengine.service.NkDocEngineFrontService;
import cn.nkpro.elcube.docengine.service.NkDocHistoryService;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        return searchService.queryList(index, null, params, true);
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

    @NkNote("2.1.拉取单据业务流")
    @RequestMapping(value = "/bizFlows",method = RequestMethod.GET)
    public List<DocDefFlowV> getEntrance(){
        return docDefService.getDocTypeFlows(null);
    }

    @NkNote("3.拉取交易详情")
    @RequestMapping(value = "/detail/{docId}",method = RequestMethod.GET)
    public DocHV get(@PathVariable("docId") String docId,@RequestParam(value = "edit",required = false,defaultValue = "false") Boolean edit) {
        return docEngine.detailView(docId, edit);
    }

    @NkNote("3.拉取交易编辑状态")
    @RequestMapping(value = "/state/{docId}",method = RequestMethod.GET)
    public DocState state(@PathVariable("docId") String docId) {
        return docEngine.state(docId);
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
        return docEngine.createForView(docType,preDocId);
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

        List<String> builder = new ArrayList<>();
        String userAgent = request.getHeader("User-Agent");
        if(userAgent!=null){
            try{
                if(userAgent.contains("MicroMessenger")){
                    builder.add("微信");
                }else if(userAgent.contains("iPhone")){
                    builder.add("iPhone");
                }else if(userAgent.contains("Android")){
                    builder.add("Android");
                }
                Browser browser = UserAgent.parseUserAgentString(userAgent).getBrowser();
                if(browser!=null){
                    Version version = browser.getVersion(userAgent);
                    builder.add(browser.getName() + (version !=null ? version.getVersion() : ""));
                }
            }catch (Exception ignored){}
        }
        if(builder.isEmpty()){
            builder.add("其他来源");
        }
        return docEngine.doUpdateView(doc, String.join(" / ", builder));
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
