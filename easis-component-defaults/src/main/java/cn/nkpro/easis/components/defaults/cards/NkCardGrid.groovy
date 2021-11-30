package cn.nkpro.easis.components.defaults.cards

import cn.nkpro.easis.annotation.NkNote
import cn.nkpro.easis.docengine.NkAbstractCard
import cn.nkpro.easis.docengine.model.DocDefIV
import cn.nkpro.easis.docengine.model.DocHV
import cn.nkpro.easis.docengine.model.NkCardFormDefI
import cn.nkpro.easis.docengine.utils.NkFormCardHelper
import com.apifan.common.random.source.NumberSource
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Deprecated
@NkNote("基础表格")
@Component("NkCardGrid")
class NkCardGrid extends NkAbstractCard<List<Map<String,Object>>,NkCardGridDef> {

    @Autowired
    private NkFormCardHelper nkFormCardHelper

    private List<Map<String,Object>> execSpEL(DocHV doc, List<Map<String,Object>> data, DocDefIV defIV, NkCardGridDef d){
        nkFormCardHelper.execSpEL(doc, null, defIV, d.getItems(), true, true)
        data.forEach({ item ->
            nkFormCardHelper.execSpEL(doc, item, defIV, d.getItems(), true, false)
        })
        return data
    }

    @Override
    List<Map<String,Object>> afterCreate(DocHV doc, DocHV preDoc, List<Map<String,Object>> data, DocDefIV defIV, NkCardGridDef d) {
        return execSpEL(doc, data, defIV, d)
    }

    @Override
    List<Map<String,Object>> afterGetData(DocHV doc, List<Map<String,Object>> data, DocDefIV defIV, NkCardGridDef d) {
        return execSpEL(doc, data, defIV, d)
    }

    @Override
    List<Map<String,Object>> calculate(DocHV doc, List<Map<String,Object>> data, DocDefIV defIV, NkCardGridDef d, boolean isTrigger, Object options) {
        return execSpEL(doc, data, defIV, d)
    }



    @Override
    List<Map<String,Object>> beforeUpdate(DocHV doc, List<Map<String,Object>> data, List<Map<String,Object>> original, DocDefIV defIV, NkCardGridDef nkCardGridDef) {
        data.forEach({ item ->
            item.remove("_XID")
            nkCardGridDef.items
                .forEach({ defItem ->

                    if(!item.containsKey(defItem.key)){
                        item.put(defItem.key, null)
                    }else{
                        Object value = item.get(defItem.key)
                        if(value instanceof String){
                            if(StringUtils.equalsIgnoreCase(defItem.getInputType(),"integer")){
                                if(item.containsKey(defItem.key)){
                                    item.put(defItem.key,StringUtils.isNotBlank(value as CharSequence)?Integer.parseInt(value.toString()):null)
                                }
                            }else
                            if(StringUtils.equalsAnyIgnoreCase(defItem.getInputType(),"decimal","percent")){
                                if(item.containsKey(defItem.key)){
                                    item.put(defItem.key,StringUtils.isNotBlank(value as CharSequence)?Float.parseFloat(value.toString()):null)
                                }
                            }
                        }
                    }
                })
        })
        return data
    }

    @Override
    List<Map<String, Object>> random(DocHV docHV, DocDefIV defIV, NkCardGridDef d) {
        int size = NumberSource.getInstance().randomInt(1,11)
        List list = new ArrayList()
        for(int i=0;i<size;i++){
            list.add(nkFormCardHelper.random(d.getItems()))
        }
        return list
    }

    @SuppressWarnings("unused")
    @JsonIgnoreProperties(ignoreUnknown=true)
    static class NkCardGridDef {

        private String preSpEL

        private List<NkCardFormDefI> items = new ArrayList<>()

        List<NkCardFormDefI> getItems() {
            return items
        }

        void setItems(List<NkCardFormDefI> items) {
            this.items = items
            if(this.items == null){
                this.items = new ArrayList<>()
            }
        }
    }
}
