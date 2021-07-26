package cn.nkpro.ts5.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;


@Slf4j
public class LocalSyncUtilz {

	private static final ThreadLocal<List<Handler>> tasksRunAfterCommit = new ThreadLocal<>();
	private static final ThreadLocal<List<Handler>> tasksRunBeforeCommit = new ThreadLocal<>();
	private static final ThreadLocal<Boolean> lock = new ThreadLocal<>();


	public static void runBeforeCommit(Function t){
		run(t, System.currentTimeMillis(), tasksRunBeforeCommit);
	}
	/**
	 * <p>在事务提交后执行函数
	 * <p>将函数放置队列中间，在{@link #runAfterCommitLast(Function)}之前
	 * <p>如果当前上下文没有事务，则立即执行
	 * @param t t
	 */
	public static void runAfterCommit(Function t){
		run(t, System.currentTimeMillis(), tasksRunAfterCommit);
	}
	
	/**
	 * <p>在事务提交后执行函数
	 * <p>将函数放置队列最后执行
	 * <p>如果当前上下文没有事务，则立即执行
	 * @param t
	 */
	@SuppressWarnings("all")
	public static void runAfterCommitLast(Function t){
		run(t, Short.MAX_VALUE+System.currentTimeMillis(), tasksRunAfterCommit);
	}

	private static void run(Function function, Long priority, ThreadLocal<List<Handler>> targetTaskList){
		
		
		if(lock.get()!=null)
			throw new RuntimeException("服务已经开始执行，不能嵌套使用");
		
		
		// 启动异步线程，在事务提交后，将需要更新solr索引的数据写入同步队列
		if(TransactionSynchronizationManager.isSynchronizationActive()){
			
			log.info("当前线程有事务管理，加入本地任务");
			
			Handler handler = new Handler(function, priority);
			
			List<Handler> handlers = targetTaskList.get();
			if(handlers==null){
				handlers = new ArrayList<>();
				targetTaskList.set(handlers);
			}
			
			handlers.add(handler);
			
			TransactionSynchronizationManager.registerSynchronization(transactionSync);
		}else{
			log.info("当前线程无事务管理，直接执行任务");
            try {
                function.apply();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
	
	private static TransactionSynchronization transactionSync = new TransactionSynchronizationAdapter() {

		@SneakyThrows
		@Override
		public void beforeCommit(boolean readOnly) {
			super.beforeCommit(readOnly);

			lock();

			List<Handler> handlers = tasksRunBeforeCommit.get();
			if(handlers!=null){
				log.info("开始执行事务提交前任务");

				tasksRunBeforeCommit.remove();

				for(Handler handler : handlers.stream().sorted().collect(Collectors.toList())){
					handler.getTask().apply();
				}
				log.info("事务提交前任务处理完成");
			}

			unlock();
		}

		@Override
	    public void afterCommit() {

			lock();
	    	
	    	List<Handler> handlers = tasksRunAfterCommit.get();
			if(handlers!=null) {
				log.info("开始处理本地任务");
				tasksRunAfterCommit.remove();

				for (Handler handler : handlers.stream().sorted().collect(Collectors.toList())) {
					try {
						handler.getTask().apply();
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
				log.info("本地任务处理完成");
			}

			unlock();
	    }
	};

	private static void lock(){
		lock.set(true);
	}

	private static void unlock(){
		lock.remove();
	}

	static class Handler implements Comparable<Handler> {

		private Function task;
		private Long priority;

		Handler(Function task, Long priority) {
			this.priority = priority;
			this.task = task;
		}

		Function getTask() {
			return task;
		}

		@Override
		public int compareTo(Handler other) {
			return priority.compareTo(other.priority);
		}
	}

    @FunctionalInterface
    public interface Function {
        void apply() throws Exception;
    }
}
