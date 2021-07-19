package cn.nkpro.tfms.platform.model;

import cn.nkpro.tfms.platform.model.po.DefDocComponent;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BizDocComponentsData extends HashMap<String,Object> {

//    private BizDocBase doc;
//
//    private Map<String,Object> datas;
//
//    public BizDocComponentsData() {
//        this.datas = new HashMap<>();
//    }
//
//    void setDoc(BizDocBase doc) {
//        this.doc = doc;
//    }
//
//    @Override
//    public boolean containsKey(Object key) {
//
//        if(datas.containsKey(key)){
//            return true;
//        }
//
//        return doc.getDefinedDoc()
//                .getCustomComponents()
//                .stream()
//                .filter(defDocComponentBO -> StringUtils.equalsAny((String) key, defDocComponentBO.getComponent(), defDocComponentBO.getComponentMapping()))
//                .findFirst()
//                .map(defDocComponentBO -> datas.containsKey(defDocComponentBO.getComponentMapping()))
//                .orElse(false);
//    }
//
//    @Override
//    public Object get(Object key) {
//
//        if(datas.containsKey(key)){
//            return datas.get(key);
//        }
//
//        return doc.getDefinedDoc()
//                .getCustomComponents()
//                .stream()
//                .filter(defDocComponentBO -> StringUtils.equalsAny((String) key, defDocComponentBO.getComponent(), defDocComponentBO.getComponentMapping()))
//                .findFirst()
//                .map(defDocComponentBO -> datas.get(defDocComponentBO.getComponentMapping()))
//                .orElse(null);
//    }
//
//    @Override
//    public Object put(String key, Object value) {
//
//        if(doc==null){
//            return datas.put(key, value);
//        }
//
//        return doc.getDefinedDoc()
//                .getCustomComponents()
//                .stream()
//                .filter(defDocComponentBO -> StringUtils.equalsAny(key, defDocComponentBO.getComponent(), defDocComponentBO.getComponentMapping()))
//                .findFirst()
//                .map(defDocComponentBO -> datas.put(defDocComponentBO.getComponentMapping(),value))
//                .orElse(null);
//    }
//
//    @Override
//    public void putAll(Map<? extends String, ?> m) {
//        if(doc==null){
//            datas.putAll(m);
//            return;
//        }
//        datas.putAll(m.entrySet()
//                .stream()
//                .collect(
//                        Collectors.toMap(
//                                (entry)-> doc.getDefinedDoc()
//                                    .getCustomComponents()
//                                    .stream()
//                                    .filter(defDocComponentBO -> StringUtils.equalsAny(entry.getKey(), defDocComponentBO.getComponent(), defDocComponentBO.getComponentMapping()))
//                                    .findFirst()
//                                    .map(DefDocComponent::getComponentMapping)
//                                    .orElse(null),
//                                Entry::getValue
//                        )
//                )
//        );
//    }
//
//    @Override
//    public Object remove(Object key) {
//
//        if(datas.containsKey(key)){
//            return datas.remove(key);
//        }
//
//        return doc.getDefinedDoc()
//                .getCustomComponents()
//                .stream()
//                .filter(defDocComponentBO -> StringUtils.equalsAny((String) key, defDocComponentBO.getComponent(), defDocComponentBO.getComponentMapping()))
//                .findFirst()
//                .map(defDocComponentBO -> datas.remove(defDocComponentBO.getComponentMapping()))
//                .orElse(null);
//    }
//
//    @Override
//    public void clear() {
//        datas.clear();
//    }
//
//    @Override
//    public Set<String> keySet() {
//        return datas.keySet();
//    }
//
//    @Override
//    public Collection<Object> values() {
//        return datas.values();
//    }
//
//    @Override
//    public Set<Entry<String, Object>> entrySet() {
//        return datas.entrySet();
//    }
//
//    @Override
//    public int size() {
//        return datas.size();
//    }
//
//    @Override
//    public boolean isEmpty() {
//        return datas.isEmpty();
//    }
//
//    @Override
//    public boolean containsValue(Object value) {
//        return datas.containsValue(value);
//    }

}
