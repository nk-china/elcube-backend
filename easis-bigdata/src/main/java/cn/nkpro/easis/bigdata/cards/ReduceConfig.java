package cn.nkpro.easis.bigdata.cards;

import cn.nkpro.easis.annotation.Keep;
import lombok.Data;

@Keep
@Data
public class ReduceConfig{

    private String docId;
    private String cardKey;

    private String preTask;
    private String programType;
    private String dataSource;
    private String dataCommand;
    private String service;
    private String dataKey;
    private String dataMapping;
}