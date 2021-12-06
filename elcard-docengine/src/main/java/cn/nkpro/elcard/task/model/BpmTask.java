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
package cn.nkpro.elcard.task.model;

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
    private String createTime;
    private String assignee;
    private List<String> candidate;

    private String deleteReason;

    private Date startTime;
    private Date endTime;

    // instance props
    private String businessKey;
    
    private Boolean revokeAble = false;

    // doc props
    private String docId;
    private String docType;
    private String classify;

    private List<String> comments;
    // flows
    private List<BpmTaskTransition> transitions;
    // variables
    private Map<String, Object> bpmVariables;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
