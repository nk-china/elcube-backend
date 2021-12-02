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
package cn.nkpro.easis.task.model;

import cn.nkpro.easis.data.elasticearch.ESFieldType;
import cn.nkpro.easis.data.elasticearch.annotation.ESDocument;
import cn.nkpro.easis.data.elasticearch.annotation.ESField;
import cn.nkpro.easis.data.elasticearch.annotation.ESId;
import cn.nkpro.easis.docengine.model.DocHV;
import cn.nkpro.easis.docengine.model.es.AbstractBaseES;
import cn.nkpro.easis.utils.BeanUtilz;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by bean on 2020/7/6.
 */
@EqualsAndHashCode(callSuper = false)
@Data
@ESDocument("document-bpm-task")
public class BpmTaskES extends AbstractBaseES {

    @ESId
    @ESField(type= ESFieldType.Keyword)
    private String taskId;

    @ESField(type= ESFieldType.Keyword)
    private String taskName;

    @ESField(type= ESFieldType.Keyword)
    private String taskAssignee;

    @ESField(type= ESFieldType.Keyword)
    private String taskState;

    @ESField(type= ESFieldType.Long)
    private Long taskStartTime;

    @ESField(type= ESFieldType.Long)
    private Long taskEndTime;

    @ESField(type= ESFieldType.Keyword)
    private String docId;

    public static BpmTaskES from(DocHV doc, String taskId, String taskName, String taskAssignee, String taskState, Long taskStartTime, Long taskEndTime){
        BpmTaskES bpmTaskES = BeanUtilz.copyFromObject(doc, BpmTaskES.class);

        bpmTaskES.setTaskId(taskId);
        bpmTaskES.setTaskName(taskName);
        bpmTaskES.setTaskAssignee(taskAssignee);
        bpmTaskES.setTaskState(taskState);
        bpmTaskES.setTaskStartTime(taskStartTime);
        bpmTaskES.setTaskEndTime(taskEndTime);

        return bpmTaskES;
    }
}