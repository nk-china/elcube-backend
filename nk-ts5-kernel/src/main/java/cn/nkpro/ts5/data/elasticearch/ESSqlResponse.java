package cn.nkpro.ts5.data.elasticearch;

import cn.nkpro.ts5.basic.Keep;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Keep
@Data
public class ESSqlResponse {

    private List<String> sqls;
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
        for(List<Object> row : rows){
            item = new HashMap<>();
            for(int i=0;i<columns.size();i++){
                column = columns.get(i);
                item.put(column.getName(),row.get(i));
            }
            ret.add(item);
        }

        return  ret;
//        避免toMap时数据有空指针，放弃lambda写法
//        return rows.stream()
//                .map(item-> item.stream()
//                        .collect(Collectors.toMap(e-> columns.get(item.indexOf(e)).getName(), e->e)))
//                .collect(Collectors.toList());
    }

    @Keep
    @Data
    public static class Column{
        private String name;
        private String type;
    }
}
