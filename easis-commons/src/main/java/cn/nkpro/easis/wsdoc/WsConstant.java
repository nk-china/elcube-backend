package cn.nkpro.easis.wsdoc;

public interface WsConstant<T,V> {
	T getCode();
	V getDesc();
	boolean is(T other);
}
