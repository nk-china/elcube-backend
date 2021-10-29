package cn.nkpro.ts5.dataengine.controller;

import cn.nkpro.ts5.annotation.NkNote;
import cn.nkpro.ts5.data.elasticearch.ESSqlResponse;
import cn.nkpro.ts5.docengine.NkDocSearchService;
import org.elasticsearch.action.fieldcaps.FieldCapabilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by bean on 2020/6/9.
 */
@PreAuthorize("authenticated")
@NkNote("3.数据分析")
@RestController
@RequestMapping("/data/analyse")
public class DataAnalyseController {

    @Autowired@SuppressWarnings("all")
    private NkDocSearchService searchService;

    @NkNote("1.索引字段列表")
    @RequestMapping(value = "/fieldCaps/{index}",method = RequestMethod.POST)
    public Map<String, Map<String, FieldCapabilities>> getFieldCaps(@PathVariable String index) {
        return searchService.getFieldCaps(index);
    }
    @NkNote("2.执行sql")
    @RequestMapping(value = "/sql",method = RequestMethod.POST)
    public ESSqlResponse sql(@RequestBody String sql) {
        return searchService.searchBySql(sql).transform();
    }
}
