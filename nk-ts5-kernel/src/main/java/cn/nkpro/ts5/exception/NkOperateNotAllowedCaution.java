package cn.nkpro.ts5.exception;

import cn.nkpro.ts5.exception.abstracts.NkCaution;

/**
 * Created by bean on 2020/1/15.
 */
public class NkOperateNotAllowedCaution extends NkCaution {

    public NkOperateNotAllowedCaution(String msg){
        super(msg);
    }

    public NkOperateNotAllowedCaution(String msg, Exception e){
        super(msg,e);
    }
}
