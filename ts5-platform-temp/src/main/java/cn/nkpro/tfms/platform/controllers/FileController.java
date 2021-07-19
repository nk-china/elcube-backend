package cn.nkpro.tfms.platform.controllers;

import cn.nkpro.tfms.platform.model.NkFileUploadStatus;
import cn.nkpro.ts5.supports.FileSupport;
import cn.nkpro.ts5.supports.defaults.file.DefaultFileSupportImpl;
import cn.nkpro.ts5.supports.defaults.file.FileInfo;
import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import javax.naming.OperationNotSupportedException;
import javax.servlet.http.HttpServletRequest;
import java.io.File;

@WsDocNote("19.文件管理")
@Controller
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileSupport fileSupport;

    @WsDocNote("1、初始化")
    @ResponseBody
    @RequestMapping(value = "/init")
    public ResponseEntity init(@RequestBody FileInfo file){
        return fileSupport.init(file);
    }


    @WsDocNote("2、上传")
    @ResponseBody
    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public NkFileUploadStatus upload(@RequestParam("file") MultipartFile file){
        return fileSupport.upload(file);
    }

    @ResponseBody
    @WsDocNote("3、下载")
    @RequestMapping(value = "/download",method = RequestMethod.GET)
    public ResponseEntity download(String url){
        return fileSupport.download(url);
    }

    @ResponseBody
    @WsDocNote("4、下载")
    @RequestMapping(value = "/d/{base}/**",method = RequestMethod.GET)
    public ResponseEntity downloadFile(@PathVariable String base,HttpServletRequest request) throws OperationNotSupportedException {
        final String path =
                request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
        final String bestMatchingPattern =
                request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();

        String filePath = base + File.separatorChar + new AntPathMatcher().extractPathWithinPattern(bestMatchingPattern,path);

        if(fileSupport instanceof DefaultFileSupportImpl){
            return ((DefaultFileSupportImpl) fileSupport).downloadFile(filePath);
        }

        throw new OperationNotSupportedException();
    }
}
