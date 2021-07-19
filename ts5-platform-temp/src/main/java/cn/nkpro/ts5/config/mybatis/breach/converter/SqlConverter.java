package cn.nkpro.ts5.config.mybatis.breach.converter;

import cn.nkpro.ts5.config.mybatis.breach.Strategy;
import net.sf.jsqlparser.statement.Statement;

import java.util.Map;

/**
 * SQL转换器
 * @author janjan, xujian_jason@163.com
 *
 */
public interface SqlConverter {
	
	/**
	 * 转换
	 * @param statement JSqlParser Statement对象（Insert、Update、Delete、Select）
	 * @param params 调用MyBatis Mapper传入的参数
	 * @param mapperId 命名空间 + Mapper id
	 * @return
	 */
	String convert(Statement statement, Object params, String mapperId, Map<String, Strategy> strategies);
	
}
