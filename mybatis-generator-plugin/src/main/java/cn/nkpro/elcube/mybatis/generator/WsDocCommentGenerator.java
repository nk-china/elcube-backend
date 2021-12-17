package cn.nkpro.elcube.mybatis.generator;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.internal.DefaultCommentGenerator;

public class WsDocCommentGenerator extends DefaultCommentGenerator {

	@Override
	public void addFieldComment(Field field, IntrospectedTable introspectedTable,
			IntrospectedColumn introspectedColumn) {
		if (introspectedColumn.getRemarks() != null && !introspectedColumn.getRemarks().equals("")) {
			field.addJavaDocLine("/**");
			field.addJavaDocLine(" * " + introspectedColumn.getRemarks());
			addJavadocTag(field, false);
			field.addJavaDocLine(" */");
		}
	}

	public void addGetterComment(Method method, IntrospectedTable introspectedTable,
			IntrospectedColumn introspectedColumn) {

		if (introspectedColumn.getRemarks() != null && !introspectedColumn.getRemarks().equals("")) {
			method.addJavaDocLine("/**");
			method.addJavaDocLine(" * 获取 " + introspectedColumn.getRemarks());
			method.addJavaDocLine(" *");
			method.addJavaDocLine(" * @return " + introspectedColumn.getRemarks());
			addJavadocTag(method, false);
			method.addJavaDocLine(" */");
		}
	}

	public void addSetterComment(Method method, IntrospectedTable introspectedTable,
			IntrospectedColumn introspectedColumn) {

		if (introspectedColumn.getRemarks() != null && !introspectedColumn.getRemarks().equals("")) {
			method.addJavaDocLine("/**");
			method.addJavaDocLine(" * 设置 " + introspectedColumn.getRemarks());
			method.addJavaDocLine(" *");
			method.addJavaDocLine(" * @return " + introspectedColumn.getRemarks());
			addJavadocTag(method, false);
			method.addJavaDocLine(" */");
		}
	}
}
