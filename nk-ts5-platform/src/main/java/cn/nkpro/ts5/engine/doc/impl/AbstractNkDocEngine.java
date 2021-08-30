package cn.nkpro.ts5.engine.doc.impl;

import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.engine.elasticearch.SearchEngine;
import cn.nkpro.ts5.engine.elasticearch.model.CustomES;
import cn.nkpro.ts5.engine.spel.TfmsSpELManager;
import cn.nkpro.ts5.orm.mb.gen.DocDefIndexCustom;
import cn.nkpro.ts5.utils.BeanUtilz;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

class AbstractNkDocEngine {

    @Autowired
    private SearchEngine searchEngine;

    @Autowired
    private TfmsSpELManager spELManager;

    /**
     * todo 如果 doc 和 original是同一个对象，可以优化性能
     */
    void indexCustom(DocHV doc, DocHV original){
        if(doc.getDef().getIndexCustoms()!=null){

            EvaluationContext context1 = spELManager.createContext(doc);
            EvaluationContext context2 = spELManager.createContext(original);

            doc.getDef().getIndexCustoms().forEach(indexCustom -> {

                // 满足前置条件
                if(StringUtils.isBlank(indexCustom.getConditionSpEL())||
                        (Boolean) spELManager.invoke(indexCustom.getConditionSpEL(),context1)){

                    // data1 新数据
                    Object data1 = spELManager.invoke(indexCustom.getDataSpEL(), context1);
                    // data2 原数据
                    Object data2 = original!=null?spELManager.invoke(indexCustom.getDataSpEL(), original):null;

                    // 数组需要转换成List
                    if(data1!=null && data1.getClass().isArray()){
                        data1 = Arrays.asList((Object[])data1);
                    }
                    if(data2!=null && data2.getClass().isArray()){
                        data2 = Arrays.asList((Object[])data2);
                    }

                    if(data1 instanceof List || data2 instanceof List){
                        // 对数组进行数据逐条处理

                        Assert.isTrue(data1 instanceof List && data2 instanceof List,"自定义索引["+indexCustom.getCustomType() + "] 数据类型错误");

                        @SuppressWarnings("unchecked")
                        List<Object> list1 = (List<Object>)data1;
                        @SuppressWarnings("unchecked")
                        List<Object> list2 = (List<Object>)data2;

                        // 如果新数据、原数据均不为空，对比处理
                        BeanUtilz.diffList(list1,list2,
                                (row)-> {
                                    context1.setVariable("row", row);
                                    return (String) spELManager.invoke(indexCustom.getKeySpEL(),context1);
                                },
                                (row)-> {
                                    context2.setVariable("row", row);
                                    return (String) spELManager.invoke(indexCustom.getKeySpEL(),context2);
                                },
                                (key, row)->{
                                    // add
                                    context1.setVariable("row", row);
                                    searchEngine.updateBeforeCommit(
                                        buildCustomES(
                                            key,
                                            indexCustom,
                                            context1
                                        )
                                    );
                                },
                                (key,row)->{
                                    // update
                                    context1.setVariable("row", row);
                                    searchEngine.updateBeforeCommit(
                                        buildCustomES(
                                            key,
                                            indexCustom,
                                            context1
                                        )
                                    );
                                },
                                (key,row)->{
                                    // delete
                                    Assert.isTrue(StringUtils.isNotBlank(key),"自定义索引["+indexCustom.getCustomType() + "] 主键id值不能为空");
                                    searchEngine.deleteBeforeCommit(CustomES.class, key);
                                }
                        );
                    }else{
                        // 更新单条数据，判断前后的key是否一致

                        context1.setVariable("row",data1);
                        String key1 = (String) spELManager.invoke(indexCustom.getKeySpEL(),context1);
                        Assert.isTrue(StringUtils.isNotBlank(key1),"自定义索引["+indexCustom.getCustomType() + "] 主键id值不能为空");

                        if(data2!=null){
                            // 判断前后的key是否一致，不一致的话需要先删除
                            context2.setVariable("row",data2);
                            String key2 = (String) spELManager.invoke(indexCustom.getKeySpEL(),context2);
                            if(!StringUtils.equals(key1,key2) && StringUtils.isNotBlank(key2)){
                                searchEngine.deleteBeforeCommit(CustomES.class,key2);
                            }
                        }

                        // update
                        searchEngine.updateBeforeCommit(
                                buildCustomES(
                                        (String) spELManager.invoke(indexCustom.getKeySpEL(),context1),
                                        indexCustom,
                                        context1
                                )
                        );
                    }
                }
            });
        }
    }

    private CustomES buildCustomES(String customId,DocDefIndexCustom indexCustom, EvaluationContext context){
        Assert.isTrue(StringUtils.isNotBlank(customId),"自定义索引["+indexCustom.getCustomType() + "] 主键id值不能为空");
        CustomES customES = JSON.parseObject(spELManager.convert(context, indexCustom.getMappingSpEL()),CustomES.class);
        customES.setCustomType(indexCustom.getCustomType());
        customES.setCustomId(customId);
        return customES;
    }
}
