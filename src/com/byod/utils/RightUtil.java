
package com.byod.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class RightUtil {

    private static Character[] elements = {'0','1','2','3','4','5','6','7','8','9',
        'q','w','e','r','t','y','u','i','o','p',
        'a','s','d','f','g','h','j','k','l',
        'z','x','c','v','b','n','m'};
    
    private static List<Character> codes = new ArrayList(Arrays.asList(elements));
    
    private static int ikey = 928;

    private static int iinc = 345;

    public static String encrypt(String password) {
        if (password == null || password.equals("")) {
            return "";
        }
        int cc;
        String cs;
        String encpassword = "";
        char[] cps;
        cps = password.toCharArray();

        for (int i = 0; i < cps.length; i++) {
            cc = ((int) cps[i] ^ ikey) + iinc;
            cs = Integer.toString(cc) + "x";
            encpassword += cs;
        }

        return encpassword;
    }

    public static String decrypt(String coded) {
        if (coded == null || coded.equals("")) {
            return "";
        }
        String password = "";
        StringTokenizer st = new StringTokenizer(coded, "x");
        while (st.hasMoreTokens()) {
            String cs = st.nextToken();
            int cc = Integer.parseInt(cs);
            char p = (char) ((cc - iinc) ^ ikey);
            password += p;
        }
        return password;
    }
    
    /*
     * 根据kbID对密码进行翻译
     * kbID：随机键盘标识，包含了请求时间
     * TODO 参数待改
     */
    public static String translatePassword(String srcPwd, String labelArr) {
        StringBuilder desPwd = new StringBuilder();
        char[] src = srcPwd.toCharArray();
        int index;
        for (char c : src) {
            //根据c在labelArr中的位置i，查找codelist的值，拼接
            index = codes.indexOf(c);
            desPwd.append(labelArr.charAt(index));
            
        }
        return desPwd.toString();
    }


    /**
     * 随机化字符串
     * @param src
     * @return
     */
    public static String random(String src) {
        String[] srcList = src.split("");
        List<String> list = Arrays.asList(srcList);
        Collections.shuffle(list);
        StringBuilder result = new StringBuilder();
        for (String i : list) {
            result.append(i);
        }
        return result.toString();
    }


}