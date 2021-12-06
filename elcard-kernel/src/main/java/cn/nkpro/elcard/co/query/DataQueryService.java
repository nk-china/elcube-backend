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
package cn.nkpro.elcard.co.query;

import cn.nkpro.elcard.co.query.model.DataFieldDesc;
import cn.nkpro.elcard.co.query.model.DataQueryRequest;
import cn.nkpro.elcard.co.query.model.DataQueryResponse;
import cn.nkpro.elcard.co.NkCustomObject;

import java.util.List;

public interface DataQueryService extends NkCustomObject {

    List<DataFieldDesc> getFieldCaps(String index);

    DataQueryResponse queryPage(DataQueryRequest request);

    DataQueryResponse queryList(DataQueryRequest request);
}
