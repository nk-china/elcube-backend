package cn.nkpro.ts5.docengine.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

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

    public SearchParams order(String orderField, boolean asc){
        this.setOrderField(orderField);
        this.setOrder(asc?"ASC":"DESC");
        return this;
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
