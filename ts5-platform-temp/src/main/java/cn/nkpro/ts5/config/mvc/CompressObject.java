package cn.nkpro.ts5.config.mvc;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.StringUtils;

public class CompressObject {

    private Object obj;

    private CompressObject(Object obj){
        this.obj = obj;
    }

    @Override
    public String toString() {
        if(obj==null){
            return StringUtils.EMPTY;
        }
        if(obj instanceof CharSequence
                ||obj instanceof Number
                ||obj instanceof Boolean){
            return obj.toString();
        }
        return JSONObject.toJSONString(this.obj,SerializerFeature.DisableCircularReferenceDetect);
    }

    public static CompressObject valueOf(Object obj){
        return new CompressObject(obj);
    }
    public static CompressObject empty(){
        return new CompressObject(null);
    }
}
