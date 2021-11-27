package dmn;

import cn.nkpro.easis.TS5Application;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testng.Assert;

/**
 * Created by bean on 2020/7/22.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes={TS5Application.class})
public class DMNVersionTest {
    @Autowired
    private ProcessEngine processEngine;



    @Test
    public void test1() throws Exception {

//        DmnEngine dmnEngine = DmnEngineConfiguration.createDefaultDmnEngineConfiguration()
//                .buildEngine();

        Assert.assertEquals(processEngine.getDecisionService()
                .evaluateDecisionByKey("Decision_0ln4mj1")
                //.version()
                .variables(Variables.createVariables().putValue("b","1"))
                .evaluate()
                .getFirstResult()
                .get("aOutput"),"V2");

        Assert.assertEquals(processEngine.getDecisionService()
                .evaluateDecisionByKey("Decision_0ln4mj1")
                .version(1)
                .variables(Variables.createVariables().putValue("b","1"))
                .evaluate()
                .getFirstResult()
                .get("aOutput"),"V1");

        Assert.assertEquals(processEngine.getDecisionService()
                .evaluateDecisionByKey("Decision_1qgqkcs")
                .variables(Variables.createVariables().putValue("b","1"))
                .evaluate()
                .getFirstResult()
                .get("text"),"V3");
    }
}
