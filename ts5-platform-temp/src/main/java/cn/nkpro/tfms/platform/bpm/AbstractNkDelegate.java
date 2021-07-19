package cn.nkpro.tfms.platform.bpm;

import cn.nkpro.tfms.platform.services.TfmsDocEngine;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractNkDelegate implements JavaDelegate {

    @Autowired
    protected ProcessEngine processEngine;
    @Autowired
    protected TfmsDocEngine docEngine;

}
