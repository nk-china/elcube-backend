package cn.nkpro.tfms.platform.controllers.sys;

import cn.nkpro.tfms.platform.model.DefProjectTypeBO;
import cn.nkpro.tfms.platform.model.po.DefProjectType;
import cn.nkpro.ts5.config.mvc.CompressResponse;
import cn.nkpro.tfms.platform.services.TfmsDefProjectTypeService;
import cn.nkpro.tfms.platform.model.util.PageList;
import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by bean on 2020/7/13.
 */
@WsDocNote("D2.业务配置")
@Controller
@RequestMapping("/def/project")
@PreAuthorize("hasAnyAuthority('*:*','DEF:*','DEF:PROJECT')")
public class SysProjectDefController {

    @Autowired
    private TfmsDefProjectTypeService defProjectTypeService;

    @WsDocNote("1、获取业务配置列表")
    @CompressResponse
    @RequestMapping("/type/page")
    public PageList<DefProjectType> getPage(
            @WsDocNote("业务类型")
            @RequestParam(value = "projectType",    required = false)           String projectType,
            @WsDocNote("查询关键字")
            @RequestParam(value = "keyword",    required = false)               String keyword,
            @WsDocNote("起始条目")
            @RequestParam(value = "from",       defaultValue = "0")             Integer from,
            @WsDocNote("条目数")
            @RequestParam(value = "rows",       defaultValue = "10")            Integer rows,
            @WsDocNote("排序字段")
            @RequestParam(value = "orderField", defaultValue = "PROJECT_TYPE")  String orderField,
            @WsDocNote("排序方式")
            @RequestParam(value = "order",      defaultValue = "")              String order
    ){
        return defProjectTypeService.getPage(projectType,keyword,from,rows,orderField,order);
    }

    @WsDocNote("2、获取业务配置详情")
    @CompressResponse
    @RequestMapping("/type/detail")
    public DefProjectType detail(
            @WsDocNote("业务类型")@RequestParam("projectType") String projectType,
            @WsDocNote("业务类型版本")@RequestParam("version")Integer version
    ) throws Exception {
        return defProjectTypeService.getProjectDefined(projectType,version);
    }

    @WsDocNote("3、获取选项")
    @ResponseBody
    @CompressResponse
    @RequestMapping("/type/options")
    public Map<String, Object> options(){
        return defProjectTypeService.options();
    }

    @WsDocNote("4、更新业务配置")
    @CompressResponse
    @RequestMapping("/type/update")
    public DefProjectType update(
            @WsDocNote("更新模式")@RequestParam("create") Boolean create,
            @WsDocNote("业务配置对象")@RequestBody DefProjectTypeBO defProjectType){
        defProjectTypeService.doUpdate(defProjectType,create,false);
        return defProjectType;
    }
}
