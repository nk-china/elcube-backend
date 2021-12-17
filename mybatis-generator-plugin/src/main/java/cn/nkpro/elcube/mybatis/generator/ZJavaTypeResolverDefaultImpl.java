package cn.nkpro.elcube.mybatis.generator;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ZJavaTypeResolverDefaultImpl extends JavaTypeResolverDefaultImpl {

    private Map<Integer, JdbcTypeInformation> typeMap = new HashMap();

    public ZJavaTypeResolverDefaultImpl() {
        this.typeMap.put(2003,  new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("ARRAY",       new FullyQualifiedJavaType(Object.class.getName())));
        this.typeMap.put(-5,    new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("BIGINT",      new FullyQualifiedJavaType(Long.class.getName())));
        this.typeMap.put(-2,    new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("BINARY",      new FullyQualifiedJavaType("byte[]")));
        this.typeMap.put(-7,    new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("BIT",         new FullyQualifiedJavaType(Boolean.class.getName())));
        this.typeMap.put(2004,  new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("BLOB",        new FullyQualifiedJavaType("byte[]")));
        this.typeMap.put(16,    new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("BOOLEAN",     new FullyQualifiedJavaType(Boolean.class.getName())));
        this.typeMap.put(1,     new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("CHAR",        new FullyQualifiedJavaType(String.class.getName())));
        this.typeMap.put(2005,  new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("CLOB",        new FullyQualifiedJavaType(String.class.getName())));
        this.typeMap.put(70,    new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("DATALINK",    new FullyQualifiedJavaType(Object.class.getName())));
        this.typeMap.put(91,    new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("DATE",        new FullyQualifiedJavaType(Date.class.getName())));
        this.typeMap.put(2001,  new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("DISTINCT",    new FullyQualifiedJavaType(Object.class.getName())));
        this.typeMap.put(8,     new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("DOUBLE",      new FullyQualifiedJavaType(Double.class.getName())));
        this.typeMap.put(6,     new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("FLOAT",       new FullyQualifiedJavaType(Double.class.getName())));
        this.typeMap.put(4,     new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("INTEGER",     new FullyQualifiedJavaType(Integer.class.getName())));
        this.typeMap.put(2000,  new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("JAVA_OBJECT", new FullyQualifiedJavaType(Object.class.getName())));
        this.typeMap.put(-4,    new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("LONGVARBINARY", new FullyQualifiedJavaType("byte[]")));
        this.typeMap.put(-1,    new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("LONGVARCHAR", new FullyQualifiedJavaType(String.class.getName())));
        this.typeMap.put(-15,   new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("NCHAR",       new FullyQualifiedJavaType(String.class.getName())));
        this.typeMap.put(2011,  new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("NCLOB",       new FullyQualifiedJavaType(String.class.getName())));
        this.typeMap.put(-9,    new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("NVARCHAR",    new FullyQualifiedJavaType(String.class.getName())));
        this.typeMap.put(0,     new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("NULL",        new FullyQualifiedJavaType(Object.class.getName())));
        this.typeMap.put(1111,  new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("OTHER",       new FullyQualifiedJavaType(Object.class.getName())));
        this.typeMap.put(7,     new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("REAL",        new FullyQualifiedJavaType(Float.class.getName())));
        this.typeMap.put(2006,  new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("REF",         new FullyQualifiedJavaType(Object.class.getName())));
        this.typeMap.put(5,     new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("SMALLINT",    new FullyQualifiedJavaType(Short.class.getName())));
        this.typeMap.put(2002,  new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("STRUCT",      new FullyQualifiedJavaType(Object.class.getName())));
        this.typeMap.put(92,    new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("TIME",        new FullyQualifiedJavaType(Date.class.getName())));
        this.typeMap.put(93,    new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("TIMESTAMP",   new FullyQualifiedJavaType(Date.class.getName())));
        this.typeMap.put(-6,    new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("TINYINT",     new FullyQualifiedJavaType(Byte.class.getName())));
        this.typeMap.put(-3,    new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("VARBINARY",   new FullyQualifiedJavaType("byte[]")));
        this.typeMap.put(12,    new ZJavaTypeResolverDefaultImpl.JdbcTypeInformation("VARCHAR",     new FullyQualifiedJavaType(String.class.getName())));
    }


    public FullyQualifiedJavaType calculateJavaType(IntrospectedColumn introspectedColumn) {
        ZJavaTypeResolverDefaultImpl.JdbcTypeInformation jdbcTypeInformation = (ZJavaTypeResolverDefaultImpl.JdbcTypeInformation)this.typeMap.get(introspectedColumn.getJdbcType());
        FullyQualifiedJavaType answer;
        if (jdbcTypeInformation == null) {
            switch(introspectedColumn.getJdbcType()) {
                case 2:
                case 3:
                    if (introspectedColumn.getScale() <= 0 && introspectedColumn.getLength() <= 20 && !this.forceBigDecimals) {
                        if (introspectedColumn.getLength() > 9) {
                            answer = new FullyQualifiedJavaType(Long.class.getName());
                        } else if (introspectedColumn.getLength() > 4) {
                            answer = new FullyQualifiedJavaType(Integer.class.getName());
                        } else {
                            answer = new FullyQualifiedJavaType(Integer.class.getName());
                        }
                    } else {
                        answer = new FullyQualifiedJavaType(Double.class.getName());
                    }
                    break;
                default:
                    answer = null;
            }
        } else {
            answer = jdbcTypeInformation.getFullyQualifiedJavaType();
        }

        return answer;
    }

    public String calculateJdbcTypeName(IntrospectedColumn introspectedColumn) {
        ZJavaTypeResolverDefaultImpl.JdbcTypeInformation jdbcTypeInformation = (ZJavaTypeResolverDefaultImpl.JdbcTypeInformation)this.typeMap.get(introspectedColumn.getJdbcType());
        String answer;
        if (jdbcTypeInformation == null) {
            switch(introspectedColumn.getJdbcType()) {
                case 2:
                    answer = "NUMERIC";
                    break;
                case 3:
                    answer = "DECIMAL";
                    break;
                default:
                    answer = null;
            }
        } else {
            answer = jdbcTypeInformation.getJdbcTypeName();
        }

        return answer;
    }

    private static class JdbcTypeInformation {
        private String jdbcTypeName;
        private FullyQualifiedJavaType fullyQualifiedJavaType;

        public JdbcTypeInformation(String jdbcTypeName, FullyQualifiedJavaType fullyQualifiedJavaType) {
            this.jdbcTypeName = jdbcTypeName;
            this.fullyQualifiedJavaType = fullyQualifiedJavaType;
        }

        public String getJdbcTypeName() {
            return this.jdbcTypeName;
        }

        public FullyQualifiedJavaType getFullyQualifiedJavaType() {
            return this.fullyQualifiedJavaType;
        }
    }
}
