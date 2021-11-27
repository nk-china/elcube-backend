package cn.nkpro.easis.co.query.model;

import cn.nkpro.easis.annotation.Keep;
import cn.nkpro.easis.basic.PageList;
import lombok.*;

import java.util.List;


public class DataQueryResponse<T> extends PageList<T> {

    @Getter@Setter
    private List<Column> columns;

    @Getter@Setter
    private List<String> sqlList;

    @Getter@Setter
    private String cursor;

    public DataQueryResponse(List<String> sqlList, List<Column> columns, List<T> list, int from, int size, long total) {
        super(list, from, size, total);
        this.sqlList = sqlList;
        this.columns = columns;
    }
    public DataQueryResponse(List<String> sqlList, List<Column> columns, List<T> list, int size, String cursor) {
        super(list, 0, size, 0);
        this.cursor = cursor;
        this.sqlList = sqlList;
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
