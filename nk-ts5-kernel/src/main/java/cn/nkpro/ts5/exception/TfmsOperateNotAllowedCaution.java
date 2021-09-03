package cn.nkpro.ts5.exception;

import cn.nkpro.ts5.exception.abstracts.NkCaution;

/**
 * Created by bean on 2020/1/15.
 */
public class TfmsOperateNotAllowedCaution extends NkCaution {

    public TfmsOperateNotAllowedCaution(String msg){
        super(msg);
    }

    public TfmsOperateNotAllowedCaution(String msg, Exception e){
        super(msg,e);
    }
}
