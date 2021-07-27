package cn.nkpro.ts5.engine.doc;

import cn.nkpro.ts5.engine.doc.model.DocHV;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadLocalContextHolder {
    private final static ThreadLocal<List<String>>            threadLocal     = new ThreadLocal<>();
    private final static ThreadLocal<Map<String, DocHV>> threadLocalDocs = new ThreadLocal<>();
    private final static ThreadLocal<DocHV>              threadLocalRuntimeDoc = new ThreadLocal<>();


   /*
    *
    * 警告： 这里不能使用computeIfAbsent
    * 由于在读取或创建 doc 的过程中，会递归读取或创建其他 doc，
    * 因此使用 computeIfAbsent 方法会导致线程安全的ConcurrentHashMap的数据在内部被修改，
    * 而抛出new IllegalStateException("Recursive update")的问题
    public static synchronized BizDocBase computeBizDocIfAbsent(String docId, Function<String, BizDocBase> mappingFunction){
        Map<String, BizDocBase> docMap = threadLocalDocs.get();
        if(docMap==null){
            docMap = new ConcurrentHashMap<>();
            threadLocalDocs.set(docMap);
        }
        return docMap.computeIfAbsent(docId,mappingFunction);
    }
    */

    public static synchronized DocHV getBizDoc(String docId){

        if(TransactionSynchronizationManager.isSynchronizationActive()){
            TransactionSynchronizationManager.registerSynchronization(transactionSync);
        }

        Map<String, DocHV> docMap = threadLocalDocs.get();
        if(docMap==null){
            docMap = new ConcurrentHashMap<>();
            threadLocalDocs.set(docMap);
        }
        return docMap.get(docId);
    }

    public static synchronized void setBizDoc(DocHV doc){

        if(TransactionSynchronizationManager.isSynchronizationActive()){
            TransactionSynchronizationManager.registerSynchronization(transactionSync);
        }

        Map<String, DocHV> docMap = threadLocalDocs.get();
        if(docMap==null){
            docMap = new ConcurrentHashMap<>();
            threadLocalDocs.set(docMap);
        }
        docMap.put(doc.getDocId(),doc);
    }

    public static void clearDoc(String docId){
        if(threadLocalDocs.get()!=null)
            threadLocalDocs.get().remove(docId);
    }

    public static synchronized void lockBizDoc(String docId){
        List<String> locks = threadLocal.get();
        if(locks==null){
            locks = new Vector<>();
            threadLocal.set(locks);
        }
        if(locks.contains(docId)){
            throw new RuntimeException("禁止在组件中对当前单据进行读取与更新操作");
        }
        locks.add(docId);
    }

    public static synchronized void unlockBizDoc(String docId){
        List<String> locks = threadLocal.get();
        if(locks!=null)
            locks.remove(docId);
    }

    public static synchronized void setRuntimeDoc(DocHV doc){
        threadLocalRuntimeDoc.set(doc);
    }

    public static synchronized void clearRuntimeDoc(){
        threadLocalRuntimeDoc.remove();
    }

    public static synchronized DocHV getRuntimeDoc(){
        return threadLocalRuntimeDoc.get();
    }

    public static void clear(){
        threadLocal.remove();
        threadLocalDocs.remove();
        threadLocalRuntimeDoc.remove();
    }


    private static TransactionSynchronization transactionSync = new TransactionSynchronizationAdapter() {
        @Override
        public void afterCommit() {
            clear();
        }
    };
}
