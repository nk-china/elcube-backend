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
package cn.nkpro.elcube.co.spel;

import cn.nkpro.elcube.exception.NkException;
import cn.nkpro.elcube.utils.DateTimeUtilz;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @date:2021/12/22
 * @author :zxc
 * @description: 1、可以获取当前日期返回秒
 */
@Component("SpELdate")
public class NkDateSpEL implements NkSpELInjection {

    //天
    private static final Long DAY = 1L;
    //周
    private static final Long WEEK = 7L;
    //月
    private static final Long MONTH = 30L;
    //季度
    private static final Long QUARTER= 90L;
    //半年
    private static final Long HALFYEAR = 180L;
    //年
    private static final Long YEAR = 365L;
    //每一天的秒数
    private static final Long SECOND = 86400L;

    //初始化还款单位
    private static final List<String> timeUnits = new ArrayList<>(Arrays.asList(new String[]{"DAY","WEEK","MONTH","QUARTER","HALFYEAR","YEAR"}));

    /**
     * @description：获取当前日期返回秒
     * @return
     */
    @SuppressWarnings("unused")
    public Object now(){
        return DateTimeUtilz.todaySeconds();
    }

    /**
     * @author 吴俊
     * @Email wujun@newcore.net.cn
     * 创建日期: 2022-01-12
     * used to: 时间相加
     */
    public Long add(Long ... dates) throws NkException {
        Long[] clone = dates.clone();
        if(clone.length < 2) throw new NkException("必须有两个参数及以上,请检查");
        return Arrays.stream(dates).mapToLong(date -> date).sum();
    }


    /**
     * @author 吴俊
     * @Email wujun@newcore.net.cn
     * 创建日期: 2022-01-12
     * used to: 时间相减
     */
    public Long sub(Long ... dates) throws NkException {
        Long result = null;
        Long[] clone = dates.clone();
        if(clone.length < 2) throw new NkException("必须有两个参数,请检查");
        result = clone[0] - clone[1];
        return result;
    }


    /**
     * @author 吴俊
     * @Email wujun@newcore.net.cn
     * 创建日期: 2022-01-12
     * used to: 单位(天、周、月、季、半年、年) * 还款期限
     */
    public Long mul(Object timeUnit,Object time) throws NkException {
        if(timeUnit == null || time == null || StringUtils.isBlank(timeUnit.toString())) throw new NkException("两个参数都不能为空或空字符串，请检查");
        if(!timeUnits.contains(timeUnit)) throw new NkException("第一个参数请填写为--DAY,WEEK,MONTH,QUARTER,HALFYEAR,YEAR其中一个");
        Long result = null;
        switch (timeUnit.toString()){
            case "DAY":
                result = calculateTimeUnit(DAY,time);
                break;
            case "WEEK":
                result = calculateTimeUnit(WEEK,time);
                break;
            case "MONTH":
                result = calculateTimeUnit(MONTH,time);
                break;
            case "QUARTER":
                result = calculateTimeUnit(QUARTER,time);
                break;
            case "HALFYEAR":
                result = calculateTimeUnit(HALFYEAR,time);
                break;
            case "YEAR":
                result = calculateTimeUnit(YEAR,time);
                break;
        }
        return result;
    }

    public static Long calculateTimeUnit(Long timeUnit,Object time) throws NkException {
        Long result = null;
        Integer flag = 0;
        if(time instanceof Integer){
            result = timeUnit * SECOND * (Integer)time;
            flag++;
        }
        if(time instanceof Long){
            result = timeUnit * SECOND * (Long)time;
            flag++;
        }
        if(time instanceof Double){
            result = timeUnit * SECOND * ((Double) time).longValue();
            flag++;
        }
        if(flag == 0){
            throw new NkException("请检查数据类型,第二个参数类型只能为integer或者long或者double");
        }
        return result;
    }

}
