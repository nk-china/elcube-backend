/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.data.mybatis.pagination;

import cn.nkpro.elcube.data.mybatis.pagination.dialect.Dialect;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

@Intercepts(@Signature(type = Executor.class, method = "query", args = {
		MappedStatement.class, Object.class, RowBounds.class,
		ResultHandler.class }))
@Slf4j
public class PaginationInterceptor implements Interceptor {

	private Dialect dialect = null;

	private static int MAPPED_STATEMENT_INDEX = 0;
	private static int PARAMETER_INDEX = 1;
	private static int ROWBOUNDS_INDEX = 2;

	public PaginationInterceptor() {
	}

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		processIntercept(invocation.getArgs());
		return invocation.proceed();
	}

	private void processIntercept(final Object[] queryArgs) {

		MappedStatement mappedStatement = (MappedStatement) queryArgs[MAPPED_STATEMENT_INDEX];
		Object parameter = queryArgs[PARAMETER_INDEX];
		RowBounds rowBounds = (RowBounds) queryArgs[ROWBOUNDS_INDEX];

		BoundSql boundSql = mappedStatement.getBoundSql(parameter);
		String originalSql = boundSql.getSql().trim();

		Object parameterObject = boundSql.getParameterObject();

		if (log.isTraceEnabled()) {
			log.trace("Parameter = " + parameter);
			log.trace("RowBounds = " + rowBounds);
			log.trace("Default RowBounds = " + (rowBounds == RowBounds.DEFAULT));
		}

		// 如果没有分页参数，则直接退出
		if (rowBounds == null || rowBounds == RowBounds.DEFAULT) {
			return;
		}

		PaginationContext context = PaginationContext.getContext();

		// 没有分页控件或者page控件未设置则不进行分页
		if (context != null) {

			int totalRows = 0;
//			StringBuffer countSql = new StringBuffer(originalSql.length() + 100);
//			countSql.append("select count(1) from (").append(originalSql)
//					.append(") t");
			// 暂时mybatis没有复杂sql
			String countSql = originalSql.replaceAll("(?<=select)[\\s\\S]*?(?=from)", " count(*) ").replaceAll("order by.*", "");

			if (log.isTraceEnabled()) {
				log.trace("生成查询数量SQL : " + originalSql);
				log.trace("生成查询数量SQL : " + countSql);
			}
			Connection connection = null;
			ResultSet rs;
			PreparedStatement countStmt;
			try {
				connection = mappedStatement.getConfiguration()
						.getEnvironment().getDataSource().getConnection();
				countStmt = connection.prepareStatement(countSql);
				BoundSql countBS = copyFromBoundSql(mappedStatement, boundSql,
						countSql);
				setParameters(countStmt, mappedStatement, countBS,
						parameterObject);
				rs = countStmt.executeQuery();
				if (rs.next()) {
					totalRows = rs.getInt(1);
				}
				rs.close();
				countStmt.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				try {
					if (connection != null && !connection.isClosed()) {
						connection.close();
					}
				} catch (SQLException e) {
					throw new RuntimeException("数据库关闭出错", e);
				}
			}

			context.setTotal(totalRows);
		}

		// 分页查询 本地化对象 修改数据库注意修改实现
		String pagesql = dialect.getLimitString(originalSql,
				rowBounds.getOffset(), rowBounds.getLimit());
		queryArgs[ROWBOUNDS_INDEX] = new RowBounds(RowBounds.NO_ROW_OFFSET,
				RowBounds.NO_ROW_LIMIT);

		BoundSql newBoundSql = copyFromBoundSql(mappedStatement, boundSql,
				pagesql);
		MappedStatement newMs = copyFromMappedStatement(mappedStatement,
				new BoundSqlSqlSource(newBoundSql));
		queryArgs[MAPPED_STATEMENT_INDEX] = newMs;

		if (log.isTraceEnabled()) {
			log.trace("生成分页SQL : " + newBoundSql.getSql()
					.replaceAll("\n"," ")
					.replaceAll("\\s{2,}"," "));
		}
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	/**
	 * 初始化数据库方言类，根据MyBatisConfig.xml中的设置初始化相应的分页方言类
	 */
	@Override
	public void setProperties(Properties properties) {
	}

	public void setDialect(Dialect dialect) {

		this.dialect = dialect;
		if (log.isInfoEnabled())
			log.info("dialect=" + dialect.getClass());
	}

	private BoundSql copyFromBoundSql(MappedStatement ms, BoundSql boundSql,
			String sql) {
		BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql,
				boundSql.getParameterMappings(), boundSql.getParameterObject());
		for (ParameterMapping mapping : boundSql.getParameterMappings()) {
			String prop = mapping.getProperty();
			if (boundSql.hasAdditionalParameter(prop)) {
				newBoundSql.setAdditionalParameter(prop,
						boundSql.getAdditionalParameter(prop));
			}
		}
		return newBoundSql;
	}

	public static class BoundSqlSqlSource implements SqlSource {
		BoundSql boundSql;

		BoundSqlSqlSource(BoundSql boundSql) {
			this.boundSql = boundSql;
		}

		public BoundSql getBoundSql(Object parameterObject) {
			return boundSql;
		}
	}

	/**
	 * 对SQL参数(?)设值,参考org.apache.ibatis.executor.parameter.
	 * DefaultParameterHandler
	 * 
	 * @param ps ps
	 * @param mappedStatement mappedStatement
	 * @param boundSql boundSql
	 * @param parameterObject parameterObject
	 * @throws SQLException SQLException
	 */
	private void setParameters(PreparedStatement ps,
			MappedStatement mappedStatement, BoundSql boundSql,
			Object parameterObject) throws SQLException {
		ErrorContext.instance().activity("setting parameters")
				.object(mappedStatement.getParameterMap().getId());
		List<ParameterMapping> parameterMappings = boundSql
				.getParameterMappings();
		if (parameterMappings != null) {
			Configuration configuration = mappedStatement.getConfiguration();
			TypeHandlerRegistry typeHandlerRegistry = configuration
					.getTypeHandlerRegistry();
			MetaObject metaObject = parameterObject == null ? null
					: configuration.newMetaObject(parameterObject);
			for (int i = 0; i < parameterMappings.size(); i++) {
				ParameterMapping parameterMapping = parameterMappings.get(i);
				if (parameterMapping.getMode() != ParameterMode.OUT) {
					Object value;
					String propertyName = parameterMapping.getProperty();
					PropertyTokenizer prop = new PropertyTokenizer(propertyName);
					if (parameterObject == null) {
						value = null;
					} else if (typeHandlerRegistry
							.hasTypeHandler(parameterObject.getClass())) {
						value = parameterObject;
					} else if (boundSql.hasAdditionalParameter(propertyName)) {
						value = boundSql.getAdditionalParameter(propertyName);
					} else if (propertyName
							.startsWith(ForEachSqlNode.ITEM_PREFIX)
							&& boundSql.hasAdditionalParameter(prop.getName())) {
						value = boundSql.getAdditionalParameter(prop.getName());
						if (value != null) {
							value = configuration.newMetaObject(value)
									.getValue(
											propertyName.substring(prop
													.getName().length()));
						}
					} else {
						value = metaObject == null ? null : metaObject
								.getValue(propertyName);
					}
					@SuppressWarnings("unchecked")
					TypeHandler<Object> typeHandler = (TypeHandler<Object>) parameterMapping
							.getTypeHandler();
					if (typeHandler == null) {
						throw new ExecutorException(
								"There was no TypeHandler found for parameter "
										+ propertyName + " of statement "
										+ mappedStatement.getId());
					}
					typeHandler.setParameter(ps, i + 1, value,
							parameterMapping.getJdbcType());
				}
			}
		}
	}

	private MappedStatement copyFromMappedStatement(MappedStatement ms,
			SqlSource newSqlSource) {
		MappedStatement.Builder builder = new MappedStatement.Builder(
				ms.getConfiguration(), ms.getId(), newSqlSource,
				ms.getSqlCommandType());

		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		// builder.keyProperty(ms.getKeyProperty());

		// setStatementTimeout()
		builder.timeout(ms.getTimeout());

		// setStatementResultMap()
		builder.parameterMap(ms.getParameterMap());

		// setStatementResultMap()
		builder.resultMaps(ms.getResultMaps());
		builder.resultSetType(ms.getResultSetType());

		// setStatementCache()
		builder.cache(ms.getCache());
		builder.flushCacheRequired(ms.isFlushCacheRequired());
		builder.useCache(ms.isUseCache());

		return builder.build();
	}
}
