package cn.nkpro.ts5.data.jdbc;

import cn.nkpro.ts5.co.easy.EasyCollection;
import cn.nkpro.ts5.co.easy.EasySingle;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class NkJdbcTemplate {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public void upsert(@NotNull String tableName, @NotNull List<String> upsertFields, @NotNull List<String> keyFields, @NotNull Object data){

        Assert.notEmpty(upsertFields,"更新字段列表不能为空");
        Assert.notEmpty(keyFields,"主键字段列表不能为空");

        EasySingle single = EasySingle.from(data);

        List<Object> args = new ArrayList<>();

        List<String> sqlWhereBuilder = new ArrayList<>();
        keyFields.forEach(key->{
            sqlWhereBuilder.add(Utils.reverse(key) + " = ?");
            args.add(single.get(key));
        });

        Integer integer = jdbcTemplate.queryForObject(
                String.format("SELECT COUNT(1) AS COUNT FROM %s WHERE %s", tableName, String.join(" AND ",sqlWhereBuilder)),
                Integer.class,
                args.toArray()
        );

        args.clear();
        if(integer==null || integer==0){

            List<String> sqlInsertFieldBuilder = new ArrayList<>();
            upsertFields.forEach(field->{
                sqlInsertFieldBuilder.add(Utils.reverse(field));
                args.add(single.get(field));
            });
            keyFields.forEach(key->{
                if(!upsertFields.contains(key)){
                    sqlInsertFieldBuilder.add(Utils.reverse(key));
                    args.add(single.get(key));
                }
            });

            String insertSql = String.format("INSERT INTO %s(%s) VALUES(%s)",
                    tableName,
                    String.join(", ",sqlInsertFieldBuilder),
                    sqlInsertFieldBuilder.stream().map(e->"?").collect(Collectors.joining(", "))
            );
            jdbcTemplate.update(insertSql, args.toArray());
        }else{

            List<String> sqlSetBuilder = new ArrayList<>();
            upsertFields.forEach(field->{
                sqlSetBuilder.add(Utils.reverse(field) + " = ?");
                args.add(single.get(field));
            });

            sqlWhereBuilder.clear();
            keyFields.forEach(key->{
                sqlWhereBuilder.add(Utils.reverse(key) + " = ?");
                args.add(single.get(key));
            });

            String updateSql = String.format("UPDATE %s SET %s WHERE %s",
                    tableName,
                    String.join(", ", sqlSetBuilder),
                    String.join(" AND ", sqlWhereBuilder)
            );

            jdbcTemplate.update(updateSql, args.toArray());
        }
    }

    public void batchUpsert(@NotNull String tableName, @NotNull List<String> upsertFields, @NotNull List<String> keyFields, Map<String,Object> existsQueryParams, @NotNull List<Object> data){

        Assert.notEmpty(upsertFields,"更新字段列表不能为空");
        Assert.notEmpty(keyFields,"主键字段列表不能为空");


        // 查询已经存在的数据
        List<Object> args = new ArrayList<>();

        // WHERE
        String where = StringUtils.EMPTY;
        if(MapUtils.isNotEmpty(existsQueryParams)){
            List<String> sqlWhereBuilder = new ArrayList<>();
            existsQueryParams.forEach((k,v)->{
                sqlWhereBuilder.add(k);
                if(v instanceof Collection){
                    args.addAll((Collection<?>) v);
                }else{
                    args.add(v);
                }
            });
            where =  "WHERE "+ String.join(" AND ", sqlWhereBuilder);
        }

        // build SQL
        String existsSql = String.format("SELECT %s FROM %s %s",
                keyFields.stream().map(Utils::reverse).collect(Collectors.joining(",")),
                tableName,
                where
         );

        List<String> exists = jdbcTemplate.queryForList(existsSql, args.toArray())
                .stream()
                .map(i-> i.keySet().stream()
                        .sorted()
                        .map(i::get)
                        .map(v->v==null?StringUtils.EMPTY:v)
                        .map(Object::toString)
                        .collect(Collectors.joining(":")))
                .collect(Collectors.toList());

        List<String> keys = keyFields.stream()
                .sorted()
                .collect(Collectors.toList());


        List<Object[]> updateArgs = new ArrayList<>();
        List<Object[]> insertArgs = new ArrayList<>();

        Set<String> insertFields = SetUtils.union(new HashSet<>(upsertFields), new HashSet<>(keyFields));

        EasyCollection.from(data).forEach(single->{
            String joinedKey = keys.stream()
                    .map(single::get)
                    .map(v->v==null?StringUtils.EMPTY:v)
                    .map(Object::toString)
                    .collect(Collectors.joining(":"));

            List<Object> arg = new ArrayList<>();
            if(exists.contains(joinedKey)){
                // update
                upsertFields.forEach(field-> arg.add(single.get(field)));
                keyFields.forEach(key-> arg.add(single.get(key)));
                updateArgs.add(arg.toArray());
            }else{
                // insert
                insertFields.forEach(field-> arg.add(single.get(field)));
                insertArgs.add(arg.toArray());
            }
        });

        if(!updateArgs.isEmpty()){
            String updateSql = String.format("UPDATE %s SET %s WHERE %s",
                    tableName,
                    upsertFields.stream().map(Utils::reverse).map(key->String.format("%s = ?",key)).collect(Collectors.joining(",")),
                    keyFields.stream().map(Utils::reverse).map(key->String.format("%s = ?",key)).collect(Collectors.joining(" AND "))
            );
            jdbcTemplate.batchUpdate(updateSql, updateArgs);
        }
        if(!insertArgs.isEmpty()){
            String insertSql = String.format("INSERT INTO %s(%s) VALUES(%s)",
                    tableName,
                    insertFields.stream().map(Utils::reverse).collect(Collectors.joining(",")),
                    insertFields.stream().map(Utils::reverse).map(key->"?").collect(Collectors.joining(","))
            );
            jdbcTemplate.batchUpdate(insertSql, insertArgs);
        }

    }

    public HashMap querySingle(String tableName, List<String> fetchFields, Map<String,Object> params){
        return query(tableName, fetchFields, params, null, new NkObjectResultSetExtractor<>(HashMap.class, 1))
                .stream()
                .findFirst()
                .orElse(null);
    }

    public <T> T querySingle(String tableName, List<String> fetchFields, Map<String,Object> params,Class<T> type){
        return query(tableName, fetchFields, params, null, new NkObjectResultSetExtractor<>(type, 1))
                .stream()
                .findFirst()
                .orElse(null);
    }

    public List<HashMap> queryList(String tableName, List<String> fetchFields, Map<String,Object> params, String orderBy){
        return query(tableName, fetchFields, params, orderBy, new NkObjectResultSetExtractor<>(HashMap.class));
    }

    public <T> List<T> queryList(String tableName, List<String> fetchFields, Map<String,Object> params, String orderBy,Class<T> type){
        return query(tableName, fetchFields, params, orderBy, new NkObjectResultSetExtractor<>(type));
    }

    private <T> T query(String tableName, List<String> fetchFields, Map<String,Object> params, String orderBy, ResultSetExtractor<T> resultSetExtractor){

        List<String> sqlBuilder = new ArrayList<>();
        List<Object> args = new ArrayList<>();

        // SELECT
        sqlBuilder.add("SELECT");
        if(CollectionUtils.isNotEmpty(fetchFields)){
            sqlBuilder.add(
                fetchFields.stream()
                    .map(Utils::reverse)
                    .collect(Collectors.joining(","))
            );
        }else{
            sqlBuilder.add("*");
        }

        // FROM
        sqlBuilder.add("FROM");
        sqlBuilder.add(tableName);

        // WHERE
        if(MapUtils.isNotEmpty(params)){
            sqlBuilder.add("WHERE");
            List<String> sqlWhereBuilder = new ArrayList<>();
            params.forEach((k,v)->{
                sqlWhereBuilder.add(k);
                if(v instanceof Collection){
                    args.addAll((Collection<?>) v);
                }else{
                    args.add(v);
                }
            });
            sqlBuilder.add(String.join(" AND ", sqlWhereBuilder));
        }

        // ORDER
        if(StringUtils.isNotBlank(orderBy)){
            sqlBuilder.add("ORDER BY");
            sqlBuilder.add(orderBy);
        }

        return jdbcTemplate.query(String.join(" ", sqlBuilder), args.toArray(), resultSetExtractor);
    }



}
