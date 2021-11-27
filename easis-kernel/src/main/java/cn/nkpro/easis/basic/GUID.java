package cn.nkpro.easis.basic;

import java.util.UUID;

public interface GUID {

    default String nextId(Class type) {
        return UUID.randomUUID().toString();
    }
    default String nextId(Class type,String docType){
        return nextId(type);
    }
}
