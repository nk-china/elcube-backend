package cn.nkpro.tfms.platform.controllers;

import cn.nkpro.tfms.platform.model.po.DefPartnerRole;
import cn.nkpro.tfms.platform.model.index.IndexPartner;
import cn.nkpro.ts5.config.mvc.CompressResponse;
import cn.nkpro.tfms.platform.services.TfmsDefPartnerRoleService;
import cn.nkpro.tfms.platform.services.TfmsPermService;
import cn.nkpro.tfms.platform.elasticearch.SearchEngine;
import cn.nkpro.tfms.platform.elasticearch.ESPageList;
import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bean on 2020/6/9.
 */
@WsDocNote("12.交易伙伴")
@Controller
@RequestMapping("/partner")
public class PartnerController {

    @Autowired
    private SearchEngine searchEngine;
    @Autowired
    protected TfmsPermService permService;
    @Autowired
    private TfmsDefPartnerRoleService defPartnerRoleService;

    @WsDocNote("1、拉取交易伙伴列表数据")
    @CompressResponse
    @RequestMapping(value = "/list",method = RequestMethod.POST)
    public ESPageList<IndexPartner> list(
            @WsDocNote("起始条目")@RequestParam("from") Integer from,
            @WsDocNote("查询条目")@RequestParam("rows") Integer rows,
            @WsDocNote("排序字段")@RequestParam(value = "orderField",   defaultValue = "createdTime") String orderField,
            @WsDocNote("排序顺序")@RequestParam(value = "order",        defaultValue = "desc") String order,
            @WsDocNote("搜索内容")@RequestParam(value = "keyword",      defaultValue = "*",required = false) String keyword,
            @WsDocNote("伙伴类型")@RequestParam(value = "partnerType",  required = false) String partnerType,
            @WsDocNote("伙伴角色")@RequestParam(value = "roles[]",      required = false) String[] roles,
            @WsDocNote("伙伴标签")@RequestParam(value = "tags[]",       required = false) String[] tags,
            @WsDocNote("汇总字段")@RequestParam(value = "$aggs[]",      required = false) String[] aggs) throws IOException {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.queryStringQuery(keyword).defaultField("$keyword"));

        if(partnerType!=null){
            boolQueryBuilder.must(QueryBuilders.termQuery("partnerType", partnerType));
        }
        if(roles!=null){
            boolQueryBuilder.must(QueryBuilders.termsQuery("roles", roles));
        }
        if(tags!=null){
            boolQueryBuilder.must(QueryBuilders.termsQuery("tags",tags));
        }

        FieldSortBuilder sortBuilder = SortBuilders.fieldSort(orderField)
                .order(StringUtils.equalsIgnoreCase(order, "desc") ? SortOrder.DESC : SortOrder.ASC);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .postFilter(permService.buildDocFilter(TfmsPermService.MODE_READ,null,"roles",true))
                .query(boolQueryBuilder)
                .sort(sortBuilder)
                .sort(SortBuilders.scoreSort())
                .from(from)
                .size(rows);

        if(aggs!=null) {
            FilterAggregationBuilder $aggs = AggregationBuilders.filter("$aggs", QueryBuilders.matchAllQuery());
            Arrays.stream(aggs).forEach(agg ->
                $aggs.subAggregation(AggregationBuilders.terms(agg).field(agg))
            );
            sourceBuilder.aggregation($aggs);
        }

        return searchEngine.searchPage(IndexPartner.class,sourceBuilder);
    }

    @WsDocNote("2、拉取交易伙伴角色定义列表")
    @CompressResponse
    @RequestMapping(value = "/all/roles")
    public List<DefPartnerRole> allRoles(){
        return defPartnerRoleService.getAllRoles(true);
    }
}
