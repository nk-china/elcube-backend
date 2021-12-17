package cn.nkpro.elcube.mybatis.generator.plugin;

import cn.nkpro.elcube.mybatis.generator.javamapper.SelectByExampleWithBLOBsSupportRowBoundsMethodGenerator;
import cn.nkpro.elcube.mybatis.generator.javamapper.SelectByExampleWithoutBLOBsSupportRowBoundsMethodGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;

import java.util.List;

public class MySQLPaginationPlugin2 extends PluginAdapter {
	
    public MySQLPaginationPlugin2(){}

    public boolean clientGenerated(Interface interfaze,
            TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable) {
        return clientSelectByExampleWithoutBLOBsMethodGeneratedSupportRowBounds(interfaze, introspectedTable);
    }
    
    private boolean clientSelectByExampleWithoutBLOBsMethodGeneratedSupportRowBounds(Interface interfaze, IntrospectedTable introspectedTable){

    	AbstractJavaMapperMethodGenerator generator1 = new SelectByExampleWithoutBLOBsSupportRowBoundsMethodGenerator();
    	generator1.setIntrospectedTable(introspectedTable);
    	generator1.setContext(context);
    	generator1.addInterfaceElements(interfaze);
    	
    	AbstractJavaMapperMethodGenerator generator2 = new SelectByExampleWithBLOBsSupportRowBoundsMethodGenerator();
    	generator2.setIntrospectedTable(introspectedTable);
    	generator2.setContext(context);
    	generator2.addInterfaceElements(interfaze);
    	
        return true;
    }

    public boolean clientSelectByExampleWithBLOBsMethodGenerated(Method method,
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return true;
    }

    public boolean clientSelectByExampleWithoutBLOBsMethodGenerated(Method method,
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
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
