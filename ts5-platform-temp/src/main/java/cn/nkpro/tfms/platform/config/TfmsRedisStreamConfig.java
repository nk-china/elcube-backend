package cn.nkpro.tfms.platform.config;

import cn.nkpro.tfms.platform.basis.TfmsCustomObjectManager;
import cn.nkpro.tfms.platform.services.TfmsStreamTask;
import cn.nkpro.tfms.platform.services.TfmsStreamTaskInfo;
import cn.nkpro.ts5.config.NKRedisTemplate;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;

import java.net.InetAddress;
import java.time.Duration;

@Slf4j
@Configuration
@ConditionalOnBean(TfmsStreamTask.class)
public class TfmsRedisStreamConfig {

    public static final String breachStreamKey = "stream.tasks";
    private static final String breachGroupKey  = "group1";

    @SuppressWarnings("all")@Autowired
    private NKRedisTemplate<String> redisTemplate;
    @SuppressWarnings("all")@Autowired
    private TfmsCustomObjectManager customObjectManager;

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(){
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(5);
        return threadPoolTaskExecutor;
    }

    @Bean
    public StreamListener<String, ObjectRecord<String, String>> tfmsStreamListener(){
        return record -> {
            log.info(Thread.currentThread().getName()+" - consumed :" +record.getValue());
            String message = JSONObject.parseObject(record.getValue(),String.class);
            TfmsStreamTaskInfo taskInfo = JSONObject.parseObject(message,TfmsStreamTaskInfo.class);
            TfmsStreamTask task = customObjectManager.getCustomObject(taskInfo.getService(),TfmsStreamTask.class);
            task.onMessage(taskInfo);

            redisTemplate.opsForStream().acknowledge(breachGroupKey,record);
        };
    }

    @Bean
    @SneakyThrows
    public StreamMessageListenerContainer<String, ObjectRecord<String, String>> subscription(RedisConnectionFactory redisConnectionFactory) {

        Boolean hasKey = redisTemplate.hasKey(breachStreamKey);
        Assert.notNull(hasKey,"发生错误");
        if(!hasKey){
            redisConnectionFactory.getConnection().execute(
                    "XGROUP",
                    "CREATE".getBytes(),
                    redisTemplate.buildKey(breachStreamKey).getBytes(),
                    breachGroupKey.getBytes(),
                    "0-0".getBytes(),
                    "MKSTREAM".getBytes()
            );
        }

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, String>> options
                = StreamMessageListenerContainer
                            .StreamMessageListenerContainerOptions
                            .builder()
                            .pollTimeout(Duration.ofSeconds(1))
                            .targetType(String.class)
                            .executor(threadPoolTaskExecutor())
                            .build();

        StreamMessageListenerContainer<String, ObjectRecord<String, String>> listenerContainer =
                StreamMessageListenerContainer.create(redisConnectionFactory, options);

        listenerContainer.register(
                StreamMessageListenerContainer.StreamReadRequest
                        .builder(StreamOffset.create(redisTemplate.buildKey(breachStreamKey), ReadOffset.lastConsumed()))
                        .errorHandler((error) -> log.error(error.getMessage(), error))
                        .cancelOnError(e -> false)
                        .consumer(Consumer.from(breachGroupKey, InetAddress.getLocalHost().getHostName()))
                        //关闭自动ack确认
                        .autoAcknowledge(false)
                        .build(),
                tfmsStreamListener());

        listenerContainer.start();
        return listenerContainer;
    }

}