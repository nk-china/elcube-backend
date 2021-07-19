package cn.nkpro.tfms.platform.model;

import cn.nkpro.tfms.platform.model.po.DefProjectDoc;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by bean on 2020/7/13.
 */
public class DefProjectDocLI extends DefProjectDoc {

    @Getter@Setter
    private String docName;

    @Getter@Setter
    private Boolean disabled = false;

    @Getter@Setter
    private String disabledDesc = null;
}
