package cn.nkpro.ts5.docengine.model;

import cn.nkpro.ts5.docengine.model.es.AbstractBaseES;
import cn.nkpro.ts5.data.elasticearch.ESFieldType;
import cn.nkpro.ts5.data.elasticearch.annotation.ESDocument;
import cn.nkpro.ts5.data.elasticearch.annotation.ESField;
import cn.nkpro.ts5.data.elasticearch.annotation.ESId;
import cn.nkpro.ts5.utils.BeanUtilz;
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