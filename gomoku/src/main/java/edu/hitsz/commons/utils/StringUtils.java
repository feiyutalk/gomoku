package edu.hitsz.commons.utils;

public class StringUtils {

    private StringUtils() {

    }

    public static boolean isEmpty(String s) {
        return s == null || s.trim().equals("");
    }
}
