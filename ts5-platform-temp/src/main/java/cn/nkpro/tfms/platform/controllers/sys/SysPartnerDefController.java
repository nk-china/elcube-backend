package cn.nkpro.tfms.platform.controllers.sys;

import cn.nkpro.tfms.platform.model.DefPartnerRoleBO;
import cn.nkpro.tfms.platform.model.po.DefPartnerRole;
import cn.nkpro.ts5.config.mvc.CompressResponse;
import cn.nkpro.tfms.platform.services.TfmsDefPartnerRoleService;
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
@WsDocNote("D3.交易伙伴配置")
@Controller
@RequestMapping("/def/partner")
@PreAuthorize("hasAnyAuthority('*:*','DEF:*','DEF:PARTNER')")
public class SysPartnerDefController {

    @Autowired
    private TfmsDefPartnerRoleService defPartnerRoleService;

    @WsDocNote("1、获取交易伙伴角色列表")
    @CompressResponse
    @RequestMapping("/type/page")
    public PageList<DefPartnerRole> getPage(
            @WsDocNote("业务类型")
            @RequestParam(value = "partnerRole",    required = false)           String partnerRole,
            @WsDocNote("查询关键字")
            @RequestParam(value = "keyword",    required = false)               String keyword,
            @WsDocNote("起始条目")
            @RequestParam(value = "from",       defaultValue = "0")             Integer from,
            @WsDocNote("条目数")
            @RequestParam(value = "rows",       defaultValue = "10")            Integer rows,
            @WsDocNote("排序字段")
            @RequestParam(value = "orderField", defaultValue = "PARTNER_ROLE")  String orderField,
            @WsDocNote("排序方式")
            @RequestParam(value = "order",      defaultValue = "")              String order
    ){
        return defPartnerRoleService.getPage(partnerRole,keyword,from,rows,orderField,order);
    }

    @WsDocNote("2、获取交易伙伴角色详情")
    @CompressResponse
    @RequestMapping("/type/detail")
    public DefPartnerRole detail(
            @WsDocNote("业务类型")@RequestParam("partnerRole") String partnerRole){
        return defPartnerRoleService.getPartnerRoleDefined(partnerRole);
    }

    @WsDocNote("3、获取选项")
    @ResponseBody
    @CompressResponse
    @RequestMapping("/type/options")
    public Map<String, Object> options(){
        return defPartnerRoleService.options();
    }

    @WsDocNote("4、更新交易伙伴角色")
    @CompressResponse
    @RequestMapping("/type/update")
    public DefPartnerRole update(
            @WsDocNote("更新模式")@RequestParam("create") Boolean create,
            @WsDocNote("交易伙伴角色对象")@RequestBody DefPartnerRoleBO defPartnerRole){
        defPartnerRoleService.doUpdate(defPartnerRole,create,false);
        return defPartnerRole;
    }
}
