package com.dontgoback.dontgo.utils;

public class Print {
    public static void log(String str){
        System.out.println("====================");
        System.out.println(str);
        System.out.println("====================");
    }

    public static void log(String str, String subject){
        System.out.printf("========= %s ========\n", subject);
        System.out.println(str);
        System.out.println("====================");
    }
}
