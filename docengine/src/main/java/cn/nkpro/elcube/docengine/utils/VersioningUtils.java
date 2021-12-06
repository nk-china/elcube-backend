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
package cn.nkpro.elcube.docengine.utils;

import org.apache.commons.lang3.StringUtils;

public class VersioningUtils {

    public static String parseMajor(String version){
        String[] split = defaultIfBlank(version).split("[.]");
        return split[0];
    }

    public static Integer parseMajorInteger(String version){
        return Integer.parseInt(parseMajor(version));
    }

    public static String parseMinor(String version){
        String[] split = defaultIfBlank(version).split("[.]");
        String major = split[0];
        String minor = split.length>1?split[1]:"0";
        return String.format("%s.%s",major,minor);
    }

    public static String nextMajor(String version){
        String[] split = defaultIfBlank(version).split("[.]");
        String major = split[0];
        return String.format("%s.0.0",Integer.parseInt(major)+1);
    }

    public static String nextMinor(String version){
        String[] split = defaultIfBlank(version).split("[.]");
        String major = split[0];
        String minor = split.length>1?split[1]:"0";
        return String.format("%s.%s.0",major,Integer.parseInt(minor)+1);
    }

    public static String nextPatch(String version){
        String[] split = defaultIfBlank(version).split("[.]");
        String major = split[0];
        String minor = split.length>1?split[1]:"0";
        String patch = split.length>2?split[2]:"0";
        return String.format("%s.%s.%s",major,minor,Integer.parseInt(patch)+1);
    }

    private static String defaultIfBlank(String version){
        return StringUtils.defaultIfBlank(version,"0.0.0");
    }
}
