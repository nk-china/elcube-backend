package cn.nkpro.elcube.docengine.service.impl;

import cn.nkpro.elcube.docengine.NkDocEngine;
import cn.nkpro.elcube.docengine.NkEqlEngine;
import cn.nkpro.elcube.docengine.gen.DocH;
import cn.nkpro.elcube.docengine.model.DocHQL;
import cn.nkpro.elcube.docengine.model.DocHV;
import cn.nkpro.elcube.exception.NkDefineException;
import cn.nkpro.elcube.utils.BeanUtilz;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class NkEqlEngineImpl extends AbstractNkDocEngine implements NkEqlEngine {


    private final static TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();

    @Autowired
    private NkDocEngine docEngine;

    @Override
    public List<DocHQL> findByEql(String eql) {
        try {
            Statement statement = CCJSqlParserUtil.parse(eql);
            Assert.isTrue(statement instanceof Select, "eql 不是一个有效的 select 语句");

            Select select = (Select) statement;

            String docType = tablesNamesFinder.getTableList(select).stream().findFirst().orElse(null);
            docType = StringUtils.equalsAnyIgnoreCase(docType,"all","doc") ? null : docType;

            PlainSelect plainSelect = (PlainSelect) select.getSelectBody();

            List<SelectExpressionItem> columns = plainSelect.getSelectItems()
                    .stream()
                    .filter(i-> !(i instanceof AllColumns || i instanceof AllTableColumns))
                    .map(i-> {
                        SelectExpressionItem selectExpressionItem = (SelectExpressionItem) i;

                        Expression expression = selectExpressionItem.getExpression();

                        Assert.isTrue(expression instanceof StringValue
                                        || expression instanceof LongValue
                                        || expression instanceof DoubleValue
                                        || expression instanceof NullValue
                                        //|| expression instanceof net.sf.jsqlparser.expression.Function  函数不需要
                                        || expression instanceof Column,
                                String.format("列定义%s不支持",expression.toString()));

                        return selectExpressionItem;
                    })
                    .collect(Collectors.toList());

            return docEngine.find(docType)
                    .expression(plainSelect.getWhere())
                    .listResult()
                    .stream()
                    .map(docH -> {
                        DocHQL doc = BeanUtilz.copyFromObject(docH, DocHQL.class);

                        if(!columns.isEmpty()){

                            DocHV hv = docEngine.detail(doc.getDocId());
                            EvaluationContext context = spELManager.createContext(hv);

                            columns.forEach(selectExpressionItem -> {
                                String name = selectExpressionItem.getAlias()==null?selectExpressionItem.getExpression().toString():
                                        selectExpressionItem.getAlias().getName();
                                if(selectExpressionItem.getExpression() instanceof Column){
                                    String el = clearExpression(((Column) selectExpressionItem.getExpression()).getName(false));
                                    doc.put(name,spELManager.invoke(el,context));
//                                }else if(selectExpressionItem.getExpression() instanceof net.sf.jsqlparser.expression.Function){
//                                    net.sf.jsqlparser.expression.Function function = ((net.sf.jsqlparser.expression.Function) selectExpressionItem.getExpression());
//                                    if(StringUtils.equalsIgnoreCase(function.getMultipartName().get(0),"EL")) {
//                                        StringValue el = (StringValue) function.getParameters().getExpressions().get(0);
//                                        doc.put(name,spELManager.invoke(el.getValue(),context));
//                                    }
                                }else if(selectExpressionItem.getExpression() instanceof StringValue){
                                    doc.put(name,((StringValue) selectExpressionItem.getExpression()).getValue());
                                }else if(selectExpressionItem.getExpression() instanceof LongValue){
                                    doc.put(name,((LongValue) selectExpressionItem.getExpression()).getValue());
                                }else if(selectExpressionItem.getExpression() instanceof DoubleValue){
                                    doc.put(name,((DoubleValue) selectExpressionItem.getExpression()).getValue());
                                }else if(selectExpressionItem.getExpression() instanceof NullValue){
                                    doc.put(name,null);
                                }
                            });
                        }
                        return doc;
                    })
                    .collect(Collectors.toList());

        }catch (JSQLParserException e){
            throw new NkDefineException(e.getMessage());
        }
    }

    private List<String> parseUpdateELs(Update update){
         return update.getUpdateSets().stream()
                .map(updateSet -> {
                    String setEl = clearExpression(updateSet.getColumns().get(0).getName(false));
                    String valueEL;

                    Expression expression = updateSet.getExpressions().get(0);
                    if(expression instanceof Column){
                        valueEL = clearExpression(((Column) expression).getName(false));
                    }else if(expression instanceof StringValue){
                        valueEL = updateSet.getExpressions().get(0).toString();
                    }else if(expression instanceof LongValue){
                        valueEL = updateSet.getExpressions().get(0).toString();
                    }else if(expression instanceof DoubleValue){
                        valueEL = updateSet.getExpressions().get(0).toString();
                    }else{
                        return null;
                    }
                    return setEl + " = " + valueEL;
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<DocH> queryByUpdate(Update update){

        String docType = update.getTable().getName();
        docType = StringUtils.equalsAnyIgnoreCase(docType,"all","doc") ? null : docType;

        return docEngine.find(docType)
                .expression(update.getWhere())
                .listResult();
    }

    @Override
    public List<DocHV> execUpdateEql(String eql){

        try {
            Statement statement = CCJSqlParserUtil.parse(eql);
            Assert.isTrue(statement instanceof Update, "eql 不是一个有效的 update 语句");

            Update update = (Update) statement;

            List<String> updateELs = parseUpdateELs(update);
            return queryByUpdate(update)
                    .stream()
                    .map(docH ->{
                        DocHV doc = docEngine.detail(docH.getDocId());
                        // 执行 update set
                        EvaluationContext context = spELManager.createContext(doc);
                        updateELs.forEach(el-> spELManager.invoke(el, context));

                        return doc;
                    })
                    .collect(Collectors.toList());
        } catch (JSQLParserException e) {
            throw new NkDefineException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public List<DocHV> doUpdateByEql(String eql, String optSource){

        try {
            Statement statement = CCJSqlParserUtil.parse(eql);
            Assert.isTrue(statement instanceof Update, "eql 不是一个有效的 update 语句");

            Update update = (Update) statement;

            List<String> updateELs = parseUpdateELs(update);
            return queryByUpdate(update)
                    .stream()
                    .map(docH ->
                        lockDocDo(docH.getDocId(),(docId)->{
                            DocHV doc = docEngine.detail(docId);
                            // 执行 update set
                            EvaluationContext context = spELManager.createContext(doc);
                            updateELs.forEach(el-> spELManager.invoke(el, context));

                            doc = execUpdate(doc, optSource);
                            doc.clearItemContent();
                            return doc;
                        })
                    )
                    .collect(Collectors.toList());
        } catch (JSQLParserException e) {
            throw new NkDefineException(e.getMessage());
        }
    }

    private String clearExpression(String name){
        if(name==null)
            return null;
        if(name.startsWith("\"") && name.endsWith("\"")){
            return name.substring(1,name.length()-1);
        }
        if(name.startsWith("`") && name.endsWith("`")){
            return name.substring(1,name.length()-1);
        }
        return name;
    }
}
