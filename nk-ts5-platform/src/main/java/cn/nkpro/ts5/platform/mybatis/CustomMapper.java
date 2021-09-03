package cn.nkpro.ts5.platform.mybatis;

import java.util.List;
import java.util.Map;

public interface CustomMapper {

    List<String> selectAllBreachPaymentDocId(Map<String, Object> params);
}