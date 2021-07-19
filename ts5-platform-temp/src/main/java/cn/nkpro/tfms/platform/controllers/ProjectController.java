package cn.nkpro.tfms.platform.controllers;

import cn.nkpro.tfms.platform.model.po.DefProjectType;
import cn.nkpro.ts5.config.mvc.CompressResponse;
import cn.nkpro.tfms.platform.services.TfmsDefProjectTypeService;
import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by bean on 2020/6/9.
 */
@WsDocNote("11.业务")
@Controller
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private TfmsDefProjectTypeService defProjectTypeService;

    @WsDocNote("6、拉取业务类型")
    @CompressResponse
    @RequestMapping(value = "/type/list",method = RequestMethod.GET)
    public List<DefProjectType> typesList(){
        return defProjectTypeService.getAllTypes();
    }
}
