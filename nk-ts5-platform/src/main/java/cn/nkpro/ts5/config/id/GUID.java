package cn.nkpro.ts5.config.id;

import java.util.UUID;

public interface GUID {

    default String nextId(Class type) {
        return UUID.randomUUID().toString();
    }
    default String nextId(Class type,String docType){
        return nextId(type);
    }
}
