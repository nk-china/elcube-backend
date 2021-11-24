package cn.nkpro.ts5.docengine.service;


import cn.nkpro.ts5.docengine.EnumDocClassify;

/**
 * Created by bean on 2020/7/7.
 */
public interface SequenceSupport {
    String next(EnumDocClassify classify, String docType);
}
