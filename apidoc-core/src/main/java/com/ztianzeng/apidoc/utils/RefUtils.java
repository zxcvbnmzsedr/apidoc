package com.ztianzeng.apidoc.utils;


public class RefUtils {

    public static String constructRef(String simpleRef) {
        return "#/components/schemas/" + simpleRef;
    }

    public static String constructRef(String simpleRef, String prefix) {
        return prefix + simpleRef;
    }

    public static String getRef(String ref) {
        return ref.replace("#/components/schemas/", "");

    }
}
