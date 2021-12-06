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
package cn.nkpro.elcube.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by bean on 2020/7/9.
 */
public interface DateTimeUtilz {

    public static String todayShortString(){
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

    public static Long nowSeconds(){
        return Calendar.getInstance().getTimeInMillis()/1000;
    }
    public static Long todaySeconds(){
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.HOUR_OF_DAY,0);
        instance.set(Calendar.MINUTE,0);
        instance.set(Calendar.SECOND,0);
        instance.set(Calendar.MILLISECOND,0);
        return instance.getTimeInMillis()/1000;
    }

    public static Long dateAdd(Long seconds,int day){
        return seconds+60*60*24*day;
    }

    public static long dateDiff(Long toSeconds,Long fromSeconds){
        return (toSeconds-fromSeconds)/60/60/24;
    }

    public static Long monthAdd(Long seconds,int month){
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(seconds * 1000);
        instance.add(Calendar.MONTH,month);
        return instance.getTimeInMillis()/1000;
    }

    public static String format(Long seconds,String format){
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(seconds * 1000);
        return new SimpleDateFormat(format).format(instance.getTime());
    }
    public static Long stringToDateLong(String str,String pattern){

        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        if (null==pattern){
            dateFormat = new SimpleDateFormat("yyyy-dMM-dd");
        }
        Date date = null;
        try {
            date = dateFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getTimeInMillis()/1000;
    }
    /**
     * 获取当前月的最后一天的日期
     * 格式为 yyyy-mm-dd 23:59:59
     * @param date
     * @return
     */
    public static Date getLastDayOfMonth (Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //获取年
        int year = calendar.get(Calendar.YEAR);
        //获取月份，0表示1月份
        int month = calendar.get(Calendar.MONTH) + 1;
        //获取本月最大天数
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        try {
            return format.parse(year+"-"+month+"-"+lastDay + " 23:59:59");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Long fromISO(String isoDate){
        return Instant.parse( isoDate ).getEpochSecond();
    }
}
