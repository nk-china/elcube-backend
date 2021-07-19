package cn.nkpro.tfms.platform.custom;

import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.DefDocTypeBO;
import cn.nkpro.tfms.platform.basis.TfmsCustomObject;

public interface TfmsComponent extends TfmsCustomObject {

    /**
     * 是否与单据一起缓存
     * @return boolean
     */
    boolean cacheDef();

    /**
     * 组件支持的单据分类
     * @param classify classify
     * @return boolean
     */
    boolean supports(EnumDocClassify classify);

    /**
     * 获取组件名称
     * @return string
     */
    String getComponentName();



    /***
     * 获取组件描述
     * @return string
     */
    String getComponentDesc();

    /***
     * 获取配置组件名称
     * @return string[]
     */
    String[] getDefComponentNames();

    /**
     * 反序列化配置
     * @param def def
     * @return def
     */
    Object deserializeDef(Object def) throws Exception;

    /**
     * 获取配置
     * @param defDocType defDocType
     * @return def
     */
    Object getDef(DefDocTypeBO defDocType) throws Exception;

    /**
     * 更新配置
     * @param defDocType defDocType
     */
    void updateDef(DefDocTypeBO defDocType) throws Exception;
    /**
     * 更新组件
     * @param doc doc
     * @param docDef docDef
     */
    void update(BizDocBase doc, DefDocTypeBO docDef, BizDocBase original) throws Exception;

    default boolean deprecated(){
        return false;
    }
}
