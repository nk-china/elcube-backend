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
package cn.nkpro.easis.bigdata.controller;

import cn.nkpro.easis.annotation.NkNote;
import cn.nkpro.easis.co.query.model.DataFieldDesc;
import cn.nkpro.easis.co.query.model.DataQueryRequest;
import cn.nkpro.easis.co.query.model.DataQueryResponse;
import cn.nkpro.easis.co.query.DataSourceManager;
import cn.nkpro.easis.co.query.model.DataSourceDef;
import cn.nkpro.easis.exception.NkDefineException;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * Created by bean on 2020/6/9.
 */
@PreAuthorize("authenticated")
@NkNote("3.数据分析与发现")
@RestController
@RequestMapping("/data/analyse")
public class DataAnalyseController {

    private final static TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();

    @SuppressWarnings("all")
    @Autowired
    private DataSourceManager dataSourceManager;


    @NkNote("1.数据源列表")
    @RequestMapping(value = "/dataSources",method = RequestMethod.GET)
    public Collection<DataSourceDef> getDataSources(){
        return dataSourceManager.getDataSources();
    }

    @NkNote("1.索引字段列表")
    @RequestMapping(value = "/fieldCaps/{datasourceName}",method = RequestMethod.POST)
    public List<DataFieldDesc> getFieldCaps(@PathVariable String datasourceName) {
        return dataSourceManager.getService(datasourceName).getFieldCaps(datasourceName);
    }

    @NkNote("2.执行sql")
    @RequestMapping(value = "/query",method = RequestMethod.POST)
    public DataQueryResponse sql(@RequestBody DataQueryRequest request) {


        Select select = request.parse().getSelects().stream().findFirst().orElse(null);
        if(select==null){
            throw new NkDefineException("查询命令不能为空");
        }
        String tableName = tablesNamesFinder.getTableList(select).stream().findFirst().orElse(null);
        if(tableName==null){
            throw new NkDefineException("数据源没有指定");
        }

        return dataSourceManager.getService(tableName).queryPage(request);
    }
}
