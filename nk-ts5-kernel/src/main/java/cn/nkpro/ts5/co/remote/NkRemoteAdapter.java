package cn.nkpro.ts5.co.remote;

import cn.nkpro.ts5.co.NkCustomObject;

public interface NkRemoteAdapter<R,T> extends NkCustomObject {
    <S> S apply(Object options, Class<S> returnType);
}
