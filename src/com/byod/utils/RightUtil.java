
package com.byod.utils;

import java.util.StringTokenizer;

public class RightUtil {

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
}
