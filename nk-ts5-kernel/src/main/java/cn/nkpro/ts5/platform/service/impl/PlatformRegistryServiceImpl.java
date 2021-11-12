package cn.nkpro.ts5.platform.service.impl;

import cn.nkpro.ts5.basic.Constants;
import cn.nkpro.ts5.data.redis.RedisSupport;
import cn.nkpro.ts5.platform.service.PlatformRegistryService;
import cn.nkpro.ts5.platform.gen.PlatformRegistry;
import cn.nkpro.ts5.platform.gen.PlatformRegistryExample;
import cn.nkpro.ts5.platform.gen.PlatformRegistryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlatformRegistryServiceImpl implements PlatformRegistryService {

    @Autowired@SuppressWarnings("all")
    private PlatformRegistryMapper constantMapper;

    @Autowired@SuppressWarnings("all")
    private RedisSupport<Object> redisSupport;


    @Override
    public List<PlatformRegistry> getAll(){
        PlatformRegistryExample example = new PlatformRegistryExample();
        example.setOrderByClause("CONSTANT_KEY");
        return constantMapper.selectByExampleWithBLOBs(example);
    }

    @Override
    public void doUpdate(List<PlatformRegistry> list){

        constantMapper.deleteByExample(null);
        list.forEach(defConstant -> constantMapper.insert(defConstant));
        redisSupport.delete(Constants.CACHE_DEF_CONSTANT);
    }
}
