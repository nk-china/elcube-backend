package cn.nkpro.easis.platform.controller;

import cn.nkpro.easis.annotation.NkNote;
import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @NkNote("登陆验证码")
    @RequestMapping(value = "/code",produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] verCode(HttpServletResponse response) throws IOException {

        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");

        String text = kaptcha.createText();
        BufferedImage image = kaptcha.createImage(text);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image,"png",out);
        out.flush();
        out.close();

        return out.toByteArray();
    }
}
