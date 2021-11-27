package cn.nkpro.easis.basic;

import cn.nkpro.easis.annotation.Keep;
import lombok.Getter;

import java.util.List;

@Keep
public class PageList<T>{

	@Getter
	private int page;
	@Getter
	private int rows;
	@Getter
	private long total;
	@Getter
	private int from;
	@Getter
	private int end;
	@Getter
	private long max;
	@Getter
	private List<T> list;

	public PageList(List<T> list, int from, int size, long total) {
		this.list = list;
		this.from = from;
		this.rows = size;
		this.page = size!=0?(from/size+1):1;
		this.total = total;
		this.max = size == 0 || total == 0 ? 0 : ((total / size) + (total % size == 0 ? 0 : 1));
		this.end = from + list.size() - 1;
	}
}
