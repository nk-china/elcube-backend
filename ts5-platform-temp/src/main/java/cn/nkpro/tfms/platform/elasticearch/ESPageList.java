package cn.nkpro.tfms.platform.elasticearch;

import cn.nkpro.tfms.platform.model.util.PageList;
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


