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
package cn.nkpro.elcube.wsdoc.model;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * Created by bean on 2019/3/18.
 */
public class Fun implements Comparable<Fun>{
    private String funcName;
    private String funcMapping;
    private String funcUrl;

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public String getFuncMapping() {
        return funcMapping;
    }

    public void setFuncMapping(String funcMapping) {
        this.funcMapping = funcMapping;
    }

    public String getFuncUrl() {
        return funcUrl;
    }

    public void setFuncUrl(String funcUrl) {
        this.funcUrl = funcUrl;
    }

    @Override
    public int compareTo(Fun o) {

        String[] titles0 =   funcName.split("[.]");
        String[] titles1 = o.funcName.split("[.]");
        for(int i=0;i<titles0.length;i++){
            if(titles1.length>i){
                int ret = 0;
                if(NumberUtils.isDigits(titles0[i])&&NumberUtils.isDigits(titles1[i])){
                    ret =  NumberUtils.createInteger(titles0[i]).compareTo(NumberUtils.createInteger(titles1[i]));
                }else{
                    ret = titles0[i].compareTo(titles1[i]);
                }
                if(ret!=0) return ret;
            }else
                return -1;
        }
        return funcName.compareTo(o.funcName);
    }
}
