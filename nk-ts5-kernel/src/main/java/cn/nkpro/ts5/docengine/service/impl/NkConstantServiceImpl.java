package cn.nkpro.ts5.docengine.service.impl;

import cn.nkpro.ts5.basic.Constants;
import cn.nkpro.ts5.docengine.service.NkConstantService;
import cn.nkpro.ts5.docengine.gen.ConstantDef;
import cn.nkpro.ts5.docengine.gen.ConstantDefExample;
import cn.nkpro.ts5.docengine.gen.ConstantDefMapper;
import cn.nkpro.ts5.data.redis.RedisSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NkConstantServiceImpl implements NkConstantService {

    @Autowired@SuppressWarnings("all")
    private ConstantDefMapper constantMapper;

    @Autowired@SuppressWarnings("all")
    private RedisSupport<Object> redisSupport;


    @Override
    public List<ConstantDef> getAll(){
        ConstantDefExample example = new ConstantDefExample();
        example.setOrderByClause("CONSTANT_KEY");
        return constantMapper.selectByExampleWithBLOBs(example);
    }

    @Override
    public void doUpdate(List<ConstantDef> list){

        constantMapper.deleteByExample(null);
        list.forEach(defConstant -> constantMapper.insert(defConstant));
        redisSupport.delete(Constants.CACHE_DEF_CONSTANT);
    }
}
