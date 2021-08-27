package cn.nkpro.ts5.engine.doc.model;

import cn.nkpro.ts5.engine.task.model.BpmTask;
import cn.nkpro.ts5.orm.mb.gen.DocI;
import cn.nkpro.ts5.utils.BeanUtilz;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 单据数据对象，与前端交互的数据格式
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude()
public class DocHV extends DocHBasis implements Cloneable {

    private BpmTask bpmTask;

    // 单据是否允许编辑，默认真
    private Boolean writeable = true;

    private boolean newCreate = false;

    public DocHV() {
        super();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        DocHV clone = (DocHV) super.clone();
        clone.setWriteable(writeable);
        if(bpmTask!=null)
            clone.setBpmTask((BpmTask) bpmTask.clone());

        return clone;
    }

    public DocHPersistent toPersistent(){
        DocHPersistent docHPersistent = BeanUtilz.copyFromObject(this, DocHPersistent.class);
        docHPersistent.setDynamics(getDynamics());
        docHPersistent.setItems(getItems().entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e->BeanUtilz.copyFromObject(e.getValue(), DocI.class)
                )));
        return docHPersistent;
    }

    public void clearItemContent(){
        getItems().forEach((k,v)->v.setCardContent(null));
    }
}
