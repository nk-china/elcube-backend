/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.components.cr.spel;

import cn.nkpro.elcube.co.spel.NkSpELInjection;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("all")
@Component("SpELsmallLoans")
public class NkSmallLoansSpEL implements NkSpELInjection {

    /**
    * @Description: 获取当前日期
    * @Param: []
    * @return: java.lang.Long
    * @Author: mab
    * @Date: 2022/1/19
    **/
    public Long date(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis()/1000;
    }
    /**
     * @Description: 获取账单结束日期
     * @Param: [start, np]
     * @return: java.lang.Long
     * @Author: mab
     * @Date: 2022/1/17
     **/
    public Long end(Long start, Integer np) {
        Long end = 0L;
        if (start != null && np != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(start * 1000);
            calendar.add(Calendar.MONTH, np);
            end = calendar.getTimeInMillis() / 1000;
        }
        return end;
    }

    /**
    * @Description: 已还期次
    * @Param: [object]
    * @return: java.lang.Long
    * @Author: mab
    * @Date: 2022/1/17
    **/
    public Long paidPeriod(Object object) {
        Long count = 0L;
        if (object == null)
            return 0L;
        count = objToListMap(object).stream().filter(l -> {
            boolean flag = false;
            if ((Double) l.get("received") >= (Double) l.get("receivable") && l.get("billType").equals("本金"))
                flag = true;
            return flag;
        }).count();
        return count;
    }

    /**
    * @Description: 累计罚息
    * @Param: [object]
    * @return: java.lang.Double
    * @Author: mab
    * @Date: 2022/1/17
    **/
    public Double defaultInterestTotal(Object object) {
        if (object == null)
            return 0D;
        return objToListMap(object).stream().filter(l -> {
            boolean flag = false;
            if (l.get("billType").equals("滞纳金"))
                flag = true;
            return flag;
        }).collect(Collectors.summarizingDouble(l -> (Double) l.get("amount"))).getSum();
    }

    /**
    * @Description: 已还罚息
    * @Param: [object]
    * @return: java.lang.Double
    * @Author: mab
    * @Date: 2022/1/17
    **/
    public Double paidDefaultInterest(Object object) {
        if (object == null)
            return 0D;
        return objToListMap(object).stream().filter(l -> {
            boolean flag = false;
            if (l.get("billType").equals("滞纳金") && (Double) l.get("received") >= (Double) l.get("receivable"))
                flag = true;
            return flag;
        }).collect(Collectors.summarizingDouble(l -> (Double) l.get("received"))).getSum();
    }

    /**
    * @Description: 已还本金
    * @Param: [object]
    * @return: java.lang.Double
    * @Author: mab
    * @Date: 2022/1/17
    **/
    public Double paidPrincipal(Object object) {
        if (object == null)
            return 0D;
        return objToListMap(object).stream().filter(l -> {
            boolean flag = false;
            if (l.get("billType").equals("本金"))
                flag = true;
            return flag;
        }).collect(Collectors.summarizingDouble(l -> (Double) l.get("received"))).getSum();
    }

    /**
    * @Description: 已还利息
    * @Param: [object]
    * @return: java.lang.Double
    * @Author: mab
    * @Date: 2022/1/17
    **/
    public Double paidInterest(Object object) {
        if (object == null)
            return 0D;
        return objToListMap(object).stream().filter(l -> {
            boolean flag = false;
            if (l.get("billType").equals("利息"))
                flag = true;
            return flag;
        }).collect(Collectors.summarizingDouble(l -> (Double) l.get("received"))).getSum();
    }

    /**
    * @Description: 剩余贷款总额
    * @Param: [object]
    * @return: java.lang.Double
    * @Author: mab
    * @Date: 2022/1/17
    **/
    public Double surplusLoanAmount(Object object) {
        if (object == null)
            return 0D;
        double sum = objToListMap(object).stream().filter(l -> {
            boolean flag = false;
            if (l.get("billType").equals("本金"))
                flag = true;
            return flag;
        }).collect(Collectors.summarizingDouble(l -> (Double) l.get("amount") - (Double) l.get("received"))).getSum();
        return sum;
    }

    /**
    * @Description: 剩余利息总额
    * @Param: [object]
    * @return: java.lang.Double
    * @Author: mab
    * @Date: 2022/1/17
    **/
    public Double surplusInterestTotal(Object object) {
        if (object == null)
            return 0D;
        return objToListMap(object).stream().filter(l -> {
            boolean flag = false;
            if (l.get("billType").equals("利息"))
                flag = true;
            return flag;
        }).collect(Collectors.summarizingDouble(l -> (Double) l.get("amount") - (Double) l.get("received"))).getSum();
    }

    /**
    * @Description: 获取应还总额
    * @Param: [obj]
    * @return: java.math.BigDecimal
    * @Author: mab
    * @Date: 2022/1/19
    **/
    public Double amount(Object obj){
        if (obj == null)
            return 0D;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long timeInMillis = calendar.getTimeInMillis()/1000;
        return objToListMap(obj).stream()
                .filter(l -> {
                    boolean flag = false;
                    Object expireDate = l.get("expireDate");
                    if (expireDate instanceof Integer) {
                        if (((Integer) expireDate).longValue() <= timeInMillis)
                            flag = true;
                    } else if (expireDate instanceof Long)
                        if ((Long) expireDate <= timeInMillis)
                            flag = true;
                    if (((String)l.get("billType")).equals("滞纳金")){
                        if (expireDate instanceof Integer) {
                            if (((Integer) expireDate).longValue() == timeInMillis)
                                flag = false;
                        } else if (expireDate instanceof Long)
                            if ((Long) expireDate == timeInMillis)
                                flag = false;
                    }
                    return flag;
                }).collect(Collectors.summarizingDouble(l -> ((Double) l.get("receivable") - (Double) l.get("received")) <= 0D ? 0D : ((Double) l.get("receivable") - (Double) l.get("received")) )).getSum();
    }

    /**
    * @Description: Object转List<Map<String,Object>
    * @Param: [object]
    * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
    * @Author: mab
    * @Date: 2022/1/17
    **/
    public List<Map<String,Object>> objToListMap(Object object){
        if (object instanceof List) {
            return ((List<Object>) object).stream().map(l -> {
                Map<String, Object> map = new HashMap<>();
                JSONObject obj = (JSONObject) JSONObject.toJSON(l);
                for (Map.Entry<String, Object> entry : obj.entrySet()) {
                    map.put(entry.getKey(), entry.getValue());
                }
                return map;
            }).collect(Collectors.toList());
        }else {
            return null;
        }
    }
}






