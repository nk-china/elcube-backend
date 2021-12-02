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
package cn.nkpro.easis.data.jdbc;

import cn.nkpro.easis.co.easy.EasySingle;
import cn.nkpro.easis.exception.NkSystemException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class NkObjectResultSetExtractor<T> implements ResultSetExtractor<List<T>> {

    private Integer expect;
    private Class<T> expectType;

    NkObjectResultSetExtractor(@NotNull Class<T> expectType){
        this.expectType = expectType;
    }

    NkObjectResultSetExtractor(@NotNull Class<T> expectType, Integer expect){
        this.expectType = expectType;
        this.expect = expect;
    }

    @Override
    public List<T> extractData(ResultSet resultSet) throws SQLException, DataAccessException {

        Assert.isTrue(expect==null || resultSet.getRow()<=expect,"查询数据库错误，期望结果条目为"+expect+"，实际数据数量为"+ resultSet.getRow());

        List<T> list = new ArrayList<>();
        T result;
        while(resultSet.next()){
            try {
                result = expectType.getConstructor().newInstance();
            } catch (Exception e) {
                throw new NkSystemException(e);
            }

            EasySingle single = EasySingle.from(result);

            ResultSetMetaData meta = resultSet.getMetaData();
            for(int i=0;i<meta.getColumnCount();i++){
                String columnName = meta.getColumnName(i + 1);
                String fieldName  = Utils.translate(columnName, false);

                single.set(fieldName, resultSet.getObject(columnName));
            }
            list.add(result);
        }

        return list;
    }
}