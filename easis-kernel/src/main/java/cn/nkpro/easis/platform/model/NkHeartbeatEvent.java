package cn.nkpro.easis.platform.model;

import org.springframework.context.ApplicationEvent;


public class NkHeartbeatEvent extends ApplicationEvent{

    public NkHeartbeatEvent(Object source) {
        super(source);
    }
}
