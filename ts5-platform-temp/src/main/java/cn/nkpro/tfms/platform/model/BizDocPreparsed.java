package cn.nkpro.tfms.platform.model;

import cn.nkpro.tfms.platform.model.po.BizDoc;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by bean on 2021/7/4.
 */
public class BizDocPreparsed extends BizDoc {

    @Getter@Setter
    private String runtimeKey;
}
