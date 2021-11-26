package cn.nkpro.ts5.data.jdbc;

public class Utils {

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
