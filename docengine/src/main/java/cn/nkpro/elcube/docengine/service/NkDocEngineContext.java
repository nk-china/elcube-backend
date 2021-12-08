/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.docengine.service;

import cn.nkpro.elcube.docengine.model.DocDefHV;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
public class NkDocEngineContext {
    private final static ThreadLocal<List<String>>          threadLocalLock = new ThreadLocal<>();
    private final static ThreadLocal<Map<String, DocDefHV>> threadLocalDocDefs = new ThreadLocal<>();


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
        threadLocalDocDefs.remove();
    }


//    private static TransactionSynchronization transactionSync = new TransactionSynchronizationAdapter() {
//        @Override
//        public void afterCompletion(int status) {
//            clear();
//        }
//    };
}
