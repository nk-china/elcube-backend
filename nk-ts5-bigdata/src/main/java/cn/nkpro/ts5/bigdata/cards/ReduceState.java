package cn.nkpro.ts5.bigdata.cards;

import cn.nkpro.ts5.annotation.Keep;
import lombok.Data;

@Keep
@Data
public class ReduceState{
    private String state = "NotActive";//Waiting、Running、Complete
    private Long records;
}