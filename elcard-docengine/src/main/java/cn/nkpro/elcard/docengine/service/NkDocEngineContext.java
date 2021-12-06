/*
 * This file is part of ELCard.
 *
 * ELCard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcard.docengine.service;

import cn.nkpro.elcard.docengine.model.DocDefHV;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class NkDocEngineContext {
    private final static ThreadLocal<Stack<String>>         threadLocalLog = new ThreadLocal<>();
    private final static ThreadLocal<List<String>>          threadLocalLock = new ThreadLocal<>();
    private final static ThreadLocal<Map<String, DocDefHV>> threadLocalDocDefs = new ThreadLocal<>();
    //private final static ThreadLocal<Map<String, DocHV>> threadLocalDocs = new ThreadLocal<>();

    private final static String c = "\t";
    private final static String d = "%s %s : ";
    private final static String e = "%s%s";

    public static synchronized void startLog(String opt, String docId){
        Stack<String> logs = threadLocalLog.get();
        if(logs==null){
            logs = new Stack<>();
            threadLocalLog.set(logs);
        }
        logs.push(String.format(d, opt, docId));
    }
    public static synchronized void endLog(){
        Stack<String> logs = threadLocalLog.get();
        if(CollectionUtils.isNotEmpty(logs)){
            logs.pop();
        }
    }
    public static synchronized String currLog(){
        Stack<String> logs = threadLocalLog.get();
        if(CollectionUtils.isNotEmpty(logs)){
            String collect = logs.stream()
                    .map(i -> StringUtils.EMPTY)
                    .collect(Collectors.joining(c));
            return String.format(e,collect,logs.peek());
        }
        return StringUtils.EMPTY;
    }

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

   public static synchronized DocDefHV localDef(String docType, Function<String, DocDefHV> function){
       Map<String, DocDefHV> docMap = threadLocalDocDefs.get();
       if(docMap==null){
            docMap = new ConcurrentHashMap<>();
            threadLocalDocDefs.set(docMap);
       }

       return docMap.computeIfAbsent(docType, function);
   }

//    因为本地单据clone后会导致数据反序列化找不到脚本编译的Class
//    /**
//     * 从当前线程中获取单据，
//     * 如果当前线程中不存在，回调function
//     */
//    public static synchronized DocHV getDoc(String docId, Function<String, DocHV> function){
//
//        if(TransactionSynchronizationManager.isSynchronizationActive()){
//            TransactionSynchronizationManager.registerSynchronization(transactionSync);
//        }
//
//        // 初始化本地线程Map
//        Map<String, DocHV> docMap = threadLocalDocs.get();
//        if(docMap==null){
//            docMap = new ConcurrentHashMap<>();
//            threadLocalDocs.set(docMap);
//        }
//
//        return function.apply(docId);
//
//        DocHV docHV = docMap.get(docId);
//
//        if(docHV!=null){
//            if(log.isInfoEnabled())
//                log.info("{}从本地线程中获取到单据",currLog());
//        }else{
//            // 如果本地线程不存在单据，回调函数
//            docHV = function.apply(docId);
//            if(docHV!=null){
//                // 并将回调函数返回的单据存入本地线程
//                docMap.put(docId,docHV);
//            }
//        }
//
//        // 返回一个克隆的单据对象，避免本地线程中的单据被污染
//        if(docHV!=null){
//            try {
//                return (DocHV) docHV.clone();
//            } catch (CloneNotSupportedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return null;
//    }

//    public static synchronized void setDoc(DocHV doc){
//
//        if(TransactionSynchronizationManager.isSynchronizationActive()){
//            TransactionSynchronizationManager.registerSynchronization(transactionSync);
//        }
//
//        Map<String, DocHV> docMap = threadLocalDocs.get();
//        if(docMap==null){
//            docMap = new ConcurrentHashMap<>();
//            threadLocalDocs.set(docMap);
//        }
//        docMap.put(doc.getDocId(),doc);
//    }

//    public static void clearDoc(String docId){
//        if(threadLocalDocs.get()!=null)
//            threadLocalDocs.get().remove(docId);
//    }

    public static synchronized void lockDoc(String docId){
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

    public static synchronized void unlockDoc(String docId){
        List<String> locks = threadLocalLock.get();
        if(locks!=null)
            locks.remove(docId);
    }

    public static void clear(){
        threadLocalLock.remove();
        threadLocalLog.remove();
        threadLocalDocDefs.remove();
//        threadLocalDocs.remove();
    }


//    private static TransactionSynchronization transactionSync = new TransactionSynchronizationAdapter() {
//        @Override
//        public void afterCompletion(int status) {
//            clear();
//        }
//    };
}
