package cn.nkpro.ts5.supports;

import cn.nkpro.tfms.platform.custom.EnumDocClassify;

/**
 * Created by bean on 2020/7/7.
 */
public interface SequenceSupport {
    String next(EnumDocClassify classify,String docType);
}
