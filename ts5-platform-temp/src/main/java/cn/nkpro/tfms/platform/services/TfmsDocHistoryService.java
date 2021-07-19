package cn.nkpro.tfms.platform.services;

import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.po.SysLogDocRecord;

import java.util.List;

public interface TfmsDocHistoryService {
    void doAddVersion(BizDocBase doc, BizDocBase original, List<String> changedCard,String source);


    List<SysLogDocRecord> getHistorys(String docId);

    SysLogDocRecord getDetail(String id);
}
