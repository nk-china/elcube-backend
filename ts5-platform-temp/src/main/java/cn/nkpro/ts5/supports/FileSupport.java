package cn.nkpro.ts5.supports;

import cn.nkpro.ts5.config.nk.NKProperties;
import cn.nkpro.ts5.supports.defaults.file.FileInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FileSupport<T> {

    default String defaultPath(String rootPath, NKProperties tfmsProperties){
        String path = rootPath + tfmsProperties.getEnvKey();
        path += path.endsWith("/")?(".nk_temp/"):("/.nk_temp/");
        path += UUID.randomUUID().toString()+'/';
        return path;
    }

    /**
     * 初始化上传配置
     * @return
     */
    ResponseEntity<T> init(FileInfo file);

    /**
     * 上传文件到临时文件夹
     * @param file 文件
     * @return 上传结果
     */
    NkFileUploadStatus upload(MultipartFile file);

    /**
     * 将文件从临时目录转移到正式目录
     * @param source
     * @param target
     */
    void moveTo(String source,String target);

    /**
     * 获取下载链接
     * @param url
     * @return
     */
    ResponseEntity download(String url);
}
