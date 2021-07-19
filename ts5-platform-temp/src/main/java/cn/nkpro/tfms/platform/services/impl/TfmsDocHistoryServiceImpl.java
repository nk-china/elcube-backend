package cn.nkpro.tfms.platform.services.impl;

import cn.nkpro.tfms.platform.mappers.gen.SysLogDocRecordMapper;
import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.DefDocTypeBO;
import cn.nkpro.tfms.platform.model.po.SysLogDocRecord;
import cn.nkpro.tfms.platform.model.po.SysLogDocRecordExample;
import cn.nkpro.tfms.platform.services.TfmsDocHistoryService;
import cn.nkpro.tfms.platform.services.TfmsPermService;
import cn.nkpro.ts5.supports.GUID;
import cn.nkpro.ts5.utils.SecurityUtilz;
import cn.nkpro.ts5.utils.TextUtils;
import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.lang.Assert;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TfmsDocHistoryServiceImpl implements TfmsDocHistoryService {

    @Autowired
    private GUID guid;
    @Autowired
    private SysLogDocRecordMapper logDocRecordMapper;
    @Autowired
    private TfmsPermService permService;

    @Transactional
    @Override
    public void doAddVersion(BizDocBase doc, BizDocBase original, List<String> changedCard,String source){

        SysLogDocRecordExample example = new SysLogDocRecordExample();
        example.createCriteria().andDocIdEqualTo(doc.getDocId());

        SysLogDocRecord record = new SysLogDocRecord();
        record.setId(guid.nextId(SysLogDocRecord.class));
        record.setDocId(doc.getDocId());
        record.setState(doc.getDocState());
        record.setStateOriginal(original==null?null:original.getDocState());
        record.setUserId(SecurityUtilz.getUser().getId());
        record.setUserRealname(SecurityUtilz.getUser().getRealname());
        record.setCardNames(String.join(",", changedCard));
        record.setData(TextUtils.compress(JSONObject.toJSONString(doc)));
        record.setUpdatedTime(doc.getUpdatedTime());
        record.setVersion(logDocRecordMapper.countByExample(example)+1);
        record.setSource(source);
        logDocRecordMapper.insert(record);
    }

    @Override
    public List<SysLogDocRecord> getHistorys(String docId) {
        SysLogDocRecordExample example = new SysLogDocRecordExample();
        example.createCriteria()
                .andDocIdEqualTo(docId);
        example.setOrderByClause("UPDATED_TIME DESC");
        return logDocRecordMapper.selectByExample(example);
    }

    @Override
    public SysLogDocRecord getDetail(String id) {
        SysLogDocRecord record = logDocRecordMapper.selectByPrimaryKey(id);

        JSONObject jsonObject = JSONObject.parseObject(TextUtils.uncompress(record.getData()));

        DefDocTypeBO definedDoc = permService.filterDocCards(TfmsPermService.MODE_READ, jsonObject.getObject("definedDoc", DefDocTypeBO.class));

        jsonObject.put("definedDoc",definedDoc);

        jsonObject.getJSONObject("componentsData")
                .entrySet()
                .removeIf(entry->
                        definedDoc.getCustomComponents()
                            .stream()
                            .noneMatch(def -> StringUtils.equals(entry.getKey(),def.getComponentMapping()))
                );

        record.setData(jsonObject.toJSONString());

        Assert.notNull(record,"没有找到对应的历史记录");
        return record;
    }
}
