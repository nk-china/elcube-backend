package cn.nkpro.tfms.platform.services.impl;

import cn.nkpro.tfms.platform.mappers.gen.BizDocPropsMapper;
import cn.nkpro.tfms.platform.model.po.BizDocProps;
import cn.nkpro.tfms.platform.model.po.BizDocPropsExample;
import cn.nkpro.tfms.platform.services.TfmsDocPropsService;
import cn.nkpro.ts5.supports.GUID;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by bean on 2020/8/4.
 */
@Service
public class TfmsDocPropsServiceImpl implements TfmsDocPropsService {

    @Autowired
    private GUID guid;
    @Autowired
    private BizDocPropsMapper docPropsMapper;

    public void create(String docId, String key, String value){
        BizDocProps prop = new BizDocProps();
        prop.setId(guid.nextId(BizDocProps.class));
        prop.setPropKey(key);
        prop.setPropValue(value);
        prop.setDocId(docId);
        docPropsMapper.insert(prop);
    }

    @Override
    public void update(String docId, String keyPrefix, Map<String,Object> dictionary){
        BizDocPropsExample example = new BizDocPropsExample();
        example.createCriteria()
                .andDocIdEqualTo(docId)
                .andPropKeyLike(String.format("%s%%",keyPrefix));
        docPropsMapper.deleteByExample(example);

        if(dictionary!=null){

            dictionary.forEach((key,value)->{
                BizDocProps prop = new BizDocProps();
                prop.setId(guid.nextId(BizDocProps.class));
                prop.setDocId(docId);
                prop.setPropKey(String.format("%s:%s",keyPrefix,key));
                prop.setPropValue(JSON.toJSONString(value));
                docPropsMapper.insert(prop);
            });
        }
    }

    @Override
    public Map<String,Object> getDicByDoc(String docId, String keyPrefix) {
        return getByDoc(docId, keyPrefix)
                .stream()
                .collect(Collectors.toMap(
                        (item)->item.getPropKey().contains(keyPrefix)
                                ?item.getPropKey().substring(keyPrefix.length()+1)
                                :item.getPropKey(),
                        (item)-> JSON.parse(item.getPropValue())
                ));
    }

    @Override
    public List<BizDocProps> getByDoc(String docId, String keyPrefix){
        BizDocPropsExample example = new BizDocPropsExample();
        example.createCriteria()
                .andDocIdEqualTo(docId)
                .andPropKeyLike(String.format("%s%%",keyPrefix));
        example.setOrderByClause("ORDERBY");

        return docPropsMapper.selectByExample(example);
    }
}
