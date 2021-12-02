/*
 * This file is part of EAsis.
 *
 * EAsis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EAsis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with EAsis.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.easis.task.controller;

import cn.nkpro.easis.annotation.Keep;
import cn.nkpro.easis.annotation.NkNote;
import cn.nkpro.easis.basic.PageList;
import cn.nkpro.easis.task.NkBpmDefService;
import cn.nkpro.easis.task.model.DmnDecisionDefinition;
import cn.nkpro.easis.task.model.DmnDecisionRunModel;
import cn.nkpro.easis.task.model.ResourceDefinition;
import cn.nkpro.easis.task.model.ResourceDeployment;
import cn.nkpro.easis.utils.BeanUtilz;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.dmn.engine.delegate.DmnDecisionTableEvaluationEvent;
import org.camunda.bpm.dmn.engine.delegate.DmnDecisionTableEvaluationListener;
import org.camunda.bpm.dmn.engine.delegate.DmnEvaluatedDecisionRule;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.DecisionDefinitionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by bean on 2020/7/17.
 */
@NkNote("36.[DevDef]决策配置")
@RestController
@RequestMapping("/def/dmn")
@PreAuthorize("hasAnyAuthority('*:*','DEF:*','DEF:DMN')")
public class SysDMNDefController {

    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private NkBpmDefService defBpmService;

    @NkNote("1.拉取定义")
    @RequestMapping(value = "/definitions")
    public PageList<DmnDecisionDefinition> processDefinitions(
            @NkNote("查询条目")@RequestParam(value = "latest",       required = false, defaultValue = "false") Boolean latest,
            @NkNote("查询条目")@RequestParam(value = "keyword",      required = false) String key,
            @NkNote("查询条目")@RequestParam(value = "orderField",   required = false) String orderField,
            @NkNote("查询条目")@RequestParam(value = "order",        required = false) String order,
            @NkNote("起始条目")@RequestParam("from") Integer from,
            @NkNote("查询条目")@RequestParam("rows") Integer rows){

        DecisionDefinitionQuery query = processEngine.getRepositoryService()
                .createDecisionDefinitionQuery();

        if(latest)
            query.latestVersion();
        if(StringUtils.isNotBlank(key))
            query.decisionRequirementsDefinitionKey(String.format("%%%s%%",key));
        if(StringUtils.isNotBlank(orderField)){
            switch (orderField){
                case "key":
                    query.orderByDecisionDefinitionKey();
                    break;
                case "name":
                    query.orderByDecisionDefinitionName();
                    break;
                case "version":
                    query.orderByDecisionDefinitionVersion();
                    break;
            }
            if("desc".equalsIgnoreCase(order)){
                query.desc();
            }else {
                query.asc();
            }
        }

        return new PageList<>(
                BeanUtilz.copyFromList(query.listPage(from,rows), DmnDecisionDefinition.class),
                from,
                rows,
                query.count());
    }
    @NkNote("2.拉取定义详情")
    @RequestMapping(value = "/definition/detail")
    public ResourceDefinition processDefinitionDetail(String definitionId){
        return defBpmService.getDmnDefinition(definitionId);
    }

    @NkNote("3.部署流程定义")
    @RequestMapping(value = "/deploy",method = RequestMethod.POST)
    public ResourceDeployment deploy(@RequestBody ResourceDefinition definition){
        return defBpmService.deploy(definition);
    }

    @NkNote("4.测试运行")
    @RequestMapping(value = "/run",method = RequestMethod.POST)
    public Map<String,Object> run(@RequestBody DmnDecisionRunModel run){
        DmnDecision decision = dmnEngine.parseDecision(run.getDecisionKey(),new ByteArrayInputStream(run.getXml().getBytes()));
        return evaluateDecision(decision, run.getVariables(), new HashMap<>());
    }

    private Map<String,Object> evaluateDecision(DmnDecision decision, Map<String,Object> variables, Map<String,Object> result){

        threadLocal.remove();
        result.put(decision.getKey(),new DmnResult(
                dmnEngine.evaluateDecision(decision, variables),
                threadLocal.get()!=null?
                        threadLocal.get()
                            .getMatchingRules()
                            .stream()
                            .map(DmnEvaluatedDecisionRule::getId)
                            .collect(Collectors.toList())
                        :null
        ));
        decision.getRequiredDecisions()
                .forEach(dmnDecision -> evaluateDecision(dmnDecision,variables,result));
        return result;
    }

    private ThreadLocal<DmnDecisionTableEvaluationEvent> threadLocal = new ThreadLocal<>();
    private DmnEngine dmnEngine = DmnEngineConfiguration.createDefaultDmnEngineConfiguration()
            .customPostDecisionTableEvaluationListeners(Collections.singletonList(new DmnDecisionTableEvaluationListener() {
                @Override
                public void notify(DmnDecisionTableEvaluationEvent dmnDecisionTableEvaluationEvent) {
                    threadLocal.set(dmnDecisionTableEvaluationEvent);
                }
            }))
            .buildEngine();

    @Keep
    @Data
    @AllArgsConstructor
    static class DmnResult{
        DmnDecisionResult result;
        List<String> matchedRules;
    }
}
