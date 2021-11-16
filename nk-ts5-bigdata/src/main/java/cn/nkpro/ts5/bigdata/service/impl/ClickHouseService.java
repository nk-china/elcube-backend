package cn.nkpro.ts5.bigdata.service.impl;

import cn.nkpro.ts5.co.query.model.DataFieldDesc;
import cn.nkpro.ts5.co.query.model.DataQueryRequest;
import cn.nkpro.ts5.co.query.model.DataQueryResponse;
import cn.nkpro.ts5.co.query.DataQueryService;
import cn.nkpro.ts5.data.clickhouse.ClickHouseConfiguration;
import cn.nkpro.ts5.data.clickhouse.ClickHouseTemplate;
import cn.nkpro.ts5.exception.NkAccessDeniedException;
import cn.nkpro.ts5.security.SecurityUtilz;
import net.sf.jsqlparser.statement.select.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.yandex.clickhouse.ClickHouseArray;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoConfigureAfter(ClickHouseConfiguration.class)
@ConditionalOnBean(ClickHouseTemplate.class)
@Import(ClickHouseConfiguration.class)
@Service("ClickHouseService")
public class ClickHouseService implements DataQueryService {

    @SuppressWarnings("all")
    @Autowired
    private ClickHouseTemplate jdbcTemplate;

    public List<DataFieldDesc> getFieldCaps(String index){

        if(!SecurityUtilz.hasAnyAuthority("*:*","#*:READ","#"+index+":READ")){
            throw new NkAccessDeniedException(String.format("没有数据源[%s]的访问权限", index));
        }

        return jdbcTemplate.query(
                "select column as \"name\",type as \"type\" from system.parts_columns where table= ? ",
                (resultSet, i) -> {

                    String type = resultSet.getString("type");

                    DataFieldDesc desc = new DataFieldDesc();
                    desc.setName(resultSet.getString("name"));
                    desc.setType(type.replaceAll("\\(.*\\)$",""));
                    desc.setAggregatable(true);
                    desc.setSearchable(true);
                    return desc;
                },
                index);
    }

    @Override
    public DataQueryResponse queryPage(DataQueryRequest request){

        List<Select> sqlList = request.getSelects();

        Assert.isTrue(sqlList.size()==1,"分页查询不支持多条语句");


        Long total = jdbcTemplate.queryForObject(String.format("select count(1) from ( %s )", sqlList.get(0).toString()), Long.class);

        List<DataQueryResponse.Column> columns = new ArrayList<>();
        List<Map<String, Object>> list = jdbcTemplate.query(
                String.format("%s limit %s", sqlList.get(0).toString(), Math.min(request.getRows(), 1000)),
                new RowMapper(columns));

        return new DataQueryResponse<>(request.getSqlList(), columns, list,request.getFrom(),request.getRows(), total == null ? 0 : total);
    }


    @Override
    public DataQueryResponse queryList(DataQueryRequest request){

        List<Map<String, Object>> list = new ArrayList<>();

        // 格式化的过滤条件，需要增加到where条件里
        // JSONObject postConditions = request.getConditions();

        List<DataQueryResponse.Column> columns = new ArrayList<>();
        request.getSelects().forEach(sql-> list.addAll(
                jdbcTemplate.queryForList(
                        sql.toString() + " limit "+Math.min(request.getRows(), 1000),
                        new RowMapper(columns)
                )
        ));

        return new DataQueryResponse<>(request.getSqlList(), columns, list, 0,null);
    }

    static class RowMapper implements org.springframework.jdbc.core.RowMapper<Map<String, Object>>{

        List<DataQueryResponse.Column> columns;

        RowMapper(List<DataQueryResponse.Column> columns){
            this.columns = columns;
            this.columns.clear();
        }

        @Override
        public Map<String, Object> mapRow(ResultSet resultSet, int i) throws SQLException {

            ResultSetMetaData metaData = resultSet.getMetaData();

            if(i==0){
                for(int c=1;c<=metaData.getColumnCount();c++){
                    columns.add(new DataQueryResponse.Column(metaData.getColumnName(c),metaData.getColumnTypeName(c)));
                }
            }

            Map<String, Object> map = new HashMap<>();
            for(int c=1;c<=metaData.getColumnCount();c++){
                Object value = resultSet.getObject(c);
                if(value instanceof ClickHouseArray){
                    map.put(metaData.getColumnName(c), ((ClickHouseArray) value).getArray());
                }else{
                    map.put(metaData.getColumnName(c),resultSet.getObject(c));
                }
            }
            return map;
        }
    }

//    private List<DataQueryResponse.Column> parseColumns(DataQueryRequest request){
//
//        Select select = request.getSelects().get(0);
//        PlainSelect selectBody = (PlainSelect) select.getSelectBody();
//
//        List<DataQueryResponse.Column> columns = selectBody.getSelectItems()
//                .stream()
//                .map(selectItem -> {
//                    if (selectItem instanceof SelectExpressionItem) {
//                        Alias alias = ((SelectExpressionItem) selectItem).getAlias();
//                        if (alias != null) {
//                            return new DataQueryResponse.Column(alias.getName(), "unknown");
//                        }
//                        Expression expression = ((SelectExpressionItem) selectItem).getExpression();
//                        if (expression instanceof Column) {
//                            return new DataQueryResponse.Column(((Column) expression).getName(true), "unknown");
//                        } else {
//                            String name = "列" + (selectBody.getSelectItems().indexOf(selectItem) + 1);
//                            ((SelectExpressionItem) selectItem).setAlias(new Alias(String.format("\"%s\"",name)));
//                            return new DataQueryResponse.Column(name, "unknown");
//                        }
//                    }
//                    return null;
//                }).filter(Objects::nonNull).collect(Collectors.toList());
//
//        request.getSelects().stream().skip(1).forEach(s->{
//
//            PlainSelect body = (PlainSelect) request.getSelects().get(0).getSelectBody();
//
//            body.getSelectItems()
//                    .forEach(selectItem -> {
//                        if (selectItem instanceof SelectExpressionItem) {
//                            SelectExpressionItem expression = (SelectExpressionItem) selectItem;
//                            int index = body.getSelectItems().indexOf(selectItem);
//                            if(index<columns.size()){
//                                String name = columns.get(index).getName();
//                                if(expression.getAlias()==null){
//                                    expression.setAlias(new Alias(String.format("\"%s\"",name)));
//                                }else{
//                                    expression.getAlias().setName(String.format("\"%s\"",name));
//                                }
//                            }
//                        }
//                    });
//        });
//
//        return columns;
//    }
}
