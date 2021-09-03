package cn.nkpro.ts5.basic.wsdoc.model;

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
