package cn.nkpro.tfms.platform.model;

import cn.nkpro.tfms.platform.model.po.BizProject;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by bean on 2020/7/7.
 */
public class BizProjectBO extends BizProject  {

    //private List<BizDocBase> transactionHistory;

    @Getter
    @Setter
    private DefProjectTypeBO defined;
}
