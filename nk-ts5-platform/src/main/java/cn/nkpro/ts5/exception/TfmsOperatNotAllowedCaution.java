package cn.nkpro.ts5.exception;

/**
 * Created by bean on 2020/1/15.
 */
public class TfmsOperatNotAllowedCaution extends TfmsCaution {

    public TfmsOperatNotAllowedCaution(String msg){
        super(msg);
    }

    public TfmsOperatNotAllowedCaution(String msg, Exception e){
        super(msg,e);
    }
}
