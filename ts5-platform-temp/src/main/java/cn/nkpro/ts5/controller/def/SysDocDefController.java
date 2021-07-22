package cn.nkpro.ts5.controller.def;

import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.config.mvc.CompressResponse;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.service.NKDocDefService;
import cn.nkpro.ts5.model.mb.gen.DocDefH;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @WsDocNote("1、获取单据配置列表")
    @CompressResponse
    @RequestMapping("/type/page")
    public PageList<DocDefH> getPage(
            @WsDocNote("单据分类")
            @RequestParam(value = "docClassify",required = false)           String docClassify,
            @WsDocNote("单据类型")
            @RequestParam(value = "docType",    required = false)           String docType,
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
        return defDocTypeService.getPage(docType,keyword,from,rows,orderField,order);
    }

    @WsDocNote("2、获取单据配置详情")
    @CompressResponse
    @RequestMapping(value = "/type/detail")
    public DocDefHV detail(
            @WsDocNote(value="单据类型") @RequestParam("docType") String docType){
        return defDocTypeService.getDocDefined(docType, 1, true, true, true);
    }

    @WsDocNote("4、获取选项")
    @ResponseBody
    @RequestMapping("/type/options")
    public Map<String, Object> options(){
        return defDocTypeService.options();
    }

    @WsDocNote("5、更新单据配置")
    @CompressResponse
    @RequestMapping("/type/update")
    public void update(
            @WsDocNote("更新模式")@RequestParam("create") Boolean create,
            @WsDocNote("单据配置对象")@RequestBody DocDefHV defDocTypeBO){
        defDocTypeService.doUpdate(defDocTypeBO, create, false);
    }
}
