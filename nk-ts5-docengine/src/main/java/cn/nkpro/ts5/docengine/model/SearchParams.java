package cn.nkpro.ts5.docengine.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class SearchParams {

    public static SearchParams defaults(){
        return new SearchParams();
    }

    public static SearchParams defaults(int limit){
        SearchParams params = defaults();
        params.setRows(limit);
        return params;
    }

    private JSONObject postCondition;
    private Integer from = 0;
    private Integer rows = 10;

    private JSONObject conditions;

    private List<String> highlight;
    private String[] source;
    private List<String> aggs;

    private String orderField;
    private String order = "ASC";

}
