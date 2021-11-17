package cn.nkpro.ts5.data.elasticearch;

import cn.nkpro.ts5.annotation.Keep;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.index.query.QueryBuilder;

@Keep
@Data
@NoArgsConstructor
public class ESSqlRequest {
    private String time_zone = "Asia/Shanghai";
    private Boolean field_multi_value_leniency = true;
    private Integer fetch_size = 100;
    private Boolean index_include_frozen = false;
    private String query;
    private QueryBuilder filter;

    public ESSqlRequest(String query, QueryBuilder filter){
        this.query = query;
        this.filter = filter;
    }

    public String toString(){
        JSONObject sourceBuilder = (JSONObject) JSONObject.toJSON(this);
        if(filter!=null){
            sourceBuilder.put("filter",JSON.parseObject(filter.toString()));
        }else{
            sourceBuilder.remove("filter");
        }
        return sourceBuilder.toString();
    }
}
