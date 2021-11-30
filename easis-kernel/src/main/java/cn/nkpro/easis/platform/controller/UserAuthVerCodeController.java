package cn.nkpro.easis.platform.controller;

import cn.nkpro.easis.annotation.NkNote;
import cn.nkpro.easis.basic.Constants;
import cn.nkpro.easis.data.redis.RedisSupport;
import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by bean on 2019/12/30.
 */

@RestController
@RequestMapping("/ver")
public class UserAuthVerCodeController {

    @Autowired
    private Producer kaptcha;
    @Autowired
    private RedisSupport<String> redisSupport;
    @Autowired
    private RedisSupport<Integer> redisSupportInteger;

    @NkNote("检查是否需要验证码")
    @RequestMapping(value = "/has/{username}",produces = MediaType.IMAGE_PNG_VALUE)
    public String verCode(@PathVariable("username") String username) {
        return String.valueOf(redisSupportInteger.get(Constants.CACHE_AUTH_ERROR+username));
    }

    @NkNote("登陆验证码")
    @RequestMapping(value = "/code/{random}",produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] verCode(HttpServletResponse response,@PathVariable("random") String random) throws IOException {

        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");

        String text = kaptcha.createText();
        BufferedImage image = kaptcha.createImage(text);

        redisSupport.set(random, text);
        redisSupport.expire(random, 60 * 10);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image,"png",out);
        out.flush();
        out.close();

        return out.toByteArray();
    }
}
