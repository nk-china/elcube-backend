/*
 * This file is part of ELCard.
 *
 * ELCard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcard.basic;

import cn.nkpro.elcard.annotation.Keep;
import lombok.Getter;

import java.util.List;

@Keep
public class PageList<T>{

	@Getter
	private int page;
	@Getter
	private int rows;
	@Getter
	private long total;
	@Getter
	private int from;
	@Getter
	private int end;
	@Getter
	private long max;
	@Getter
	private List<T> list;

	public PageList(List<T> list, int from, int size, long total) {
		this.list = list;
		this.from = from;
		this.rows = size;
		this.page = size!=0?(from/size+1):1;
		this.total = total;
		this.max = size == 0 || total == 0 ? 0 : ((total / size) + (total % size == 0 ? 0 : 1));
		this.end = from + list.size() - 1;
	}
}
