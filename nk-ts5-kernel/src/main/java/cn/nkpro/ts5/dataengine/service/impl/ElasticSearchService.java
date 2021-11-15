package cn.nkpro.ts5.dataengine.service.impl;

import cn.nkpro.ts5.data.elasticearch.ESSqlRequest;
import cn.nkpro.ts5.data.elasticearch.ESSqlResponse;
import cn.nkpro.ts5.data.elasticearch.LimitQueryBuilder;
import cn.nkpro.ts5.data.elasticearch.SearchEngine;
import cn.nkpro.ts5.data.elasticearch.annotation.ESDocument;
import cn.nkpro.ts5.dataengine.model.DataFieldDesc;
import cn.nkpro.ts5.dataengine.model.DataQueryResponse;
import cn.nkpro.ts5.dataengine.model.DataQueryRequest;
import cn.nkpro.ts5.dataengine.service.DataQueryService;
import cn.nkpro.ts5.docengine.model.es.DocHES;
import cn.nkpro.ts5.docengine.service.NkDocPermService;
import cn.nkpro.ts5.exception.NkAccessDeniedException;
import cn.nkpro.ts5.security.SecurityUtilz;
import cn.nkpro.ts5.utils.BeanUtilz;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.fieldcaps.FieldCapabilitiesResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component("ElasticSearchService")
public class ElasticSearchService implements DataQueryService {

    @Autowired@SuppressWarnings("all")
    private SearchEngine searchEngine;
    @Autowired@SuppressWarnings("all")
    protected NkDocPermService docPermService;


    @Override
    public List<DataFieldDesc> getFieldCaps(String index){

        if(!SecurityUtilz.hasAnyAuthority("*:*","#*:READ","#"+index+":READ")){
            throw new NkAccessDeniedException(String.format("没有索引[%s]的访问权限", index));
        }

        FieldCapabilitiesResponse fieldCaps = searchEngine.getFieldCaps(index);

        return fieldCaps.get().values()
                .stream()
                .map(a-> a.values().stream().findFirst().orElse(null))
                .filter(Objects::nonNull)
                .filter(e->!e.getName().startsWith("_"))
                .filter(e->!e.getName().equals("$keyword"))
                .filter(e->!e.getType().equals("object"))
                .map(e-> BeanUtilz.copyFromObject(e,DataFieldDesc.class))
                .collect(Collectors.toList());
    }

    @Override
    public DataQueryResponse queryPage(DataQueryRequest request) {
        return queryList(request);
    }

    @Override
    public DataQueryResponse queryList(DataQueryRequest request) {

        DataQueryResponse<Map<String,Object>> response = null;

        for(String sql : request.getSqlList()) {

            String index = searchEngine.parseSqlIndex(sql);

            QueryBuilder filterBuilder;
            if (DocHES.class.getAnnotation(ESDocument.class).value().equals(index)) {
                filterBuilder = docPermService.buildDocFilter(NkDocPermService.MODE_READ, null, null, false);
            } else {
                filterBuilder = docPermService.buildIndexFilter(index);
            }


            if (request.getConditions() != null) {
                BoolQueryBuilder conditionsFilter = QueryBuilders.boolQuery();
                JSONObject filter = request.getConditions();
                if (filter != null) {
                    filter.forEach((k, v) -> conditionsFilter.must(new LimitQueryBuilder(filter.getJSONObject(k))));
                }
                if (filterBuilder != null)
                    conditionsFilter.must(filterBuilder);

                if (!conditionsFilter.must().isEmpty())
                    filterBuilder = conditionsFilter;
            }

            ESSqlResponse sqlResponse = searchEngine.sql(new ESSqlRequest(sql, filterBuilder));

            if(response == null){
                List<DataQueryResponse.Column> columns = sqlResponse.getColumns().stream()
                        .map(column -> BeanUtilz.copyFromObject(column, DataQueryResponse.Column.class))
                        .collect(Collectors.toList());
                response = new DataQueryResponse<>(request.getSqlList(), columns, sqlResponse.transform().getList(),100,sqlResponse.getCursor());
            }else{
                response.getList().addAll(sqlResponse.transform().getList());
            }
        }

        if(response!=null && request.getSqlList().size()>1){
            response.setCursor(null);
        }

        return response;
    }
}
