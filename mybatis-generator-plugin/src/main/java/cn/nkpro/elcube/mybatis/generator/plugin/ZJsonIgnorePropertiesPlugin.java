package cn.nkpro.elcube.mybatis.generator.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

public class ZJsonIgnorePropertiesPlugin extends PluginAdapter {

	public ZJsonIgnorePropertiesPlugin() {
	}
	
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable) {

    	topLevelClass.addImportedType("com.fasterxml.jackson.annotation.JsonIgnoreProperties");
    	topLevelClass.addImportedType("com.fasterxml.jackson.annotation.JsonInclude");

    	topLevelClass.addAnnotation("@JsonIgnoreProperties(ignoreUnknown = true)");
    	topLevelClass.addAnnotation("@JsonInclude(JsonInclude.Include.NON_NULL)");
        return true;
    }

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}
}
