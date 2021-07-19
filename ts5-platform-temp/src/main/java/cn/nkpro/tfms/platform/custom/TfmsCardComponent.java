package cn.nkpro.tfms.platform.custom;

import cn.nkpro.tfms.platform.model.DefDocTypeBO;
import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.index.IndexDoc;
import cn.nkpro.ts5.config.security.TfmsDefaultPermissions;
import cn.nkpro.ts5.config.security.TfmsPermissions;

/**
 * Created by bean on 2020/6/10.
 */
public interface TfmsCardComponent extends TfmsComponent {

    int order();

    /**
     * 是否与单据一起缓存
     * @return boolean
     */
    boolean cached();

    /**
     * 获取数据组件名称
     * @return string
     */
    String getDataComponentName();

    /**
     * 获取组件的扩展组件名称
     * @return string[]
     */
    String[] getDataComponentExtNames();

    /**
     * 获取组件页面名称
     * @return string[]
     */
    String[] getPageComponentNames();

    /**
     * 反序列化数据
     * @param data data
     * @return data
     */
    Object deserialize(Object data) throws Exception;

    /**
     * 获取组件数据
     * @param doc doc
     * @return data
     */
    Object getData(BizDocBase doc) throws Exception;

    void processData(BizDocBase doc) throws Exception;

    void afterGetData(BizDocBase doc, DefDocTypeBO docDef) throws Exception;
    /**
     * 预创建组件数据
     * @param doc doc
     * @param preDoc preDoc
     * @param docDef docDef
     * @return data
     */
    Object create(BizDocBase doc, BizDocBase preDoc, DefDocTypeBO docDef) throws Exception;

    void calculate(String calculate, BizDocBase doc, DefDocTypeBO docDef) throws Exception;

    TfmsCardIndexItemExec indexItem(BizDocBase doc, DefDocTypeBO docDef, IndexDoc indexDoc) throws Exception;

    /**
     * 组件的权限定义
     * @return perms
     */
    @Deprecated
    @SuppressWarnings("unused")
    default TfmsPermissions[] permissions(){
        return TfmsDefaultPermissions.values();
    }

    Object call(String event, BizDocBase doc, DefDocTypeBO definedDoc) throws Exception;

}
