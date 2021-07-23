package cn.nkpro.ts5.engine.doc;

import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.model.mb.gen.*;
import cn.nkpro.ts5.utils.BeanUtilz;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;

public class NKDocDefManager {

    @Autowired
    private DocDefHMapper docDefHMapper;
    @Autowired
    private DocDefIMapper docDefIMapper;

    public DocDefHV getDef(String docType, String version){

        DocDefHKey key = new DocDefHKey();
        key.setDocType(docType);
        key.setVersion(version);

        DocDefHV def = BeanUtilz.copyFromObject(docDefHMapper.selectByPrimaryKey(key), DocDefHV.class);

        DocDefIExample example = new DocDefIExample();
        example.createCriteria()
                .andDocTypeEqualTo(docType)
                .andVersionEqualTo(version);

//        def.setItems(
//                docDefIMapper.selectByExampleWithBLOBs(example)
//                        .stream()
//                        .collect(Collectors.toList())
//        );

        return def;
    }
}
