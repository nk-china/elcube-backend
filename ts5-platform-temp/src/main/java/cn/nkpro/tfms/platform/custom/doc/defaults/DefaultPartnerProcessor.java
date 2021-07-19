package cn.nkpro.tfms.platform.custom.doc.defaults;

import cn.nkpro.tfms.platform.basis.Constants;
import cn.nkpro.tfms.platform.custom.EnumDocClassify;
import cn.nkpro.tfms.platform.custom.doc.AbstractDocProcessor;
import cn.nkpro.tfms.platform.mappers.gen.BizPartnerMapper;
import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.BizPartnerBO;
import cn.nkpro.tfms.platform.model.DefDocTypeBO;
import cn.nkpro.tfms.platform.model.index.IndexDoc;
import cn.nkpro.tfms.platform.model.index.IndexPartner;
import cn.nkpro.tfms.platform.model.po.BizDoc;
import cn.nkpro.tfms.platform.model.po.BizPartner;
import cn.nkpro.tfms.platform.services.TfmsDefPartnerRoleService;
import cn.nkpro.ts5.supports.RedisSupport;
import cn.nkpro.ts5.utils.BeanUtilz;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component("nkDefaultPartnerProcessor")
public class DefaultPartnerProcessor extends AbstractDocProcessor {


    @Autowired
    private RedisSupport<BizPartnerBO> redisSupport;
    @Autowired
    private TfmsDefPartnerRoleService defPartnerRoleService;

    @Autowired
    private BizPartnerMapper partnerMapper;

    private static final Map<String,Object> defaultOptions = new HashMap<>();
    static {
        defaultOptions.put("partnerType",JSON.parseArray("[{label:'自然人',value:'NATURAL'},{label:'法人',value:'LEGAL'},{label:'组织机构',value:'ORG'}]"));
    }

    @Override
    public String getDocHeaderComponentName() {
        return "nk-page-partner-header-default";
    }

    @Override
    public EnumDocClassify classify() {
        return EnumDocClassify.PARTNER;
    }
    @Override
    public Class<? extends BizDocBase> dataType() {
        return BizPartnerRoleDoc.class;
    }

    @Override
    public String[] getDocDefComponentNames() {
        return new String[]{"nk-doc-partner-default-def"};
    }

    @Override
    public Map<String, Object> getDocProcessDef(DefDocTypeBO def) {

        Map<String,Object> defaults = new HashMap<>(defaultOptions);

        if(StringUtils.isNotBlank(def.getRefObjectTypeOptions())) {
            try {
                defaults.putAll(JSON.parseObject(def.getRefObjectTypeOptions()));
            }catch (Exception e){
                def.setRefObjectTypeOptions(def.getRefObjectTypeOptions()+"\n//解析配置发生错误，请检查"+e.getMessage());
            }
        }
        return defaults;
    }

    @Override
    public BizDocBase detailAfterComponent(BizDocBase docBase) {

        BizPartnerRoleDoc partnerDoc = (BizPartnerRoleDoc) super.detailAfterComponent(docBase);

        BizPartnerBO partner = getPartnerBase(partnerDoc.getRefObjectId());
        partner.setDefRoles(defPartnerRoleService.getAllRoles(true));

        partnerDoc.setDefinedRefObject(defPartnerRoleService.getPartnerRoleRuntimeDefined(partnerDoc,null));
        partnerDoc.setRefObject(partner);
        partnerDoc.setPartnerName(partner.getPartnerName());

        return partnerDoc;
    }

