package cn.nkpro.ts5.wsdoc;

public interface WsConstant<T,V> {
	T getCode();
	V getDesc();
	boolean is(T other);
}
