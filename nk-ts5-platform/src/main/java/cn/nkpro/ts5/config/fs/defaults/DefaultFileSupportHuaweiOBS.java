package cn.nkpro.ts5.config.fs.defaults;

import cn.nkpro.ts5.config.fs.FSConfig;
import cn.nkpro.ts5.exception.TfmsIllegalContentException;
import cn.nkpro.ts5.config.fs.FSSupport;
import cn.nkpro.ts5.config.fs.properties.HuaweiProperties;
import cn.nkpro.ts5.config.global.NKProperties;
import cn.nkpro.ts5.config.fs.FileUploadStatus;
import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.model.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultFileSupportHuaweiOBS implements FSSupport<Map<String, Object>>, InitializingBean, DisposableBean {

    private Format format = new SimpleDateFormat("yyyyMMdd/HHmmss/");

    private static AuthTypeEnum authType = AuthTypeEnum.OBS;


    @Autowired
    private NKProperties tfmsProperties;
    @Autowired
    private HuaweiProperties huawei;

    private ObsClient obsClient;

    @Override
    public ResponseEntity<Map<String, Object>> init(FSConfig file){

        // 设置上传路径
        String path = defaultPath(huawei.getObs().getPath(),tfmsProperties);

        try {
            Map<String, Object> data = new HashMap<>();
            data.put("x-obs-acl", "public-read");
            data.put("content-type", "text/plain");

            PostSignatureRequest request = new PostSignatureRequest();
            request.setExpires(30);
            request.setFormParams(data);

            PostSignatureResponse response = obsClient.createPostSignature(request);

            data.put("accesskeyid", huawei.getAccessKeyId());
            data.put("signature", response.getSignature());
            data.put("policy", response.getPolicy());
            data.put("key", path + file.getName());


            Map<String, Object> responseMap = new LinkedHashMap<>();
            responseMap.put("host",huawei.getObs().getHost());
            responseMap.put("path",path);
            responseMap.put("filename",file.getName());
            responseMap.put("data",data);

            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST")
                    .body(responseMap);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @Override
    public FileUploadStatus upload(MultipartFile file) {
        throw new TfmsIllegalContentException("不支持操作");
    }

    @Override
    public void moveTo(String source, String target) {

    }

    @Override
    public ResponseEntity download(String url) {

        TemporarySignatureRequest request = new TemporarySignatureRequest(HttpMethodEnum.GET, 30);
        request.setBucketName(huawei.getObs().getBucket());
        request.setObjectKey(url);
        request.setQueryParams(Collections.singletonMap("response-content-type","application/octet-stream"));

        TemporarySignatureResponse response = obsClient.createTemporarySignature(request);

        return ResponseEntity.ok(response.getSignedUrl());
    }

    @Override
    public void afterPropertiesSet(){
        ObsConfiguration config = new ObsConfiguration();
        config.setEndPoint(huawei.getObs().getEndpoint());
        config.setAuthType(authType);
        obsClient = new ObsClient(huawei.getAccessKeyId(), huawei.getAccessKeySecret(), config);
    }

    @Override
    public void destroy() throws Exception {
        obsClient.close();
    }
}
