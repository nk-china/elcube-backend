package cn.nkpro.ts5.engine.elasticearch.model;

import cn.nkpro.ts5.engine.elasticearch.ESFieldType;
import cn.nkpro.ts5.engine.elasticearch.annotation.ESDocument;
import cn.nkpro.ts5.engine.elasticearch.annotation.ESField;
import cn.nkpro.ts5.engine.elasticearch.annotation.ESId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by bean on 2020/7/6.
 */
@EqualsAndHashCode(callSuper = false)
@Data
@ESDocument("document-bpm-task")
public class BpmTaskES extends DocBaseES {

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
}