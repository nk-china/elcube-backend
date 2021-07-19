package cn.nkpro.tfms.platform.custom;

import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.DefDocTypeBO;
import io.jsonwebtoken.lang.Assert;
import org.springframework.stereotype.Component;

/**
 *
 * 这是组件demo
 * TfmsBaseComponent 有两个泛型参数
 * 1表示组件的数据类型，2表示组件的配置数据类型
 * Created by bean on 2020/7/31.
 */
@Component("nk-card-hello-world")
public class NkCardHelloWorld extends TfmsAbstractCard<String,String> {

    /**
     * 组件支持的单据分类
     * @param classify 单据分类
     * @return 是否支持
     */
    @Override
    public boolean supports(EnumDocClassify classify) {
        return true;
    }

    /**
     * @return 组件描述
     */
    @Override
    public String getComponentDesc() {
        return "Hello World";
    }

    /**
     * 根据单据对象返回组件数据
     * 注意，这里的doc对象仅包含ta抬头数据，其他组件数据可能还没有初始化完成
     * @param doc 单据对象
     * @return 返回组件数据
     */
    @Override
    protected String doGetData(BizDocBase doc, String def) {
        return "你好，我是一个组件";
    }

    /**
     * 预初始化组件数据，注意这里不要对数据进行持久化
     * @param doc 单据对象
     * @param preDoc 前序单据对象
     * @param docDef 单据配置信息，这里可以获取组件自己的配置信息
     * @see #getDef
     * @return 返回组件的临时数据
     */
    @Override
    protected String toCreate(BizDocBase doc, BizDocBase preDoc, DefDocTypeBO docDef, String def) {
        return "你好，我是一个组件";
    }

    /**
     * 保存组件数据
     * @param doc 单据对象
     * @param docDef 单据配置信息，这里可以获取组件自己的配置信息
     * @see #getDef
     */
    @Override
    protected void doUpdate(BizDocBase doc, DefDocTypeBO docDef, String data, String def) {
        // 更新的时候前端的数据会传回来
        Assert.isTrue("你好，我是一个组件".equals(data));
    }
}
