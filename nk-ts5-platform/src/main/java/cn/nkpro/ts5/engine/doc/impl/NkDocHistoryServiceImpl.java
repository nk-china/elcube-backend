package cn.nkpro.ts5.engine.doc.impl;

import cn.nkpro.ts5.config.id.GUID;
import cn.nkpro.ts5.config.security.SecurityUtilz;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.engine.doc.model.DocHHistory;
import cn.nkpro.ts5.engine.doc.service.NkDocHistoryService;
import cn.nkpro.ts5.orm.mb.gen.SysLogDocRecord;
import cn.nkpro.ts5.orm.mb.gen.SysLogDocRecordExample;
import cn.nkpro.ts5.orm.mb.gen.SysLogDocRecordMapper;
import cn.nkpro.ts5.utils.BeanUtilz;
import cn.nkpro.ts5.utils.TextUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.lang.Assert;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NkDocHistoryServiceImpl implements NkDocHistoryService {

    @Autowired
    private GUID guid;
    @Autowired
    private SysLogDocRecordMapper logDocRecordMapper;

    @Transactional
    @Override
    public void doAddVersion(DocHV doc, DocHV original, List<String> changedCard, String source){

        SysLogDocRecordExample example = new SysLogDocRecordExample();
        example.createCriteria().andDocIdEqualTo(doc.getDocId());

        SysLogDocRecord record = new SysLogDocRecord();
        record.setId(guid.nextId(SysLogDocRecord.class));
        record.setDocId(doc.getDocId());
        record.setState(doc.getDocState());
        record.setStateOriginal(original==null?null:original.getDocState());
        record.setUserId(SecurityUtilz.getUser().getId());
        record.setUserRealname(SecurityUtilz.getUser().getRealname());
        record.setCardNames(JSONObject.toJSONString(changedCard));
        record.setData(TextUtils.compress(JSONObject.toJSONString(BeanUtilz.copyFromObject(doc, DocHHistory.class))));
        record.setUpdatedTime(doc.getUpdatedTime());
        record.setVersion(logDocRecordMapper.countByExample(example)+1);
        record.setSource(source);
        logDocRecordMapper.insert(record);
    }

    @Override
    public List<SysLogDocRecord> getHistories(String docId, int offset) {
        SysLogDocRecordExample example = new SysLogDocRecordExample();
        example.createCriteria().andDocIdEqualTo(docId);
        example.setOrderByClause("UPDATED_TIME DESC");

        return logDocRecordMapper.selectByExample(example,new RowBounds(offset,11));
    }

    @Override
    public DocHHistory getDetail(String historyId) {
        SysLogDocRecord record = logDocRecordMapper.selectByPrimaryKey(historyId);
        Assert.notNull(record,"没有找到对应的历史记录");

        DocHHistory doc = JSONObject.parseObject(TextUtils.uncompress(record.getData()), DocHHistory.class);

        DocDefHV docDef = doc.getDef();

        doc.getItems()
            .entrySet()
            .removeIf(entry->
                docDef.getCards().stream()
                    .noneMatch(def -> StringUtils.equals(entry.getKey(),def.getCardKey()))
            );

        doc.setHistoryChangedCards(JSON.parseArray(record.getCardNames()));
        doc.setHistoryVersion(record.getVersion());
        doc.setHistoryUserId(record.getUserId());
        doc.setHistoryUserRealName(record.getUserRealname());
        doc.setHistoryCreateTime(record.getUpdatedTime());

        return doc;
    }
}
