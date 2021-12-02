/*
 * This file is part of EAsis.
 *
 * EAsis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EAsis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with EAsis.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.easis.co.spel;

import cn.nkpro.easis.co.NkCustomObjectManager;
import cn.nkpro.easis.exception.NkDefineException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class NkSpELManager {

    private static final NkMapAccessor mapAccessor = new NkMapAccessor();
    private static final ExpressionParser parser = new SpelExpressionParser();

    // 这个表达式有问题，${{}}嵌套匹配不了
    // private static final Pattern pattern = Pattern.compile("\"?\\$\\{(?:[^\"'}]|\"[^\"]*\"|'[^']*'|\\{\\{|}})*+}\"?");

    // 目前支持 ${#exp {{}}} 3层匹配，层数多了还是有问题
    // 不知道java的平衡组该怎么写，留一个坑给大神 :)
    // 分组数量为2的时候，效率略低，因此采用1分组方案
    // private static final Pattern pattern = Pattern.compile("\"?\\$\\{((?:\"[^\"]*\"|'[^']*'|\\{[^}]*\\{[^}]*}[^}]*}|\\{[^}]*}|[^\"'}])*?)}\"?");
    private static final Pattern pattern = Pattern.compile("\"?\\$\\{(?:\"[^\"]*\"|'[^']*'|\\{[^}]*\\{[^}]*}[^}]*}|\\{[^}]*}|[^\"'}])*?}\"?");

    @Autowired@SuppressWarnings("all")
    private NkCustomObjectManager customObjectManager;

    public EvaluationContext createContext(Object root){
        StandardEvaluationContext ctx = new StandardEvaluationContext(root);
        ctx.addPropertyAccessor(mapAccessor);
        ctx.setBeanResolver((evaluationContext, s) -> customObjectManager.getCustomObject("SpEL"+s,NkSpELInjection.class));
        return ctx;
    }

    public Object invoke(String el, Object root){
        StandardEvaluationContext context = new StandardEvaluationContext(root);
        context.addPropertyAccessor(mapAccessor);
        el = convert(el, context);
        try{
            return parser.parseExpression(el).getValue(context);
        }catch (ParseException | EvaluationException e){
            throw new NkDefineException(String.format("表达式 %s 错误: %s",el, e.getMessage()),e);
        }
    }

    public Object invoke(String el, EvaluationContext context){
        el = convert(el, context);
        try{
            return parser.parseExpression(el).getValue(context);
        }catch (ParseException | EvaluationException e){
            throw new NkDefineException(String.format("表达式 %s 错误: %s",el, e.getMessage()),e);
        }
    }

    /**
     * 为什么没有采用SpEL的Template的#{} 语法？
     * 因为我们的目的是通过一个字符串模版生成一个JSON格式的内容，#{} 只能将表达式的返回值通过toString()的方式潜入到模版中，
     * 这不是我要的效果
     * 经过测试与TEMPLATE_EXPRESSION方式的对比，1000000次执行结果差距仅500ms，那么对一个单据业务来说影响微乎其微
     *
     * Expression expression = parser.parseExpression(input, ParserContext.TEMPLATE_EXPRESSION);
     */
    public String convert(String input,Object root){
        return convert(input, createContext(root));
    }

    public String convert(String input,EvaluationContext context){

        if(StringUtils.isBlank(input)){
            return StringUtils.EMPTY;
        }

        Matcher matcher = pattern.matcher(input);
        boolean bool  = true,
                bool2 = false;

        while(matcher.find()){

            if(bool && log.isDebugEnabled()){
                bool = false;
                log.debug("解析SpEL模版 {}",input);
            }

            String expression   = matcher.group(0);
            String el = expression.substring(
                    expression.charAt(0)=='"'                    ?                    3:                    2,
                    expression.charAt(expression.length()-1)=='"'?expression.length()-2:expression.length()-1
            );

            try{
                Object value = parser.parseExpression(el).getValue(context);

                String strValue;
                if(value == null){
                    strValue = "null";
                }else if(value instanceof Boolean || value instanceof Number){
                    strValue = value.toString();
                }else{
                    strValue = JSON.toJSONString(value);
                }
                input = input.replace(expression, strValue);

                if(log.isDebugEnabled())log.debug("\t {} => {} >> {}",expression, el, strValue);

            }catch (ParseException | EvaluationException e){
                throw new NkDefineException(String.format("表达式模版 %s 错误 : %s",input, expression),e);
            }
            bool2 = true;
        }
        if(bool2 && log.isDebugEnabled()){
            log.debug("解析SpEL模版 完成 {}",input);
        }

        return input;
    }
}
