package cn.nkpro.ts5.dataengine.service;

import cn.nkpro.ts5.co.NkCustomObject;
import cn.nkpro.ts5.dataengine.model.DataFieldDesc;
import cn.nkpro.ts5.dataengine.model.DataQueryRequest;
import cn.nkpro.ts5.dataengine.model.DataQueryResponse;

import java.util.List;

public interface DataQueryService extends NkCustomObject {

    List<DataFieldDesc> getFieldCaps(String index);

    DataQueryResponse queryPage(DataQueryRequest request);

    DataQueryResponse queryList(DataQueryRequest request);
}
