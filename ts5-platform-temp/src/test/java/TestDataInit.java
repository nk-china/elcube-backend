import cn.nkpro.tfms.platform.custom.EnumDocClassify;
import cn.nkpro.tfms.platform.mappers.gen.BizDocMapper;
import cn.nkpro.tfms.platform.mappers.gen.BizPartnerMapper;
import cn.nkpro.tfms.platform.mappers.gen.BizProjectMapper;
import cn.nkpro.tfms.platform.model.po.BizPartner;
import cn.nkpro.tfms.platform.model.index.IndexDoc;
import cn.nkpro.tfms.platform.model.index.IndexPartner;
import cn.nkpro.tfms.platform.model.po.BizDoc;
import cn.nkpro.ts5.utils.BeanUtilz;
import cn.nkpro.ts5.supports.SequenceSupport;
import cn.nkpro.tfms.platform.elasticearch.SearchEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * Created by bean on 2020/6/19.
 */
@Component
public class TestDataInit implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private SearchEngine searchEngine;

    @Autowired
    private BizProjectMapper projectMapper;

    @Autowired
    private BizDocMapper docMapper;

    @Autowired
    private BizPartnerMapper partnerMapper;

    @Autowired
    private SequenceSupport sequenceUtils;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {

            //redisTemplate.delete(Constants.CACHE_NAV_MENUS);
            //initPartner();

            //initDocument();

            //initProject();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initDocument() throws IOException{

        searchEngine.deleteIndices(IndexDoc.class);
        searchEngine.createIndices(IndexDoc.class);

        IndexDoc document = new IndexDoc();

        for (int i=0;i<12;i++){

            document.setDocId(UUID.randomUUID().toString());
            document.setDocNumber(sequenceUtils.next(EnumDocClassify.TRANSACTION,"ZR01"));
            document.setDocState("NEW");
            document.setClassify(EnumDocClassify.TRANSACTION.name());
            document.setDocDefVersion(1);
            document.setDocDesc("谢炳盛的融资租赁报价单");
            document.setDocName("融资租赁报价单");
            document.setDocType("ZR01");
            document.setCreatedTime(new Date().getTime()/1000);
            document.setUpdatedTime(new Date().getTime()/1000);
            document.setRefObjectId("a6d6e04d-ed48-426a-9556-1d8d27bffea5");
            document.setTags(new String[]{"TEST","SQ"});

            searchEngine.indexAfterCommit(document);

            BizDoc bizDoc = BeanUtilz.copyFromObject(document, BizDoc.class);
            bizDoc.setDocTags("TEST,SQ");
            docMapper.insert(bizDoc);
        };
    }

    private void initPartner() throws IOException {


        searchEngine.deleteIndices(IndexPartner.class);
        searchEngine.createIndices(IndexPartner.class);

        IndexPartner partnerIndex = new IndexPartner();

        for (int i=0;i<3;i++){

            partnerIndex.setPartnerId(UUID.randomUUID().toString());
            partnerIndex.setPartnerName("客户"+i);
            partnerIndex.setPartnerType(Math.random()>0.5?"LEGAL":"NATURAL");

            if(Math.random()>0.8){
                partnerIndex.setRoles(new String[]{"新能源客户","轻卡承租人"});
            }else if(Math.random()>0.6){
                partnerIndex.setRoles(new String[]{"轻卡承租人"});
            }else if(Math.random()>0.2){
                partnerIndex.setRoles(new String[]{"融资方"});
            }else{
                partnerIndex.setRoles(new String[]{"保险公司"});
            }

            if(Math.random()>0.7){
                partnerIndex.setTags(new String[]{"RZZL"});
            }else if(Math.random()>0.3){
                partnerIndex.setTags(new String[]{"LCV1"});
            }else{
                partnerIndex.setTags(new String[]{"LCV2"});
            }

            partnerIndex.setRemark("备注"+i);
            partnerIndex.setCreatedTime(new Date().getTime()/1000);
            partnerIndex.setUpdatedTime(new Date().getTime()/1000);

            searchEngine.indexAfterCommit(partnerIndex);

            partnerMapper.insert(BeanUtilz.copyFromObject(partnerIndex, BizPartner.class));
        }
    }

}
