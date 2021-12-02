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
package cn.nkpro.easis.docengine.datasync.defaults;

import cn.nkpro.easis.docengine.datasync.NkAbstractDocDataDiffedSyncAdapter;
import cn.nkpro.easis.docengine.gen.DocDefDataSync;
import cn.nkpro.easis.exception.NkDefineException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component("NkDocSQLDataSync")
public class NkDocSQLDataSyncImpl extends NkAbstractDocDataDiffedSyncAdapter<Map<String,Object>> {

    @Autowired@SuppressWarnings("all")
    private JdbcTemplate jdbcTemplate;

    @Override
    public void onInsert(Map<Map<String,Object>,Map<String,Object>> list, DocDefDataSync def) {

        list.entrySet()
                .stream()
                .findAny()
                .map(e->{

                    e.getKey().keySet().stream()
                            .filter(k-> e.getValue().containsKey(k))
                            .findAny()
                            .ifPresent(i->{throw new NkDefineException(String.format("数据同步服务 %s 配置错误，KEY[ %s ]重复",def.getTargetSvr(),i));});

                    String keyFields       = String.join(",", e.getKey().keySet());
                    String keyPlaceholder  = e.getKey().keySet().stream().map(i->"?").collect(Collectors.joining(","));
                    String valueFields     = String.join(",", e.getValue().keySet());
                    String valuePlaceholder= e.getValue().keySet().stream().map(i->"?").collect(Collectors.joining(","));

                    return String.format("insert into %s(%s,%s) values(%s,%s)", def.getTargetArgs(), keyFields, valueFields, keyPlaceholder, valuePlaceholder);
                }).ifPresent(sql ->{
                    if(log.isDebugEnabled())log.debug(sql);
                    jdbcTemplate.batchUpdate(
                        sql,
                        list.entrySet().stream().map(e -> {
                            List<Object> data = new ArrayList<>();
                            data.addAll(e.getKey().values());
                            data.addAll(e.getValue().values());
                            return data.toArray();
                        }).collect(Collectors.toList())
                    );
                });
    }

    @Override
    public void onModify(Map<Map<String,Object>,Map<String,Object>> list, DocDefDataSync def) {

        list.entrySet()
                .stream()
                .findAny()
                .map(e->{
                    Set<String> listKeys = e.getKey().keySet();
                    Assert.isTrue(listKeys.size()>0,String.format("数据同步服务 %s 配置错误，KEY 数量不能为空",def.getTargetSvr()));

                    String countSql;
                    if(listKeys.size()==1){
                        countSql = String.format(
                                "select count(1) from %s where %s in (%s)",
                                def.getTargetArgs(),
                                listKeys.stream().findFirst().orElse(null),
                                list.keySet().stream().map(i -> "?").collect(Collectors.joining(","))
                        );
                    }else{
                        countSql = String.format(
                                "select count(1) from %s where %s",
                                def.getTargetArgs(),
                                listKeys.stream().map(i -> String.format("%s = ?", i)).collect(Collectors.joining(" or ","(",")"))
                        );
                    }

                    String keyPlaceholder  = listKeys.stream().map(i -> String.format("%s = ?", i)).collect(Collectors.joining(","));
                    String valuePlaceholder= e.getValue().keySet().stream().map(i->String.format("%s = ?",i)).collect(Collectors.joining(","));

                    return new String[]{
                        countSql,
                        String.format("update %s set %s where %s", def.getTargetArgs(), valuePlaceholder, keyPlaceholder)
                    };
                }).ifPresent(arr -> {
                    if(log.isDebugEnabled()){
                        log.debug(arr[0]);
                    }

                    List<Object> keyValues = new ArrayList<>();
                    list.keySet().forEach(e->keyValues.addAll(e.values()));

                    Long count = jdbcTemplate.queryForObject(arr[0], keyValues.toArray(), Long.class);

                    if(count==null || (count<list.size() && count > 0)){
                        if(log.isDebugEnabled()){
                            log.debug(String.format("WARN: 数据库记录数不一致： exists(%d) < update(%d)，先删除，再insert",count, list.size()));
                        }
                        // 先删除，再insert
                        this.onRemove(list, def);
                        this.onInsert(list, def);
                    }else if(count==0){
                        if(log.isDebugEnabled()){
                            log.debug(String.format("WARN: 数据库记录数不一致： exists(%d) < update(%d)，直接insert",count, list.size()));
                        }
                        // 不删除，直接insert
                        this.onInsert(list, def);
                    }else{
                        if(log.isDebugEnabled()){
                            log.debug(arr[1]);
                        }
                        // 正常更新
                        jdbcTemplate.batchUpdate(
                                arr[1],
                                list.entrySet().stream().map(e -> {
                                    List<Object> data = new ArrayList<>();
                                    data.addAll(e.getValue().values());
                                    data.addAll(e.getKey().values());
                                    return data.toArray();
                                }).collect(Collectors.toList())
                        );
                    }

                });
    }

    @Override
    public void onRemove(Map<Map<String,Object>,Map<String,Object>> list, DocDefDataSync def) {

        list.entrySet()
                .stream()
                .findAny()
                .map(e->{
                    String keyPlaceholder  = e.getKey()  .keySet().stream().map(i->String.format("%s = ?",i)).collect(Collectors.joining(","));

                    return String.format("delete from %s where %s", def.getTargetArgs(), keyPlaceholder);
                }).ifPresent(sql -> {
                    if(log.isDebugEnabled())log.debug(sql);
                    jdbcTemplate.batchUpdate(
                            sql,
                            list.keySet()
                                    .stream()
                                    .map(e -> e.values().toArray())
                                    .collect(Collectors.toList())
                    );
                });
    }
}
