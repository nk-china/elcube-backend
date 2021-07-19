package cn.nkpro.tfms.platform.custom.doc.defaults;

import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.BizProjectBO;
import cn.nkpro.tfms.platform.model.DefProjectTypeBO;
import cn.nkpro.tfms.platform.model.index.IndexDoc;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * Created by bean on 2020/7/23.
 */
public class BizProjectDoc extends BizDocBase {

    public BizProjectDoc(){
        super();
    }

    @Getter
    @Setter
    private BizProjectBO refObject;
}
