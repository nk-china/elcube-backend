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
package cn.nkpro.elcube.task.model;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by bean on 2020/7/22.
 */
@Data
public class BpmTask implements Cloneable{
    private String id;
    private String name;
    private String priority;
    private String taskDefinitionKey;
    private String processDefinitionId;
    private String processInstanceId;
    private String activityInstanceId;
    private String executionId;
    private Long createTime;
    private String assignee;
    private List<String> candidate;
    private List<BpmUser> users;

    private String deleteReason;
    private String delegationState;

    private Date startTime;
    private Date endTime;

    // instance props
    private String businessKey;
    
    private Boolean revokeAble = false;

    // doc props
    private String docId;
    private String docType;
    private String classify;

    private List<BpmComment> comments;
    // flows
    private List<BpmTaskTransition> transitions;
    // variables
    private Map<String, Object> bpmVariables;

    //private List<BpmComment> instanceComments;

    private List<BpmTask> historicalTasks;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
