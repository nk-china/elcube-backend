package cn.nkpro.ts5.dataengine.model;

import cn.nkpro.ts5.basic.Keep;
import cn.nkpro.ts5.basic.PageList;
import lombok.*;

import java.util.List;


public class DataQueryResponse<T> extends PageList<T> {

    @Getter@Setter
    private List<Column> columns;

    @Getter@Setter
    private String cursor;

    public DataQueryResponse(List<Column> columns, List<T> list, int from, int size, long total) {
        super(list, from, size, total);
        this.columns = columns;
    }
    public DataQueryResponse(List<Column> columns, List<T> list, int size, String cursor) {
        super(list, 0, size, 0);
        this.cursor = cursor;
        this.columns = columns;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Keep
    @Data
    public static class Column{
        private String name;
        private String type;
    }
}
