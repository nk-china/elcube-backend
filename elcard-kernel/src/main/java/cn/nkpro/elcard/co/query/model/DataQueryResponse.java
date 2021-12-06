/*
 * This file is part of ELCard.
 *
 * ELCard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcard.co.query.model;

import cn.nkpro.elcard.annotation.Keep;
import cn.nkpro.elcard.basic.PageList;
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
