package cn.nkpro.easis.co.query.model;

import cn.nkpro.easis.annotation.Keep;
import lombok.Data;

@Keep
@Data
public class DataQueryDrill {
    private String from;
    private Object fromValue;
    private String to;
}