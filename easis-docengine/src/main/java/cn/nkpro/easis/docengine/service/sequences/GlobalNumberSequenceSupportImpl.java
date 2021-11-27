package cn.nkpro.easis.docengine.service.sequences;

import cn.nkpro.easis.docengine.EnumDocClassify;
import cn.nkpro.easis.docengine.gen.DocH;
import cn.nkpro.easis.docengine.gen.DocHExample;
import cn.nkpro.easis.docengine.gen.DocHMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by bean on 2020/7/7.
 */
public class GlobalNumberSequenceSupportImpl extends AbstractRedisSequenceSupport {

    @Autowired
    private DocHMapper docHMapper;

    @Override
    protected long prev(EnumDocClassify classify, String docType){
        DocHExample example = new DocHExample();
        example.setOrderByClause("DOC_NUMBER DESC");

        if(StringUtils.isNotBlank(docType))
            example.createCriteria().andDocTypeEqualTo(docType);

        List<DocH> docHES = docHMapper.selectByExample(example, new RowBounds(0, 1));

        return docHES.stream()
                .findFirst()
                .map(DocH::getDocNumber)
                .map(Long::parseLong)
                .orElse(100000000L);
    }

    @Override
    public String next(EnumDocClassify classify, String docType){
        return String.valueOf(increment(classify, docType,"GlobalNumber"));
    }
}
