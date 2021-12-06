/*
 * This file is part of ELCard.
 *
 * ELCard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcard.task.controller;

import cn.nkpro.elcard.annotation.NkNote;
import cn.nkpro.elcard.basic.PageList;
import cn.nkpro.elcard.task.NkBpmTaskManager;
import cn.nkpro.elcard.task.NkBpmTaskService;
import cn.nkpro.elcard.task.model.BpmInstance;
import cn.nkpro.elcard.task.model.BpmTaskComplete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Created by bean on 2020/7/17.
 */
@NkNote("21.[DevOps]工作流管理")
@RestController
@RequestMapping("/ops/bpm")
@PreAuthorize("hasAnyAuthority('*:*','DEVOPS:*','DEVOPS:BPM')")
public class SysTaskManagerController {

    @Autowired@SuppressWarnings("all")
    private NkBpmTaskManager bpmTaskManager;
    @Autowired@SuppressWarnings("all")
    private NkBpmTaskService bpmTaskService;

    @NkNote("1.拉取流程实例")
    @RequestMapping(value = "/instances")
    public PageList<BpmInstance> processInstances(
            @NkNote("起始条目")@RequestParam("from") Integer from,
            @NkNote("查询条目")@RequestParam("rows") Integer rows){
        return bpmTaskManager.processInstancePage(from, rows);
    }

    @NkNote("2.拉取流程详情")
    @RequestMapping(value = "/instance/detail")
    @ResponseBody
    public BpmInstance processInstanceDetail(
            @NkNote("任务Id")@RequestParam("instanceId") String instanceId) {
        return bpmTaskManager.processInstanceDetail(instanceId);
    }

    @NkNote("3.强制执行任务")
    @RequestMapping(value = "/instance/complete")
    @ResponseBody
    public void processCompleteTask(
            @NkNote("任务Id")@RequestBody BpmTaskComplete taskComplete) {
        bpmTaskService.complete(taskComplete);
    }

    @NkNote("4.强制终止流程实例")
    @RequestMapping(value = "/instance/kill")
    @ResponseBody
    public void processInstanceKill(
            @NkNote("任务Id")@RequestParam("instanceId") String instanceId,
            @NkNote("删除原因")@RequestParam("deleteReason") String deleteReason) {
        bpmTaskManager.deleteProcessInstance(instanceId,deleteReason);
    }
}
