package cn.nkpro.ts5.platform.controller;

import cn.nkpro.ts5.annotation.NkNote;
import cn.nkpro.ts5.co.fs.FSConfig;
import cn.nkpro.ts5.co.fs.FSSupport;
import cn.nkpro.ts5.co.fs.FileUploadStatus;
import cn.nkpro.ts5.co.fs.defaults.DefaultFileSupportImpl;
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

@NkNote("5.文件管理")
@Controller
@RequestMapping("/fs")
public class FSController {

    @SuppressWarnings("all")
    @Autowired
    private FSSupport fileSupport;

    @NkNote("1.初始化")
    @ResponseBody
    @RequestMapping(value = "/init")
    public ResponseEntity init(@RequestBody FSConfig file){
        return fileSupport.init(file);
    }


    @NkNote("2.上传")
    @ResponseBody
    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public FileUploadStatus upload(@RequestParam("file") MultipartFile file){
        return fileSupport.upload(file);
    }

    @ResponseBody
    @NkNote("3.生成下载URL")
    @RequestMapping(value = "/download",method = RequestMethod.GET)
    public ResponseEntity download(String url){
        return fileSupport.download(url);
    }

    @ResponseBody
    @NkNote("4.获取下载文件流")
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
