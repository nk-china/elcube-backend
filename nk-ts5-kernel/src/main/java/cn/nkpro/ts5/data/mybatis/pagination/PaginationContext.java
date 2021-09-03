package cn.nkpro.ts5.data.mybatis.pagination;


public class PaginationContext {

	private Integer total;

	public Integer getTotal() {
		return this.total;
	}

	void setTotal(Integer total) {
		this.total = total;
	}

	/***
	 * ThreadLocal设置每次查询的查询参数，通过mybatis的拦截，从线程中获取查询参数，并将查询的总数放在参数中，
	 * 注意的问题是如果一个功能需要查询多个分页时， 则需要每次都重新设置context中PageContext的参数值
	 */

	private static ThreadLocal<PaginationContext> context = new ThreadLocal<PaginationContext>();

	static PaginationContext getContext() {
		return context.get();
	}

	public static void removeContext() {
		context.remove();
	}

	public static PaginationContext init() {
		PaginationContext pageContext = new PaginationContext();
		context.set(pageContext);
		return pageContext;
	}

}