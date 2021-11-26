package cn.nkpro.ts5.data.jdbc;

import cn.nkpro.ts5.co.easy.EasySingle;
import cn.nkpro.ts5.exception.NkSystemException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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