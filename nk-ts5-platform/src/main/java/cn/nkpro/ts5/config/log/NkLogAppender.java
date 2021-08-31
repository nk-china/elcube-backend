//package cn.nkpro.ts5.config.log;
//
//import ch.qos.logback.core.UnsynchronizedAppenderBase;
//import ch.qos.logback.core.encoder.Encoder;
//import ch.qos.logback.core.spi.DeferredProcessingAware;
//import ch.qos.logback.core.status.ErrorStatus;
//import lombok.Setter;
//
//import java.nio.charset.StandardCharsets;
//import java.util.LinkedList;
//import java.util.List;
//
//public class NkLogAppender<E> extends UnsynchronizedAppenderBase<E> {
//
//    private static ThreadLocal<List<String>> threadLocal = new ThreadLocal<>();
//    @Setter
//    protected Encoder<E> encoder;
//
//    public static void setup(){
//        System.out.println(Thread.currentThread().getName());
//        threadLocal.set(new LinkedList<>());
//        System.out.println(threadLocal.get());
//    }
//
//    public static List<String> getLog(){
//        return threadLocal.get();
//    }
//
//    static void finished(){
//        System.out.println(threadLocal.get());
//        threadLocal.remove();
//        System.out.println(Thread.currentThread().getName()+"Removed");
//    }
//
//    public void start() {
//        int errors = 0;
//        if (this.encoder == null) {
//            this.addStatus(new ErrorStatus("No encoder set for the appender named \"" + this.name + "\".", this));
//            ++errors;
//        }
//
//        if (errors == 0) {
//            super.start();
//        }
//    }
//
//    @Override
//    protected void append(E event) {
//        System.out.println("append:"+Thread.currentThread().getName());
//        System.out.println(threadLocal.get());
//        List<String> logs = threadLocal.get();
//        if (this.isStarted() && logs!=null) {
//            if (event instanceof DeferredProcessingAware) {
//                ((DeferredProcessingAware)event).prepareForDeferredProcessing();
//            }
//            byte[] byteArray = this.encoder.encode(event);
//            System.out.println(Thread.currentThread().getName()+":" + new String(byteArray, StandardCharsets.UTF_8));
//            logs.add(new String(byteArray, StandardCharsets.UTF_8));
//        }
//    }
//}
