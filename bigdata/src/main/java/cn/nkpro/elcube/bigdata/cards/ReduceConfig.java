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
package cn.nkpro.elcube.bigdata.cards;

import cn.nkpro.elcube.annotation.Keep;
import lombok.Data;

@Keep
@Data
public class ReduceConfig{

    private String docId;
    private String cardKey;

    private String preTask;
    private String programType;
    private String dataSource;
    private String dataCommand;
    private String service;
    private String dataKey;
    private String dataMapping;
}