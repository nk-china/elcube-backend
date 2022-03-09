package cn.nkpro.elcube.bigdata.service.impl;

import cn.nkpro.elcube.co.query.DataQueryService;
import cn.nkpro.elcube.co.query.model.DataFieldDesc;
import cn.nkpro.elcube.co.query.model.DataQueryRequest;
import cn.nkpro.elcube.co.query.model.DataQueryResponse;
import cn.nkpro.elcube.data.jdbc.NkJdbcProperties;
import cn.nkpro.elcube.exception.NkAccessDeniedException;
import cn.nkpro.elcube.security.SecurityUtilz;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.jdbc.core.JdbcTemplate;
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

@ConditionalOnBean(NkJdbcProperties.class)
@Service("JdbcMySqlService")
public class JdbcMySqlService implements DataQueryService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NkJdbcProperties properties;

    @Override
    public List<DataFieldDesc> getFieldCaps(String index) {

        if(!SecurityUtilz.hasAnyAuthority("*:*","#*:READ","#"+index+":READ")){
            throw new NkAccessDeniedException(String.format("没有数据源[%s]的访问权限", index));
        }

        return jdbcTemplate.query(
                "SELECT COLUMN_NAME,DATA_TYPE " +
                    "  FROM information_schema.COLUMNS " +
                    " WHERE TABLE_NAME=? " +
                    "   AND TABLE_SCHEMA=?",
                new Object[]{index,properties.getSchema()},
                (resultSet, i) -> {
                    DataFieldDesc desc = new DataFieldDesc();
                    desc.setName(resultSet.getString("COLUMN_NAME"));
                    desc.setType(resultSet.getString("DATA_TYPE"));

                    switch (desc.getType()){
                        case "short":
                        case "int":
                        case "long":
                        case "bigint":
                        case "varchar":
                            desc.setSearchable(true);
                            desc.setAggregatable(true);
                            break;
                        default:
                            desc.setSearchable(false);
                            desc.setAggregatable(false);
                            break;
                    }
                    return desc;
                }
        );
    }

    @Override
    public DataQueryResponse queryPage(DataQueryRequest request) {
        List<Select> sqlList = request.getSelects();

        Assert.isTrue(sqlList.size()==1,"分页查询不支持多条语句");

        Long total = jdbcTemplate.queryForObject(String.format("select count(1) from ( %s ) as t", sqlList.get(0).toString()), Long.class);

        String orderBy = StringUtils.isNotBlank(request.getOrderField())?
                String.format("ORDER BY %s %s",request.getOrderField(),StringUtils.defaultString(request.getOrder(),"ASC"))
                :StringUtils.EMPTY;

        List<DataQueryResponse.Column> columns = new ArrayList<>();
        List<Map<String, Object>> list = jdbcTemplate.query(
                String.format(
                        "%s %s limit %s,%s",
                        sqlList.get(0).toString(),
                        orderBy,
                        request.getFrom(),
                        Math.min(request.getRows(), 1000)
                ),
                new RowMapper(columns));

        return new DataQueryResponse<>(request.getSqlList(), columns, list,request.getFrom(),request.getRows(), total == null ? 0 : total);
    }

    @Override
    public DataQueryResponse queryList(DataQueryRequest request) {

        List<Map<String, Object>> list = new ArrayList<>();

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
                    columns.add(new DataQueryResponse.Column(metaData.getColumnLabel(c),metaData.getColumnTypeName(c)));
                }
            }

            Map<String, Object> map = new HashMap<>();
            for(int c=1;c<=metaData.getColumnCount();c++){
                map.put(metaData.getColumnLabel(c),resultSet.getObject(c));
            }
            return map;
        }
    }
}
