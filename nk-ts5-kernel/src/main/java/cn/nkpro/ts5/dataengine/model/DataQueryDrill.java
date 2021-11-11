package cn.nkpro.ts5.dataengine.model;

import cn.nkpro.ts5.basic.Keep;
import lombok.Data;

@Keep
@Data
public class DataQueryDrill {
    private String from;
    private Object fromValue;
    private String to;
}