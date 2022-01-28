package cn.nkpro.elcube.docengine.service.impl;

import cn.nkpro.elcube.data.mybatis.NkMybatisProperties;
import cn.nkpro.elcube.data.mybatis.pagination.dialect.Dialect;
import cn.nkpro.elcube.docengine.gen.DocH;
import cn.nkpro.elcube.exception.NkDefineException;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.relational.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@SuppressWarnings("unused")
public class NkDocFinder implements InitializingBean {

    @Autowired@SuppressWarnings("all")
    private JdbcTemplate jdbcTemplate;

    @Autowired@SuppressWarnings("all")
    private NkMybatisProperties properties;

    private Dialect dialect;

    private static ThreadLocal<List<String>> where = new ThreadLocal<>();
    private static ThreadLocal<List<Object>> args  = new ThreadLocal<>();
    private static ThreadLocal<List<String>> order = new ThreadLocal<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        dialect = (Dialect) Class.forName(properties.getDialect()).getConstructor().newInstance();
    }

    public NkDocFinder createFinder(String... value){

        args.set(new ArrayList<>());
        where.set(new ArrayList<>());
        order.set(new ArrayList<>());

        if(value.length==1&&StringUtils.isNotBlank(value[0])){
            where.get().add("h.doc_type = ?");
            args.get().add(value[0]);
        }else if(value.length>1){
            where.get().add(
                String.format("h.doc_type IN (%s)",
                Arrays.stream(value).map(i->"?")
                    .collect(Collectors.joining(", ")))
            );
            args.get().addAll(Arrays.asList(value));
        }
        return this;
    }

    public NkDocFinder docTypeEquals(String value){
        where.get().add("h.doc_type = ?");
        args.get().add(value);
        return this;
    }

    public NkDocFinder dynamicEquals(String key, String value){
        where.get().add(String.format(
                "EXISTS (" +
                        "\n         SELECT i.doc_id FROM nk_doc_i_index AS i " +
                        "\n          WHERE i.doc_id = h.doc_id " +
                        "\n            AND i.name   = '%s' " +
                        "\n            AND i.value  = ?" +
                        "\n       )",
                key
        ));
        args.get().add(value);
        return this;
    }

    public NkDocFinder dynamicEquals(Map map){
        map.forEach((key,value)->{
            where.get().add(String.format(
                    "EXISTS (" +
                            "\n         SELECT i.doc_id FROM nk_doc_i_index AS i " +
                            "\n          WHERE i.doc_id = h.doc_id " +
                            "\n            AND i.name   = '%s' " +
                            "\n            AND i.value  = ?" +
                            "\n       )",
                    key
            ));
            args.get().add(value);
        });
        return this;
    }

    public NkDocFinder dynamicEquals(String key, Number value){
        where.get().add(String.format(
                "EXISTS (" +
                        "\n         SELECT i.doc_id FROM nk_doc_i_index AS i " +
                        "\n          WHERE i.doc_id = h.doc_id " +
                        "\n            AND i.name   = '%s' " +
                        "\n            AND i.value  = ?" +
                        "\n       )",
                key
        ));
        args.get().add(value);
        return this;
    }
    public NkDocFinder dynamicStartWiths(String key, String value){
        where.get().add(String.format(
                "EXISTS (" +
                        "\n         SELECT i.doc_id FROM nk_doc_i_index AS i " +
                        "\n          WHERE i.doc_id = h.doc_id " +
                        "\n            AND i.name   = '%s' " +
                        "\n            AND i.value  like ?" +
                        "\n       )",
                key
        ));
        args.get().add(value+"%");
        return this;
    }
    
    public NkDocFinder dynamicGreater(String key, Number value){
        where.get().add(String.format(
                "EXISTS (" +
                        "\n         SELECT i.doc_id FROM nk_doc_i_index AS i " +
                        "\n          WHERE i.doc_id = h.doc_id " +
                        "\n            AND i.name   = '%s' " +
                        "\n            AND i.number_value  > ?" +
                        "\n       )",
                key
        ));
        args.get().add(value);
        return this;
    }
    public NkDocFinder dynamicGreaterEquals(String key, Number value){
        where.get().add(String.format(
                "EXISTS (" +
                        "\n         SELECT i.doc_id FROM nk_doc_i_index AS i " +
                        "\n          WHERE i.doc_id = h.doc_id " +
                        "\n            AND i.name   = '%s' " +
                        "\n            AND i.number_value  >= ?" +
                        "\n       )",
                key
        ));
        args.get().add(value);
        return this;
    }
    public NkDocFinder dynamicLess(String key, Number value){
        where.get().add(String.format(
                "EXISTS (" +
                        "\n         SELECT i.doc_id FROM nk_doc_i_index AS i " +
                        "\n          WHERE i.doc_id = h.doc_id " +
                        "\n            AND i.name   = '%s' " +
                        "\n            AND i.number_value  < ?" +
                        "\n       )",
                key
        ));
        args.get().add(value);
        return this;
    }
    public NkDocFinder dynamicLessEquals(String key, Number value){
        where.get().add(String.format(
                "EXISTS (" +
                        "\n         SELECT i.doc_id FROM nk_doc_i_index AS i " +
                        "\n          WHERE i.doc_id = h.doc_id " +
                        "\n            AND i.name   = '%s' " +
                        "\n            AND i.number_value  <= ?" +
                        "\n       )",
                key
        ));
        args.get().add(value);
        return this;
    }
    public NkDocFinder dynamicBetween(String key, Number begin, Number end){
        where.get().add(String.format(
                "EXISTS (" +
                        "\n         SELECT i.doc_id FROM nk_doc_i_index AS i " +
                        "\n          WHERE i.doc_id = h.doc_id " +
                        "\n            AND i.name   = '%s' " +
                        "\n            AND i.number_value  BETWEEN ? AND ?" +
                        "\n       )",
                key
        ));
        args.get().add(begin);
        args.get().add(end);
        return this;
    }
    public NkDocFinder dynamicIn(String key, String... value){
        return dynamicIn(key,Arrays.asList(value));
    }
    public NkDocFinder dynamicIn(String key, Number... value){
        return dynamicIn(key,Arrays.asList(value));
    }
    public NkDocFinder dynamicIn(String key, List<?> value){

        Assert.notEmpty(value, "IN 查询 条件不能为空");

        String collect = value.stream().map(i -> "?").collect(Collectors.joining(", "));

        where.get().add(String.format(
                "EXISTS (" +
                        "\n         SELECT i.doc_id FROM nk_doc_i_index AS i " +
                        "\n          WHERE i.doc_id = h.doc_id " +
                        "\n            AND i.name   = '%s' " +
                        "\n            AND i.value  IN (%s)" +
                        "\n       )",
                key,
                collect
        ));
        args.get().addAll(value);
        return this;
    }

    public NkDocFinder dynamicExists(String key){
        where.get().add(String.format(
                "EXISTS (" +
                        "\n         SELECT i.doc_id FROM nk_doc_i_index AS i " +
                        "\n          WHERE i.doc_id = h.doc_id " +
                        "\n            AND i.name   = '%s' " +
                        "\n       )",
                key
        ));
        return this;
    }

    public NkDocFinder dynamicNotExists(String key){
        where.get().add(String.format(
                "NOT EXISTS (" +
                        "\n         SELECT i.doc_id FROM nk_doc_i_index AS i " +
                        "\n          WHERE i.doc_id = h.doc_id " +
                        "\n            AND i.name   = '%s' " +
                        "\n       )",
                key
        ));
        return this;
    }

    public NkDocFinder docNumberEquals(String value){
        where.get().add("h.doc_number = ?");
        args.get().add(value);
        return this;
    }
    public NkDocFinder docStateEquals(String value){
        where.get().add("h.doc_state = ?");
        args.get().add(value);
        return this;
    }
    public NkDocFinder docStateIn(String... value){
        Assert.notEmpty(value,"state is not empty");

        String collect = Arrays.stream(value).map(i -> "?").collect(Collectors.joining(", "));
        where.get().add(String.format("h.doc_state IN (%s)", collect));
        args.get().addAll(Arrays.asList(value));
        return this;
    }
    public NkDocFinder prevIdEquals(String value){
        where.get().add("h.pre_doc_id = ?");
        args.get().add(value);
        return this;
    }
    public NkDocFinder partnerIdEquals(String value){
        where.get().add("h.partner_id = ?");
        args.get().add(value);
        return this;
    }
    public NkDocFinder refObjectIdEqual(String value){
        where.get().add("h.ref_object_id = ?");
        args.get().add(value);
        return this;
    }
    public NkDocFinder businessKeyEquals(String value){
        where.get().add("h.business_key = ?");
        args.get().add(value);
        return this;
    }
    public NkDocFinder businessKeyStartWiths(String value){
        where.get().add("h.business_key like ?");
        args.get().add(value+"%");
        return this;
    }
    public NkDocFinder docNameStartWiths(String value){
        where.get().add("h.doc_name like ?");
        args.get().add(value+"%");
        return this;
    }
    public NkDocFinder processInstanceIdEquals(String value){
        where.get().add("h.process_instance_id = ?");
        args.get().add(value);
        return this;
    }
    public NkDocFinder createdTimeGreaterEquals(String value){
        where.get().add("h.created_time >= ?");
        args.get().add(value);
        return this;
    }
    public NkDocFinder createdTimeLessEquals(String value){
        where.get().add("h.created_time <= ?");
        args.get().add(value);
        return this;
    }
    public NkDocFinder createdTimeBetween(Long begin, Long end){
        where.get().add("h.between ? AND ?");
        args.get().add(begin);
        args.get().add(end);
        return this;
    }

    public NkDocFinder orderByDocType(String key){
        order.get().add("doc_type");
        return this;
    }
    public NkDocFinder orderByDocTypeDesc(String key){
        order.get().add("doc_type DESC");
        return this;
    }
    public NkDocFinder orderByDocNumber(String key){
        order.get().add("doc_number");
        return this;
    }
    public NkDocFinder orderByDocNumberDesc(String key){
        order.get().add("doc_number DESC");
        return this;
    }
    public NkDocFinder orderByCreatedTime(String key){
        order.get().add("created_time");
        return this;
    }
    public NkDocFinder orderByCreatedTimeDesc(String key){
        order.get().add("created_time DESC");
        return this;
    }
    public NkDocFinder orderByUpdatedTime(String key){
        order.get().add("updated_time");
        return this;
    }
    public NkDocFinder orderByUpdatedTimeDesc(String key){
        order.get().add("updated_time DESC");
        return this;
    }
    public NkDocFinder orderBy(String key){
        order.get().add(key);
        return this;
    }
    public NkDocFinder orderByDesc(String key){
        order.get().add(key + " DESC");
        return this;
    }

    private String build(Integer offset, Integer limit){
        String sql = String.format(
                "SELECT doc_id," +
                "\n       classify," +
                "\n       def_version," +
                "\n       doc_type," +
                "\n       doc_name," +
                "\n       doc_desc," +
                "\n       doc_number," +
                "\n       doc_state," +
                "\n       doc_tags," +
                "\n       pre_doc_id," +
                "\n       partner_id," +
                "\n       identification," +
                "\n       ref_object_id," +
                "\n       business_key," +
                "\n       process_instance_id," +
                "\n       created_time," +
                "\n       updated_time" +
                "\n  FROM nk_doc_h AS h" +
                "%s " +
                "%s ",
                where.get().isEmpty()?
                    StringUtils.EMPTY:
                    where.get().stream().collect(Collectors.joining("\n   AND ","\n WHERE ",StringUtils.EMPTY)),
                order.get().isEmpty()?
                    StringUtils.EMPTY:
                    order.get().stream().collect(Collectors.joining(", ","\n ORDER BY ", StringUtils.EMPTY))
        );

        if(offset!=null && limit!=null){
            sql = dialect.getLimitString(sql, offset, limit);
        }

        if(log.isInfoEnabled())
            log.info(
                    "执行DocFinder查找单据: \n"+
                    sql+
                    "\nWith Parameters: "+
                    args.get().stream().map(i->i==null?null:i.toString()).collect(Collectors.joining(", "))
            );

        return sql;
    }


    private String buildCount(){
        String sql = String.format(
                "SELECT COUNT(1) " +
                        "\n  FROM nk_doc_h AS h" +
                        "\n WHERE %s " +
                        "\n %s",
                String.join("\n   AND ", where.get()),
                order.get().isEmpty()?
                        StringUtils.EMPTY:
                        order.get().stream().collect(Collectors.joining(", ","ORDER BY ", StringUtils.EMPTY))
        );

        if(log.isInfoEnabled())
            log.info(
                    "执行DocFinder查找单据: \n"+
                            sql+
                            "\nWith Parameters: "+
                            args.get().stream().map(i->i==null?null:i.toString()).collect(Collectors.joining(", "))
            );

        return sql;
    }

    private RowMapper<DocH> rowMapper = (resultSet, i) -> {
        DocH doc = new DocH();
        doc.setDocId(resultSet.getString("DOC_ID"));
        doc.setClassify(resultSet.getString("CLASSIFY"));
        doc.setDefVersion(resultSet.getString("DEF_VERSION"));
        doc.setDocType(resultSet.getString("DOC_TYPE"));
        doc.setDocName(resultSet.getString("DOC_NAME"));
        doc.setDocDesc(resultSet.getString("DOC_DESC"));
        doc.setDocNumber(resultSet.getString("DOC_NUMBER"));
        doc.setDocState(resultSet.getString("DOC_STATE"));
        doc.setDocTags(resultSet.getString("DOC_TAGS"));
        doc.setPreDocId(resultSet.getString("PRE_DOC_ID"));
        doc.setPartnerId(resultSet.getString("PARTNER_ID"));
        doc.setIdentification(resultSet.getString("IDENTIFICATION"));
        doc.setRefObjectId(resultSet.getString("REF_OBJECT_ID"));
        doc.setBusinessKey(resultSet.getString("BUSINESS_KEY"));
        doc.setProcessInstanceId(resultSet.getString("PROCESS_INSTANCE_ID"));
        doc.setCreatedTime(resultSet.getLong("CREATED_TIME"));
        doc.setUpdatedTime(resultSet.getLong("UPDATED_TIME"));
        return doc;
    };

    public List<DocH> listResult(int offset, int limit){
        try{
            return jdbcTemplate.query(build(offset, limit), args.get().toArray(), rowMapper);
        }finally {
            args.remove();
            where.remove();
            order.remove();
        }
    }

    public List<DocH> listResult(){
        try{
            return jdbcTemplate.query(build(null,null), args.get().toArray(), rowMapper);
        }finally {
            args.remove();
            where.remove();
            order.remove();
        }
    }

    public DocH singleResult(){
        try{
            return jdbcTemplate.queryForObject(build(0,1), args.get().toArray(), rowMapper);
        }finally {
            args.remove();
            where.remove();
            order.remove();
        }
    }

    public long countResult(){
        try{
            Long count = jdbcTemplate.queryForObject(buildCount(), args.get().toArray(), Long.class);
            return count == null ? 0 : count;
        }finally {
            args.remove();
            where.remove();
            order.remove();
        }
    }

    public NkDocFinder where(String condWhere, Object... condArgs){
        Assert.hasLength(condWhere,"where condition is not empty");
        where.get().add("h."+condWhere);
        if(condArgs!=null && condArgs.length>0)
            Arrays.stream(condArgs).forEach(arg-> args.get().add(arg));
        return this;
    }

    public NkDocFinder expression(Expression exp) {

        if(exp!=null){

            List<Expression> expressions = new ArrayList<>();
            exp.accept(new NkEqlExpressionVisitor(expressions));


            expressions.forEach(expression -> {
                    if(expression instanceof LikeExpression || expression instanceof ComparisonOperator){
                        Object value = getExpressionValue(((BinaryExpression) expression).getRightExpression());
                        args.get().add(value);

                        String string = value instanceof String ? "value" : "number_value";
                        Expression leftExpression = ((BinaryExpression) expression).getLeftExpression();
                        String dynamicColumn = convertPropertyToDynamic(leftExpression.toString());
                        if(dynamicColumn!=null){
                            where.get().add(String.format(
                                    "EXISTS (" +
                                            "\n         SELECT i.doc_id FROM nk_doc_i_index AS i " +
                                            "\n          WHERE i.doc_id = h.doc_id " +
                                            "\n            AND i.name   = '%s' " +
                                            "\n            AND i.%s  >= ?" +
                                            "\n       )",
                                    dynamicColumn,
                                    string
                            ));
                        }else{
                            where.get().add(
                                    String.format(
                                            "%s %s ?",
                                            convertPropertyToColumn(leftExpression.toString()),
                                            ((BinaryExpression) expression).getStringExpression()
                                    )
                            );
                        }

                    }else if(expression instanceof Between){

                        Object start = getExpressionValue(((Between) expression).getBetweenExpressionStart());
                        Object end   = getExpressionValue(((Between) expression).getBetweenExpressionEnd());
                        args.get().add(start);
                        args.get().add(end);

                        String string = start instanceof String ? "value" : "number_value";
                        Expression leftExpression = ((Between) expression).getLeftExpression();
                        String dynamicColumn = convertPropertyToDynamic(leftExpression.toString());
                        if(dynamicColumn!=null){
                            where.get().add(String.format(
                                    "EXISTS (" +
                                            "\n         SELECT i.doc_id FROM nk_doc_i_index AS i " +
                                            "\n          WHERE i.doc_id = h.doc_id " +
                                            "\n            AND i.name   = '%s' " +
                                            "\n            AND i.%s BETWEEN ? AND ?" +
                                            "\n       )",
                                    dynamicColumn,
                                    string
                            ));
                        }else{
                            where.get().add(
                                    String.format(
                                            "%s BETWEEN ? AND ?",
                                            convertPropertyToColumn(leftExpression.toString())
                                    )
                            );
                        }

                    }else if(expression instanceof InExpression){

                        ExpressionList list = (ExpressionList) ((InExpression) expression).getRightItemsList();
                        assert list.getExpressions().size() > 0;

                        List<Object> values = list.getExpressions()
                                .stream()
                                .map(this::getExpressionValue)
                                .collect(Collectors.toList());


                        values.forEach(v->args.get().add(v));

                        String string = values.get(0) instanceof String ? "value" : "number_value";
                        Expression leftExpression = ((InExpression) expression).getLeftExpression();
                        String dynamicColumn = convertPropertyToDynamic(leftExpression.toString());
                        if(dynamicColumn!=null){
                            where.get().add(String.format(
                                    "EXISTS (" +
                                            "\n         SELECT i.doc_id FROM nk_doc_i_index AS i " +
                                            "\n          WHERE i.doc_id = h.doc_id " +
                                            "\n            AND i.name   = '%s' " +
                                            "\n            AND i.%s IN (%s)" +
                                            "\n       )",
                                    dynamicColumn,
                                    string,
                                    list.getExpressions().stream().map(e->"?").collect(Collectors.joining(", "))
                            ));
                        }else{
                            where.get().add(
                                    String.format(
                                            "%s IN (%s)",
                                            convertPropertyToColumn(leftExpression.toString()),
                                            list.getExpressions().stream().map(e->"?").collect(Collectors.joining(", "))
                                    )
                            );
                        }

                    }else if(expression instanceof IsNullExpression){
                        Expression leftExpression = ((IsNullExpression) expression).getLeftExpression();
                        String dynamicColumn = convertPropertyToDynamic(leftExpression.toString());
                        if(dynamicColumn!=null){
                            if(((IsNullExpression) expression).isNot()){
                                where.get().add(String.format(
                                        "EXISTS (" +
                                                "\n         SELECT i.doc_id FROM nk_doc_i_index AS i " +
                                                "\n          WHERE i.doc_id = h.doc_id " +
                                                "\n            AND i.name   = '%s' " +
                                                "\n       )",
                                        dynamicColumn
                                ));
                            }else{
                                where.get().add(String.format(
                                        "NOT EXISTS (" +
                                                "\n         SELECT i.doc_id FROM nk_doc_i_index AS i " +
                                                "\n          WHERE i.doc_id = h.doc_id " +
                                                "\n            AND i.name   = '%s' " +
                                                "\n       )",
                                        dynamicColumn
                                ));
                            }
                        }else{
                            if(((IsNullExpression) expression).isNot()){
                                where.get().add(
                                        String.format(
                                                "%s IS NOT NULL",
                                                convertPropertyToColumn(leftExpression.toString())
                                        )
                                );
                            }else{
                                where.get().add(
                                        String.format(
                                                "%s IS NULL",
                                                convertPropertyToColumn(leftExpression.toString())
                                        )
                                );
                            }
                        }
                    }else{
                        throw new NkDefineException("不支持的操作");
                    }
                });
        }
        return this;
    }

    private Object getExpressionValue(Expression expression){
        if(expression instanceof StringValue){
            return ((StringValue) expression).getValue();
        }
        if(expression instanceof LongValue){
            return ((LongValue) expression).getValue();
        }
        if(expression instanceof DoubleValue){
            return ((DoubleValue) expression).getValue();
        }
        if(expression instanceof HexValue){
            return Long.parseLong(((HexValue) expression).getValue().substring(2),16);
        }
        throw new NkDefineException("不支持的操作");
    }

    private String convertPropertyToColumn(String property){
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<property.length();i++){
            char c = property.charAt(i);
            if(Character.isUpperCase(c)){
                builder.append('_');
                builder.append(Character.toLowerCase(c));
            }else{
                builder.append(c);
            }
        }
        return builder.toString();
    }

    private String convertPropertyToDynamic(String property){
        if(property.toUpperCase().startsWith("DYNAMIC.")){
            return property.substring(8);
        }
        if(property.toUpperCase().startsWith("DY.")){
            return property.substring(3);
        }
        if(property.toUpperCase().startsWith("D.")){
            return property.substring(2);
        }
        return null;
    }
}
