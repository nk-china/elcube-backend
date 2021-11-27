package cn.nkpro.easis.docengine.service;


import cn.nkpro.easis.docengine.EnumDocClassify;

/**
 * Created by bean on 2020/7/7.
 */
public interface SequenceSupport {
    String next(EnumDocClassify classify, String docType);
}
