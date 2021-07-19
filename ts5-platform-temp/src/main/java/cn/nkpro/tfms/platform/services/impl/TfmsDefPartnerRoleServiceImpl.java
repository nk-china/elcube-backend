package cn.nkpro.tfms.platform.services.impl;

import cn.nkpro.tfms.platform.basis.Constants;
import cn.nkpro.tfms.platform.custom.EnumDocClassify;
import cn.nkpro.tfms.platform.services.TfmsDefDeployAble;
import cn.nkpro.tfms.platform.services.TfmsPermService;
import cn.nkpro.ts5.supports.RedisSupport;
import cn.nkpro.tfms.platform.basis.TfmsCustomObjectManager;
import cn.nkpro.tfms.platform.custom.interceptor.TfmsPartnerDocInterceptor;
import cn.nkpro.tfms.platform.mappers.gen.DefPartnerRoleDocMapper;
import cn.nkpro.tfms.platform.mappers.gen.DefPartnerRoleMapper;
import cn.nkpro.tfms.platform.model.*;
import cn.nkpro.tfms.platform.model.po.*;
import cn.nkpro.tfms.platform.services.TfmsDefDocTypeService;
import cn.nkpro.tfms.platform.services.TfmsDefPartnerRoleService;
import cn.nkpro.tfms.platform.custom.interceptor.TfmsPartnerInterceptor;
import cn.nkpro.ts5.utils.BeanUtilz;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import cn.nkpro.tfms.platform.model.util.PageList;
import cn.nkpro.ts5.config.mybatis.pagination.PaginationContext;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ArrayUtils;
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
 * Created by bean on 2020/7/15.
 */
@Service
public class TfmsDefPartnerRoleServiceImpl implements TfmsDefPartnerRoleService, TfmsDefDeployAble,InitializingBean {

    @Autowired
    private TfmsDefDocTypeService defDocTypeService;
    @Autowired
    private DefPartnerRoleMapper partnerRoleMapper;
    @Autowired
    private DefPartnerRoleDocMapper partnerRoleDocMapper;
    @Autowired
    private TfmsCustomObjectManager customObjectManager;
    @Autowired
    private RedisSupport<DefPartnerRoleBO> redisSupport;
    @Autowired
    private RedisSupport<List<DefPartnerRole>> redisSupportList;
    @Autowired
    private TfmsPermService permService;

    @Override
    public PageList<DefPartnerRole> getPage(String partnerRole, String keyword, Integer from, Integer rows, String orderField, String order) {
        DefPartnerRoleExample example = new DefPartnerRoleExample();

        DefPartnerRoleExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(partnerRole))
            criteria.andPartnerRoleEqualTo(partnerRole);
        if(StringUtils.isNotBlank(keyword))
            criteria.andPartnerRoleDescLike(String.format("%%%s%%",keyword));
        if(StringUtils.isNotBlank(orderField)){
            example.setOrderByClause(String.format("%s %s",orderField,order));
        }

