package cn.nkpro.groovy.customized

import cn.nkpro.ts5.annotation.NkNote
import cn.nkpro.ts5.data.jdbc.NkJdbcTemplate
import cn.nkpro.ts5.docengine.NkField
import cn.nkpro.ts5.docengine.cards.NkDynamicBase
import cn.nkpro.ts5.docengine.cards.NkDynamicFormDef
import cn.nkpro.ts5.docengine.cards.NkDynamicFormField
import cn.nkpro.ts5.docengine.model.DocDefIV
import cn.nkpro.ts5.docengine.model.DocHV
import cn.nkpro.ts5.co.easy.EasySingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.util.stream.Collectors

@NkNote("中信建投定制表单")
@Component("CiticCardForm")
class CiticCardForm extends NkDynamicBase<Map<String,Object>, CiticFormDef> {

    @Autowired
    NkJdbcTemplate jdbcTemplate

    @Override
    Map<String,Object> afterCreate(DocHV doc, DocHV preDoc, Map<String,Object> data, DocDefIV defIV, CiticFormDef d) {
        this.copyFromPre(preDoc, data, defIV, d.getItems())
        this.processOptions(EasySingle.from(data), doc, d.getItems())
        return super.afterCreate(doc, preDoc, data, defIV, d) as Map
    }

    @Override
    Map<String,Object> afterGetData(DocHV doc, Map<String,Object> data, DocDefIV defIV, CiticFormDef d) {

        def upsetFields = d.getItems()
                .stream()
                .filter{ i-> !d.getKeys().contains(i) }
                .map { i -> i.getKey() }
                .collect(Collectors.toList())

        data = jdbcTemplate.querySingle(d.getTable(), upsetFields, spELManager.invoke(d.getKeysMapping(),doc) as Map)

        this.processOptions(EasySingle.from(data), doc, d.getItems())

        return data
    }

    @Override
    Map<String, Object> beforeUpdate(DocHV doc, Map<String, Object> data, Map<String, Object> original, DocDefIV defIV, CiticFormDef d) {

        def upsetFields = d.getItems()
                .stream()
                .filter{ i-> !d.getKeys().contains(i) }
                .map { i -> i.getKey() }
                .collect(Collectors.toList())

        jdbcTemplate.upsert(d.getTable(), upsetFields, d.getKeys(), data)

        // 返回null，这样数据就不会通过DocEngine保存了
        return null
    }

    @Override
    Map<String,Object> calculate(DocHV doc, Map<String,Object> data, DocDefIV defIV, CiticFormDef d, boolean isTrigger, Object options) {
        this.execSpEL(EasySingle.from(data), doc, d.getItems(), defIV.getCardKey(), isTrigger, (Map) options)
        return super.calculate(doc, data, defIV, d, isTrigger, options) as Map
    }

    @Override
    Object callDef(CiticFormDef d, Object options) {
        return customObjectManager.getCustomObjectDescriptionList(NkField.class, false, { entry -> entry.getValue() instanceof NkDynamicFormField })
    }

    static class CiticFormDef extends NkDynamicFormDef{
        String table
        List<String> keys
        String keysMapping
    }
}