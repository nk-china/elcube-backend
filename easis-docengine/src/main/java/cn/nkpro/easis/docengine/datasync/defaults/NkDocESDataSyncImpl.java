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
package cn.nkpro.easis.docengine.datasync.defaults;

import cn.nkpro.easis.data.elasticearch.SearchEngine;
import cn.nkpro.easis.docengine.datasync.NkAbstractDocDataDiffedSyncAdapter;
import cn.nkpro.easis.docengine.gen.DocDefDataSync;
import cn.nkpro.easis.docengine.model.es.DocExtES;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Map;

@Slf4j
@Component("NkDocESDataSync")
public class NkDocESDataSyncImpl extends NkAbstractDocDataDiffedSyncAdapter<String> {

    @Autowired@SuppressWarnings("all")
    private SearchEngine searchEngine;

    @Override
    public void onInsert(Map<String,Map<String,Object>> list, DocDefDataSync def) {
        list.forEach((key,value)->
            searchEngine.updateBeforeCommit(
                    buildCustomES(
                            def,
                            key,
                            value
                    )
            )
        );
    }

    @Override
    public void onModify(Map<String,Map<String,Object>> list, DocDefDataSync def) {
        this.onInsert(list, def);
    }

    @Override
    public void onRemove(Map<String,Map<String,Object>> list, DocDefDataSync def) {
        list.forEach((key,value)->searchEngine.deleteBeforeCommit(DocExtES.class, key));
    }

    private DocExtES buildCustomES(DocDefDataSync def, String customId, Map<String,Object> value){
        Assert.isTrue(StringUtils.isNotBlank(customId),"数据同步服务["+getBeanName() + "] 主键id值不能为空");
        DocExtES customES = new JSONObject(value).toJavaObject(DocExtES.class);
        customES.setCustomType(def.getTargetArgs());
        customES.setCustomId(customId);
        return customES;
    }
}
