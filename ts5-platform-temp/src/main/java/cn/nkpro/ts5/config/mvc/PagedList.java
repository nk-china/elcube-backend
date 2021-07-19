package cn.nkpro.ts5.config.mvc;

import cn.nkpro.ts5.config.mybatis.pagination.PaginationContext;
import org.apache.ibatis.session.RowBounds;

import java.util.ArrayList;
import java.util.List;

public class PagedList<T> extends ArrayList<T>{

	public static final int DEFAULT_SIZE = 10;

	private static final long serialVersionUID = 1L;

	protected int rows;

	protected long start;

	protected long end;

	protected long total;

	public PagedList() {}


	public static Paging startPaging(){
		Paging ret = new Paging();
		ret.ctx = PaginationContext.init();
		return ret;
	}

	public static class Paging{

		private PaginationContext ctx;

		public Integer getTotal() {
			return ctx.getTotal();
		}
	}

	public static RowBounds paging(int page) {
		return paging(page, DEFAULT_SIZE);
	}

	public static RowBounds paging(int page, Integer limit) {
		if(limit == null || limit < 0){
			limit = DEFAULT_SIZE;
		}
		if (page < 1) {
			page = 1;
		}
		return new RowBounds((page - 1) * limit, limit);
	}

	public static <R> PagedList<R> fromList(List<R> list,int rows,int page,long total){
		PagedList<R> plist = new PagedList<>();
		plist.addAll(list);
		plist.rows = rows;
		plist.start = (page-1)*rows;
		plist.end = plist.start + list.size() - 1;
		plist.total = total;
		return plist;
	}

	public int getRows() {
		return rows;
	}

	public long getStart() {
		return start;
	}

	public long getEnd() {
		return end;
	}

	public long getTotal() {
		return total;
	}

}
