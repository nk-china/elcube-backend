package cn.nkpro.ts5.task.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * Created by bean on 2020/7/22.
 */
@Data
public class BpmTaskComplete {
    private String taskId;
    private String comment;
    private BpmTaskTransition transition;
    private JSONObject variables = new JSONObject();
}
