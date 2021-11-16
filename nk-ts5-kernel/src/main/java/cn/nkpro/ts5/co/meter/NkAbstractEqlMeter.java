package cn.nkpro.ts5.co.meter;

import cn.nkpro.ts5.co.query.DataSourceManager;
import cn.nkpro.ts5.data.redis.RedisSupport;
import cn.nkpro.ts5.co.query.model.DataQueryRequest;
import cn.nkpro.ts5.exception.NkDefineException;
import cn.nkpro.ts5.security.SecurityUtilz;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class NkAbstractEqlMeter extends NkAbstractMeter<List>{

    private final static TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
    @Autowired
    private RedisSupport<List> redisSupport;
    @Autowired
    private DataSourceManager dataSourceManager;

    @Override
    public List getData(Object config) {
        String sql = (String) ((Map)config).get("sql");

        if(StringUtils.isBlank(sql)){
            return Collections.emptyList();
        }


        Select select = null;
        try {
            select = (Select) CCJSqlParserUtil.parse(sql);
        } catch (JSQLParserException e) {
            e.printStackTrace();
            throw new NkDefineException("查询命令不能为空");
        }
        String tableName = tablesNamesFinder.getTableList(select).stream().findFirst().orElse(null);
        if(tableName==null){
            throw new NkDefineException("数据源没有指定");
        }

        String key = SecurityUtilz.getUser().getId() +':'+ DigestUtils.md5DigestAsHex(sql.getBytes());
        List list = redisSupport.getIfAbsent(key, () -> dataSourceManager.getService(tableName).queryList(DataQueryRequest.fromSql(sql)).getList());

        redisSupport.expire(key,60);

        return list;
    }
}
