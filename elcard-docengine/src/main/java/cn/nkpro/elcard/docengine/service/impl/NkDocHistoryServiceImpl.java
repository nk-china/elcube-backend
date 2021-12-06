/*
 * This file is part of ELCard.
 *
 * ELCard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcard.docengine.service.impl;

import cn.nkpro.elcard.basic.GUID;
import cn.nkpro.elcard.docengine.gen.DocRecord;
import cn.nkpro.elcard.docengine.gen.DocRecordExample;
import cn.nkpro.elcard.docengine.gen.DocRecordMapper;
import cn.nkpro.elcard.docengine.model.DocDefHV;
import cn.nkpro.elcard.docengine.model.DocHHistory;
import cn.nkpro.elcard.docengine.model.DocHV;
import cn.nkpro.elcard.docengine.service.NkDocHistoryService;
import cn.nkpro.elcard.security.SecurityUtilz;
import cn.nkpro.elcard.utils.BeanUtilz;
import cn.nkpro.elcard.utils.TextUtils;
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
