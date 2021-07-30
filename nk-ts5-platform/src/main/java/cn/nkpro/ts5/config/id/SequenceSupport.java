package cn.nkpro.ts5.config.id;

import cn.nkpro.ts5.engine.doc.NkDocProcessor;

/**
 * Created by bean on 2020/7/7.
 */
public interface SequenceSupport {
    String next(NkDocProcessor.EnumDocClassify classify, String docType);
}
