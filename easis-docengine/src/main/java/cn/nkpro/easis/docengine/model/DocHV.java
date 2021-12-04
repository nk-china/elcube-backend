/*
 * This file is part of EAsis.
 *
 * EAsis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EAsis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with EAsis.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.easis.docengine.model;

import cn.nkpro.easis.co.easy.EasyCollection;
import cn.nkpro.easis.co.easy.EasySingle;
import cn.nkpro.easis.docengine.gen.DocI;
import cn.nkpro.easis.task.model.BpmTask;
import cn.nkpro.easis.utils.BeanUtilz;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 单据数据对象，与前端交互的数据格式
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude()
public class DocHV extends DocHBasis {

    private BpmTask bpmTask;

    // 单据是否允许编辑，默认真
    private Boolean writeable = true;

    private boolean newCreate = false;

    private String runtimeKey = null;

    public DocHV() {
        super();
    }

//    @Override
//    public Object clone() throws CloneNotSupportedException {
//        DocHV clone = (DocHV) super.clone();
//        clone.setWriteable(writeable);
//        if(bpmTask!=null)
//            clone.setBpmTask((BpmTask) bpmTask.clone());
//
//        return clone;
//    }

    public EasySingle fetch(String cardKey){
        Object o = getData().get(cardKey);
        Assert.notNull(o,String.format("卡片数据[ %s ]不存在",cardKey));
        return EasySingle.from(o);
    }

    public EasyCollection fetchList(String cardKey){
        Object o = getData().get(cardKey);
        Assert.notNull(o,String.format("卡片数据[ %s ]不存在",cardKey));

        DocDefIV docDefIV = getDef().getCards()
                .stream()
                .filter(c -> StringUtils.equals(c.getCardKey(), cardKey)).findFirst()
                .orElse(null);
        Assert.notNull(docDefIV,String.format("卡片配置[ %s ]不存在",cardKey));

        return EasyCollection.from(o);
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
