package cn.nkpro.easis.components.defaults.customized

import cn.nkpro.easis.annotation.NkNote
import cn.nkpro.easis.co.easy.EasySingle
import cn.nkpro.easis.data.jdbc.NkJdbcTemplate
import cn.nkpro.easis.docengine.NkField
import cn.nkpro.easis.docengine.cards.NkDynamicBase
import cn.nkpro.easis.docengine.cards.NkDynamicGridDef
import cn.nkpro.easis.docengine.cards.NkDynamicGridDefI
import cn.nkpro.easis.docengine.cards.NkDynamicGridField
import cn.nkpro.easis.docengine.model.DocDefIV
import cn.nkpro.easis.docengine.model.DocHV
import cn.nkpro.easis.docengine.utils.CopyUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.util.stream.Collectors

@NkNote("中信建投定制表格")
@Component("CiticCardGrid")
class CiticCardGrid extends NkDynamicBase<List<Map>, CiticGridDef> {

    @Autowired
    NkJdbcTemplate jdbcTemplate

    @Override
    List<Map> afterCreate(DocHV doc, DocHV preDoc, List<Map> data, DocDefIV defIV, CiticGridDef d) {

        if(preDoc != null && defIV.getCopyFromRef()!=null&&defIV.getCopyFromRef()==1){
            CopyUtils.copy(
                    preDoc.getData().get(defIV.getCardKey()),
                    data,
                    HashMap.class,
                    d.getItems()
                        .stream()
                        .map{i->i.getKey()}
                        .collect(Collectors.toList())
            )
        }

        this.processOptions(EasySingle.from(Collections.emptyMap()), doc, d.getItems())
        return super.afterCreate(doc, preDoc, data, defIV, d) as List
    }

    @Override
    List<Map> afterGetData(DocHV doc, List<Map> data, DocDefIV defIV, CiticGridDef d) {

        def upsetFields = d.getItems()
                .stream()
                .filter{ i-> !d.getKeys().contains(i) }
                .map { i -> i.getKey() }
                .collect(Collectors.toList())
        data = jdbcTemplate.queryList(d.getTable(), upsetFields, spELManager.invoke(d.getWhereMapping(),doc) as Map, d.getOrder()) as List<Map>

        this.processOptions(EasySingle.from(Collections.emptyMap()), doc, d.getItems())

        return super.afterGetData(doc, data, defIV, d) as List
    }

    @Override
    List<Map> beforeUpdate(DocHV doc, List<Map> data, List<Map> original, DocDefIV defIV, CiticGridDef d) {

        def upsetFields = d.getItems()
                .stream()
                .filter{ i-> !d.getKeys().contains(i) }
                .map { i -> i.getKey() }
                .collect(Collectors.toList())

        jdbcTemplate.batchUpsert(d.getTable(), upsetFields, d.getKeys(), spELManager.invoke(d.getWhereMapping(),doc) as Map, data)

        // 返回null，数据就不通过DocEngine保存
        return null
    }

    @Override
    List<Map> calculate(DocHV doc, List<Map> data, DocDefIV defIV, CiticGridDef d, boolean isTrigger, Object options) {
        this.execSpEL(data, doc, d.getItems(), defIV.getCardKey(), isTrigger, (Map) options)
        return super.calculate(doc, data, defIV, d, isTrigger, options) as List
    }

    private void execSpEL(List<Map> data, DocHV doc, List<NkDynamicGridDefI> fields, String cardKey, boolean isTrigger, Map options){
        if(data.isEmpty())
            execSpEL(EasySingle.from(new HashMap()), doc, fields,cardKey, isTrigger, options)
        else
            data.forEach({ item -> execSpEL(EasySingle.from(item), doc, fields, cardKey, isTrigger, options) })
    }

    @Override
    Object callDef(CiticGridDef d, Object options) {
        return customObjectManager.getCustomObjectDescriptionList(NkField.class, false, { entry -> entry.getValue() instanceof NkDynamicGridField })
    }

    static class CiticGridDef extends NkDynamicGridDef{
        String table
        List<String> keys
        String whereMapping
        String order
    }
}
