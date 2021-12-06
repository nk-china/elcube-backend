/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package dmn;

import cn.nkpro.elcube.ELCubeApplication;
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
@SpringBootTest(classes={ELCubeApplication.class})
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
