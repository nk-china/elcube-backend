package cn.nkpro.tfms.platform.model.util;

import lombok.Getter;

import java.util.List;

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
		this.page = from/size+1;
		this.total = total;
		this.max = total == 0 ? 0
				: ((total / size) + (total % size == 0 ? 0 : 1));
		this.end = from + list.size() - 1;
	}
}
