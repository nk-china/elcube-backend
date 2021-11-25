package cn.nkpro.ts5.docengine.controller;

import cn.nkpro.ts5.annotation.Keep;
import cn.nkpro.ts5.annotation.NkNote;
import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.docengine.NkDocEngine;
import cn.nkpro.ts5.docengine.gen.DocDefH;
import cn.nkpro.ts5.docengine.model.DocDefHV;
import cn.nkpro.ts5.docengine.model.DocDefIV;
import cn.nkpro.ts5.docengine.service.NkDocDefService;
import lombok.Data;
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
@NkNote("33.[DevDef]单据配置")
@RestController
@RequestMapping("/def/doc")
@PreAuthorize("hasAnyAuthority('*:*','DEF:*','DEF:DOCTYPE')")
public class SysDocDefController {


    @Autowired@SuppressWarnings("all")
    private NkDocEngine docEngine;
    @Autowired@SuppressWarnings("all")
    private NkDocDefService defDocTypeService;

    @NkNote("1.获取单据配置列表")
    @RequestMapping("/type/page")
    public PageList<DocDefH> getPage(
            @NkNote("单据分类")
            @RequestParam(value = "docClassify",required = false)           String docClassify,
            @NkNote("单据类型")
            @RequestParam(value = "docType",    required = false)           String docType,
            @NkNote("状态")
            @RequestParam(value = "state",      required = false)           String state,
            @NkNote("查询关键字")
            @RequestParam(value = "keyword",    required = false)           String keyword,
            @NkNote("起始条目")
            @RequestParam(value = "from",       defaultValue = "0")         Integer from,
            @NkNote("条目数")
            @RequestParam(value = "rows",       defaultValue = "10")        Integer rows,
            @NkNote("排序字段")
            @RequestParam(value = "orderField", defaultValue = "DOC_TYPE")  String orderField,
            @NkNote("排序方式")
            @RequestParam(value = "order",      defaultValue = "")          String order
    ){
        return defDocTypeService.getPage(docClassify,docType,state, keyword,from,rows,orderField,order);
    }

    @NkNote("2.获取所有单据类型")
    @RequestMapping("/type/types")
    public List<DocDefH> types(){
        return defDocTypeService.getAllDocTypes();
    }

    @NkNote("2.获取单据类型的所有版本")
    @RequestMapping("/type/list/{docType}/{page}")
    public List<DocDefH> list(@PathVariable String docType, @PathVariable Integer page){
        return defDocTypeService.getList(docType,page);
    }

    @NkNote("3.获取单据配置详情")
    @RequestMapping(value = "/type/detail/{docType}/{version}")
    public DocDefHV detail(
            @NkNote(value = "单据类型") @PathVariable("docType") String docType, @PathVariable String version){
        return defDocTypeService.getDocDefForEdit(docType, version);
    }

    @NkNote("5.更新单据配置")
    @RequestMapping("/type/update")
    public DocDefHV update(
            @NkNote("单据配置对象")@RequestBody DocDefHV def){
        return defDocTypeService.doUpdate(def, false);
    }

    @NkNote("6.创建一个新分支")
    @RequestMapping("/type/breach")
    public DocDefHV breach(
            @NkNote("单据配置对象")@RequestBody DocDefHV def){
        return defDocTypeService.doBreach(def);
    }

    @NkNote("7.激活配置")
    @RequestMapping("/type/active")
    public DocDefHV active(
            @NkNote("单据配置对象")@RequestBody DocDefHV def){
        return defDocTypeService.doActive(def);
    }

    @NkNote("8.删除配置")
    @RequestMapping("/type/delete")
    public void delete(
            @NkNote("单据配置对象")@RequestBody DocDefHV def,@RequestParam("force") boolean force){
        defDocTypeService.doDelete(def,force);
    }

    @NkNote("9.调试配置")
    @RequestMapping("/type/debug")
    public DocDefHV debug(
            @NkNote("单据配置对象")@RequestBody DocDefHV def, @RequestParam(value="run")boolean run){
        return defDocTypeService.doRun(def, run);
    }

    @NkNote("10.获取单据配置的Options")
    @RequestMapping("/type/options")
    public Map<String, Object> options(@NkNote("分类")@RequestParam(value="classify",required = false) String classify){
        return defDocTypeService.options(classify);
    }

    @NkNote("11.获取卡片信息")
    @RequestMapping("/card/{cardHandlerName}")
    public DocDefIV cardDescribe(@NkNote("分类")@PathVariable String cardHandlerName){
        return defDocTypeService.getCardDescribe(cardHandlerName);
    }

    @NkNote("8、简单调用")
    @RequestMapping(value = "/call",method = RequestMethod.POST)
    public Object callDef(
            @NkNote(value="单据JSON") @RequestBody CallDefModel callModel) {
        return defDocTypeService.callDef(callModel.getDef(),callModel.getFromCard(),callModel.getOptions());
    }

    @NkNote("12.随机生成单据")
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

    @Keep
    @Data
    public static class CallDefModel {
        String fromCard;
        Object options;
        Object def;
    }
}
