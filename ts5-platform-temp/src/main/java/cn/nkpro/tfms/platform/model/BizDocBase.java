package cn.nkpro.tfms.platform.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by bean on 2020/6/10.
 */
public class BizDocBase extends BizDocPreparsed {

    @Getter@Setter
    private Boolean writeable;

    @Getter@Setter
    private String partnerName;

    @Getter@Setter
    private BizDocBase preDoc;

    @Getter@Setter
    private Set<String> sysStatus;

    @Getter@Setter
    private Map<String,Object> dynamics;

    /**
     * 日期上下文
     */
    @Getter@Setter
    private Map<String,Long> dateContext = new HashMap<>();

    /**
     * 价格上下文
     */
    @Getter@Setter
    private Map<String,Double> priceContext = new HashMap<>();

    @Getter@Setter
    private BizDocComponentsData componentsData = new BizDocComponentsData();

    public String[] getTags(){
        return StringUtils.split(this.getDocTags(),",");
    }

    @Getter@Setter
    private DefDocTypeBO definedDoc;

//    @Getter@Setter
//    private boolean isNewCreate;

    @Getter@Setter
    private List<String> exception;

    /**
     * @see cn.nkpro.tfms.platform.model.index.IndexDoc
     * @see cn.nkpro.tfms.platform.elasticearch.annotation.ESDynamicTemplate
     * @param key 添加的动态字段的名称，规则参考ESRoot
     * @param value 值
     */
    public void addDynamicField(String key, Object value){
        Assert.isTrue(key.contains("_"),String.format("dynamic key [ %s ] 不合法，必须以下划线[_]分割，以数据类型结尾", key));
        if(dynamics==null){
            dynamics = new HashMap<>();
        }
        dynamics.put(key, value);
    }
}
