package cn.nkpro.tfms.platform.services.impl;

import cn.nkpro.tfms.platform.basis.Constants;
import cn.nkpro.tfms.platform.custom.EnumDocClassify;
import cn.nkpro.tfms.platform.services.TfmsDefDeployAble;
import cn.nkpro.tfms.platform.services.TfmsPermService;
import cn.nkpro.ts5.supports.RedisSupport;
import cn.nkpro.tfms.platform.basis.TfmsCustomObjectManager;
import cn.nkpro.tfms.platform.custom.interceptor.TfmsProjectDocInterceptor;
import cn.nkpro.tfms.platform.mappers.gen.*;
import cn.nkpro.tfms.platform.model.DefDocTypeBO;
import cn.nkpro.tfms.platform.model.DefProjectDocLI;
import cn.nkpro.tfms.platform.model.DefProjectTypeBO;
import cn.nkpro.tfms.platform.model.po.*;
import cn.nkpro.tfms.platform.services.TfmsDefDocTypeService;
import cn.nkpro.tfms.platform.services.TfmsDefProjectTypeService;
import cn.nkpro.tfms.platform.custom.interceptor.TfmsProjectInterceptor;
import cn.nkpro.tfms.platform.custom.interceptor.TfmsProjectStateInterceptor;
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
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by bean on 2020/7/7.
 */
@Slf4j
@Service
public final class TfmsDefProjectTypeServiceImpl implements TfmsDefProjectTypeService, TfmsDefDeployAble, InitializingBean {

    @Autowired
    private TfmsDefDocTypeService defDocTypeService;
    @Autowired
    private RedisSupport<DefProjectTypeBO> redisSupport;
    @Autowired
    private DefProjectTypeMapper defProjectTypeMapper;
    @Autowired
    private DefProjectStatusMapper defProjectStatusMapper;
    @Autowired
    private DefProjectDocMapper defProjectDocMapper;
    @Autowired
    private TfmsCustomObjectManager customObjectManager;
    @Autowired
    private TfmsPermService permService;

    @Override
    public PageList<DefProjectType> getPage(String projectType, String keyword, int from, int rows,String orderField,String order){

        DefProjectTypeExample example = new DefProjectTypeExample();

        DefProjectTypeExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(projectType))
            criteria.andProjectTypeEqualTo(projectType);
        if(StringUtils.isNotBlank(keyword))
            criteria.andProjectTypeDescLike(String.format("%%%s%%",keyword));
        if(StringUtils.isNotBlank(orderField)){
            example.setOrderByClause(String.format("%s %s",orderField,order));
        }

