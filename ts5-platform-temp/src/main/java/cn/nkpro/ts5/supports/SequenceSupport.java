package cn.nkpro.ts5.supports;

import cn.nkpro.ts5.engine.doc.NKDocProcessor;

/**
 * Created by bean on 2020/7/7.
 */
public interface SequenceSupport {
    String next(NKDocProcessor.EnumDocClassify classify, String docType);
}
