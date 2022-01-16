package cn.nkpro.elcube.co.spel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author 吴俊
 * @Email wujun@newcore.net.cn
 * 创建日期: 2022/1/16
 * used to: 编号生成规则
 */
@Component("SpELNumberCreate")
public class NkNumberCreateSpEL implements NkSpELInjection{

    @Autowired
    private RedisTemplate<String, Long> redisTemplate;

    public String create(String prefix){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String format = simpleDateFormat.format(date);
        Boolean key = redisTemplate.hasKey(prefix + format);
        Long increment = 1L;
        if(Boolean.TRUE.equals(key)){
            increment = redisTemplate.opsForValue().increment(format, 1L);
        }else{
            redisTemplate.opsForValue().set(prefix + format,increment,1L, TimeUnit.DAYS);
        }
        return prefix + format + increment + "";
    }

}
