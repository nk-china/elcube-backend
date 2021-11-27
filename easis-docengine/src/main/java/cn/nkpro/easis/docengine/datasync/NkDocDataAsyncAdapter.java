package cn.nkpro.easis.docengine.datasync;

import cn.nkpro.easis.docengine.gen.DocAsyncQueue;

public interface NkDocDataAsyncAdapter extends NkDocDataAdapter {

    void run(DocAsyncQueue asyncQueue);
}
