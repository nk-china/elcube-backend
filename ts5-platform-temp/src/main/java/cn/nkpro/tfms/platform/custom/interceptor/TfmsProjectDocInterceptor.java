package cn.nkpro.tfms.platform.custom.interceptor;

import cn.nkpro.tfms.platform.basis.TfmsCustomObject;
import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.BizProjectBO;
import cn.nkpro.tfms.platform.model.DefProjectDocLI;
import cn.nkpro.tfms.platform.model.po.BizProject;

/**
 *
 * 业务单据拦截器
 * Created by bean on 2020/7/13.
 */
public interface TfmsProjectDocInterceptor extends TfmsCustomObject {

    /**
     * 判断 preDoc 单据 是否符合 创建后续单据的条件
     * 自定义的前置条件
     * @param project 业务对象
     * @param preDoc 单据对象
     * @param def
     * @return
     */
    default boolean preCondition(BizProject project, BizDocBase preDoc, DefProjectDocLI def){
        return true;
    }
}
