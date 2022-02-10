package cn.nkpro.elcube.security.impl;

import cn.nkpro.elcube.platform.gen.UserAccountSecret;
import cn.nkpro.elcube.security.NkCodeUserDetailsService;
import cn.nkpro.elcube.security.UserAccountService;
import cn.nkpro.elcube.security.bo.UserDetails;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.X509Certificate;

@ConditionalOnProperty({
        "nk.wxmp.app-id",
        "nk.wxmp.app-secret"
})
@Service
public class NKWXMPUserDetailsServiceImpl implements NkCodeUserDetailsService {


    @Value("${nk.wxmp.app-id}")
    private String appId;
    @Value("${nk.wxmp.app-secret}")
    private String appSecert;

    @SuppressWarnings("all")
    @Autowired
    private UserAccountService accountService;

    @Override
    public boolean supports(String type) {
        return StringUtils.equalsIgnoreCase(type,"wxmp");
    }

    @Override
    public UserDetails loadUser(String code,String secret) {

        JSONObject jsonObject;
        try (CloseableHttpClient client = getClient()) {
            HttpGet get = new HttpGet(
                    String.format(
                            "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                            appId,
                            appSecert,
                            code
                    )
            );
            HttpResponse execute = client.execute(get);
            jsonObject = JSON.parseObject(EntityUtils.toString(execute.getEntity()));
        } catch (IOException e) {
            throw new AuthenticationServiceException(e.getMessage());
        }

        if(jsonObject.getInteger("errcode")!=0){
            throw new AuthenticationServiceException(jsonObject.getString("errmsg"));
        }

        // 根据openId获取账号信息
        String openId = jsonObject.getString("openid");
        UserAccountSecret accountSecret = accountService.getAccountSecretByCode(openId);

        // openId 未绑定
        if(accountSecret==null){
            return null;
        }

        // 返回账号信息
        return accountService.loadUserById(accountSecret.getAccountId());
    }


    private static CloseableHttpClient getClient() {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                @Override
                public void checkClientTrusted(X509Certificate[] arg0,String arg1) {
                }
                @Override
                public void checkServerTrusted(X509Certificate[] arg0,String arg1) {
                }
            };
            ctx.init(null, new TrustManager[] { tm }, null);
            SSLConnectionSocketFactory ssf = new SSLConnectionSocketFactory(
                    ctx, NoopHostnameVerifier.INSTANCE);
            return HttpClients.custom()
                    .setSSLSocketFactory(ssf).build();
        } catch (Exception e) {
            return HttpClients.createDefault();
        }
    }
}
