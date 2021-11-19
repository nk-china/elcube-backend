package cn.nkpro.ts5.docengine.datasync;

import cn.nkpro.ts5.docengine.gen.DocAsyncQueue;

public interface NkDocDataAsyncAdapter extends NkDocDataAdapter {

    void run(DocAsyncQueue asyncQueue);
}
