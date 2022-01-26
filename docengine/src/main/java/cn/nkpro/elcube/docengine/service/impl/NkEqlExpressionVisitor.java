package cn.nkpro.elcube.docengine.service.impl;

import cn.nkpro.elcube.exception.NkDefineException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.conditional.XorExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.List;

public class NkEqlExpressionVisitor implements ExpressionVisitor {

    private List<Expression> expressions;

    NkEqlExpressionVisitor(List<Expression> expressions) {
        this.expressions = expressions;
    }

    @Override
    public void visit(OrExpression orExpression) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(AndExpression andExpression) {
        andExpression.getLeftExpression().accept(this);
        andExpression.getRightExpression().accept(this);
    }

    @Override
    public void visit(EqualsTo equalsTo) {
        this.expressions.add(equalsTo);
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        this.expressions.add(notEqualsTo);
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        this.expressions.add(greaterThan);
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        this.expressions.add(greaterThanEquals);
    }

    @Override
    public void visit(MinorThan minorThan) {
        this.expressions.add(minorThan);
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        this.expressions.add(minorThanEquals);
    }

    @Override
    public void visit(LikeExpression likeExpression) {
        this.expressions.add(likeExpression);
    }

    @Override
    public void visit(Between between) {
        this.expressions.add(between);
    }

    @Override
    public void visit(InExpression inExpression) {
        this.expressions.add(inExpression);
    }

    /**
     * is null
     */
    @Override
    public void visit(IsNullExpression isNullExpression) {
        this.expressions.add(isNullExpression);
    }

    ////////////////
    ////////////////
    ////////////////
    ////////////////
    ////////////////
    ////////////////
    ////////////////
    ////////////////
    ////////////////

    /**
     * String
     */
    @Override
    public void visit(StringValue stringValue) {
        throw new NkDefineException("不支持的操作");
    }

    /**
     * Long
     */
    @Override
    public void visit(LongValue longValue) {
        throw new NkDefineException("不支持的操作");
    }

    /**
     * Double
     */
    @Override
    public void visit(DoubleValue doubleValue) {
        throw new NkDefineException("不支持的操作");
    }

    /**
     * 0xAF
     */
    @Override
    public void visit(HexValue hexValue) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(NullValue nullValue) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(TimeValue timeValue) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(DateValue dateValue) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(TimestampValue timestampValue) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(SignedExpression signedExpression) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(Division division) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(IntegerDivision integerDivision) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(Multiplication multiplication) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(Subtraction subtraction) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(FullTextSearch fullTextSearch) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(IsBooleanExpression isBooleanExpression) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(Column column) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(CaseExpression caseExpression) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(AnyComparisonExpression anyComparisonExpression) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(BitwiseXor bitwiseXor) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(CastExpression castExpression) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(AnalyticExpression analyticExpression) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(ExtractExpression extractExpression) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(IntervalExpression intervalExpression) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(TimeKeyExpression timeKeyExpression) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(DateTimeLiteralExpression dateTimeLiteralExpression) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(NextValExpression nextValExpression) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(CollateExpression collateExpression) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(SimilarToExpression similarToExpression) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(ArrayExpression arrayExpression) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(ArrayConstructor arrayConstructor) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(VariableAssignment variableAssignment) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(XMLSerializeExpr xmlSerializeExpr) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(TimezoneExpression timezoneExpression) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(JsonAggregateFunction jsonAggregateFunction) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(JsonFunction jsonFunction) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(ConnectByRootOperator connectByRootOperator) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(OracleNamedFunctionParameter oracleNamedFunctionParameter) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(BitwiseRightShift bitwiseRightShift) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(BitwiseLeftShift bitwiseLeftShift) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(Function function) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(OracleHierarchicalExpression oracleHierarchicalExpression) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(RegExpMatchOperator regExpMatchOperator) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(JsonExpression jsonExpression) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(JsonOperator jsonOperator) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(RegExpMySQLOperator regExpMySQLOperator) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(UserVariable userVariable) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(NumericBind numericBind) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(KeepExpression keepExpression) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(MySQLGroupConcat mySQLGroupConcat) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(ValueListExpression valueListExpression) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(RowConstructor rowConstructor) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(RowGetExpression rowGetExpression) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(OracleHint oracleHint) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(Modulo modulo) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(Concat concat) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(Matches matches) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(BitwiseAnd bitwiseAnd) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(BitwiseOr bitwiseOr) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(SubSelect subSelect) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(WhenClause whenClause) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(ExistsExpression existsExpression) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(JdbcParameter jdbcParameter) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(JdbcNamedParameter jdbcNamedParameter) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(Parenthesis parenthesis) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(Addition addition) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(NotExpression notExpression) {
        throw new NkDefineException("不支持的操作");
    }

    @Override
    public void visit(XorExpression xorExpression) {
        throw new NkDefineException("不支持的操作");
    }
}



