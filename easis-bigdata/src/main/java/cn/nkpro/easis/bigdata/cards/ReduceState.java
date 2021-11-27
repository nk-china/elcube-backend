package cn.nkpro.easis.bigdata.cards;

import cn.nkpro.easis.annotation.Keep;
import lombok.Data;

@Keep
@Data
public class ReduceState{
    private String state = "NotActive";//Waiting、Running、Complete
    private Long records;
}