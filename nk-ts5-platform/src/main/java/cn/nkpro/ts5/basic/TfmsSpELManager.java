package cn.nkpro.ts5.basic;

import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.exception.TfmsException;
import cn.nkpro.ts5.utils.BeanUtilz;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class TfmsSpELManager {

    private static final ExpressionParser parser = new SpelExpressionParser();
    private static final Pattern pattern = Pattern.compile("\\$\\{(?:[^\"'}]|\"[^\"]*\"|'[^']*'|\\{\\{|}})*}");


    @Autowired@SuppressWarnings("all")
    private NKCustomObjectManager customObjectManager;

    public EvaluationContext createContext(DocHV doc){
        StandardEvaluationContext ctx = new StandardEvaluationContext(make(doc));
        ctx.addPropertyAccessor(new MapAccessor());
        getSpELMap(doc).forEach(ctx::setVariable);

//        customObjectManager.getCustomObjects(TfmsSpELInjection.class)
//                .forEach((k,v)->{
//                    if(doc!=null && v instanceof TfmsSpELInjectionDocAwared){
//                        ((TfmsSpELInjectionDocAwared) v).setDoc(doc);
//                    }
//                    ctx.setVariable(v.getSpELName(),v);
//                });

        return ctx;
    }

    public Map<String, Object> getSpELMap(DocHV doc){
        return customObjectManager.getCustomObjects(TfmsSpELInjection.class)
                .values()
                .stream()
                .peek(tfmsSpELInjection -> {
                    if (doc != null && tfmsSpELInjection instanceof TfmsSpELInjectionDocAwared) {
                        ((TfmsSpELInjectionDocAwared) tfmsSpELInjection).setDoc(doc);
                    }
                }).collect(Collectors.toMap(TfmsSpELInjection::getSpELName,t->t));
    }

    public ExpressionParser parser(){
        return parser;
    }

    public String convert(String input){
        StandardEvaluationContext ctx = new StandardEvaluationContext();
        ctx.addPropertyAccessor(new MapAccessor());
        customObjectManager.getCustomObjects(TfmsSpELInjection.class)
                .forEach((k,v)-> ctx.setVariable(v.getSpELName(),v));
        return convert(ctx,input);
    }
    public String convert(DocHV doc,String input){

        StandardEvaluationContext ctx = new StandardEvaluationContext(make(doc));
        ctx.addPropertyAccessor(new MapAccessor());

        customObjectManager.getCustomObjects(TfmsSpELInjection.class)
                .forEach((k,v)->{
                    if(doc!=null && v instanceof TfmsSpELInjectionDocAwared){
                        ((TfmsSpELInjectionDocAwared) v).setDoc(doc);
                    }
                    ctx.setVariable(v.getSpELName(),v);
                });

        return convert(ctx,input);
    }
    public String convert(EvaluationContext context,String input){

        if(StringUtils.isBlank(input)){
            return StringUtils.EMPTY;
        }

        Matcher matcher = pattern.matcher(input);

        while(matcher.find()){

            String expression = matcher.group(0);
            String SpEL = expression
                    .substring(2,expression.length()-1)
                    .replaceAll("\\{\\{","{")
                    .replaceAll("}}","}");
            try{
                String value = parser.parseExpression(SpEL).getValue(context,String.class);
                input = input.replace(expression, StringUtils.defaultString(value));
            }catch (ParseException | EvaluationException e){
                throw new TfmsException(String.format("表达式%s错误",SpEL),e);
            }
        }

        return input;
    }

    private TfmsSpELDoc make(DocHV doc){
        return BeanUtilz.copyFromObject(doc,TfmsSpELDoc.class);
    }
}
