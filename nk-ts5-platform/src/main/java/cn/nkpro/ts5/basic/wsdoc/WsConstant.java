package cn.nkpro.ts5.basic.wsdoc;

public interface WsConstant<T,V> {
	T getCode();
	V getDesc();
	boolean is(T other);
}
