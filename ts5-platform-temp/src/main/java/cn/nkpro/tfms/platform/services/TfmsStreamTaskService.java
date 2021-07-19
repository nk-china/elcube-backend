package cn.nkpro.tfms.platform.services;

import cn.nkpro.tfms.platform.config.TfmsRedisStreamConfig;
import cn.nkpro.ts5.config.NKRedisTemplate;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class TfmsStreamTaskService {

    @Autowired
    private NKRedisTemplate<String> redisTemplate;

    public void createTask(String service,String body){

        StreamOperations<String, Object, String> ops = redisTemplate.opsForStream();

        TfmsStreamTaskInfo taskInfo = new TfmsStreamTaskInfo();
        taskInfo.setService(service);
        taskInfo.setBody(body);

        ObjectRecord<String, String> record = StreamRecords.newRecord()
                .ofObject(JSON.toJSON(taskInfo).toString())
                .withStreamKey(TfmsRedisStreamConfig.breachStreamKey);
        ops.add(record);
    }

    public void doInLock(Object obj,Function func){
        Boolean lock= false;
        try{
            ValueOperations<String, String> opsString = redisTemplate.opsForValue();
            lock = opsString.setIfAbsent(obj.getClass().getName(), obj.getClass().getName());
            if(lock!=null && lock){
                func.apply();
            }
        }finally {
            if(lock!=null && lock){
                redisTemplate.delete(obj.getClass().getName());
            }
        }
    }

    public interface Function{
        void apply();
    }
}
