package cn.nkpro.ts5.engine.doc.impl.datasync;

import cn.nkpro.ts5.engine.doc.abstracts.NkAbstractDocDataDiffedSync;
import cn.nkpro.ts5.exception.TfmsDefineException;
import cn.nkpro.ts5.orm.mb.gen.DocDefDataSync;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component("NkDocSQLDataSync")
public class NkDocSQLDataSyncImpl extends NkAbstractDocDataDiffedSync<Map<String,Object>> {

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
                            .ifPresent(i->{throw new TfmsDefineException(String.format("数据同步服务 %s 配置错误，KEY[ %s ]重复",def.getTargetSvr(),i));});

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
                    String valuePlaceholder= e.getValue().keySet().stream().map(i->String.format("%s = ?",i)).collect(Collectors.joining(","));
                    String keyPlaceholder  = e.getKey()  .keySet().stream().map(i->String.format("%s = ?",i)).collect(Collectors.joining(","));

                    return String.format("update %s set %s where %s", def.getTargetArgs(), valuePlaceholder, keyPlaceholder);
                }).ifPresent(sql -> {
                    if(log.isDebugEnabled())log.debug(sql);
                    jdbcTemplate.batchUpdate(
                            sql,
                            list.entrySet().stream().map(e -> {
                                List<Object> data = new ArrayList<>();
                                data.addAll(e.getValue().values());
                                data.addAll(e.getKey().values());
                                return data.toArray();
                            }).collect(Collectors.toList())
                    );
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
