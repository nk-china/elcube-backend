package cn.nkpro.ts5.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtHelper {

    /**
     * token 过期时间, 单位: 秒. 这个值表示 10 天
     */
    private static final long TOKEN_EXPIRED_TIME = 10L * 24 * 60 * 60 * 1000;
    /**
     * token 还有3天过期时重新生成
     */
    private static final long TOKEN_RENEW_TIME = 5L * 24 * 60 * 60 * 1000;

    //private static final long TOKEN_RENEW_TIME = 864000000-300000;

    /**
     * token 过期时间, 单位: 秒. 这个值表示 60 分钟
     */
    private static final long TOKEN_EXPIRED_TIME_WEBL = 60L * 60 * 1000;

    /**
     * token 还有30分钟过期时重新生成
     */
    private static final long TOKEN_RENEW_TIME_WEB = 30L * 60 * 1000;

    /**
     * jwt 加密解密密钥
     */
    private static final String JWT_SECRET = "MDk4ZjZiY2Q0NjIxZDM3M2NhZGU0ZTgzMjYyN2I0ZjY=";

    private static final String jwtId = "tokenId";
    /**
     * 创建JWT
     */
    public String createJWT(Map<String, Object> claims, Long time) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256; //指定签名的时候使用的签名算法，也就是header那部分，jwt已经将这部分内容封装好了。
        Date now = new Date(System.currentTimeMillis());

        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis();//生成JWT的时间
        //下面就是在为payload添加各种标准声明和私有声明了
        JwtBuilder builder = Jwts.builder() //这里其实就是new一个JwtBuilder，设置jwt的body
                .setClaims(claims)          //如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setId(jwtId)               //设置jti(JWT ID)：是JWT的唯一标识，根据业务需要，这个可以设置为一个不重复的值，主要用来作为一次性token,从而回避重放攻击。
                .setIssuedAt(now)           //iat: jwt的签发时间
                .signWith(signatureAlgorithm, secretKey);//设置签名使用的签名算法和签名使用的秘钥
        if (time >= 0) {
            long expMillis = nowMillis + time;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);     //设置过期时间
        }
        return builder.compact();
    }

    /**
     * 验证jwt
     */
    public Claims verifyJwt(String token) {
        //签名秘钥，和生成的签名的秘钥一模一样
        SecretKey key = generalKey();
        Claims claims;
        try {
            claims = Jwts.parser()              //得到DefaultJwtParser
                    .setSigningKey(key)         //设置签名的秘钥
                    .parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }//设置需要解析的jwt
        return claims;
    }

    /**
     * 由字符串生成加密key
     *
     * @return
     */
    private SecretKey generalKey() {
        byte[] encodedKey = Base64.getDecoder().decode(JWT_SECRET);
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }

    private Map<String,Object> generateTokenData(String unionId, String userId,String phone,
                                                        String systemId,String accountName,String deviceId,
                                                        Integer deviceType){
        Map<String, Object> map = new HashMap<>();
        map.put("unionId", unionId);
        map.put("accountId", userId);
        map.put("accountPhone", phone);
        map.put("systemId", systemId);
        map.put("accountName", accountName);
        map.put("deviceId", deviceId);
        map.put("deviceType", deviceType);
        return map;
    }
}