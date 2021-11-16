package cn.nkpro.ts5.co.query.model;

import cn.nkpro.ts5.basic.Keep;
import cn.nkpro.ts5.exception.NkDefineException;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Keep
@Data
public class DataQueryRequest {
    private List<String> sqlList;
    private List<Select> selects;
    private Integer from = 0;
    private Integer rows = 100;
    private JSONObject conditions;
    private DataQueryDrill drill;

    public void setSql(String sql){
        this.sqlList = Collections.singletonList(sql);
    }

    public static DataQueryRequest fromSql(String sql){
        DataQueryRequest sqlSearchRequest = new DataQueryRequest();
        sqlSearchRequest.setSqlList(Collections.singletonList(sql));
        return sqlSearchRequest;
    }

    public DataQueryRequest parse(){
        this.selects = new ArrayList<>();
        this.sqlList = sqlList.stream().map(sql -> {
            try {
                Select select = (Select) CCJSqlParserUtil.parse(sql);
                selects.add(select);

                if(getDrill()!=null&& StringUtils.isNotBlank(getDrill().getFrom())) {

                    PlainSelect selectBody = (PlainSelect) select.getSelectBody();

                    SelectExpressionItem selectItem = (SelectExpressionItem) selectBody.getSelectItems()
                            .stream()
                            .filter(s -> {
                                if (s instanceof SelectExpressionItem) {
                                    SelectExpressionItem expressionItem = (SelectExpressionItem) s;
                                    String alias =
                                            expressionItem.getAlias() != null ?
                                                    expressionItem.getAlias().getName() :
                                                    expressionItem.getExpression().toString();

                                    alias = alias.replaceAll("(^\")|(\"$)", "");
                                    return StringUtils.equals(alias, getDrill().getFrom());
                                }
                                return false;
                            }).findFirst().orElse(null);

                    if (selectItem == null) {
                        throw new NkDefineException(String.format("下钻列[%s]不存在", getDrill().getFrom()));
                    }

                    if (!(selectItem.getExpression() instanceof Column)) {
                        throw new NkDefineException(String.format("下钻列[%s]不支持", getDrill().getFrom()));
                    }


                    String alias =
                            selectItem.getAlias() != null ?
                                    selectItem.getAlias().getName() :
                                    selectItem.getExpression().toString();

                    // 移除select字段
                    selectBody.getSelectItems().remove(selectItem);

                    // 移除group by字段
                    selectBody.getGroupBy().getGroupByExpressionList().getExpressions()
                            .removeIf(expression -> expression instanceof Column &&
                                    StringUtils.equals(((Column) expression).getColumnName(), alias));

                    Column columnTo = new Column(getDrill().getTo());
                    // 添加select字段
                    selectBody.getSelectItems().add(new SelectExpressionItem(columnTo).withAlias(new Alias(alias)));

                    // 添加group by字段
                    selectBody.getGroupBy().addGroupByExpressions(columnTo);

                    // 添加fromValue 条件
                    EqualsTo equalsTo = new EqualsTo(selectItem.getExpression(), new StringValue((String) getDrill().getFromValue()));
                    if (selectBody.getWhere() != null) {
                        selectBody.setWhere(new AndExpression(equalsTo, selectBody.getWhere()));
                    } else {
                        selectBody.setWhere(equalsTo);
                    }

                    if (selectBody.getGroupBy().getGroupByExpressionList().getExpressions().isEmpty()) {
                        selectBody.setGroupByElement(null);
                    }
                    return selectBody.toString();
                }
                return sql;

            } catch (JSQLParserException e) {
                throw new NkDefineException("下钻发生错误："+e.getMessage());
            }
        }).collect(Collectors.toList());

        return this;
    }
}