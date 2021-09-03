package cn.nkpro.ts5.docengine.datasync;

import cn.nkpro.ts5.docengine.gen.NkAsyncQueue;

public interface NkDocDataAsyncAdapter extends NkDocDataAdapter {

    void run(NkAsyncQueue asyncQueue);
}
