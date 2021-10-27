package cn.nkpro.ts5.docengine.service.impl;

import cn.nkpro.ts5.docengine.service.NkDocPermService;
import cn.nkpro.ts5.exception.NkSystemException;
import cn.nkpro.ts5.docengine.model.DocDefHV;
import cn.nkpro.ts5.docengine.model.DocDefIV;
import cn.nkpro.ts5.docengine.model.DocHV;
import cn.nkpro.ts5.docengine.model.es.DocHES;
import cn.nkpro.ts5.data.elasticearch.SearchEngine;
import cn.nkpro.ts5.exception.NkAccessDeniedException;
import cn.nkpro.ts5.security.bo.GrantedAuthority;
import cn.nkpro.ts5.security.SecurityUtilz;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NkDocPermServiceImpl implements NkDocPermService {

    @Autowired@SuppressWarnings("all")
    private SearchEngine searchEngine;

    /**
     * 当前账号下 根据授权过滤单据卡片
     * @param mode 操作
     * @param docHV 单据
     */
    @Override
    public void filterDocCards(String mode, DocHV docHV){

        DocDefHV defHV = docHV.getDef();

        // 移除没有权限的卡片
        getDocAuthorities(mode,defHV.getDocType())
                .stream()
                .findFirst()
                .map(authority -> StringUtils.split(authority.getSubResource(),'|'))
                .ifPresent(authority->
                    defHV.getCards().removeIf(defIV ->{
                        boolean removeIf = !ArrayUtils.contains(authority,defIV.getCardKey());
                        if(removeIf){
                            docHV.getData().remove(defIV.getCardKey());
                        }
                        return removeIf;
                    })
                );

        // 设置卡片的writeable值
        defHV.getCards()
                .forEach(defIV -> defIV.setWriteable(true));
        getDocAuthorities(NkDocPermService.MODE_WRITE, defHV.getDocType())
                .stream()
                .findFirst()
                .map(writeAuthority -> StringUtils.split(writeAuthority.getSubResource(),'|'))
                .ifPresent(writeAuthority ->
                        defHV.getCards()
                            .stream()
                            .filter(DocDefIV::getWriteable)
                            .forEach(defIV -> defIV.setWriteable(
                                    ArrayUtils.contains(writeAuthority,defIV.getCardKey())
                            ))
                );
    }

    /**
     * 当前账号下 检查是否具有对指定单据的权限
     * @param mode 操作
     * @param docType 单据类型
     */
    @Override
    public void assertHasDocPerm(String mode, String docType){
        if(!hasDocPerm(mode,docType)){
            throw new NkAccessDeniedException(String.format("没有单据类型[%s]-[%s]访问权限",docType, mode));
        }
    }

    /**
     * 当前账号下 检查是否具有对指定单据类型的权限
     * @param mode 操作
     * @param docType 单据类型
     * @return boolean
     */
    @Override
    public boolean hasDocPerm(String mode, String docType){
        return !getDocAuthorities(mode,docType).isEmpty();
    }

    /**
     * 当前账号下 检查是否具有对指定单据的权限
     * @param mode 操作
     * @param docId 单据ID
     * @param docType 允许为空，查询所有
     * @throws NkAccessDeniedException 访问被拒绝
     */
    @Override
    public void assertHasDocPerm(String mode, String docId, String docType){
        if(!hasDocPerm(mode,docId,docType)){
            if(docType!=null)
                throw new NkAccessDeniedException(String.format("没有单据[%s:%s]-[%s]的访问权限", docType, docId, mode));
            else
                throw new NkAccessDeniedException(String.format("没有单据[%s]-[%s]的访问权限", docId, mode));
        }
    }
    /**
     * 当前账号下 检查是否具有对指定单据ID的权限
     * @param mode 操作
     * @param docId 单据ID
     * @param docType 单据类型
     * @return boolean
     */
    @Override
    public boolean hasDocPerm(String mode, String docId, String docType) {

        if(log.isInfoEnabled())log.info("* 检测单据权限 docType = {}, docId = {}, mode = {}", docType, docId, mode);
        if(SecurityUtilz.getAuthorities().stream()
                .anyMatch(g-> g.getAuthority().equals("*:*"))){
            if(log.isInfoEnabled())log.info("检测到 超级权限 pass");
            return true;
        }
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder permQuery = buildDocFilter(mode, docType, null, false);
        if(permQuery!=null)
            sourceBuilder.postFilter(permQuery);

        sourceBuilder.query(QueryBuilders.termQuery("docId",docId));
        try {
            boolean has = searchEngine.exists(DocHES.class,sourceBuilder);

            if(log.isInfoEnabled())log.info("* 检测到 权限 = {}, filter = {}", has, permQuery);
            return has;
        } catch (IOException e) {
            throw new NkSystemException(e);
        }
    }



    /**
     * 当前账号下 创建一个doc权限过滤器
     * @param mode 模式
     * @param docType 允许为空，查询所有
     * @return BoolQueryBuilder
     */
    @Override
    public BoolQueryBuilder buildDocFilter(String mode, String docType, String typeKey, boolean ignoreLimit){
        // 处理权限
        List<GrantedAuthority> authorities = getDocAuthorities(mode, docType);

        BoolQueryBuilder filter = QueryBuilders.boolQuery();

        if(authorities.isEmpty()){
            filter.must(QueryBuilders.idsQuery().addIds("NONE"));
            return filter;
        }

        // 这里优化了下，把相同limit的单据类型条件合并
        Map<String, GrantedAuthority> distinct = new HashMap<>();
        authorities.forEach(authority -> distinct.putIfAbsent(authority.getDocType(),authority));

        Map<String,List<String>> collectByLimit = new HashMap<>();

        distinct.forEach((type,authority)-> {
            String limit = ignoreLimit?null:authority.getLimitQuery();
            collectByLimit.putIfAbsent(limit,new ArrayList<>());
            collectByLimit.get(limit).add(authority.getDocType());
        });

        /*
         *  todo 注意，这里有个隐藏的bug
         *  如果用户的权限中包含*:*，或@*:*，且权限中还包含其他非*的单据类型权限，那么需要将*号解析为not exists docTypes
         *  否则的话，其他单据类型的limit在这个过滤条件中将失效，导致用户能查询到所有的单据数据
         */
        collectByLimit.forEach((limit,docTypes)->{
            BoolQueryBuilder builder = QueryBuilders.boolQuery();

            if(!docTypes.contains("*")){// 如果单据类型包含*号，表示需要匹配所有单据类型，因此不需要增加terms条件
                builder.must(QueryBuilders.termsQuery(StringUtils.defaultIfBlank(typeKey,"docType"), docTypes));
            }

            if(StringUtils.isNotBlank(limit)){
                builder.must(QueryBuilders.wrapperQuery(limit));
            }

            filter.should(builder);
        });

        log.trace("查询权限过滤:\n"+filter);

        return filter;
    }

    /**
     * 当前账号下 获取指定单据类型的授权模型对象
     * @param mode 操作
     * @param docType 单据类型
     * @return 授权模型对象集合
     */
    private List<GrantedAuthority> getDocAuthorities(String mode, String docType){
        return SecurityUtilz.getUser().getAuthorities()
                .stream()
                .filter(authority->
                        authority.getPermResource().equals("*")||
                                authority.getPermResource().equals("@*")||
                                authority.getPermResource().equalsIgnoreCase(String.format("@%s", docType))||
                                (docType==null && authority.getPermResource().startsWith("@"))
                )
                .filter(authority-> StringUtils.equalsAnyIgnoreCase(authority.getPermOperate(),"*",mode))
                .collect(Collectors.toList());
    }
}
