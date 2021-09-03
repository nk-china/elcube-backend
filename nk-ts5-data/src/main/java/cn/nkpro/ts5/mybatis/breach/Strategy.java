package cn.nkpro.ts5.mybatis.breach;

/**
 * 分表策略
 * @author janjan, xujian_jason@163.com
 *
 */
public interface Strategy {
	/**
	 * 获取最终表名
	 * @param baseTableName 基础表名
	 * @param params 传入Mapper的参数
	 * @return 最终表名
	 */
	String getFinalTable(String baseTableName, Object params);
}
