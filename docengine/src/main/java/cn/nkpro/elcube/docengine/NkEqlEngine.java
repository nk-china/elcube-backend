package cn.nkpro.elcube.docengine;

import cn.nkpro.elcube.docengine.gen.DocH;
import cn.nkpro.elcube.docengine.model.DocHQL;
import cn.nkpro.elcube.docengine.model.DocHV;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NkEqlEngine {

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    List<? extends DocH> executeEql(String eql);

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    List<DocHQL> findByEql(String eql);

    List<DocHV> execUpdateEql(String eql);

    @Transactional
    List<DocHV> doUpdateByEql(String eql, String optSource);
}
