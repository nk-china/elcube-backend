package cn.nkpro.ts5.docengine.utils;

import cn.nkpro.ts5.docengine.model.DocDefIV;
import cn.nkpro.ts5.docengine.model.DocHV;
import cn.nkpro.ts5.docengine.model.NkCardFormDefI;
import cn.nkpro.ts5.docengine.service.NkDocEngineContext;
import cn.nkpro.ts5.docengine.utils.RandomUtils;
import cn.nkpro.ts5.exception.NkDefineException;
import cn.nkpro.ts5.spel.NkSpELManager;
import com.alibaba.fastjson.JSON;
import com.apifan.common.random.source.AreaSource;
import com.apifan.common.random.source.DateTimeSource;
import com.apifan.common.random.source.NumberSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class NkFormCardHelper {

    @Autowired
    private NkSpELManager spELManager;

    public Map random(List<NkCardFormDefI> items){

        Map<String,Object> docRef = new HashMap<>();
        docRef.put("docName","支付表");
        docRef.put("docId","8f79d0bc-7cb9-4031-98f0-034ab2968071");

        Map<String,Object> map = new HashMap<>();

        if(items!=null){
            items.forEach(item->{

                Object value = null;
                switch (item.getInputType()){
                    case "text":
                        value = RandomUtils.randomText();
                        break;
                    case "integer":
                        value = NumberSource.getInstance().randomInt(0, 100000001);
                        break;
                    case "decimal":
                        value = NumberSource.getInstance().randomDouble(10000D, 100000D);
                        break;
                    case "percent":
                        value = NumberSource.getInstance().randomDouble(0.01D, 0.99D);
                        break;
                    case "switch":
                        value = NumberSource.getInstance().randomInt(0,2)==1;
                        break;
                    case "select":
                        if(StringUtils.equals(item.getSelectMode(),"multiple")){
                            value = new Integer[]{NumberSource.getInstance().randomInt(1,3)};
                        }else{
                            value = NumberSource.getInstance().randomInt(1,3);
                        }
                        break;
                    case "cascader":
                    case "tree":
                        value = AreaSource.getInstance().randomCity(",").split(",");
                        break;
                    case "date":
                    case "datetime":
                        value = DateTimeSource.getInstance().randomTimestamp(LocalDate.of(2021, 11, 30))/1000;
                        break;
                    case "ref":
                        value = docRef.get("docId");
                        map.put(item.getKey() + "$doc", docRef);
                        break;
                    default:
                }

                map.put(item.getKey(),value);
            });
        }

        return map;
    }

    public Map execSpEL(DocHV doc, Map<String, Object> data, DocDefIV defIV, List<NkCardFormDefI> items, boolean isNewCreate, boolean execOptions){

        EvaluationContext context = spELManager.createContext(doc);

        if(items!=null){
            items
            .stream()
            .sorted(Comparator.comparing(NkCardFormDefI::getCalcOrder))
            .peek(item -> {
                if(data!=null){
                    if(StringUtils.isNotBlank(item.getSpELContent())){

                        String trigger = null;
                        if(ArrayUtils.contains(item.getSpELTriggers(),"ALWAYS")){
                            trigger = "ALWAYS";
                        }else if(ArrayUtils.contains(item.getSpELTriggers(),"INIT") && isNewCreate){
                            trigger = "INIT";
                        }else if(ArrayUtils.contains(item.getSpELTriggers(),"BLANK") && isBlank(data.get(item.getKey()))){
                            trigger = "BLANK";
                        }

                        if(trigger!=null){

                            if(log.isInfoEnabled())
                                log.info("{}\t\t{} 执行表达式 KEY={} T={} EL={}",
                                        NkDocEngineContext.currLog(),
                                        defIV.getCardKey(),
                                        item.getKey(),
                                        trigger,
                                        item.getSpELContent()
                                );

                            try{
                                data.put(item.getKey(),spELManager.invoke(item.getSpELContent(),context));
                            }catch(Exception e){
                                throw new NkDefineException(
                                        String.format("KEY=%s T=%s %s",
                                                item.getKey(),
                                                trigger,
                                                e.getMessage()
                                        )
                                );
                            }
                        }
                    }

                    if(data.get(item.getKey())==null){
                        if("switch".equals(item.getInputType())){
                            data.put(item.getKey(),true);
                        }else if("select".equals(item.getInputType()) && "multiple".equals(item.getSelectMode())){
                            data.put(item.getKey(),new ArrayList<>());
                        }else{
                            data.put(item.getKey(),null);
                        }
                    }
                    context.setVariable(item.getKey(), data.get(item.getKey()));
                }
            })
            .forEach( item -> {

                if(execOptions){
                    if(StringUtils.isNotBlank(item.getSpELControl())){

                        if(log.isInfoEnabled())
                            log.info("{}\t\t{} 执行表达式 KEY={} T=CONTROL EL={}",
                                    NkDocEngineContext.currLog(),
                                    defIV.getCardKey(),
                                    item.getKey(),
                                    item.getSpELControl()
                            );
                        try{
                            item.setControl((Integer) spELManager.invoke(item.getSpELControl(),context));
                        }catch(Exception e){
                            throw new NkDefineException(
                                    String.format("KEY=%s T=CONTROL %s",
                                            item.getKey(),
                                            e.getMessage()
                                    )
                            );
                        }
                    }

                    if(StringUtils.isNotBlank(item.getOptions())){
                        if(log.isInfoEnabled())
                            log.info("{}\t\t{} 执行表达式模版 KEY={} T=OPTIONS EL={}",
                                    NkDocEngineContext.currLog(),
                                    defIV.getCardKey(),
                                    item.getKey(),
                                    item.getSpELControl()
                            );
                        try {
                            item.setOptionsObject(JSON.parse(spELManager.convert(item.getOptions(), context)));
                        }catch(Exception e){
                            throw new NkDefineException(
                                    String.format("KEY=%s T=OPTIONS %s",
                                            item.getKey(),
                                            e.getMessage()
                                    )
                            );
                        }
                    }
                }
            });
        }
        return data;
    }

    private static boolean isBlank(Object value){
        return value == null ||
                (value instanceof Array && Array.getLength(value)==0) ||
                (value instanceof Collection && CollectionUtils.isEmpty((Collection<?>) value)) ||
                StringUtils.isBlank(value.toString());
    }
}
