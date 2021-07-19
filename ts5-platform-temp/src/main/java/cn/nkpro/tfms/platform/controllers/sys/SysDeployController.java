package cn.nkpro.tfms.platform.controllers.sys;

import cn.nkpro.tfms.platform.mappers.gen.DefDocTypeMapper;
import cn.nkpro.tfms.platform.model.po.DefDocType;
import cn.nkpro.tfms.platform.model.po.DefDocTypeExample;
import cn.nkpro.ts5.config.mvc.CompressResponse;
import cn.nkpro.tfms.platform.services.TfmsDefBpmService;
import cn.nkpro.tfms.platform.services.TfmsDefDeployService;
import com.alibaba.fastjson.JSONObject;
import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by bean on 2020/7/13.
 */
@WsDocNote("D2.部署服务")
@Controller
@RequestMapping("/def/deploy")
@PreAuthorize("hasAnyAuthority('*:*','DEF:*','DEF:DEPLOY')")
public class SysDeployController {

    @Autowired
    private TfmsDefDeployService defDeployService;

    @Autowired
    private TfmsDefBpmService defBpmService;

    @Autowired
    private DefDocTypeMapper defDocTypeMapper;


    @ResponseBody
    @WsDocNote("1、导出")
    @RequestMapping("/export")
    public ResponseEntity<ByteArrayResource> buildExportKey(@RequestBody JSONObject config){
        //return defDeployService.buildExportKey(config);

        byte[] export = defDeployService.defExport(config).getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment",
                new String("def.ts5".getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
        headers.setContentLength(export.length);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(export.length)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new ByteArrayResource(export));

    }



//    @ResponseBody
//    @WsDocNote("2、下载导出文件")
//    @RequestMapping("/d/{key}")
//    public ResponseEntity<ByteArrayResource> defExport(@PathVariable String key){
//
//        byte[] export = defDeployService.defExport(key).getBytes(StandardCharsets.UTF_8);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentDispositionFormData("attachment",
//                new String("def.ts5".getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
//        headers.setContentLength(export.length);
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//
//        return ResponseEntity.ok()
//                .headers(headers)
//                .contentLength(export.length)
//                .contentType(MediaType.parseMediaType("application/octet-stream"))
//                .body(new ByteArrayResource(export));
//    }

    @WsDocNote("3、导入")
    @ResponseBody
    @RequestMapping("/import")
    public void defImport(@RequestBody String pointsTxt){
        defDeployService.defImport(pointsTxt);
    }

    @CompressResponse
    @WsDocNote("4、查询所有已激活单据类型")
    @RequestMapping("/query/docTypes")
    public List<DefDocType> queryDocType(){
        DefDocTypeExample example = new DefDocTypeExample();
        example.createCriteria().andStateEqualTo("ACTIVE");
        example.setOrderByClause("DOC_TYPE");
        return defDocTypeMapper.selectByExample(example);
    }

    @CompressResponse
    @WsDocNote("5、查询所有工作流")
    @RequestMapping("/query/bpmns")
    public List<Object> allBpmnDeploymentss(){
        return defBpmService.getAllDeployments();
    }
}
