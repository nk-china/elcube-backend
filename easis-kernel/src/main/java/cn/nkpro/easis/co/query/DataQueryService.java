package cn.nkpro.easis.co.query;

import cn.nkpro.easis.co.query.model.DataFieldDesc;
import cn.nkpro.easis.co.query.model.DataQueryRequest;
import cn.nkpro.easis.co.query.model.DataQueryResponse;
import cn.nkpro.easis.co.NkCustomObject;

import java.util.List;

public interface DataQueryService extends NkCustomObject {

    List<DataFieldDesc> getFieldCaps(String index);

    DataQueryResponse queryPage(DataQueryRequest request);

    DataQueryResponse queryList(DataQueryRequest request);
}