        PaginationContext context = PaginationContext.init();
        List<DefProjectType> list = defProjectTypeMapper.selectByExample(example, new RowBounds(from, rows));
        return new PageList<>(list,from, rows, context.getTotal());
    }

    @Override
    public List<DefProjectType> getAllTypes(){

        DefProjectTypeExample example = new DefProjectTypeExample();
        example.setOrderByClause("PROJECT_TYPE");

        return defProjectTypeMapper.selectByExample(example)
                .stream()
                .filter(defProjectType -> permService.hasDocPerm(TfmsPermService.MODE_WRITE,defProjectType.getProjectType()))
                .collect(Collectors.toList());
    }
    /**
     * 根据业务类型 获取当前日期下 业务对应的配置信息
     * @param projectType 业务类型
     * @return 业务类型
     */
    @Override
    public DefProjectTypeBO getProjectDefined(String projectType) {

        String today = DateTimeUtilz.todayShortString();

        DefProjectTypeExample example = new DefProjectTypeExample();
        example.createCriteria()
                .andProjectTypeEqualTo(projectType)
                .andValidFromLessThanOrEqualTo(today)
                .andValidToGreaterThanOrEqualTo(today);
        example.setOrderByClause("VERSION DESC");

        Optional<DefProjectType> first = defProjectTypeMapper
                .selectByExample(example, new RowBounds(0, 1))
                .stream()
                .findFirst();

        Assert.isTrue(first.isPresent(),String.format("业务类型[%s]的配置没有找到",projectType));

        return getProjectDefined(projectType,first.get().getVersion());

    }

    @Override
    public DefProjectTypeBO getProjectDefined(String projectType, Integer version) {

        DefProjectTypeBO defProject = redisSupport.getIfAbsent(
                Constants.CACHE_DEF_PROJECT, String.format("%s-%d",projectType,version), ()->{

            DefProjectTypeKey key = new DefProjectTypeKey();
            key.setProjectType(projectType);
            key.setVersion(version);

            DefProjectType defProjectType = defProjectTypeMapper.selectByPrimaryKey(key);
            Assert.notNull(defProjectType,String.format("业务类型[%s]的配置没有找到",projectType));

            DefProjectTypeBO def = BeanUtilz.copyFromObject(defProjectType,DefProjectTypeBO.class);

            DefProjectStatusExample statusExample = new DefProjectStatusExample();
            statusExample.createCriteria()
                    .andProjectTypeEqualTo(projectType)
                    .andVersionEqualTo(version);
            statusExample.setOrderByClause("orderby");
            def.setStatus(defProjectStatusMapper.selectByExample(statusExample));

            // 获取单据类型
            DefProjectDocExample docExample = new DefProjectDocExample();
            docExample.createCriteria()
                    .andProjectTypeEqualTo(projectType)
                    .andVersionEqualTo(version);
            docExample.setOrderByClause("orderby");

            List<DefProjectDoc> projectDocList = defProjectDocMapper.selectByExample(docExample);
            def.setDocs(BeanUtilz.copyFromList(projectDocList,DefProjectDocLI.class));

            return def;
        });

        if(!CollectionUtils.isEmpty(defProject.getDocs())){
            defProject.getDocs().forEach(defProjectDoc -> {
                // 默认获取版本号为1的docType配置
                DefDocTypeBO defDocTypeBO = defDocTypeService.getDocDefined(
                        defProjectDoc.getDocType(),
                        1,
                        false,
                        false,
                        false);
                Assert.notNull(defDocTypeBO,
                        String.format("没有找到业务下单据类型[%s]版本号为1的配置信息",defProjectDoc.getDocType()));

                defProjectDoc.setDocName(defDocTypeBO.getDocName());
            });
        }

        return defProject;
    }

    @Override
    public Map<String, Object> options(){

        Map<String,Object> options = new HashMap<>();
        options.put("docTypes",defDocTypeService.getDocTypes(EnumDocClassify.TRANSACTION));
        options.put("projectInterceptors",customObjectManager.getInterceptorNames(TfmsProjectInterceptor.class,false));
        options.put("projectStateInterceptors",customObjectManager.getInterceptorNames(TfmsProjectStateInterceptor.class,true));
        options.put("projectDocInterceptors",customObjectManager.getInterceptorNames(TfmsProjectDocInterceptor.class,true));
        return options;
    }

    @Override
    public void doUpdate(DefProjectTypeBO defProjectType, boolean create, boolean force){

        DefProjectType exists = defProjectTypeMapper.selectByPrimaryKey(defProjectType);

        Assert.isTrue(force || !create || exists==null,
                String.format("业务类型[%s]已存在",defProjectType.getProjectType()));

        DefDocTypeBO docDefined = defDocTypeService.getDocDefined(defProjectType.getProjectType());
        Assert.isTrue(EnumDocClassify.valueOf(docDefined.getDocClassify()).isContainer(),
                String.format("单据类型[%s]不是一个有效的业务分类",defProjectType.getProjectType()));

        // 状态
        DefProjectStatusExample projectStatusExample = new DefProjectStatusExample();
        projectStatusExample.createCriteria()
                .andProjectTypeEqualTo(defProjectType.getProjectType())
                .andVersionEqualTo(defProjectType.getVersion());
        defProjectStatusMapper.deleteByExample(projectStatusExample);
        defProjectType.getStatus()
                .forEach(state->{

                    state.setProjectType(defProjectType.getProjectType());
                    state.setVersion(defProjectType.getVersion());
                    state.setOrderby(defProjectType.getStatus().indexOf(state));
                    state.setUpdatedTime(DateTimeUtilz.nowSeconds());

                    defProjectStatusMapper.insertSelective(state);
                });

        // 单据
        DefProjectDocExample projectDocExample = new DefProjectDocExample();
        projectDocExample.createCriteria()
                .andProjectTypeEqualTo(defProjectType.getProjectType())
                .andVersionEqualTo(defProjectType.getVersion());
        defProjectDocMapper.deleteByExample(projectDocExample);
        defProjectType.getDocs()
                .forEach(doc -> {

                    doc.setProjectType(defProjectType.getProjectType());
                    doc.setVersion(defProjectType.getVersion());
                    doc.setOrderby(defProjectType.getDocs().indexOf(doc));
                    doc.setUpdatedTime(DateTimeUtilz.nowSeconds());

                    defProjectDocMapper.insertSelective(doc);
                });

        // 业务
        defProjectType.setUpdatedTime(DateTimeUtilz.nowSeconds());
        if(exists==null){
            defProjectTypeMapper.insertSelective(defProjectType);
        }else{
            defProjectTypeMapper.updateByPrimaryKeySelective(defProjectType);
        }

        redisSupport.delete(Constants.CACHE_DEF_PROJECT,
                String.format("%s-%d",defProjectType.getProjectType(),defProjectType.getVersion()));
    }

    @Override
    public final void afterPropertiesSet() {
        // 系统初始化时删除配置缓存
        redisSupport.delete(Constants.CACHE_DEF_PROJECT);
    }

    @Override
    public int deployOrder() {
        return 0;
    }

    @Override
    public List<DefProjectTypeBO> deployExport(JSONObject config) {
        JSONArray array = config.getJSONArray("docTypes");
        if(array!=null && array.size()>0) {
            DefProjectTypeExample example = new DefProjectTypeExample();
            example.createCriteria()
                    .andProjectTypeIn(array.toJavaList(String.class));

            return defProjectTypeMapper.selectByExample(null)
                    .stream()
                    .map(defProjectType ->
                            getProjectDefined(defProjectType.getProjectType(),defProjectType.getVersion())
                    )
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public void deployImport(Object data) {
        if(data!=null)
            ((JSONArray)data).toJavaList(DefProjectTypeBO.class)
                .forEach(defProjectTypeBO -> doUpdate(defProjectTypeBO,true,true));
    }
}
