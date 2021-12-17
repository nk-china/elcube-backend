package cn.nkpro.elcube.mybatis.generator.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class WsDocPlugin extends PluginAdapter {

	public WsDocPlugin() {
	}

	public String chinaToUnicode(String str) {
		String result = "";
		for (int i = 0; i < str.length(); i++) {
			int chr1 = (char) str.charAt(i);
			if (chr1 >= 19968 && chr1 <= 171941) {// 汉字范围 \u4e00-\u9fa5 (中文)
				result += "\\u" + Integer.toHexString(chr1);
			} else {
				result += str.charAt(i);
			}
		}
		return result;
	}

	public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass,
			IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable,
			Plugin.ModelClassType modelClassType) {

		String type = getProperties().getProperty("annotationType","org.beanopen.fw.ws.annotation.CodeFieldNotes");

		System.out.println(type);

		try {
			String remark = introspectedColumn.getRemarks();
			if (remark == null) remark = "";
			method.addAnnotation("@"+type+"(\""
					+ new String(remark.getBytes("UTF-8"), "UTF-8") + "\")");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return true;
	}

	/**
	 * This plugin is always valid - no properties are required
	 */
	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}
}