//package cn.nkpro.elcube.docengine.service.impl;
//
//import cn.nkpro.elcube.exception.NkDefineException;
//import io.jsonwebtoken.lang.Assert;
//import net.sf.jsqlparser.expression.*;
//import net.sf.jsqlparser.expression.operators.arithmetic.*;
//import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
//import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
//import net.sf.jsqlparser.expression.operators.conditional.XorExpression;
//import net.sf.jsqlparser.expression.operators.relational.*;
//import net.sf.jsqlparser.schema.Column;
//import net.sf.jsqlparser.statement.select.SubSelect;
//
//import java.util.stream.Collectors;
//
//public class NkEqlExpressionVisitor implements ExpressionVisitor {
//
//    private NkDocFinder docFinder;
//
//    private StringBuilder whereBuilder;
//
//    public NkEqlExpressionVisitor(NkDocFinder docFinder) {
//        this.docFinder = docFinder;
//    }
//
//    @Override
//    public void visit(OrExpression orExpression) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(AndExpression andExpression) {
//        andExpression.getLeftExpression().accept(this);
//        andExpression.getRightExpression().accept(this);
//    }
//
//    /**
//     * =
//     */
//    @Override
//    public void visit(EqualsTo equalsTo) {
//        whereBuilder = new StringBuilder();
//        equalsTo.getLeftExpression().accept(this);
//        whereBuilder.append(' ')
//                .append(equalsTo.getStringExpression())
//                .append(' ');
//        equalsTo.getRightExpression().accept(this);
//        NkDocFinder.where.get().add(whereBuilder.toString());
//    }
//
//    private String columnWhere(Column column, String exp){
//        if(column.getFullyQualifiedName().startsWith("dynamic.")){
//            return "EXISTS (" +
//                    "\n         SELECT i.doc_id FROM nk_doc_i_index AS i " +
//                    "\n          WHERE i.doc_id = h.doc_id " +
//                    "\n            AND i.name   = '%s' " +
//                    "\n            AND i.number_value  >= ?" +
//                    "\n       )";
//        }else{
//            return column.getFullyQualifiedName() + exp;
//        }
//    }
//
//    /**
//     * <>
//     */
//    @Override
//    public void visit(NotEqualsTo notEqualsTo) {
//        whereBuilder = new StringBuilder();
//
//
//        where.get().add(String.format(
//                "EXISTS (" +
//                        "\n         SELECT i.doc_id FROM nk_doc_i_index AS i " +
//                        "\n          WHERE i.doc_id = h.doc_id " +
//                        "\n            AND i.name   = '%s' " +
//                        "\n            AND i.number_value  >= ?" +
//                        "\n       )",
//                key
//        ));
//
//        equalsTo.getLeftExpression().accept(this);
//        whereBuilder.append(' ')
//                .append(notEqualsTo.getStringExpression())
//                .append(' ');
//        notEqualsTo.getRightExpression().accept(this);
//        NkDocFinder.where.get().add(whereBuilder.toString());
//    }
//
//    @Override
//    public void visit(GreaterThan greaterThan) {
//        whereBuilder = new StringBuilder();
//        whereBuilder.append(greaterThan.getLeftExpression())
//                .append(' ')
//                .append(greaterThan.getStringExpression())
//                .append(' ')
//                .append('?');
//        greaterThan.getRightExpression().accept(this);
//        NkDocFinder.where.get().add(whereBuilder.toString());
//    }
//
//    @Override
//    public void visit(GreaterThanEquals greaterThanEquals) {
//        whereBuilder = new StringBuilder();
//        whereBuilder.append(greaterThanEquals.getLeftExpression())
//                .append(' ')
//                .append(greaterThanEquals.getStringExpression())
//                .append(' ')
//                .append('?');
//        greaterThanEquals.getRightExpression().accept(this);
//        NkDocFinder.where.get().add(whereBuilder.toString());
//    }
//
//    @Override
//    public void visit(MinorThan minorThan) {
//        whereBuilder = new StringBuilder();
//        whereBuilder.append(minorThan.getLeftExpression())
//                .append(' ')
//                .append(minorThan.getStringExpression())
//                .append(' ');
//        minorThan.getRightExpression().accept(this);
//        NkDocFinder.where.get().add(whereBuilder.toString());
//    }
//
//    @Override
//    public void visit(MinorThanEquals minorThanEquals) {
//        whereBuilder = new StringBuilder();
//        whereBuilder.append(minorThanEquals.getLeftExpression())
//                .append(' ')
//                .append(minorThanEquals.getStringExpression())
//                .append(' ')
//                .append('?');
//        minorThanEquals.getRightExpression().accept(this);
//        NkDocFinder.where.get().add(whereBuilder.toString());
//    }
//
//    @Override
//    public void visit(LikeExpression likeExpression) {
//        whereBuilder = new StringBuilder();
//        whereBuilder.append(likeExpression.getLeftExpression())
//                .append(' ')
//                .append(likeExpression.getStringExpression())
//                .append(' ')
//                .append('?');
//        likeExpression.getRightExpression().accept(this);
//        NkDocFinder.where.get().add(whereBuilder.toString());
//    }
//
//    @Override
//    public void visit(Between between) {
//        whereBuilder = new StringBuilder();
//        whereBuilder.append(between.getLeftExpression())
//                .append(' ')
//                .append("BETWEEN")
//                .append(' ')
//                .append('?')
//                .append(" AND ")
//                .append('?');
//        between.getBetweenExpressionStart().accept(this);
//        between.getBetweenExpressionEnd().accept(this);
//        NkDocFinder.where.get().add(whereBuilder.toString());
//    }
//
//    @Override
//    public void visit(InExpression inExpression) {
//
//        Assert.isTrue(inExpression.getRightItemsList() instanceof ExpressionList,"不支持的操作");
//
//        ExpressionList list = (ExpressionList) inExpression.getRightItemsList();
//
//        whereBuilder = new StringBuilder();
//        whereBuilder.append(inExpression.getLeftExpression())
//                .append(' ')
//                .append("IN(")
//                .append(list.getExpressions().stream().map(e->"?").collect(Collectors.joining(", ")))
//                .append(')');
//        NkDocFinder.where.get().add(whereBuilder.toString());
//
//        list.getExpressions().forEach(expression -> expression.accept(this));
//    }
//
//    /**
//     * is null
//     */
//    @Override
//    public void visit(IsNullExpression isNullExpression) {
//        NkDocFinder.where.get().add(isNullExpression.toString());
//    }
//
//    /**
//     * String
//     */
//    @Override
//    public void visit(StringValue stringValue) {
//        //whereBuilder.append('?');
//        NkDocFinder.args.get().add(stringValue.getValue());
//    }
//
//    /**
//     * Long
//     */
//    @Override
//    public void visit(LongValue longValue) {
//        //whereBuilder.append('?');
//        NkDocFinder.args.get().add(longValue.getValue());
//    }
//
//    /**
//     * Double
//     */
//    @Override
//    public void visit(DoubleValue doubleValue) {
//        //whereBuilder.append('?');
//        NkDocFinder.args.get().add(doubleValue.getValue());
//    }
//
//    /**
//     * 0xAF
//     */
//    @Override
//    public void visit(HexValue hexValue) {
//        //whereBuilder.append('?');
//        NkDocFinder.args.get().add(Long.valueOf(hexValue.getValue()));
//    }
//
//    ////////////////
//    ////////////////
//    ////////////////
//    ////////////////
//    ////////////////
//    ////////////////
//    ////////////////
//    ////////////////
//    ////////////////
//
//    @Override
//    public void visit(NullValue nullValue) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(TimeValue timeValue) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(DateValue dateValue) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(TimestampValue timestampValue) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(SignedExpression signedExpression) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(Division division) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(IntegerDivision integerDivision) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(Multiplication multiplication) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(Subtraction subtraction) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(FullTextSearch fullTextSearch) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(IsBooleanExpression isBooleanExpression) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(Column column) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(CaseExpression caseExpression) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(AnyComparisonExpression anyComparisonExpression) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(BitwiseXor bitwiseXor) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(CastExpression castExpression) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(AnalyticExpression analyticExpression) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(ExtractExpression extractExpression) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(IntervalExpression intervalExpression) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(TimeKeyExpression timeKeyExpression) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(DateTimeLiteralExpression dateTimeLiteralExpression) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(NextValExpression nextValExpression) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(CollateExpression collateExpression) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(SimilarToExpression similarToExpression) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(ArrayExpression arrayExpression) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(ArrayConstructor arrayConstructor) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(VariableAssignment variableAssignment) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(XMLSerializeExpr xmlSerializeExpr) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(TimezoneExpression timezoneExpression) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(JsonAggregateFunction jsonAggregateFunction) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(JsonFunction jsonFunction) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(ConnectByRootOperator connectByRootOperator) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(OracleNamedFunctionParameter oracleNamedFunctionParameter) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(BitwiseRightShift bitwiseRightShift) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(BitwiseLeftShift bitwiseLeftShift) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(Function function) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(OracleHierarchicalExpression oracleHierarchicalExpression) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(RegExpMatchOperator regExpMatchOperator) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(JsonExpression jsonExpression) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(JsonOperator jsonOperator) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(RegExpMySQLOperator regExpMySQLOperator) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(UserVariable userVariable) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(NumericBind numericBind) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(KeepExpression keepExpression) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(MySQLGroupConcat mySQLGroupConcat) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(ValueListExpression valueListExpression) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(RowConstructor rowConstructor) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(RowGetExpression rowGetExpression) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(OracleHint oracleHint) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(Modulo modulo) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(Concat concat) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(Matches matches) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(BitwiseAnd bitwiseAnd) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(BitwiseOr bitwiseOr) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(SubSelect subSelect) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(WhenClause whenClause) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(ExistsExpression existsExpression) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(JdbcParameter jdbcParameter) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(JdbcNamedParameter jdbcNamedParameter) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(Parenthesis parenthesis) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(Addition addition) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(NotExpression notExpression) {
//        throw new NkDefineException("不支持的操作");
//    }
//
//    @Override
//    public void visit(XorExpression xorExpression) {
//        throw new NkDefineException("不支持的操作");
//    }
//}
