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
package cn.nkpro.elcube.task.controller;

import cn.nkpro.elcube.annotation.NkNote;
import cn.nkpro.elcube.basic.PageList;
import cn.nkpro.elcube.task.NkBpmDefService;
import cn.nkpro.elcube.task.model.ResourceDefinition;
import cn.nkpro.elcube.task.model.ResourceDeployment;
import cn.nkpro.elcube.utils.BeanUtilz;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.DeploymentQuery;
import org.camunda.bpm.engine.repository.ProcessDefinitionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Created by bean on 2020/7/17.
 */
@NkNote("36.[DevDef]工作流配置")
@RestController
@RequestMapping("/def/bpm")
@PreAuthorize("hasAnyAuthority('*:*','DEF:*','DEF:BPM')")
public class SysBPMDefController {

    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private NkBpmDefService defBpmService;

    @NkNote("1.拉取定义")
    @RequestMapping(value = "/process/definitions")
    public PageList<ResourceDefinition> processDefinitions(
            @NkNote("查询条目")@RequestParam(value = "latest",       required = false, defaultValue = "false") Boolean latest,
            @NkNote("查询条目")@RequestParam(value = "keyword",      required = false) String key,
            @NkNote("查询条目")@RequestParam(value = "orderField",   required = false) String orderField,
            @NkNote("查询条目")@RequestParam(value = "order",        required = false) String order,
            @NkNote("起始条目")@RequestParam("from") Integer from,
            @NkNote("查询条目")@RequestParam("rows") Integer rows){

        ProcessDefinitionQuery query = processEngine.getRepositoryService()
                .createProcessDefinitionQuery();

        if(latest)
            query.latestVersion();
        if(StringUtils.isNotBlank(key))
            query.processDefinitionKeyLike(String.format("%%%s%%",key));
        if(StringUtils.isNotBlank(orderField)){
            switch (orderField){
                case "key":
                    query.orderByProcessDefinitionKey();
                    break;
                case "name":
                    query.orderByProcessDefinitionName();
                    break;
                case "version":
                    query.orderByProcessDefinitionVersion();
                    break;
            }
            if("desc".equalsIgnoreCase(order)){
                query.desc();
            }else {
                query.asc();
            }
        }

        return new PageList<>(
                BeanUtilz.copyFromList(query.listPage(from,rows), ResourceDefinition.class),
                from,
                rows,
                query.count());
    }
    @NkNote("2.拉取定义详情")
    @RequestMapping(value = "/process/definition/detail")
    public ResourceDefinition processDefinitionDetail(String definitionId){
        return defBpmService.getProcessDefinition(definitionId);
    }

    @NkNote("3.部署流程定义")
    @RequestMapping(value = "/deploy",method = RequestMethod.POST)
    public ResourceDeployment deploy(@RequestBody ResourceDefinition definition){
        return defBpmService.deploy(definition);
    }

    @NkNote("4.拉取部署记录")
    @RequestMapping(value = "/deployments")
    public PageList<ResourceDeployment> deployments(
            @NkNote("起始条目")@RequestParam("from") Integer from,
            @NkNote("查询条目")@RequestParam("rows") Integer rows){

        DeploymentQuery query = processEngine.getRepositoryService()
                .createDeploymentQuery();

        return new PageList<>(
                BeanUtilz.copyFromList(query.orderByDeploymentTime().desc()
                        .orderByDeploymentName().desc()
                        .listPage(from,rows), ResourceDeployment.class),
                from,
                rows,
                query.count());
    }
}
