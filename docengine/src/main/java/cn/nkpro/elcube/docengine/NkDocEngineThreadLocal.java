package cn.nkpro.elcube.docengine;

import cn.nkpro.elcube.docengine.model.DocDefHV;
import cn.nkpro.elcube.docengine.model.DocHV;
import cn.nkpro.elcube.exception.NkSystemException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
public class NkDocEngineThreadLocal {

    final static ThreadLocal<List<String>>          threadLocalLock = new ThreadLocal<>();
    final static ThreadLocal<Map<String, DocDefHV>> threadLocalDocDefs = new ThreadLocal<>();
    final static ThreadLocal<Map<String, DocHV>>    threadLocalDocUpdated = new ThreadLocal<>();

    final static ThreadLocal<DocHV>                 threadLocalCurr = new ThreadLocal<>();

    public static DocDefHV localDef(String docType, Function<String, DocDefHV> function){
        Map<String, DocDefHV> docMap = threadLocalDocDefs.get();
        if(docMap==null){
            docMap = new ConcurrentHashMap<>();
            threadLocalDocDefs.set(docMap);
        }

        return docMap.computeIfAbsent(docType, function);
    }

    public static void unlockDoc(String docId){
        List<String> locks = threadLocalLock.get();
        if(locks!=null)
            locks.remove(docId);
    }

    public static void lockDoc(String docId){
        List<String> locks = threadLocalLock.get();
        if(locks==null){
            locks = new Vector<>();
            threadLocalLock.set(locks);
        }
        if(locks.contains(docId)){
            throw new RuntimeException("禁止在组件中对当前单据进行读取与更新操作");
        }
        locks.add(docId);
    }

    public static void setCurr(DocHV doc){
        threadLocalCurr.set(doc);
    }

    public static void clearCurr(){
        threadLocalCurr.remove();
    }

    public static DocHV getCurr(){
        return threadLocalCurr.get();
    }

    public static void addUpdated(DocHV doc){
        if(threadLocalDocUpdated.get()==null){
            threadLocalDocUpdated.set(new ConcurrentHashMap<>());
        }
        threadLocalDocUpdated.get().put(doc.getDocId(),doc);
    }

    public static DocHV getUpdated(String docId){
        DocHV doc = threadLocalDocUpdated.get()!=null ? threadLocalDocUpdated.get().get(docId) : null;
        if(doc==null){
            throw new NkSystemException("没有找到被修改的单据");
        }
        return doc;
    }
}
