package cn.nkpro.ts5.docengine.datasync.defaults;

import cn.nkpro.ts5.docengine.datasync.NkAbstractDocDataDiffedSyncAdapter;
import cn.nkpro.ts5.docengine.gen.DocDefDataSync;
import cn.nkpro.ts5.docengine.model.es.CustomES;
import cn.nkpro.ts5.data.elasticearch.SearchEngine;
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
        list.forEach((key,value)->searchEngine.deleteBeforeCommit(CustomES.class, key));
    }

    private CustomES buildCustomES(DocDefDataSync def, String customId, Map<String,Object> value){
        Assert.isTrue(StringUtils.isNotBlank(customId),"数据同步服务["+getBeanName() + "] 主键id值不能为空");
        CustomES customES = new JSONObject(value).toJavaObject(CustomES.class);
        customES.setCustomType(def.getTargetArgs());
        customES.setCustomId(customId);
        return customES;
    }
}
