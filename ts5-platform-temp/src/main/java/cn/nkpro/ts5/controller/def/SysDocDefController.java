package cn.nkpro.ts5.controller.def;

import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.config.mvc.CompressResponse;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.model.DocDefIV;
import cn.nkpro.ts5.engine.doc.service.NKDocDefService;
import cn.nkpro.ts5.model.mb.gen.DocDefH;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by bean on 2020/1/15.
 */
@WsDocNote("D1.单据配置")
@Controller
@RequestMapping("/def/doc")
@PreAuthorize("hasAnyAuthority('*:*','DEF:*','DEF:DOCTYPE')")
public class SysDocDefController {

    @Autowired
    private NKDocDefService defDocTypeService;

    @WsDocNote("1.获取单据配置列表")
    @CompressResponse
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
    @ResponseBody
    @RequestMapping("/type/types")
    public List<DocDefH> types(){
        return defDocTypeService.getAllDocTypes();
    }

    @WsDocNote("3.获取单据配置详情")
    @CompressResponse
    @RequestMapping(value = "/type/detail/{docType}/{version}")
    public DocDefHV detail(
            @WsDocNote(value = "单据类型") @PathVariable("docType") String docType, @PathVariable String version){
        return defDocTypeService.getDocDefined(docType, version, true, true);
    }

    @WsDocNote("4.预处理编辑单据配置")
    @ResponseBody
    @RequestMapping("/type/edit")
    public DocDefHV edit(
            @WsDocNote("单据配置对象")@RequestBody DocDefHV def){
        return defDocTypeService.doEdit(def);
    }

    @WsDocNote("5.更新单据配置")
    @ResponseBody
    @RequestMapping("/type/update")
    public DocDefHV update(
            @WsDocNote("单据配置对象")@RequestBody DocDefHV def){
        return defDocTypeService.doUpdate(def, false);
    }

    @WsDocNote("6.创建一个新分支")
    @ResponseBody
    @RequestMapping("/type/breach")
    public DocDefHV breach(
            @WsDocNote("单据配置对象")@RequestBody DocDefHV def){
        return defDocTypeService.doBreach(def);
    }

    @WsDocNote("7.激活配置")
    @ResponseBody
    @RequestMapping("/type/active")
    public DocDefHV active(
            @WsDocNote("单据配置对象")@RequestBody DocDefHV def){
        return defDocTypeService.doActive(def);
    }

    @WsDocNote("8.删除配置")
    @ResponseBody
    @RequestMapping("/type/delete")
    public void delete(
            @WsDocNote("单据配置对象")@RequestBody DocDefHV def){
        defDocTypeService.doDelete(def,false);
    }

    @WsDocNote("9.获取单据配置的Options")
    @ResponseBody
    @RequestMapping("/type/options")
    public Map<String, Object> options(@WsDocNote("分类")@RequestParam(value="classify",required = false) String classify){
        return defDocTypeService.options(classify);
    }

    @WsDocNote("10.获取卡片信息")
    @ResponseBody
    @RequestMapping("/card/{cardHandlerName}")
    public DocDefIV cardDescribe(@WsDocNote("分类")@PathVariable String cardHandlerName){
        return defDocTypeService.getCardDescribe(cardHandlerName);
    }
}
