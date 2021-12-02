/*
 * This file is part of EAsis.
 *
 * EAsis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EAsis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with EAsis.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.easis.data.mybatis.pagination;


public class PaginationContext {

	private Integer total;

	public Integer getTotal() {
		return this.total;
	}

	void setTotal(Integer total) {
		this.total = total;
	}

	/***
	 * ThreadLocal设置每次查询的查询参数，通过mybatis的拦截，从线程中获取查询参数，并将查询的总数放在参数中，
	 * 注意的问题是如果一个功能需要查询多个分页时， 则需要每次都重新设置context中PageContext的参数值
	 */

	private static ThreadLocal<PaginationContext> context = new ThreadLocal<PaginationContext>();

	static PaginationContext getContext() {
		return context.get();
	}

	public static void removeContext() {
		context.remove();
	}

	public static PaginationContext init() {
		PaginationContext pageContext = new PaginationContext();
		context.set(pageContext);
		return pageContext;
	}

}