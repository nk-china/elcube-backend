package cn.nkpro.ts5.co.query;

import cn.nkpro.ts5.co.query.model.DataFieldDesc;
import cn.nkpro.ts5.co.query.model.DataQueryRequest;
import cn.nkpro.ts5.co.query.model.DataQueryResponse;
import cn.nkpro.ts5.co.NkCustomObject;

import java.util.List;

public interface DataQueryService extends NkCustomObject {

    List<DataFieldDesc> getFieldCaps(String index);

    DataQueryResponse queryPage(DataQueryRequest request);

    DataQueryResponse queryList(DataQueryRequest request);
}
