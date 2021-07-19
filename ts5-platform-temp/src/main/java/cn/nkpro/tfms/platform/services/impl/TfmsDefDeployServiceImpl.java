package cn.nkpro.tfms.platform.services.impl;

import cn.nkpro.tfms.platform.services.*;
import cn.nkpro.ts5.supports.RedisSupport;
import cn.nkpro.ts5.utils.TextUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TfmsDefDeployServiceImpl implements TfmsDefDeployService, ApplicationContextAware {

    private ApplicationContext applicationContext;
    private RedisSupport<String> redisSupport;

    @Autowired
    public TfmsDefDeployServiceImpl(RedisSupport<String> redisSupport) {
        this.redisSupport = redisSupport;
    }

//    @Override
//    public String buildExportKey(JSONObject config){
//
//        if(config!=null){
//
//            Map<String,Object> def = applicationContext.getBeansOfType(TfmsDefDeployAble.class)
//                    .entrySet()
//                    .stream()
//                    .collect(Collectors.toMap(Map.Entry::getKey,(e)->e.getValue().deployExport(config)));
//
//            String export = JSON.toJSONString(def);
//            if(Objects.equals(config.getBoolean("compress"),true)){
//                export = TextUtils.compress(export);
//            }
//
//            String key = UUID.randomUUID().toString();
//            redisSupport.set(key,export);
//            redisSupport.expire(key,10);
//            return key;
//        }
//
//        return null;
//    }

    @Override
    public String defExport(JSONObject config){
        if(config!=null){

            Map<String,Object> def = applicationContext.getBeansOfType(TfmsDefDeployAble.class)
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey,(e)->e.getValue().deployExport(config)));

            String export = JSON.toJSONString(def);
            if(Objects.equals(config.getBoolean("compress"),true)){
                export = TextUtils.compress(export);
            }

//            String key = UUID.randomUUID().toString();
//            redisSupport.set(key,export);
//            redisSupport.expire(key,10);
            return export;
        }

        return null;
    }

    @Override
    @Transactional
    public void defImport(String pointsTxt){

        String uncompress = pointsTxt.startsWith("H4sIAAAAA")?TextUtils.uncompress(pointsTxt):pointsTxt;
        HashMap data = JSON.parseObject(uncompress,HashMap.class);

        Map<String, TfmsDefDeployAble> beans = applicationContext.getBeansOfType(TfmsDefDeployAble.class);
        Map<TfmsDefDeployAble, Object> configs = new HashMap<>();

        beans.forEach((k,v)->configs.put(v,data.get(k)));
        beans.values()
                .stream()
                .sorted(Comparator.comparing(TfmsDefDeployAble::deployOrder))
                .forEach(b->b.deployImport(configs.get(b)));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
