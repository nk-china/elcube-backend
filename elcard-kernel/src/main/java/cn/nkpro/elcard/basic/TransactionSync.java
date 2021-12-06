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
package cn.nkpro.elcard.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 * Spring 事务同步管理器的包装
 *
 * 因为Spring自身的{@link TransactionSynchronizationManager}只能增加一个同步任务，而不能完全满足单据模型的复杂事务机制
 *
 *
 * 主要有3中运行情况
 * 1、beforeCommit 在事务提交前执行，如果事务回滚，不执行
 * 2、afterCommit  在事务提交后执行，如果事务回滚，不执行
 * 3、afterCompletion 在事务完成后执行，不论事务提交与回滚，都会执行
 *
 * beforeCommit 与 afterCommit 的队列区别：
 * beforeCommit 的队列中，一旦任务发生异常，则终止后续的任务
 * afterCommit与afterCompletion  的队列中，即使一个任务发生异常，后续的任务仍然执行
 *
 * last：没有last标记的方法，添加的任务按顺序执行，被标记为last的方法，任务倒序执行
 * 即最先添加到last对列里的任务，最末执行，以此类推
 * 
 * @author bean 2021-12-03
 *
 */
@Slf4j
public class TransactionSync {

	private static final ThreadLocal<List<Handler>> tasksRunAfterCommit = new ThreadLocal<>();
	private static final ThreadLocal<List<Handler>> tasksRunBeforeCommit = new ThreadLocal<>();
	private static final ThreadLocal<List<HandlerCompletion>> tasksRunAfterCompletion = new ThreadLocal<>();
	private static final ThreadLocal<Boolean> lock = new ThreadLocal<>();

	/**
	 * <p>在事务提交前执行函数
	 * <p>如果当前上下文没有事务，则立即执行
	 * @param t t
	 */
	public static void runBeforeCommit(String taskDesc, Function t){
		run(taskDesc, t, System.currentTimeMillis(), tasksRunBeforeCommit);
	}
	/**
	 * <p>在事务提交后执行函数
	 * <p>将函数放置队列中间，在{@link #runAfterCommitLast(String, Function)}之前
	 * <p>如果当前上下文没有事务，则立即执行
	 * @param t t
	 */
	public static void runAfterCommit(String taskDesc, Function t){
		run(taskDesc, t, System.currentTimeMillis(), tasksRunAfterCommit);
	}
	
	/**
	 * <p>在事务提交后执行函数
	 * <p>将函数放置队列后执行
	 * <p>如果当前上下文没有事务，则立即执行
	 * @param t
	 */
	@SuppressWarnings("all")
	public static void runAfterCommitLast(String taskDesc, Function t){
		run(taskDesc, t, Long.MAX_VALUE - System.currentTimeMillis(), tasksRunAfterCommit);
	}


	public static void runAfterCompletion(String taskDesc, FunctionCompletion t){
		runAfterCompletion(taskDesc, t, System.currentTimeMillis());
	}
	public static void runAfterCompletionLast(String taskDesc, FunctionCompletion t){
		runAfterCompletion(taskDesc, t, Long.MAX_VALUE - System.currentTimeMillis());
	}
	private static void runAfterCompletion(String taskDesc, FunctionCompletion function, Long priority){
		if(lock.get()!=null)
			throw new RuntimeException("事务已经开始提交，不能嵌套绑定任务");


		// 启动异步线程，在事务提交后，将需要更新solr索引的数据写入同步队列
		if(TransactionSynchronizationManager.isSynchronizationActive()){

			HandlerCompletion handler = new HandlerCompletion(taskDesc, function, priority);

			List<HandlerCompletion> handlers = tasksRunAfterCompletion.get();
			if(handlers==null){
				handlers = new ArrayList<>();
				tasksRunAfterCompletion.set(handlers);
			}

			handlers.add(handler);

			TransactionSynchronizationManager.registerSynchronization(transactionSync);

			if(log.isInfoEnabled()){
				log.info("* 绑定事务任务, 任务将在事务完成后执行, 当前任务数量 = {}",handlers.size());
				handlers.stream().sorted().forEach(h-> log.info("* -- {}",h.taskDesc));
			}

		}else{
			log.warn("* 当前线程无事务管理，任务将立即执行");
			try {
				function.apply(TransactionSynchronization.STATUS_UNKNOWN);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void run(String taskDesc, Function function, Long priority, ThreadLocal<List<Handler>> targetTaskList){
		
		
		if(lock.get()!=null)
			throw new RuntimeException("服务已经开始执行，不能嵌套使用");
		
		
		// 启动异步线程，在事务提交后，将需要更新solr索引的数据写入同步队列
		if(TransactionSynchronizationManager.isSynchronizationActive()){
			
			Handler handler = new Handler(taskDesc, function, priority);
			
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
				handlers.stream().sorted().forEach(h-> log.info("* -- {}",h.taskDesc));
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


				Handler last = null;
				try{
					for(Handler handler : handlers.stream().sorted().collect(Collectors.toList())){
						last = handler;
						log.info("* >> 执行： {}",handler.taskDesc);
						handler.getTask().apply();
						log.info("* >> 完成： {}",handler.taskDesc);
					}
					log.info("* <<<<<<<< 事务提交前的任务执行完成");
				}catch(RuntimeException e){
					log.error("* >> 错误： {} Exception: {}",last!=null?last.taskDesc:"unknown",e.getMessage(), e);
					log.info("* <<<<<<<< 事务提交前的任务执行终止");
					throw e;
				}
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
						log.info("* >> 执行： {}",handler.taskDesc);
						handler.getTask().apply();
						log.info("* >> 完成： {}",handler.taskDesc);
					} catch (Exception e) {
						log.error("* >> 错误： {} Exception: {}",handler.taskDesc,e.getMessage(), e);
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
						log.info("* >> 执行： {}",handler.taskDesc);
						handler.getTask().apply(status);
						log.info("* >> 完成： {}",handler.taskDesc);
					} catch (Exception e) {
						log.error("* >> 错误： {} Exception: {}",handler.taskDesc,e.getMessage(), e);
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

		private String taskDesc;
		private Function task;
		private Long priority;

		Handler(String taskDesc, Function task, Long priority) {
			this.taskDesc = taskDesc;
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

	static class HandlerCompletion implements Comparable<HandlerCompletion> {

		private String taskDesc;
		private FunctionCompletion task;
		private Long priority;

		HandlerCompletion(String taskDesc, FunctionCompletion task, Long priority) {
			this.taskDesc = taskDesc;
			this.priority = priority;
			this.task = task;
		}

		FunctionCompletion getTask() {
			return task;
		}

		@Override
		public int compareTo(HandlerCompletion other) {
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
