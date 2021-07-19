package cn.nkpro.tfms.platform.model;

import cn.nkpro.tfms.platform.model.po.DefProjectStatus;
import cn.nkpro.tfms.platform.model.po.DefProjectType;
import lombok.Data;

import java.util.List;

/**
 * Created by bean on 2020/7/7.
 */
@Data
public class DefProjectTypeBO extends DefProjectType {
    private List<DefProjectStatus> status;
    private List<DefProjectDocLI> docs;
}
