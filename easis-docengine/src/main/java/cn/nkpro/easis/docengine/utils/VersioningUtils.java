package cn.nkpro.easis.docengine.utils;

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
