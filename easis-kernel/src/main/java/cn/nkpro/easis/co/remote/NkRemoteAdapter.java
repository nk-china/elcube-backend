package cn.nkpro.easis.co.remote;

import cn.nkpro.easis.co.NkCustomObject;

public interface NkRemoteAdapter<R,T> extends NkCustomObject {
    <S> S apply(Object options, Class<S> returnType);
}
