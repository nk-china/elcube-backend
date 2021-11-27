package cn.nkpro.easis.co.fs;

import cn.nkpro.easis.basic.NkProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FSSupport<T> {

    default String defaultPath(String rootPath, NkProperties tfmsProperties){
        String path = rootPath + tfmsProperties.getEnvKey();
        path += path.endsWith("/")?(".nk_temp/"):("/.nk_temp/");
        path += UUID.randomUUID().toString()+'/';
        return path;
    }

    /**
     * 初始化上传配置
     * @return
     */
    ResponseEntity<T> init(FSConfig file);

    /**
     * 上传文件到临时文件夹
     * @param file 文件
     * @return 上传结果
     */
    FileUploadStatus upload(MultipartFile file);

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
