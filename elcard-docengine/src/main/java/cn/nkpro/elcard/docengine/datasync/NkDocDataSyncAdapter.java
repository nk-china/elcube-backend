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
package cn.nkpro.elcard.docengine.datasync;

import cn.nkpro.elcard.docengine.gen.DocDefDataSync;

import java.util.Map;

public interface NkDocDataSyncAdapter<K> extends NkDocDataAdapter {

    default void onInsert(Map<K, Map<String, Object>> listMapped, DocDefDataSync config){}
    default void onModify(Map<K, Map<String, Object>> listMapped, DocDefDataSync config){}
    default void onRemove(Map<K, Map<String, Object>> listMapped, DocDefDataSync config){}
}
