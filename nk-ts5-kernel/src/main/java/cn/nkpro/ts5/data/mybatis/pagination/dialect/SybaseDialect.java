package cn.nkpro.ts5.data.mybatis.pagination.dialect;

import cn.nkpro.ts5.annotation.Keep;

@Keep
public class SybaseDialect extends Dialect{

	public boolean supportsLimit() {
		return false;
	}

	public boolean supportsLimitOffset() {
		return false;
	}

	public String getLimitString(String sql, int offset,String offsetPlaceholder, int limit, String limitPlaceholder) {
		throw new UnsupportedOperationException( "paged queries not supported" );
	}

}
