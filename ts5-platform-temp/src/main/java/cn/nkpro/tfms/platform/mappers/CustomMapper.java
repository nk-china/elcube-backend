package cn.nkpro.tfms.platform.mappers;

import java.util.List;
import java.util.Map;

public interface CustomMapper {

    List<String> selectAllBreachPaymentDocId(Map<String, Object> params);
}