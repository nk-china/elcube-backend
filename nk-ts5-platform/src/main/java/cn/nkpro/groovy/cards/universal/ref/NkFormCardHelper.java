package cn.nkpro.groovy.cards.universal.ref;

import cn.nkpro.ts5.engine.doc.impl.NkDocEngineContext;
import cn.nkpro.ts5.engine.doc.model.DocDefIV;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.engine.spel.TfmsSpELManager;
import cn.nkpro.ts5.exception.TfmsDefineException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class NkFormCardHelper {

    @Autowired
    private TfmsSpELManager spELManager;

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
                                throw new TfmsDefineException(
                                        String.format("KEY=%s T=%s %s",
                                                item.getKey(),
                                                trigger,
                                                e.getMessage()
                                        )
                                );
                            }
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
                            throw new TfmsDefineException(
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
                            throw new TfmsDefineException(
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
