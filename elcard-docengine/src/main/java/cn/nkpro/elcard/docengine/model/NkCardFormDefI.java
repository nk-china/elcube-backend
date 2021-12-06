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
package cn.nkpro.elcard.docengine.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class NkCardFormDefI {
    private String key;
    private String name;
    private String inputType;
    private String calcTrigger;
    private Integer calcOrder;
    private Integer col;
    private Boolean required;
    private Integer control;
    private String spELControl;
    private String spELContent;
    private String[] spELTriggers;
    private String format;
    private String options;
    private Object optionsObject;
    private Float min;
    private Float max;
    private Integer maxLength;
    private Integer digits;
    private Float step;
    private String selectMode;
    private String checked;
    private String unChecked;
    private String modal;
    private String pattern;
    private String message;
}