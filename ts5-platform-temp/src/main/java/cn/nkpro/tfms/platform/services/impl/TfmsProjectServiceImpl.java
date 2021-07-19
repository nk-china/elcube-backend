package cn.nkpro.tfms.platform.services.impl;

import cn.nkpro.tfms.platform.basis.Constants;
import cn.nkpro.tfms.platform.basis.TfmsCustomObjectManager;
import cn.nkpro.tfms.platform.custom.doc.defaults.BizProjectDoc;
import cn.nkpro.tfms.platform.custom.doc.defaults.BizProjectTranDoc;
import cn.nkpro.tfms.platform.custom.interceptor.TfmsProjectDocInterceptor;
import cn.nkpro.ts5.exception.TfmsDefineException;
import cn.nkpro.tfms.platform.mappers.gen.BizProjectMapper;
import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.BizProjectBO;
import cn.nkpro.tfms.platform.model.DefProjectDocLI;
import cn.nkpro.tfms.platform.model.DefProjectTypeBO;
import cn.nkpro.tfms.platform.model.po.BizProject;
import cn.nkpro.tfms.platform.services.TfmsDefProjectTypeService;
import cn.nkpro.tfms.platform.services.TfmsPermService;
import cn.nkpro.tfms.platform.services.TfmsProjectService;
import cn.nkpro.ts5.supports.RedisSupport;
import cn.nkpro.ts5.utils.BeanUtilz;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TfmsProjectServiceImpl implements TfmsProjectService {


    @Autowired
    private RedisSupport<BizProjectBO> redisSupport;
    @Autowired
    private TfmsPermService permService;
    @Autowired
    private TfmsCustomObjectManager customObjectManager;
    @Autowired
    private TfmsDefProjectTypeService defProjectTypeService;
    @Autowired
    private BizProjectMapper projectMapper;

    @Override
    public BizProjectBO createProject(String projectId, String projectType) {

        DefProjectTypeBO define = defProjectTypeService.getProjectDefined(projectType);
        Assert.notNull(define,                  "业务类型未配置");
        Assert.notEmpty(define.getStatus(),     "业务状态（里程碑）未配置");

        BizProjectBO projectBO = new BizProjectBO();
        projectBO.setProjectState(define.getStatus().get(0).getProjectState());

        projectBO.setProjectId(projectId);
        projectBO.setProjectType(projectType);
        projectBO.setProjectNumber("<NEW PROJECT>");
        projectBO.setProjectState(define.getStatus().get(0).getProjectState());

        projectBO.setCreatedTime(DateTimeUtilz.nowSeconds());
        projectBO.setUpdatedTime(DateTimeUtilz.nowSeconds());
        projectBO.setDefVersion(define.getVersion());
        projectBO.setDefined(define);

        return projectBO;
    }

    @Override
    public void doUpdateProject(BizProjectBO projectBO, boolean isCreate){

        Assert.notNull(projectBO,"业务单据不能为空");
        Assert.notNull(projectBO.getProjectType(),"业务类型不能为空");

        if(isCreate){
            // projectId 同单据ID
            projectBO.setCreatedTime(DateTimeUtilz.nowSeconds());
            projectMapper.insert(projectBO);
        }else{
            DefProjectTypeBO define = defProjectTypeService.getProjectDefined(projectBO.getProjectType(), projectBO.getDefVersion());
            Assert.isTrue(define.getStatus()
                            .stream()
                            .anyMatch(defProjectStatus -> StringUtils.equals(defProjectStatus.getProjectState(), projectBO.getProjectState())),
                    "业务状态（里程碑）不合法");

            projectMapper.updateByPrimaryKeySelective(projectBO);
        }
    }

    /**
     * 获取业务详情
     * @param doc       单据信息
     * @return          业务对象
     */
    @Override
    public BizProjectBO getProject(BizProjectDoc doc) {
        BizProjectBO projectBO = getProject(doc.getRefObjectId());
        projectBO.setDefined(defProjectTypeService.getProjectDefined(projectBO.getProjectType(),projectBO.getDefVersion()));
        projectBO.getDefined().setDocs(filterDocs(projectBO.getDefined(),projectBO,doc));
        return projectBO;
    }

    @Override
    public BizProjectBO toCreateProjectDoc(BizProjectTranDoc doc){

        BizProjectBO projectBO = getProject(doc.getRefObjectId());
        DefProjectTypeBO define = defProjectTypeService.getProjectDefined(projectBO.getProjectType(),projectBO.getDefVersion());

        // 检查
        filterDocs(define,projectBO,doc.getPreDoc())
                .stream()
                .filter(defProjectDocLI -> StringUtils.equals(defProjectDocLI.getDocType(),doc.getDocType()))
                .filter(defProjectDocLI -> !defProjectDocLI.getDisabled())
                .findAny()
                .orElseThrow(()->new TfmsDefineException("不满足业务流配置"));

        projectBO.setDefined(define);
        return projectBO;
    }

    private BizProjectBO getProject(String projectId){
        return redisSupport.getIfAbsent(String.format("%s%s", Constants.CACHE_PROJECT,projectId),()->{
            BizProject project = projectMapper.selectByPrimaryKey(projectId);
            Assert.notNull(project,"业务没有找到");
            return BeanUtilz.copyFromObject(project, BizProjectBO.class);
        });
    }

    private List<DefProjectDocLI> filterDocs(DefProjectTypeBO define, BizProjectBO project, BizDocBase preDoc){
        return define.getDocs()
            .stream()
            .peek(def->{
                if(StringUtils.equals(def.getPreDocType(),Constants.BIZ_DEFAULT_EMPTY))
                    def.setPreDocType(define.getProjectType());
            })
            // 过滤前序单据不符合条件的单据类型
            .filter(def->StringUtils.equalsAny(def.getPreDocType(),preDoc.getDocType()))
            .filter(def->{
                if(StringUtils.isNotBlank(def.getRefObjectType())){
                    TfmsProjectDocInterceptor interceptor = customObjectManager.getCustomObject(def.getRefObjectType(),TfmsProjectDocInterceptor.class);
                    return interceptor.preCondition(project,preDoc,def);
                }
                return true;
            })
            // 过滤没有创建权限的单据类型
            .filter(def->permService.hasDocPerm(TfmsPermService.MODE_WRITE,def.getDocType()))
            .peek(def->{
                if(StringUtils.isNotBlank(def.getPreProjectStatus())
                        &&!ArrayUtils.contains(StringUtils.split(def.getPreProjectStatus(),','),project.getProjectState())){
                    def.setDisabled(true);
                    def.setDisabledDesc("业务里程碑不符合条件");
                }
                if(StringUtils.isNotBlank(def.getPreDocStatus())
                        &&!ArrayUtils.contains(StringUtils.split(def.getPreDocStatus(),','),preDoc.getDocState())){
                    def.setDisabled(true);
                    def.setDisabledDesc("单据状态不符合条件");
                }
            }).collect(Collectors.toList());
    }
}
