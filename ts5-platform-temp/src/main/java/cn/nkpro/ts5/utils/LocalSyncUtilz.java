package cn.nkpro.ts5.utils;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;


@Slf4j
public class LocalSyncUtilz {

	private static final ThreadLocal<List<Handler>> tasks = new ThreadLocal<>();
	private static final ThreadLocal<Boolean> lock = new ThreadLocal<>();


	/**
	 * <p>在事务提交后执行函数
	 * <p>将函数放置队列中间，在{@link #runAfterCommitLast(RunAfterCommit)}之前
	 * <p>如果当前上下文没有事务，则立即执行
	 * @param t t
	 */
	public static void runAfterCommit(RunAfterCommit t){
		run(t, System.currentTimeMillis());
	}
	
	/**
	 * <p>在事务提交后执行函数
	 * <p>将函数放置队列最后执行
	 * <p>如果当前上下文没有事务，则立即执行
	 * @param t
	 */
	@SuppressWarnings("all")
	public static void runAfterCommitLast(RunAfterCommit t){
		run(t, Short.MAX_VALUE+System.currentTimeMillis());
	}
	
	private static void run(RunAfterCommit tasker, Long priority){
		
		
		if(lock.get()!=null)
			throw new RuntimeException("服务已经开始执行，不能嵌套使用");
		
		
		// 启动异步线程，在事务提交后，将需要更新solr索引的数据写入同步队列
		if(TransactionSynchronizationManager.isSynchronizationActive()){
			
			log.info("当前线程有事务管理，加入本地任务");
			
			Handler handler = new Handler(tasker, priority);
			
			List<Handler> handlers = tasks.get();
			if(handlers==null){
				handlers = new ArrayList<>();
				tasks.set(handlers);
			}
			
			handlers.add(handler);
			
			TransactionSynchronizationManager.registerSynchronization(transactionSync);
		}else{
			log.info("当前线程无事务管理，直接执行任务");
            try {
                tasker.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
	
	private static TransactionSynchronization transactionSync = new TransactionSynchronizationAdapter() {
	    @Override
	    public void afterCommit() {

			log.info("开始处理本地任务");
	    	lock.set(true);
	    	
	    	List<Handler> taskers = tasks.get();
			tasks.remove();

            taskers.stream()
                .sorted()
                .forEach((t) -> {
                    try {
                        t.getTasker().run();
                    }catch (Exception e){
                        log.error(e.getMessage(),e);
                    }
                });

			lock.remove();
			log.info("本地任务处理完成");
	    }
	};

	static class Handler implements Comparable<Handler> {

		private RunAfterCommit tasker;
		private Long priority;

		Handler(RunAfterCommit tasker, Long priority) {
			this.priority = priority;
			this.tasker = tasker;
		}

		RunAfterCommit getTasker() {
			return tasker;
		}

		@Override
		public int compareTo(Handler other) {
			return priority.compareTo(other.priority);
		}
	}

    @FunctionalInterface
    public interface RunAfterCommit {
        void run() throws Exception;
    }
}
