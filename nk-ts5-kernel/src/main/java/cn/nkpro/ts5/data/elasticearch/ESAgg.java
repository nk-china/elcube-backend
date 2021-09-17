package cn.nkpro.ts5.data.elasticearch;

import cn.nkpro.ts5.basic.Keep;
import lombok.Data;

import java.util.List;

/**
 * Created by bean on 2020/6/17.
 */
@Keep
@Data
public class ESAgg {
	private String name;
	private List<ESBucket> buckets;
	private Number value;
}
