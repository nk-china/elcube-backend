package cn.nkpro.easis.platform.controller;

import cn.nkpro.easis.annotation.NkNote;
import cn.nkpro.easis.platform.service.DeployService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by bean on 2020/7/17.
 */
@NkNote("99.[DevOps]部署导入导出")
@RestController
@RequestMapping("/ops/deploy")
@PreAuthorize("hasAnyAuthority('*:*','DEVOPS:*','DEVOPS:DEPLOY')")
public class DevOpsDeployController {

    @Autowired@SuppressWarnings("all")
    private DeployService deployService;


    @NkNote("1.加载导出配置")
    @ResponseBody
    @RequestMapping("/load")
    public JSONArray load(){
        return deployService.load();
    }

    @ResponseBody
    @NkNote("2.导出")
    @RequestMapping("/export")
    public ResponseEntity<ByteArrayResource> buildExportKey(@RequestBody JSONObject config){

        byte[] export = deployService.export(config).getBytes(StandardCharsets.UTF_8);

        String date = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        String filename = "config."+date+".ts5";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment",
                new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
        headers.setContentLength(export.length);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(export.length)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new ByteArrayResource(export));

    }
    @NkNote("3.导入")
    @ResponseBody
    @RequestMapping("/import")
    public List<String> defImport(@RequestBody String pointsTxt){
        return deployService.imports(pointsTxt);
    }
}
