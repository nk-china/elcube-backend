package cn.nkpro.tfms.platform.basis.defaults;

import cn.nkpro.tfms.platform.basis.TfmsSpELInjection;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class NkDefaultSpELJSONInjection implements TfmsSpELInjection {

    @Override
    public String getSpELName() {
        return "json";
    }

//    public String toString(Collection<Number> list){
//        return JSONArray.toJSONString(list);
//    }

    public String stringify(Object input){
        return JSONObject.toJSONString(input);
    }
}
