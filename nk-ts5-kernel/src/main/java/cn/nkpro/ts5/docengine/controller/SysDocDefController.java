package cn.nkpro.ts5.docengine.controller;

import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.docengine.NkDocEngine;
import cn.nkpro.ts5.docengine.model.DocDefHV;
import cn.nkpro.ts5.docengine.service.NkDocDefService;
import cn.nkpro.ts5.docengine.gen.DocDefH;
import cn.nkpro.ts5.docengine.model.DocDefIV;
import cn.nkpro.ts5.wsdoc.annotation.WsDocNote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by bean on 2020/1/15.
 */
@Slf4j
@WsDocNote("33.[DevDef]单据配置")
@RestController
@RequestMapping("/def/doc")
@PreAuthorize("hasAnyAuthority('*:*','DEF:*','DEF:DOCTYPE')")
public class SysDocDefController {


    @Autowired@SuppressWarnings("all")
    private NkDocEngine docEngine;
    @Autowired@SuppressWarnings("all")
    private NkDocDefService defDocTypeService;

    @WsDocNote("1.获取单据配置列表")
    @RequestMapping("/type/page")
    public PageList<DocDefH> getPage(
            @WsDocNote("单据分类")
            @RequestParam(value = "docClassify",required = false)           String docClassify,
            @WsDocNote("单据类型")
            @RequestParam(value = "docType",    required = false)           String docType,
            @WsDocNote("状态")
            @RequestParam(value = "state",      required = false)           String state,
            @WsDocNote("查询关键字")
            @RequestParam(value = "keyword",    required = false)           String keyword,
            @WsDocNote("起始条目")
            @RequestParam(value = "from",       defaultValue = "0")         Integer from,
            @WsDocNote("条目数")
            @RequestParam(value = "rows",       defaultValue = "10")        Integer rows,
            @WsDocNote("排序字段")
            @RequestParam(value = "orderField", defaultValue = "DOC_TYPE")  String orderField,
            @WsDocNote("排序方式")
            @RequestParam(value = "order",      defaultValue = "")          String order
    ){
        return defDocTypeService.getPage(docClassify,docType,state, keyword,from,rows,orderField,order);
    }

    @WsDocNote("2.获取所有单据类型")
    @RequestMapping("/type/types")
    public List<DocDefH> types(){
        return defDocTypeService.getAllDocTypes();
    }

    @WsDocNote("2.获取单据类型的所有版本")
    @RequestMapping("/type/list/{docType}/{page}")
    public List<DocDefH> list(@PathVariable String docType, @PathVariable Integer page){
        return defDocTypeService.getList(docType,page);
    }

    @WsDocNote("3.获取单据配置详情")
    @RequestMapping(value = "/type/detail/{docType}/{version}")
    public DocDefHV detail(
            @WsDocNote(value = "单据类型") @PathVariable("docType") String docType, @PathVariable String version){
        return defDocTypeService.getDocDefForEdit(docType, version);
    }

    @WsDocNote("5.更新单据配置")
    @RequestMapping("/type/update")
    public DocDefHV update(
            @WsDocNote("单据配置对象")@RequestBody DocDefHV def){
        return defDocTypeService.doUpdate(def, false);
    }

    @WsDocNote("6.创建一个新分支")
    @RequestMapping("/type/breach")
    public DocDefHV breach(
            @WsDocNote("单据配置对象")@RequestBody DocDefHV def){
        return defDocTypeService.doBreach(def);
    }

    @WsDocNote("7.激活配置")
    @RequestMapping("/type/active")
    public DocDefHV active(
            @WsDocNote("单据配置对象")@RequestBody DocDefHV def){
        return defDocTypeService.doActive(def);
    }

    @WsDocNote("8.删除配置")
    @RequestMapping("/type/delete")
    public void delete(
            @WsDocNote("单据配置对象")@RequestBody DocDefHV def){
        defDocTypeService.doDelete(def,false);
    }

    @WsDocNote("9.调试配置")
    @RequestMapping("/type/debug")
    public DocDefHV debug(
            @WsDocNote("单据配置对象")@RequestBody DocDefHV def, @RequestParam(value="run")boolean run){
        return defDocTypeService.doRun(def, run);
    }

    @WsDocNote("10.获取单据配置的Options")
    @RequestMapping("/type/options")
    public Map<String, Object> options(@WsDocNote("分类")@RequestParam(value="classify",required = false) String classify){
        return defDocTypeService.options(classify);
    }

    @WsDocNote("11.获取卡片信息")
    @RequestMapping("/card/{cardHandlerName}")
    public DocDefIV cardDescribe(@WsDocNote("分类")@PathVariable String cardHandlerName){
        return defDocTypeService.getCardDescribe(cardHandlerName);
    }

    @WsDocNote("12.随机生成单据")
    @RequestMapping(value = "/random/{docType}/{count}")
    public int random(@PathVariable String docType, @PathVariable Integer count) {

        int ret = 0;
        for(int i=0;i<count;i++){
            try{
                docEngine.doUpdate(docEngine.random(docEngine.create(docType, null)),"随机生成");
                ret ++;
            }catch (Exception e){
                log.error(e.getMessage(),e);
            }
        }
        return ret;
    }
}
