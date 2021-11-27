package cn.nkpro.easis.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;


@Slf4j
public class TransactionSync {

	private static final ThreadLocal<List<Handler>> tasksRunAfterCommit = new ThreadLocal<>();
	private static final ThreadLocal<List<Handler>> tasksRunBeforeCommit = new ThreadLocal<>();
	private static final ThreadLocal<List<HandlerCompletion>> tasksRunAfterCompletion = new ThreadLocal<>();
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
	public static void runAfterCompletion(FunctionCompletion t){
		if(lock.get()!=null)
			throw new RuntimeException("事务已经开始提交，不能嵌套绑定任务");


		// 启动异步线程，在事务提交后，将需要更新solr索引的数据写入同步队列
		if(TransactionSynchronizationManager.isSynchronizationActive()){

			HandlerCompletion handler = new HandlerCompletion(t, System.currentTimeMillis());

			List<HandlerCompletion> handlers = tasksRunAfterCompletion.get();
			if(handlers==null){
				handlers = new ArrayList<>();
				tasksRunAfterCompletion.set(handlers);
			}

			handlers.add(handler);

			TransactionSynchronizationManager.registerSynchronization(transactionSync);

			if(log.isInfoEnabled()){
				log.info("* 绑定事务任务, 任务将在事务完成后执行, 当前任务数量 = {}",handlers.size());
			}

		}else{
			log.warn("* 当前线程无事务管理，任务将立即执行");
			try {
				t.apply(TransactionSynchronization.STATUS_UNKNOWN);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void run(Function function, Long priority, ThreadLocal<List<Handler>> targetTaskList){
		
		
		if(lock.get()!=null)
			throw new RuntimeException("服务已经开始执行，不能嵌套使用");
		
		
		// 启动异步线程，在事务提交后，将需要更新solr索引的数据写入同步队列
		if(TransactionSynchronizationManager.isSynchronizationActive()){
			
			Handler handler = new Handler(function, priority);
			
			List<Handler> handlers = targetTaskList.get();
			if(handlers==null){
				handlers = new ArrayList<>();
				targetTaskList.set(handlers);
			}
			
			handlers.add(handler);
			
			TransactionSynchronizationManager.registerSynchronization(transactionSync);

			if(log.isInfoEnabled()){
				if(targetTaskList==tasksRunBeforeCommit){
					log.info("* 绑定事务任务, 任务将在事务提交前执行, 当前任务数量 = {}",handlers.size());
				}else{
					log.info("* 绑定事务任务, 任务将在事务提交后执行, 当前任务数量 = {}",handlers.size());
				}
			}

		}else{
			log.warn("* 当前线程无事务管理，任务将立即执行");
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
				log.info("* >>>>>>>> 准备执行事务提交前的任务，任务数量 = {}", handlers.size());

				tasksRunBeforeCommit.remove();

				for(Handler handler : handlers.stream().sorted().collect(Collectors.toList())){
					handler.getTask().apply();
				}
				log.info("* <<<<<<<< 事务提交前的任务执行完成");
			}

			unlock();
		}

		@Override
	    public void afterCommit() {

			lock();
	    	
	    	List<Handler> handlers = tasksRunAfterCommit.get();
			if(handlers!=null) {
				log.info("* >>>>>>>> 准备执行事务提交后的任务，任务数量 = {}", handlers.size());

				tasksRunAfterCommit.remove();

				for (Handler handler : handlers.stream().sorted().collect(Collectors.toList())) {
					try {
						handler.getTask().apply();
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
				log.info("* <<<<<<<< 事务提交后的任务执行完成");
			}

			unlock();
	    }

		@Override
		public void afterCompletion(int status) {
			lock();

			List<HandlerCompletion> handlers = tasksRunAfterCompletion.get();
			if(handlers!=null) {
				log.info("* >>>>>>>> 准备执行事务完成的任务，任务数量 = {}", handlers.size());

				tasksRunAfterCompletion.remove();

				for (HandlerCompletion handler : handlers.stream().sorted().collect(Collectors.toList())) {
					try {
						handler.getTask().apply(status);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
				log.info("* <<<<<<<< 事务完成的任务执行完成");
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

	static class HandlerCompletion implements Comparable<Handler> {

		private FunctionCompletion task;
		private Long priority;

		HandlerCompletion(FunctionCompletion task, Long priority) {
			this.priority = priority;
			this.task = task;
		}

		FunctionCompletion getTask() {
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

	@FunctionalInterface
	public interface FunctionCompletion {
		void apply(int status) throws Exception;
	}
}
