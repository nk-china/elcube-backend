package cn.nkpro.tfms.platform.custom;

import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.DefDocTypeBO;

/**
 *
 * 抽象的组件类
 * Created by bean on 2020/7/31.
 */
public abstract class TfmsAbstractService<DDT> extends TfmsAbstractComponent<DDT> {

    /**
     * @param doc doc
     * @param docDef docDef
     */
    @SuppressWarnings("all")
    @Override
    public final void update(BizDocBase doc, DefDocTypeBO docDef, BizDocBase original) throws Exception{
        this.doUpdate(doc, original, docDef,
                (DDT) docDef.getCustomComponentsDef().get(componentName));
    }

    /**
     * 更新组件
     * @param doc doc
     * @param docDef docDef
     */
    protected abstract void doUpdate(BizDocBase doc, BizDocBase original, DefDocTypeBO docDef, DDT def) throws Exception;
}
