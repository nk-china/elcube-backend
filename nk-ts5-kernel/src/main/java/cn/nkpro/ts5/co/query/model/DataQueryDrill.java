package cn.nkpro.ts5.co.query.model;

import cn.nkpro.ts5.annotation.Keep;
import lombok.Data;

@Keep
@Data
public class DataQueryDrill {
    private String from;
    private Object fromValue;
    private String to;
}