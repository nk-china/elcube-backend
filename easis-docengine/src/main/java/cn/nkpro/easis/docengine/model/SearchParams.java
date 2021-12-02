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
package cn.nkpro.easis.docengine.model;

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

    private JSONObject suggest;

}
