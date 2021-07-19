package cn.nkpro.tfms.platform.services.impl;

import cn.nkpro.tfms.platform.basis.Constants;
import cn.nkpro.tfms.platform.mappers.gen.DefConstantMapper;
import cn.nkpro.tfms.platform.model.po.DefConstant;
import cn.nkpro.tfms.platform.model.po.DefConstantExample;
import cn.nkpro.tfms.platform.services.TfmsDefConstantService;
import cn.nkpro.tfms.platform.services.TfmsDefDeployAble;
import cn.nkpro.ts5.supports.RedisSupport;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class TfmsDefConstantServiceImpl implements TfmsDefConstantService, TfmsDefDeployAble {

    @Autowired
    private DefConstantMapper constantMapper;

    @Autowired
    private RedisSupport<Object> redisSupport;


    @Override
    public List<DefConstant> getAll(){
        DefConstantExample example = new DefConstantExample();
        example.setOrderByClause("CONSTANT_KEY");
        return constantMapper.selectByExampleWithBLOBs(example);
    }

    @Override
    public void doUpdate(List<DefConstant> list){

        constantMapper.deleteByExample(null);
        list.forEach(defConstant -> constantMapper.insert(defConstant));
        redisSupport.delete(Constants.CACHE_DEF_CONSTANT);
    }

    @Override
    public int deployOrder() {
        return 0;
    }

    @Override
    public List<DefConstant> deployExport(JSONObject config) {
        if(config.containsKey("includeConstant")&&config.getBoolean("includeConstant")) {
            return constantMapper.selectByExampleWithBLOBs(null);
        }
        return Collections.emptyList();
    }

    @Override
    public void deployImport(Object data) {
        if(data!=null)
            doUpdate(((JSONArray)data).toJavaList(DefConstant.class));
    }
}
