package cn.nkpro.tfms.platform.bpm.listener;

import lombok.Setter;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("NkBPMTestListener")
public class NkBPMTestExecutionListener implements ExecutionListener {

    @Setter
    private Map map;

    @Override
    public void notify(DelegateExecution delegateExecution) {

        System.out.println(map);

    }
}
