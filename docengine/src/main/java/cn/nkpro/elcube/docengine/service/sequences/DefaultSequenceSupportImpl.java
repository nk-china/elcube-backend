/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.docengine.service.sequences;

import cn.nkpro.elcube.docengine.EnumDocClassify;
import cn.nkpro.elcube.docengine.gen.DocH;
import cn.nkpro.elcube.docengine.gen.DocHExample;
import cn.nkpro.elcube.docengine.gen.DocHMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by bean on 2020/7/7.
 */
public class DefaultSequenceSupportImpl extends AbstractRedisSequenceSupport {

    @Autowired
    private DocHMapper docHMapper;

    protected long prev(EnumDocClassify classify, String docType){
        DocHExample example = new DocHExample();
        example.setOrderByClause("DOC_NUMBER DESC");

        if(StringUtils.isNotBlank(docType))
            example.createCriteria().andDocTypeEqualTo(docType);

        List<DocH> docHES = docHMapper.selectByExample(example, new RowBounds(0, 1));

        return docHES.stream()
                .findFirst()
                .map(DocH::getDocNumber)
                .map(s -> Long.parseLong(s.substring(3)))
                .orElse(0L);
    }

    @Override
    public String next(EnumDocClassify classify, String docType){
        return String.format("DOC%09d", increment(classify,docType,"DOC"));
    }
}
