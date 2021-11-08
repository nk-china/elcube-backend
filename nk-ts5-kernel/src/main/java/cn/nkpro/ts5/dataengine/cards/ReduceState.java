package cn.nkpro.ts5.dataengine.cards;

import cn.nkpro.ts5.basic.Keep;
import lombok.Data;

@Keep
@Data
public class ReduceState{
    private String state = "NotActive";//Waiting、Running、Complete
    private Long records;
}