    @Override
    public BizDocBase createBeforeComponent(BizDocBase doc) {

        // 初始化交易伙伴对象
        BizPartnerBO bizPartnerBO;

        if(!Objects.equals(doc.getRefObjectId(), Constants.BIZ_DEFAULT_EMPTY)&&StringUtils.isNotBlank(doc.getRefObjectId())){
            bizPartnerBO = getPartnerBase(doc.getRefObjectId());
        }else{
            bizPartnerBO = new BizPartnerBO();
            bizPartnerBO.setPartnerId(guid.nextId(BizPartner.class));
            bizPartnerBO.setRoles(new ArrayList<>());
            bizPartnerBO.setCreatedTime(DateTimeUtilz.nowSeconds());
            doc.setRefObjectId(bizPartnerBO.getPartnerId());
        }

        bizPartnerBO.setUpdatedTime(DateTimeUtilz.nowSeconds());
        bizPartnerBO.setDefRoles(defPartnerRoleService.getAllRoles(true));// 设置所有已定义的角色列表

        BizPartnerRoleDoc partnerDoc = (BizPartnerRoleDoc) super.createBeforeComponent(doc);
        partnerDoc.setRefObject(bizPartnerBO);

        // 将当前新创建的角色加入到角色列表
        bizPartnerBO.getRoles().add(BeanUtilz.copyFromObject(partnerDoc, BizDocBase.class));
        return partnerDoc;
    }

    @Override
    public BizDocBase updateAfterComponent(BizDocBase doc, boolean isCreate) {

        BizPartnerRoleDoc partnerDoc = (BizPartnerRoleDoc) doc;

        // 获取交易伙伴信息，如果交易伙伴为新创建，则插入数据库
        BizPartnerBO partner = partnerDoc.getRefObject();
        Assert.notNull(partner,"交易伙伴对象不能为空");
        if(partnerMapper.selectByPrimaryKey(partner.getPartnerId())==null){
            partnerMapper.insertSelective(partner);
        }

        // 交易伙伴单据的默认交易伙伴为自己
        partnerDoc.setPartnerId(partnerDoc.getDocId());
        partnerDoc.setPartnerName(partner.getPartnerName());
        // 设置单据名称为partner名称 并 更新单据

        partner.setUpdatedTime(DateTimeUtilz.nowSeconds());

        // 更新交易伙伴主数据
        BizPartner record = new BizPartner();
        record.setPartnerId(partner.getPartnerId());
        record.setUpdatedTime(partner.getUpdatedTime());
        partnerMapper.updateByPrimaryKeySelective(partner);

        // 清除缓存
        redisSupport.delete(String.format(Constants.CACHE_PARTNER,partner.getPartnerId()));

        return partnerDoc;
    }

    @Override
    public IndexDoc buildIndex(BizDocBase docBO, IndexDoc indexDoc) {
        super.buildIndex(docBO,indexDoc);

        BizPartnerRoleDoc partnerDoc = (BizPartnerRoleDoc) docBO;
        IndexPartner partnerIndex = BeanUtilz.copyFromObject(partnerDoc.getRefObject(), IndexPartner.class);

        partnerIndex.setRoleIds(
                partnerDoc.getRefObject().getRoles()
                        .stream()
                        .map(BizDoc::getDocId)
                        .toArray(String[]::new));
        partnerIndex.setRoles(
                partnerDoc.getRefObject().getRoles()
                        .stream()
                        .map(BizDoc::getDocType)
                        .toArray(String[]::new)
        );
        partnerIndex.setTags(StringUtils.split(partnerDoc.getRefObject().getPartnerTags(),','));

        searchEngine.indexAfterCommit(partnerIndex);

        return indexDoc;
    }

    private BizPartnerBO getPartnerBase(String partnerId) {
        return redisSupport.getIfAbsent(String.format(Constants.CACHE_PARTNER,partnerId),()->{
            BizPartnerBO partner =  BeanUtilz.copyFromObject(partnerMapper.selectByPrimaryKey(partnerId), BizPartnerBO.class);

            Assert.notNull(partner,"交易伙伴角色没有找到");

            // 所有已创建的角色
            partner.setRoles(docService.getListByRefObject(partnerId,Constants.BIZ_DEFAULT_EMPTY, null));
            return partner;
        });
    }
}
