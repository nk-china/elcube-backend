package cn.nkpro.ts5.co.fs.defaults;

import cn.nkpro.ts5.co.fs.FSConfig;
import cn.nkpro.ts5.co.fs.FileUploadStatus;
import cn.nkpro.ts5.co.fs.properties.AliyunProperties;
import cn.nkpro.ts5.co.fs.FSSupport;
import cn.nkpro.ts5.basic.NkProperties;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

public class DefaultFileSupportAliyunOSS implements FSSupport<Map<String, Object>>, InitializingBean {

    private Format format = new SimpleDateFormat("yyyyMMdd/HHmmss/");

    @Autowired
    private NkProperties tfmsProperties;
    @Autowired
    private AliyunProperties aliyunProperties;

    private OSS ossClient;

    @Override
    public ResponseEntity<Map<String, Object>> init(FSConfig file){

        // 设置上传路径
        String path = defaultPath(aliyunProperties.getOss().getPath(),tfmsProperties);

        try {
            // 有效期30秒
            long expireEndTime = System.currentTimeMillis() + 30 * 1000;
            Date expiration = new Date(expireEndTime);

            // PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1024*1024*100);// 最大100M
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, aliyunProperties.getOss().getPath());

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("OSSAccessKeyId", aliyunProperties.getAccessKeyId());
            data.put("signature", postSignature);
            data.put("policy", encodedPolicy);
            data.put("path", path);
            data.put("expire", String.valueOf(expireEndTime / 1000));
            data.put("key", path+file.getName());
            data.put("success_action_status","200");


            Map<String, Object> response = new LinkedHashMap<>();
            response.put("host",aliyunProperties.getOss().getHost());
            response.put("path",path);
            response.put("filename",file.getName());
            response.put("data",data);

            return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST")
                    .body(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    public FileUploadStatus upload(MultipartFile file) {
        throw new RuntimeException("不支持操作");
    }

    @Override
    public void moveTo(String source, String target) {

    }

    @Override
    public ResponseEntity download(String url) {

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(
                aliyunProperties.getOss().getBucket(),
                url,HttpMethod.GET);
        generatePresignedUrlRequest.setExpiration(new Date(new Date().getTime() + 30 * 1000));
        generatePresignedUrlRequest.setHeaders(new HashMap<>(Collections.singletonMap("response-content-type","application/octet-stream")));

        return ResponseEntity.ok(ossClient.generatePresignedUrl(generatePresignedUrlRequest).toString());
    }

    @Override
    public void afterPropertiesSet(){
        ossClient = new OSSClientBuilder().build(
                aliyunProperties.getOss().getEndpoint(),
                aliyunProperties.getAccessKeyId(),
                aliyunProperties.getAccessKeySecret());
    }
}
