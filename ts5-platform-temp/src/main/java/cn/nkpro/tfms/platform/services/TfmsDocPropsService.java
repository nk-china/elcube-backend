package cn.nkpro.tfms.platform.services;

import cn.nkpro.tfms.platform.model.po.BizDocProps;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by bean on 2020/8/4.
 */
public interface TfmsDocPropsService {
    void update(String docId, String keyPrefix, Map<String,Object> map);

    Map<String,Object> getDicByDoc(String docId, String keyPrefix);

    List<BizDocProps> getByDoc(String docId, String keyPrefix);
}
