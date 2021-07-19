package cn.nkpro.tfms.platform.basis;

/**
 * Created by bean on 2020/7/16.
 */
public interface TfmsCustomObject {
     default  boolean isFinal(){
        return false;
    }
}
