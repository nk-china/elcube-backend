package cn.nkpro.tfms.platform.services.impl;

import cn.nkpro.tfms.platform.basis.Constants;
import cn.nkpro.tfms.platform.custom.EnumDocClassify;
import cn.nkpro.tfms.platform.custom.TfmsComponent;
import cn.nkpro.ts5.exception.TfmsComponentException;
import cn.nkpro.ts5.exception.TfmsIllegalContentException;
import cn.nkpro.tfms.platform.mappers.gen.*;
import cn.nkpro.tfms.platform.model.DefDocStatusExt;
import cn.nkpro.tfms.platform.services.TfmsDefDeployAble;
import cn.nkpro.ts5.supports.RedisSupport;
import cn.nkpro.tfms.platform.basis.TfmsCustomObjectManager;
import cn.nkpro.tfms.platform.model.DefDocComponentBO;
import cn.nkpro.tfms.platform.model.DefDocTypeBO;
import cn.nkpro.tfms.platform.model.po.*;
import cn.nkpro.tfms.platform.custom.TfmsCardComponent;
import cn.nkpro.tfms.platform.services.TfmsDefDocTypeService;
import cn.nkpro.tfms.platform.custom.doc.TfmsDocProcessor;
import cn.nkpro.tfms.platform.custom.interceptor.TfmsDocStateInterceptor;
import cn.nkpro.ts5.utils.BeanUtilz;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import cn.nkpro.tfms.platform.model.util.PageList;
import cn.nkpro.ts5.config.mybatis.pagination.PaginationContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by bean on 2020/6/10.
 */
@Slf4j
@Service
public class TfmsDefDocTypeServiceImpl implements TfmsDefDocTypeService, TfmsDefDeployAble,InitializingBean {

    private ExpressionParser parser = new SpelExpressionParser();
    @Autowired
    private DefDocTypeMapper defDocTypeMapper;
    @Autowired
    private DefDocStatusMapper defDocStatusMapper;
    @Autowired
    private DefDocIndexRuleMapper defDocIndexRuleMapper;
    @Autowired
    private DefDocBpmMapper defDocBpmMapper;
    @Autowired
    private DefDocComponentMapper defDocComponentMapper;
    @Autowired
    private RedisSupport<DefDocTypeBO> redisSupport;
    @Autowired
    private RedisSupport<Object> redisSupportObject;
    @Autowired
    private TfmsCustomObjectManager customObjectManager;

    @Override
    public PageList<DefDocType> getPage(String docClassify, String docType, String keyword, int from, int rows,String orderField,String order){

        DefDocTypeExample example = new DefDocTypeExample();

        DefDocTypeExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(docClassify))
            criteria.andDocClassifyEqualTo(docClassify);
        if(StringUtils.isNotBlank(docType))
            criteria.andDocTypeEqualTo(docType);
        if(StringUtils.isNotBlank(keyword))
            criteria.andDocNameLike(String.format("%%%s%%",keyword));
        if(StringUtils.isNotBlank(orderField)){
            example.setOrderByClause(String.format("%s %s",orderField,order));
        }

