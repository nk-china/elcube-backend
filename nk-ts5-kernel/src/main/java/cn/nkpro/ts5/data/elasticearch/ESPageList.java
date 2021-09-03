package cn.nkpro.ts5.data.elasticearch;

import cn.nkpro.ts5.basic.PageList;
import lombok.Getter;

import java.util.List;
import java.util.Map;

public class ESPageList<T> extends PageList<T> {

	@Getter
	private Map<String,ESAgg> aggs;

	public ESPageList(List<T> list, Map<String,ESAgg> aggs, int from, int size, long total) {
		super(list,from,size,total);
		this.aggs = aggs;
	}
}


