package cn.nkpro.tfms.platform.custom;

import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.DefDocTypeBO;
import cn.nkpro.tfms.platform.model.index.IndexDoc;
import cn.nkpro.ts5.supports.GUID;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * 抽象的组件类
 * Created by bean on 2020/7/31.
 */
public abstract class TfmsAbstractCard<DT,DDT> extends TfmsAbstractComponent<DDT> implements TfmsCardComponent {

    @Autowired
    protected GUID guid;

    public TfmsAbstractCard(){
        super();
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public boolean cached() {
        return true;
    }

    @Override
    public abstract String getComponentDesc();

    @Override
    public String getDataComponentName() {
        return componentName;
    }

    @Override
    public String[] getDataComponentExtNames() {
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }

    @Override
    public String[] getDefComponentNames() {
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }

    @Override
    public String[] getPageComponentNames() {
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }


    @SuppressWarnings("all")
    @Override
    public final DT getData(BizDocBase doc) throws Exception {
        // 这里预留逻辑
        return doGetData(doc,(DDT) doc.getDefinedDoc().getCustomComponentsDef().get(componentName));
    }

    /**
     * 获取组件数据
     * @param doc doc
     * @return dt
     */
    protected abstract DT doGetData(BizDocBase doc, DDT def) throws Exception;

    @SuppressWarnings("all")
    @Override
    public final void afterGetData(BizDocBase doc, DefDocTypeBO docDef) throws Exception {
        doAfterGetData(
                doc,
                (DT) doc.getComponentsData().get(componentName),
                (DDT) doc.getDefinedDoc().getCustomComponentsDef().get(componentName));
    }

    /**
     * 获取数据方法之后执行
     * 在 doGetData 之后执行
     * @see #doGetData
     * @param doc doc
     */
    protected void doAfterGetData(BizDocBase doc, DT data, DDT def) throws Exception{
        // nothing to do
    }

    /**
     * @param doc
     * @throws Exception
     */
    @SuppressWarnings("all")
    @Override
    public final void processData(BizDocBase doc) throws Exception {
        doc.getComponentsData().put(
                componentName,
                doProcessData(doc,
                        (DT)  doc.getComponentsData().get(componentName),
                        (DDT) doc.getDefinedDoc().getCustomComponentsDef().get(componentName)
                )
        );
    }


    /**
     *
     * 在所有组件的获取数据方法都执行完之后执行
     * @param doc
     * @param data
     * @param def
     * @return
     * @throws Exception
     */
    public DT doProcessData(BizDocBase doc, DT data, DDT def) throws Exception {return data;}

    @SuppressWarnings("all")
    @Override
    public final DT create(BizDocBase doc, BizDocBase preDoc, DefDocTypeBO docDef) throws Exception{
        return this.toCreate(doc, preDoc, docDef, (DDT) docDef.getCustomComponentsDef().get(componentName));
    }

    /**
     * 预创建组件数据
     * @param doc doc
     * @param preDoc pre
     * @param docDef def
     * @return dt
     */
    protected abstract DT toCreate(BizDocBase doc, BizDocBase preDoc, DefDocTypeBO docDef, DDT def) throws Exception;

    /**
     * @param doc doc
     * @param docDef docDef
     */
    @SuppressWarnings("all")
    @Override
    public final void update(BizDocBase doc, DefDocTypeBO docDef, BizDocBase original) throws Exception{
        this.doUpdate(doc, docDef,
                (DT) doc.getComponentsData().get(componentName),
                (DDT) docDef.getCustomComponentsDef().get(componentName));
    }

    /**
     * 更新组件
     * @param doc doc
     * @param docDef docDef
     */
    protected abstract void doUpdate(BizDocBase doc, DefDocTypeBO docDef, DT data, DDT def) throws Exception;

    @SuppressWarnings("all")
    @Override
    public final Object call(String event, BizDocBase doc, DefDocTypeBO docDef) throws Exception{
        return doCall(event,doc,docDef,
                (DT) doc.getComponentsData().get(componentName),
                (DDT) docDef.getCustomComponentsDef().get(componentName));
    }

    protected Object doCall(String event, BizDocBase doc, DefDocTypeBO docDef, DT data, DDT def) throws Exception{
        return null;
    }

    @SuppressWarnings("all")
    @Override
    public final void calculate(String calculate, BizDocBase doc, DefDocTypeBO docDef) throws Exception{
        doc.getComponentsData().put(
                componentName,
                doCalculate(calculate,
                            doc,
                            docDef,
                            (DT) doc.getComponentsData().get(componentName),
                            (DDT) docDef.getCustomComponentsDef().get(componentName)
                )
        );
    }

    /**
     * 对组件数据重新进行计算
     * @param doc doc
     * @param docDef docDef
     * @param data data
     * @param def def
     * @throws Exception e
     */
    protected DT doCalculate(String calculate, BizDocBase doc, DefDocTypeBO docDef, DT data, DDT def) throws Exception{return data;}

    @SuppressWarnings("all")
    @Override
    public final TfmsCardIndexItemExec indexItem(BizDocBase doc, DefDocTypeBO docDef, IndexDoc indexDoc) throws Exception {
        return this.doIndexItem(doc, docDef,
                (DT) doc.getComponentsData().get(componentName),
                (DDT) docDef.getCustomComponentsDef().get(componentName),
                indexDoc);
    }

    /**
     * @param doc doc
     * @param docDef docDef
     */
    @SuppressWarnings("all")
    protected TfmsCardIndexItemExec doIndexItem(BizDocBase doc, DefDocTypeBO docDef, DT data, DDT def, IndexDoc indexDoc) throws Exception{
        return null;
    }

    @Override
    public final Object deserializeDef(Object def){
        return parse(def, getType(1));
    }

    @Override
    public final Object deserialize(Object data){
        return parse(data, getType(0));
    }

}