        PaginationContext context = PaginationContext.init();
        List<DefPartnerRole> list = partnerRoleMapper.selectByExample(example, new RowBounds(from, rows));
        return new PageList<>(list,from, rows, context.getTotal());
    }

    @Override
    public DefPartnerRoleBO getPartnerRoleRuntimeDefined(BizDocBase role, BizDocBase preDoc){

        Assert.notNull(role,"角色不能为空");
        DefPartnerRoleBO defPartnerRole = getPartnerRoleDefined(role.getDocType());

        String[] preDocType = preDoc==null?
                new String[]{Constants.BIZ_DEFAULT_EMPTY,role.getDocType()} :
                new String[]{preDoc.getDocType()};//前置单据preDocType=@表示为角色主单据
        defPartnerRole.getDocs()
            .removeIf(defDoc ->
                !(StringUtils.equalsAny(defDoc.getPreDocType(),preDocType)
                &&
                // 符合前序单据状态的前置条件
                (
                    preDoc==null ||
                    StringUtils.isBlank(defDoc.getPreDocStatus()) ||
                    ArrayUtils.contains(StringUtils.split(defDoc.getPreDocStatus(),','),preDoc.getDocState())
                )
            ));

        return defPartnerRole;
    }

    @Override
    public DefPartnerRoleBO getPartnerRoleDefined(String partnerRole) {

        DefPartnerRoleBO defPartnerRoleBO = redisSupport.getIfAbsent(Constants.CACHE_DEF_PARTNER, partnerRole, ()->{

            DefPartnerRole defProjectType = partnerRoleMapper.selectByPrimaryKey(partnerRole);
            Assert.notNull(defProjectType,String.format("交易伙伴角色[%s]的配置没有找到",partnerRole));

            DefPartnerRoleBO def = BeanUtilz.copyFromObject(defProjectType,DefPartnerRoleBO.class);

            // 获取单据类型
            DefPartnerRoleDocExample docExample = new DefPartnerRoleDocExample();
            docExample.createCriteria()
                    .andPartnerRoleEqualTo(partnerRole);
            docExample.setOrderByClause("orderby");

            List<DefPartnerRoleDoc> projectDocList = partnerRoleDocMapper.selectByExample(docExample);
            def.setDocs(BeanUtilz.copyFromList(projectDocList,DefPartnerRoleDocLI.class));

            return def;
        });

        if(!CollectionUtils.isEmpty(defPartnerRoleBO.getDocs())){
            defPartnerRoleBO.getDocs().forEach(def -> {
                // 默认获取版本号为1的docType配置
                DefDocTypeBO defDocTypeBO = defDocTypeService.getDocDefined(def.getDocType(), 1, false,false, false);
                Assert.notNull(defDocTypeBO,String.format("没有找到交易伙伴下单据类型[%s]版本号为1的配置信息",def.getDocType()));

                def.setDocName(defDocTypeBO.getDocName());
            });
        }

        return defPartnerRoleBO;
    }

    @Override
    public Map<String, Object> options() {

        Map<String,Object> options = new HashMap<>();
        options.put("docTypes",defDocTypeService.getDocTypes(EnumDocClassify.PARTNER));
        options.put("flowDocTypes",defDocTypeService.getDocTypes(EnumDocClassify.PARTNER_T));
        options.put("partnerInterceptors",customObjectManager.getInterceptorNames(TfmsPartnerInterceptor.class,true));
        options.put("partnerDocInterceptors",customObjectManager.getInterceptorNames(TfmsPartnerDocInterceptor.class,true));

        return options;
    }

    @Override
    public void doUpdate(DefPartnerRoleBO defPartnerRole, Boolean create,boolean force) {

        DefPartnerRole exists = partnerRoleMapper.selectByPrimaryKey(defPartnerRole.getPartnerRole());
        Assert.isTrue(force || !create || exists==null,
                String.format("交易伙伴角色%s已存在",defPartnerRole.getPartnerRole()));

        // 单据
        DefPartnerRoleDocExample partnerRoleDocExample = new DefPartnerRoleDocExample();
        partnerRoleDocExample.createCriteria()
                .andPartnerRoleEqualTo(defPartnerRole.getPartnerRole());
        partnerRoleDocMapper.deleteByExample(partnerRoleDocExample);

        defPartnerRole.getDocs()
                .forEach(doc -> {

                    doc.setPartnerRole(defPartnerRole.getPartnerRole());
                    doc.setOrderby(defPartnerRole.getDocs().indexOf(doc));
                    doc.setUpdatedTime(DateTimeUtilz.nowSeconds());

                    partnerRoleDocMapper.insertSelective(doc);
                });

        // 业务
        defPartnerRole.setUpdatedTime(DateTimeUtilz.nowSeconds());
        if(exists==null){
            partnerRoleMapper.insertSelective(defPartnerRole);
        }else{
            partnerRoleMapper.updateByPrimaryKeySelective(defPartnerRole);
        }

        redisSupport.delete(Constants.CACHE_DEF_PARTNER,defPartnerRole.getPartnerRole());
        redisSupportList.delete(Constants.CACHE_DEF_PARTNERROLES);
    }

    @Override
    public List<DefPartnerRole> getAllRoles(boolean filterByPerm){
        List<DefPartnerRole> data = redisSupportList.getIfAbsent(Constants.CACHE_DEF_PARTNERROLES,()->{
            DefPartnerRoleExample defPartnerRoleExample = new DefPartnerRoleExample();
            defPartnerRoleExample.setOrderByClause("PARTNER_ROLE");
            return partnerRoleMapper.selectByExample(defPartnerRoleExample);
        });
        if(filterByPerm){
            data = data.stream()
                    .filter(defPartnerRole -> permService.hasDocPerm(TfmsPermService.MODE_WRITE,defPartnerRole.getPartnerRole()))
                    .collect(Collectors.toList());
        }
        return data;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 系统初始化时删除配置缓存
        redisSupport.delete(Constants.CACHE_DEF_PARTNER);
        redisSupportList.delete(Constants.CACHE_DEF_PARTNERROLES);
    }

    @Override
    public int deployOrder() {
        return 0;
    }

    @Override
    public List<DefPartnerRoleBO> deployExport(JSONObject config) {

        JSONArray array = config.getJSONArray("docTypes");
        if(array!=null && array.size()>0){
            DefPartnerRoleExample example = new DefPartnerRoleExample();
            example.createCriteria()
                    .andPartnerRoleIn(array.toJavaList(String.class));

            return partnerRoleMapper.selectByExample(null)
                    .stream()
                    .map(defPartnerRole ->
                            getPartnerRoleDefined(defPartnerRole.getPartnerRole())
                    )
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public void deployImport(Object data) {
        if(data!=null)
            ((JSONArray)data).toJavaList(DefPartnerRoleBO.class)
                .forEach(defPartnerRoleBO -> doUpdate(defPartnerRoleBO,true,true));
    }
}