        PaginationContext context = PaginationContext.init();
        List<DefDocType> list = defDocTypeMapper.selectByExample(example, new RowBounds(from, rows));
        return new PageList<>(list,from, rows, context.getTotal());
    }

    @Override
    public List<DefDocType> getDocTypes(EnumDocClassify classify){
        // todo 这里要加上cache
        DefDocTypeExample example = new DefDocTypeExample();
        // 默认获取版本号为1的docType配置
        example.createCriteria().andVersionEqualTo(1)
                .andDocClassifyEqualTo(classify.name());
        example.setOrderByClause("DOC_TYPE");

        return defDocTypeMapper.selectByExample(example);
    }


    @Override
    public List<Map<String, String>> classifys(){
        return Arrays.stream(EnumDocClassify.values())
                .map(classify -> {
                    Map<String,String> map = new HashMap<>();
                    map.put("value",classify.name());
                    map.put("label",String.format("%s | %s",classify.getDesc(),classify.name()));
                    return map;
                })
                .collect(Collectors.toList());
    }


    @Override
    public Map<String, Object> options(EnumDocClassify docClassify){
        Map<String,Object> options = new HashMap<>();
        options.put("docInterceptors",customObjectManager.getDocProcessorNames(docClassify));
        options.put("docStateInterceptors",customObjectManager.getInterceptorNames(TfmsDocStateInterceptor.class,true));
        options.put("components",customObjectManager.getCustomComponents(docClassify));
        return options;
    }

    @Override
    public void doUpdate(DefDocTypeBO defDocTypeBO, boolean create,boolean force){

        DefDocType exists = defDocTypeMapper.selectByPrimaryKey(defDocTypeBO);
        Assert.isTrue(force || !create || exists==null,
                String.format("单据类型%s已存在",defDocTypeBO.getDocType()));

        Assert.isTrue(Pattern.matches("^[A-Z0-9]{4}$",defDocTypeBO.getDocType()),
                "单据类型必须为A-Z以及0-9组成的4位字符串");

        TfmsDocProcessor docProcessor = customObjectManager.getCustomObject(defDocTypeBO.getRefObjectType(),TfmsDocProcessor.class);

        if(!create){
            Assert.isTrue(StringUtils.equals(exists.getDocClassify(),docProcessor.classify().name()),"对象扩展分类不一致");
        }

        // status
        DefDocStatusExample docStatusExample = new DefDocStatusExample();
        docStatusExample.createCriteria()
                .andDocTypeEqualTo(defDocTypeBO.getDocType())
                .andVersionEqualTo(defDocTypeBO.getVersion());
        defDocStatusMapper.deleteByExample(docStatusExample);
        defDocTypeBO.getStatus()
                .forEach(state->{

                    state.setDocType(defDocTypeBO.getDocType());
                    state.setVersion(defDocTypeBO.getVersion());
                    state.setOrderby(defDocTypeBO.getStatus().indexOf(state));
                    state.setUpdatedTime(DateTimeUtilz.nowSeconds());

                    defDocStatusMapper.insertSelective(state);
                });

        // bpmn
        DefDocBpmKey defDocBpmKey = new DefDocBpmKey();
        defDocBpmKey.setDocType(defDocTypeBO.getDocType());
        defDocBpmKey.setVersion(defDocTypeBO.getVersion());

        defDocBpmMapper.deleteByPrimaryKey(defDocBpmKey);
        if(defDocTypeBO.getBpm()!=null && StringUtils.isNotBlank(defDocTypeBO.getBpm().getProcessKey())){
            DefDocBpm defDocBpm = defDocTypeBO.getBpm();
            defDocBpm.setDocType(defDocTypeBO.getDocType());
            defDocBpm.setVersion(defDocTypeBO.getVersion());
            defDocBpm.setUpdatedTime(DateTimeUtilz.nowSeconds());
            defDocBpmMapper.insert(defDocBpm);
        }

        // indexRules
        DefDocIndexRuleExample docIndexRuleExample = new DefDocIndexRuleExample();
        docIndexRuleExample.createCriteria()
                .andDocTypeEqualTo(defDocTypeBO.getDocType())
                .andVersionEqualTo(defDocTypeBO.getVersion());
        defDocIndexRuleMapper.deleteByExample(docIndexRuleExample);
        if(defDocTypeBO.getIndexRules()!=null) {
            defDocTypeBO.getIndexRules()
                    .forEach(indexRule -> {
                        indexRule.setDocType(defDocTypeBO.getDocType());
                        indexRule.setVersion(defDocTypeBO.getVersion());
                        indexRule.setUpdatedTime(DateTimeUtilz.nowSeconds());

                        defDocIndexRuleMapper.insertSelective(indexRule);
                    });
        }

        // components
        DefDocComponentExample docComponentExample = new DefDocComponentExample();
        docComponentExample.createCriteria()
                .andDocTypeEqualTo(defDocTypeBO.getDocType())
                .andVersionEqualTo(defDocTypeBO.getVersion());
        defDocComponentMapper.deleteByExample(docComponentExample);
        defDocTypeBO.getCustomComponents()
                .forEach(doc -> {

                    doc.setDocType(defDocTypeBO.getDocType());
                    doc.setVersion(defDocTypeBO.getVersion());
                    doc.setUpdatedTime(DateTimeUtilz.nowSeconds());
                    doc.setOrderby(defDocTypeBO.getCustomComponents().indexOf(doc));
                    doc.setMarkdownFlag(StringUtils.isNotBlank(doc.getMarkdown())?1:0);
                    defDocComponentMapper.insertSelective(doc);
                });

        // 循环调用组件
        log.debug("对单据数据进行反序列化");
        doInComponents(defDocTypeBO,(component,defDocComponent)-> {

            Assert.isTrue(component!=null,String.format("组件实现类[%s]不存在",defDocComponent.getComponent()));

            Object componentData = defDocTypeBO.getCustomComponentsDef().get(component.getComponentName());
            try {
                defDocTypeBO.getCustomComponentsDef().put(
                        component.getComponentName(),
                        component.deserializeDef(componentData)
                );
                component.updateDef(defDocTypeBO);
            } catch (Exception e) {
                throw new TfmsComponentException(component.getComponentName(), e);
            }
        });

        //defDocTypeBO.setRefObjectTypeOptions(JSON.toJSONString(defDocTypeBO.getDocDef()));
        defDocTypeBO.setMarkdownFlag(StringUtils.isBlank(defDocTypeBO.getMarkdown())?0:1);

        // 单据
        defDocTypeBO.setUpdatedTime(DateTimeUtilz.nowSeconds());
        if(exists==null){
            defDocTypeMapper.insertSelective(defDocTypeBO);
        }else{
            defDocTypeMapper.updateByPrimaryKeySelective(defDocTypeBO);
        }

        redisSupport.delete(String.format(Constants.CACHE_DEF_DOC,defDocTypeBO.getDocType(),defDocTypeBO.getVersion()));
    }

    @Override
    public String getDocComponentDefMarkdown(DefDocComponentKey key){

        if(StringUtils.isNotBlank(key.getComponent())){
            DefDocComponent docComponent = defDocComponentMapper.selectByPrimaryKey(key);
            if(docComponent!=null){
                return docComponent.getMarkdown();
            }
        }else if(StringUtils.isNotBlank(key.getDocType())){
            DefDocType docType = defDocTypeMapper.selectByPrimaryKey(BeanUtilz.copyFromObject(key, DefDocTypeKey.class));
            if(docType!=null){
//                if(StringUtils.equals(EnumDocClassify.PROJECT.name(),docType.getDocClassify())){
//                    DefProjectDocExample example = new DefProjectDocExample();
//                    example.createCriteria().andProjectTypeEqualTo(docType.getDocType());
//                    defProjectDocMapper.selectByExample()
//                }
                return docType.getMarkdown();
            }
        }else{
            DefDocTypeExample example = new DefDocTypeExample();
            example.createCriteria()
                    //.andHeadVersionEqualTo(1)
                    //.andMarkdownFlagEqualTo(1)
            ;
            example.setOrderByClause("DOC_TYPE");
            List<DefDocType> list = defDocTypeMapper.selectByExample(example);

            StringBuilder builder = new StringBuilder();
            builder.append("\n\n### 业务 \n");
            list.stream()
                    .filter(type->StringUtils.equals(type.getDocClassify(),EnumDocClassify.PROJECT.name()))
                    .forEach(type->{
                        builder.append(String.format("* [%s | %s](nkdn://user/%s/%s)",type.getDocType(),type.getDocName(),type.getDocType(),type.getVersion()));
                        builder.append('\n');
                    });
            builder.append("\n\n### 交易 \n");
            list.stream()
                    .filter(type->StringUtils.equals(type.getDocClassify(),EnumDocClassify.TRANSACTION.name()))
                    .forEach(type->{
                        builder.append(String.format("* [%s | %s](nkdn://user/%s/%s)",type.getDocType(),type.getDocName(),type.getDocType(),type.getVersion()));
                        builder.append('\n');
                    });
            builder.append("\n\n### 伙伴角色 \n");
            list.stream()
                    .filter(type->StringUtils.equals(type.getDocClassify(),EnumDocClassify.PARTNER.name()))
                    .forEach(type->{
                        builder.append(String.format("* [%s | %s](nkdn://user/%s/%s)",type.getDocType(),type.getDocName(),type.getDocType(),type.getVersion()));
                        builder.append('\n');
                    });
            builder.append("\n\n### 伙伴单据 \n");
            list.stream()
                    .filter(type->StringUtils.equals(type.getDocClassify(),EnumDocClassify.PARTNER_T.name()))
                    .forEach(type->{
                        builder.append(String.format("* [%s | %s](nkdn://user/%s/%s)",type.getDocType(),type.getDocName(),type.getDocType(),type.getVersion()));
                        builder.append('\n');
                    });

            builder.append('\n').append('\n').append('\n').append('\n').append('\n').append('\n');

            return builder.toString();
        }
        return null;
    }

    /**
     * 根据单据类型 获取当前日期下 单据对应的配置信息
     * @param docType docType
     * @return DefDocTypeBO
     */
    @Override
    public DefDocTypeBO getDocDefined(String docType){

        String today = DateTimeUtilz.todayShortString();

        DefDocTypeExample example = new DefDocTypeExample();
        example.createCriteria()
                .andDocTypeEqualTo(docType)
                .andValidFromLessThanOrEqualTo(today)
                .andValidToGreaterThanOrEqualTo(today);
        example.setOrderByClause("VERSION DESC");

        Optional<DefDocType> first = defDocTypeMapper
                .selectByExample(example, new RowBounds(0, 1))
                .stream()
                .findFirst();

        Assert.isTrue(first.isPresent(),String.format("单据类型[%s]的配置没有找到",docType));

        return getDocDefined(docType,first.get().getVersion(),true, false,false);
    }

    /**
     * 根据单据类型及版本号 获取单据对应的配置信息
     * @param docType docType
     * @param version version
     * @return DefDocTypeBO
     */
    @Override
    public DefDocTypeBO getDocDefined(String docType,Integer version, boolean includeComponentDef, boolean includeComponentMarkdown, boolean ignoreError){
        String cacheKey = String.format(Constants.CACHE_DEF_DOC,docType,version);

        DefDocTypeBO defDocTypeBO = redisSupport.getIfAbsent(cacheKey,StringUtils.EMPTY,()->{

            // todo 暂时只考虑了type的版本管理，子配置表都没有版本管理
            DefDocTypeKey key = new DefDocTypeKey();
            key.setDocType(docType);
            key.setVersion(version);

            DefDocType defDocType = defDocTypeMapper.selectByPrimaryKey(key);
            defDocType.setMarkdown(null);
            Assert.notNull(defDocType,String.format("单据类型[%s]的配置没有找到",docType));

            DefDocTypeBO def = BeanUtilz.copyFromObject(defDocType,DefDocTypeBO.class);

            // status
            DefDocStatusExample statusExample = new DefDocStatusExample();
            statusExample.createCriteria()
                    .andDocTypeEqualTo(docType)
                    .andVersionEqualTo(version);
            statusExample.setOrderByClause("ORDERBY");
            def.setStatus(BeanUtilz.copyFromList(defDocStatusMapper.selectByExample(statusExample), DefDocStatusExt.class));

            // indexRules
            DefDocIndexRuleExample indexRuleExample = new DefDocIndexRuleExample();
            indexRuleExample.createCriteria()
                    .andDocTypeEqualTo(docType)
                    .andVersionEqualTo(version);
            def.setIndexRules(defDocIndexRuleMapper.selectByExample(indexRuleExample));

            // bpm
            DefDocBpmKey defDocBpmKey = new DefDocBpmKey();
            defDocBpmKey.setDocType(def.getDocType());
            defDocBpmKey.setVersion(def.getVersion());

            def.setBpm(defDocBpmMapper.selectByPrimaryKey(defDocBpmKey));

            // components
            DefDocComponentExample componentExample = new DefDocComponentExample();
            componentExample.createCriteria()
                    .andDocTypeEqualTo(docType)
                    .andVersionEqualTo(version);
            componentExample.setOrderByClause("ORDERBY");
            def.setCustomComponents(BeanUtilz.copyFromList(defDocComponentMapper.selectByExample(componentExample), DefDocComponentBO.class));


            return def;
        });

        TfmsDocProcessor docProcessor = customObjectManager.getCustomObject(defDocTypeBO.getRefObjectType(),TfmsDocProcessor.class);

        defDocTypeBO.setDocHeaderComponent(docProcessor.getDocHeaderComponentName());
        defDocTypeBO.setDocDefNames(docProcessor.getDocDefComponentNames());
        defDocTypeBO.setDocDef(docProcessor.getDocProcessDef(defDocTypeBO));

        if(includeComponentMarkdown){
            DefDocComponentExample componentExample = new DefDocComponentExample();
            componentExample.createCriteria()
                    .andDocTypeEqualTo(docType)
                    .andVersionEqualTo(version);
            componentExample.setOrderByClause("ORDERBY");
            defDocTypeBO.setCustomComponents(BeanUtilz.copyFromList(defDocComponentMapper.selectByExampleWithBLOBs(componentExample), DefDocComponentBO.class));
        }

        if(includeComponentDef){

            List<String> hashKeys = new ArrayList<>();
            doInComponents(defDocTypeBO,(component,defDocComponent) -> {
                if (component != null) {
                    if(component.cacheDef()){
                        hashKeys.add(component.getComponentName());
                    }
                }
            });
            Map<String, Object> objectHash = redisSupportObject.getHash(cacheKey, hashKeys);

            // 循环加载自定义组件配置
            doInComponents(defDocTypeBO,(component,defDocComponent) -> {

                if(component!=null){
                    defDocComponent.setComponentName(StringUtils.defaultIfBlank(defDocComponent.getComponentName(),component.getComponentDesc()));

                    if(component instanceof TfmsCardComponent) {
                        TfmsCardComponent cardComponent = (TfmsCardComponent) component;
                        defDocComponent.setDataComponent(cardComponent.getDataComponentName());
                        defDocComponent.setDataComponentExtNames(cardComponent.getDataComponentExtNames());
                        defDocComponent.setPageComponentNames(cardComponent.getPageComponentNames());
                    }
                    defDocComponent.setDefComponentNames(component.getDefComponentNames());

                    Object def;
                    if(component.cacheDef()){
                        def = objectHash.computeIfAbsent(component.getComponentName(), (key) -> {
                            // 执行组件getData方法
                            Object object;
                            try {
                                object = component.getDef(defDocTypeBO);
                            } catch (Exception e) {
                                throw new TfmsComponentException(component.getComponentName(), e);
                            }
                            //object = object == null? Collections.EMPTY_MAP:object;
                            redisSupportObject.putHash(cacheKey, key, object);

                            return object;
                        });
                    }else{
                        try {
                            def = component.getDef(defDocTypeBO);
                        } catch (Exception e) {
                            throw new TfmsComponentException(component.getComponentName(), e);
                        }
                    }

                    defDocTypeBO.getCustomComponentsDef().put(
                            component.getComponentName(),
                            def
                    );
                }else{
                    defDocComponent.setComponentName("<组件对象缺失，请检查配置>");
                }
            });

        }

        if(!ignoreError)
            Assert.notEmpty(defDocTypeBO.getStatus(),String.format("单据类型[%s]的状态配置没有找到",docType));

        return defDocTypeBO;
    }

    @Override
    public DefDocTypeBO getDocDefinedRuntime(String docType, BizDoc doc){

        DefDocTypeBO defined = doc!=null?getDocDefined(doc.getDocType(), doc.getDefVersion(), true, false,false)
                                        :getDocDefined(docType);
        String runtimeState =  doc!=null?doc.getDocState()
                                        :defined.getStatus()
                                            .stream()
                                            .filter(state->StringUtils.equals(state.getPreDocState(),Constants.BIZ_DEFAULT_EMPTY))
                                            .findAny()
                                            .orElseThrow(IllegalArgumentException::new)
                                            .getDocState();

        defined.setStatus(
                defined.getStatus().stream()
                        .map(state->BeanUtilz.copyFromObject(state, DefDocStatusExt.class))
                        .peek(state->state.setAvailable(StringUtils.equalsAny(
                                runtimeState,
                                state.getPreDocState(),
                                state.getDocState())))
                        //.distinct()
                        .collect(Collectors.toList())
        );

        BizDoc cxt = doc!=null?doc:new BizDoc();
        defined.getCustomComponents()
                .forEach(defDocComponentBO -> {
                    defDocComponentBO.setWriteable(true);
                    if(StringUtils.isNotBlank(defDocComponentBO.getEditableSpEL())){
                        try {
                            defDocComponentBO.setWriteable((Boolean) parser.parseExpression(defDocComponentBO.getEditableSpEL()).getValue(cxt));
                        }catch (Exception e){
                            throw new TfmsIllegalContentException("SpEL表达式["+defDocComponentBO.getEditableSpEL()+"]错误:"+e.getMessage());
                        }
                    }
                });
        return defined;
    }

    private void doInComponents(DefDocTypeBO defDocTypeBO, RunInComponents runInComponents){

        defDocTypeBO.getCustomComponents().forEach(defDocComponent -> {
            // 找到对应的组件实现类
            runInComponents.run(
                    customObjectManager.getCustomObjectIfExists(defDocComponent.getComponent(), TfmsComponent.class),
                    defDocComponent);
        });
    }

    @Override
    public int deployOrder() {
        return Integer.MIN_VALUE + 1;
    }

    @Override
    public Object deployExport(JSONObject config) {
        JSONArray array = config.getJSONArray("docTypes");
        if(array!=null){
            return array.stream()
                            .map(docType-> getDocDefined(docType.toString(),1,
                                    true,true,false))
                            .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public void deployImport(Object data) {
        if(data!=null)
            ((JSONArray)data).toJavaList(DefDocTypeBO.class)
                .forEach(defDocTypeBO -> doUpdate(defDocTypeBO,true,true));
    }

    @FunctionalInterface
    private interface RunInComponents{
        void run(TfmsComponent component, DefDocComponentBO defDocComponent);
    }

    @Override
    public void afterPropertiesSet() {
        // 系统初始化时删除配置缓存
        redisSupport.deletes(String.format(Constants.CACHE_DEF_DOC,"*","*"));
    }
}
