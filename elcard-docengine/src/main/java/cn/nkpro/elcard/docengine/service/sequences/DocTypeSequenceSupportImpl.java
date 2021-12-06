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
package cn.nkpro.elcard.docengine.service.sequences;

import cn.nkpro.elcard.docengine.EnumDocClassify;
import cn.nkpro.elcard.docengine.gen.DocH;
import cn.nkpro.elcard.docengine.gen.DocHExample;
import cn.nkpro.elcard.docengine.gen.DocHMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by bean on 2020/7/7.
 */
public class DocTypeSequenceSupportImpl extends AbstractRedisSequenceSupport {

    @Autowired
    private DocHMapper docHMapper;

    @Override
    protected long prev(EnumDocClassify classify, String docType){
        DocHExample example = new DocHExample();

        if(StringUtils.isNotBlank(docType))
            example.createCriteria().andDocTypeEqualTo(docType);

        example.setOrderByClause("DOC_NUMBER DESC");

        List<DocH> docHES = docHMapper.selectByExample(example, new RowBounds(0, 1));

        return docHES.stream()
                .findFirst()
                .map(DocH::getDocNumber)
                .map(s -> Long.parseLong(s.substring(4)))
                .orElse(0L);
    }

    @Override
    public String next(EnumDocClassify classify, String docType){
        return String.format("%s%08d",docType, increment(classify, docType, docType));
    }
}
