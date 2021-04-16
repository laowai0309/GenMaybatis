package com.bbx.mybatisUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringTools {
    /**
     * 转换为驼峰格式
     * @param name
     * @return
     */
    public static String toHump(String name, Boolean firstUpper) {
        StringBuilder sb = new StringBuilder();

        if (StringUtils.isEmpty(name))
            return  "";

        int flag = 0;
        name  = StringUtils.lowerCase(name);
        for(int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == '_')
                flag = 1;
            else{
                // 首字母大写
                if ( firstUpper && i == 0 ) {
                    sb.append(Character.toUpperCase(c));
                    continue;
                }

                if (flag == 1 && Character.isLowerCase(c)) {
                    sb.append(Character.toUpperCase(c));
                } else {
                    sb.append(c);
                }
                flag = 0;
            }
        }

        return sb.toString();
    }

    /**
     * 裁剪前缀
     * @param prefix
     * @param s
     * @return
     */
    public static String subPrefix(String prefix, String s) {
        if (StringUtils.isEmpty(prefix) || StringUtils.isEmpty(s))
            return s;

        String pattern = "^" + prefix;
        Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher m = r.matcher(s);
        if (m.find( )) {
            int pos = m.end();
            return s.substring(pos);
        } else {
            return s;
        }

    }

    /**
     * 首字母大写
     * @param s
     * @return
     */
    public static String firstUpper(String s) {
        if (StringUtils.isEmpty(s))
            return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (i == 0)
                sb.append(Character.toUpperCase(s.charAt(i)));
            else
                sb.append(s.charAt(i));
        }

        return sb.toString();
    }

    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();

        String temp = str.substring(0, 8) + str.substring(9, 13)
                + str.substring(14, 18) + str.substring(19, 23) + str.substring(24);
        return temp;

    }

}
