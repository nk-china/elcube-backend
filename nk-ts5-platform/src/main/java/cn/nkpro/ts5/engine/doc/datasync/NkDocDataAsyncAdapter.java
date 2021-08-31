package cn.nkpro.ts5.engine.doc.datasync;

import cn.nkpro.ts5.orm.mb.gen.NkAsyncQueue;

public interface NkDocDataAsyncAdapter extends NkDocDataAdapter {

    void run(NkAsyncQueue asyncQueue);
}
