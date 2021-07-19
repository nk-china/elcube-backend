package cn.nkpro.tfms.platform.model.bpm;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * Created by bean on 2020/7/22.
 */
@Data
public class BpmTaskComplete {
    private String taskId;
    private String flow;
    private String flowName;
    private String comment;
    private JSONObject variables = new JSONObject();
}
