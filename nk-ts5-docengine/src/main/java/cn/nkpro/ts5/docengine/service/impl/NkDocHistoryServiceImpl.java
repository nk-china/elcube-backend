package cn.nkpro.ts5.docengine.service.impl;

import cn.nkpro.ts5.basic.GUID;
import cn.nkpro.ts5.docengine.gen.DocRecord;
import cn.nkpro.ts5.docengine.gen.DocRecordExample;
import cn.nkpro.ts5.docengine.gen.DocRecordMapper;
import cn.nkpro.ts5.docengine.model.DocDefHV;
import cn.nkpro.ts5.docengine.model.DocHHistory;
import cn.nkpro.ts5.docengine.model.DocHV;
import cn.nkpro.ts5.docengine.service.NkDocHistoryService;
import cn.nkpro.ts5.security.SecurityUtilz;
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

    @Autowired@SuppressWarnings("all")
    private GUID guid;
    @Autowired@SuppressWarnings("all")
    private DocRecordMapper logDocRecordMapper;

    @Transactional
    @Override
    public void doAddVersion(DocHV doc, DocHV original, List<String> changedCard, String source){

        DocRecordExample example = new DocRecordExample();
        example.createCriteria().andDocIdEqualTo(doc.getDocId());

        DocRecord record = new DocRecord();
        record.setId(guid.nextId(DocRecord.class));
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
    public List<DocRecord> getHistories(String docId, int offset) {
        DocRecordExample example = new DocRecordExample();
        example.createCriteria().andDocIdEqualTo(docId);
        example.setOrderByClause("UPDATED_TIME DESC");

        return logDocRecordMapper.selectByExample(example,new RowBounds(offset,6));
    }

    @Override
    public DocHHistory getDetail(String historyId) {
        DocRecord record = logDocRecordMapper.selectByPrimaryKey(historyId);
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
