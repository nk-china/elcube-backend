package cn.nkpro.easis.data.elasticearch;

import cn.nkpro.easis.annotation.Keep;
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
