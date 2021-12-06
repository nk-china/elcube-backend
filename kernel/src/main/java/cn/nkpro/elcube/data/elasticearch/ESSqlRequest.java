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
package cn.nkpro.elcube.data.elasticearch;

import cn.nkpro.elcube.annotation.Keep;
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
