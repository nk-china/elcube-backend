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
package cn.nkpro.elcube.data.mybatis.pagination.dialect;


import cn.nkpro.elcube.annotation.Keep;

/**
 * A dialect compatible with the H2 database.
 * 
 * @author Thomas Mueller
 *
 */
@Keep
public class H2Dialect extends Dialect {

    public boolean supportsLimit() {
        return true;
    }

	public String getLimitString(String sql, int offset,String offsetPlaceholder, int limit, String limitPlaceholder) {
		return new StringBuffer(sql.length() + 40).
			append(sql).
			append((offset > 0) ? " limit "+limitPlaceholder+" offset "+offsetPlaceholder : " limit "+limitPlaceholder).
			toString();
	}

	@Override
	public boolean supportsLimitOffset() {
		return true;
	}
    
//    public boolean bindLimitParametersInReverseOrder() {
//        return true;
//    }    
//
//    public boolean bindLimitParametersFirst() {
//        return false;
//    }

    

}