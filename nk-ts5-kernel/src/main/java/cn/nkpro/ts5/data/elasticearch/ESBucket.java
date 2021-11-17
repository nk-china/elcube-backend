package cn.nkpro.ts5.data.elasticearch;

import cn.nkpro.ts5.annotation.Keep;
import lombok.Data;

@Keep
@Data
public class ESBucket {
    private String key;
	private Long docCount;
	private Number sum;
	private Number max;
	private Number min;
	private Number avg;
}