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
package cn.nkpro.easis.data.jdbc;

class Utils {

    static String reverse(String fieldName){
        StringBuilder result = new StringBuilder();
        if (fieldName != null && fieldName.length() > 0) {

            boolean flag = false;
            for (int i = 0; i < fieldName.length(); i++) {
                char ch = fieldName.charAt(i);

                if(flag&&Character.isUpperCase(ch)){
                    result.append(ch);
                }else if(Character.isUpperCase(ch)){
                    result.append('_');
                    result.append(ch);
                    flag = true;
                }else{
                    result.append(Character.toUpperCase(ch));
                    flag = false;
                }
            }
        }
        return result.toString();
    }

    static String translate(String underscoreName, Boolean isPascal) {
        StringBuilder result = new StringBuilder();
        if (underscoreName != null && underscoreName.length() > 0) {
            boolean flag = false;
            char firstChar = underscoreName.charAt(0);
            if (isPascal) {
                result.append(Character.toUpperCase(firstChar));
            } else {
                result.append(Character.toLowerCase(firstChar));
            }
            for (int i = 1; i < underscoreName.length(); i++) {
                char ch = underscoreName.charAt(i);
                if ('_' == ch) {  //如果是下划线，不拼接
                    flag = true;
                } else {
                    if (flag) {  //如果遇见下划线，则转换大写追加
                        result.append(Character.toUpperCase(ch));
                        flag = false;
                    } else {
                        result.append(Character.toLowerCase(ch));
                    }
                }
            }
        }
        return result.toString();
    }
}
