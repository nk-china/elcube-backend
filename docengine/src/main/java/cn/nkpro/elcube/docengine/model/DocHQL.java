package cn.nkpro.elcube.docengine.model;

import cn.nkpro.elcube.docengine.gen.DocH;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DocHQL extends DocH implements Map<String,Object> {

    private Map<String,Object> target = new HashMap<>();

    @Override
    public String getDocId() {
        return (String) target.get("docId");
    }

    @Override
    public void setDocId(String docId) {
        target.put("docId", docId);
    }

    @Override
    public String getClassify() {
        return (String) target.get("classify");
    }

    @Override
    public void setClassify(String classify) {
        target.put("classify", classify);
    }

    @Override
    public String getDefVersion() {
        return (String) target.get("defVersion");
    }

    @Override
    public void setDefVersion(String defVersion) {
        target.put("defVersion", defVersion);
    }

    @Override
    public String getDocType() {
        return (String) target.get("docType");
    }

    @Override
    public void setDocType(String docType) {
        target.put("docType", docType);
    }

    @Override
    public String getDocName() {
        return (String) target.get("docName");
    }

    @Override
    public void setDocName(String docName) {
        target.put("docName", docName);
    }

    @Override
    public String getDocDesc() {
        return (String) target.get("docDesc");
    }

    @Override
    public void setDocDesc(String docDesc) {
        target.put("docDesc", docDesc);
    }

    @Override
    public String getDocNumber() {
        return (String) target.get("docNumber");
    }

    @Override
    public void setDocNumber(String docNumber) {
        target.put("docNumber", docNumber);
    }

    @Override
    public String getDocState() {
        return (String) target.get("docState");
    }

    @Override
    public void setDocState(String docState) {
        target.put("docState", docState);
    }

    @Override
    public String getDocTags() {
        return (String) target.get("docTags");
    }

    @Override
    public void setDocTags(String docTags) {
        target.put("docTags", docTags);
    }

    @Override
    public String getPreDocId() {
        return (String) target.get("preDocId");
    }

    @Override
    public void setPreDocId(String preDocId) {
        target.put("preDocId", preDocId);
    }

    @Override
    public String getPartnerId() {
        return (String) target.get("partnerId");
    }

    @Override
    public void setPartnerId(String partnerId) {
        target.put("partnerId", partnerId);
    }

    @Override
    public String getIdentification() {
        return (String) target.get("identification");
    }

    @Override
    public void setIdentification(String identification) {
        target.put("identification", identification);
    }

    @Override
    public String getRefObjectId() {
        return (String) target.get("refObjectId");
    }

    @Override
    public void setRefObjectId(String refObjectId) {
        target.put("refObjectId", refObjectId);
    }

    @Override
    public String getBusinessKey() {
        return (String) target.get("businessKey");
    }

    @Override
    public void setBusinessKey(String businessKey) {
        target.put("businessKey", businessKey);
    }

    @Override
    public String getProcessInstanceId() {
        return (String) target.get("processInstanceId");
    }

    @Override
    public void setProcessInstanceId(String processInstanceId) {
        target.put("processInstanceId", processInstanceId);
    }

    @Override
    public Long getCreatedTime() {
        return (Long) target.get("createdTime");
    }

    @Override
    public void setCreatedTime(Long createdTime) {
        target.put("createdTime", createdTime);
    }

    @Override
    public Long getUpdatedTime() {
        return (Long) target.get("updatedTime");
    }

    @Override
    public void setUpdatedTime(Long updatedTime) {
        target.put("updatedTime", updatedTime);
    }

    ///// map

    @Override
    public int size() {
        return target.size();
    }

    @Override
    public boolean isEmpty() {
        return target.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return target.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return target.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return target.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return target.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return target.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends String,?> m) {
        target.putAll(m);
    }

    @Override
    public void clear() {
        target.clear();
    }

    @Override
    public Set<String> keySet() {
        return target.keySet();
    }

    @Override
    public Collection<Object> values() {
        return target.values();
    }

    @Override
    public Set<Entry<String,Object>> entrySet() {
        return target.entrySet();
    }
}
