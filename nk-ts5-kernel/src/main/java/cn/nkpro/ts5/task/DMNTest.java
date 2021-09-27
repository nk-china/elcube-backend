package cn.nkpro.ts5.task;

import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class DMNTest {

    private DmnEngine dmnEngine;

    ProcessEngine processEngine;

    public void test(){

        processEngine.getDecisionService();




//        processEngine.getHistoryService().createHistoricDecisionInstanceQuery()
//                .
//
//        DmnEngine dmnEngine = DmnEngineConfiguration.createDefaultDmnEngineConfiguration().buildEngine();
//
//        // parse a decision
//        DmnDecision decision = dmnEngine.parseDecision("orderDecision", "CheckOrder.dmn");
//
//        Map<String, Object> data = new HashMap<String, Object>();
//        data.put("status", "gold");
//        data.put("sum", 354.12d);
//
//        // evaluate a decision
//        DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(decision, data);
    }
}
