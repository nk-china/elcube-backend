/*
 * This file is part of ELCard.
 *
 * ELCard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcard.wsdoc.model;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;

/**
 * Created by bean on 2019/3/18.
 */
public class Ctrl implements Comparable<Ctrl>{
    private String beanName;
    private String ctrlName;
    private String ctrlDesc;
    private List<Fun> funcs;

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getCtrlName() {
        return ctrlName;
    }

    public void setCtrlName(String ctrlName) {
        this.ctrlName = ctrlName;
    }

    public String getCtrlDesc() {
        return ctrlDesc;
    }

    public void setCtrlDesc(String ctrlDesc) {
        this.ctrlDesc = ctrlDesc;
    }

    public List<Fun> getFuncs() {
        return funcs;
    }

    public void setFuncs(List<Fun> funcs) {
        this.funcs = funcs;
    }


    @Override
    public int compareTo(Ctrl o) {

        String[] titleThis  =   ctrlName.split("[.]");
        String[] titleOther = o.ctrlName.split("[.]");
        for(int i=0;i<titleThis.length;i++){
            if(titleOther.length>i){
                int ret = 0;
                if(NumberUtils.isDigits(titleThis[i])&&NumberUtils.isDigits(titleOther[i])){
                    ret = NumberUtils.createInteger(titleThis[i]).compareTo(NumberUtils.createInteger(titleOther[i]));
                }else{
                    ret = titleThis[i].compareTo(titleOther[i]);
                }
                if(ret!=0)
                    return ret;
            }else
                return -1;
        }
        return ctrlName.compareTo(o.ctrlName);
    }
}
