package cn.nkpro.ts5.engine.elasticearch;

import lombok.Data;

import java.util.List;

/**
 * Created by bean on 2020/6/17.
 */
@Data
public class ESAgg {
	private String name;
	private List<ESBucket> buckets;
	private Number value;
}
