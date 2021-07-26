package cn.nkpro.ts5.engine.doc.impl;

import cn.nkpro.ts5.basic.Constants;
import cn.nkpro.ts5.engine.doc.service.NKConstantService;
import cn.nkpro.ts5.model.mb.gen.ConstantDef;
import cn.nkpro.ts5.model.mb.gen.ConstantDefExample;
import cn.nkpro.ts5.model.mb.gen.ConstantDefMapper;
import cn.nkpro.ts5.supports.RedisSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NKConstantServiceImpl implements NKConstantService {

    @Autowired
    private ConstantDefMapper constantMapper;

    @Autowired
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
