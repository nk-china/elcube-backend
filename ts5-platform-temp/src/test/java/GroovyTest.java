import cn.nkpro.TfmsPlatformApplication;
import cn.nkpro.tfms.platform.custom.TfmsCardComponent;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.junit4.SpringRunner;

import javax.script.ScriptException;

/**
 * Created by bean on 2020/7/16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes={TfmsPlatformApplication.class})
public class GroovyTest implements ApplicationContextAware{

    private ApplicationContext beanFactory;

    @Test
    public void test() throws ScriptException {

        Assert.assertTrue(beanFactory.containsBean("defaultTfmsDocInterceptorImplByGroovy"));

        System.out.println(beanFactory.getBeansOfType(TfmsCardComponent.class));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.beanFactory = applicationContext;
    }
}
