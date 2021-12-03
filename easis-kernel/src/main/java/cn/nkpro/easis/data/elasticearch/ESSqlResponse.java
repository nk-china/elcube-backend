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
package cn.nkpro.easis.data.elasticearch;

import cn.nkpro.easis.annotation.Keep;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Keep
@Data
public class ESSqlResponse {

    private List<String> sqlList;
    private List<Column> columns;
    private List<List<Object>> rows;
    private List<Map<String,Object>> list;
    private String cursor;

    public ESSqlResponse transform(){
        this.list = toList();
        this.rows.clear();
        return this;
    }

    public List<Map<String,Object>> toList(){
        Column column;
        Map<String,Object> item;
        List<Map<String,Object>> ret = new ArrayList<>();
//        避免toMap时数据有空指针，放弃lambda写法
//        return rows.stream()
//                .map(item-> item.stream()
//                        .collect(Collectors.toMap(e-> columns.get(item.indexOf(e)).getName(), e->e)))
//                .collect(Collectors.toList());
        for(List<Object> row : rows){
            item = new HashMap<>();
            for(int i=0;i<columns.size();i++){
                column = columns.get(i);
                item.put(column.getName(),row.get(i));
            }
            ret.add(item);
        }

        return  ret;
    }

    @Keep
    @Data
    public static class Column{
        private String name;
        private String type;
    }
}
