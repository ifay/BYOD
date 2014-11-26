package com.byod.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MyDateUtil {


    /**
     * @param return ���ظ�ʽ==>1335921046421
     */
    public static long getLongDate() {

        return System.currentTimeMillis();
    }

    /**
     * @param return ���ظ�ʽ==>Wed May 02 09:10:46 CST 2012
     */
    public static String getTime1() {

        java.util.Date date1 = new java.util.Date();
        return date1.toString();
    }

    /**
     * @param return ���ظ�ʽ==>2012-05-02
     */
    public static String getYearMonthDay1() {

        java.sql.Date date2 = new java.sql.Date(System.currentTimeMillis());
        return date2.toString();
    }

    /**
     * @param return ���ظ�ʽ==>2012-05-02
     */
    public static String getYearMonthDay2() {

        java.util.Date date1 = new java.util.Date();
        java.sql.Date date3 = new java.sql.Date(date1.getTime());
        return date3.toString();
    }

    /**
     * @param return ���ظ�ʽ==>2012-05-02 09:10:46.436
     */
    public static String getSeconds1() {

        Timestamp stamp1 = new Timestamp(System.currentTimeMillis());
        return stamp1.toString();
    }

    /**
     * @param return ���ظ�ʽ==>2012-05-02 09:10:46.436
     */
    public static String getSeconds2() {

        java.util.Date date1 = new java.util.Date();
        Timestamp stamp2 = new Timestamp(date1.getTime());
        return stamp2.toString();
    }

    /**
     * @param return ���ظ�ʽ==>2012/05/02 09:10:46
     */
    public static String getYear_Second1() {

        Timestamp stamp1 = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStr = sdf.format(stamp1);
        return timeStr;
    }

    /**
     * @param return ���ظ�ʽ==>2012/05/02 09:10:46
     */
    public static String getYear_Second2() {

        java.util.Date date1 = new java.util.Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStr2 = df.format(date1);
        return timeStr2;
    }

    /**
     * ���ַ�ת��Ϊ����
     *
     * @param str    ��θ�ʽ==>yyyy-MM-dd HH:mm:ss
     * @param return ���ظ�ʽ==>Thu Dec 10 05:12:02 CST 2009
     */
    public static String changeStringToDate1(String str) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date date6 = null;
        try {
            date6 = sdf.parse(str);
            return date6.toString();
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * ���ַ�ת��Ϊ����
     *
     * @param str    ��θ�ʽ==>yyyy-MM-dd HH:mm:ss
     * @param return ���ظ�ʽ==>2009-12-10
     */
    public static String changeStringToDate2(String str) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date date6 = null;
        try {
            date6 = sdf.parse(str);
            java.sql.Date date7 = new java.sql.Date(date6.getTime());
            return date7.toString();
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * ���ַ�ת��Ϊ����
     *
     * @param str    ��θ�ʽ==>yyyy-MM-dd HH:mm:ss
     * @param return ���ظ�ʽ==>2009-12-10 05:12:02.0
     */
    public static String changeStringToDate3(String str) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date date6 = null;
        try {
            date6 = sdf.parse(str);
            java.sql.Date date7 = new java.sql.Date(date6.getTime());
            Timestamp stamp9 = new Timestamp(date7.getTime());
            return stamp9.toString();
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }


}
