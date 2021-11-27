package cn.nkpro.groovy.cards.simple

import cn.nkpro.easis.annotation.NkNote
import cn.nkpro.easis.co.NkCustomObjectManager
import cn.nkpro.easis.co.remote.NkRemoteAdapter
import cn.nkpro.easis.docengine.NkDocEngine
import cn.nkpro.easis.docengine.model.DocDefIV
import cn.nkpro.easis.docengine.model.DocHV
import cn.nkpro.easis.docengine.standard.NkStandardAbstractDefCard
import cn.nkpro.easis.docengine.standard.NkStandardDef
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@NkNote("演示标准配置卡片")
@Component("NkSimpleCardDev002")
class NkSimpleCardDev002 extends NkStandardAbstractDefCard<NkCardSimpleData,NkCardSimpleDef> {

    @Autowired
    NkDocEngine docEngine

    @Autowired
    NkCustomObjectManager customObjectManager


    @Override
    Object callDef(NkCardSimpleDef nkCardSimpleDef, Object options) {

        if(Objects.equals(options,"listedAdapters"))
            return customObjectManager.getCustomObjectDescriptionList(NkRemoteAdapter.class, true, null)

        return super.callDef(nkCardSimpleDef, options)
    }

    @Override
    NkCardSimpleData calculate(DocHV doc, NkCardSimpleData data, DocDefIV defIV, NkCardSimpleDef d, boolean isTrigger, Object options) {

        if(Objects.equals(options,"search")){

            if(StringUtils.isNotBlank(d.getRemoteAdapter())){

                Map params = null

                if(StringUtils.isNotBlank(d.getRemoteAdapterParamsSpEL())){
                    def context = spELManager.createContext(doc)
                    context.setVariable("self",data)
                    params = spELManager.invoke(d.getRemoteAdapterParamsSpEL(), context) as Map
                }

                Map ret = customObjectManager.getCustomObject(d.getRemoteAdapter(), NkRemoteAdapter.class)
                    .apply(params, Map.class) as Map

                data.setAge(ret.get("age") as Integer)
            }
        }

        return super.calculate(doc, data, defIV, d, isTrigger, options)
    }

    static class NkCardSimpleData {

        private String name
        private Integer age
        private Integer sex
        private String likes

        String getName() {
            return name
        }

        void setName(String name) {
            this.name = name
        }

        Integer getAge() {
            return age
        }

        void setAge(Integer age) {
            this.age = age
        }

        Integer getSex() {
            return sex
        }

        void setSex(Integer sex) {
            this.sex = sex
        }

        String getLikes() {
            return likes
        }

        void setLikes(String likes) {
            this.likes = likes
        }
    }


    static class NkCardSimpleDef extends NkStandardDef {

        private String remoteAdapter;

        private String remoteAdapterParamsSpEL;

        String getRemoteAdapter() {
            return remoteAdapter
        }

        void setRemoteAdapter(String remoteAdapter) {
            this.remoteAdapter = remoteAdapter
        }

        String getRemoteAdapterParamsSpEL() {
            return remoteAdapterParamsSpEL
        }

        void setRemoteAdapterParamsSpEL(String remoteAdapterParamsSpEL) {
            this.remoteAdapterParamsSpEL = remoteAdapterParamsSpEL
        }
    }
}
