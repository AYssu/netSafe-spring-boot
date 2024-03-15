package com.netsafe.netsafe.utils;

public class LogUtil {
    private static final String TAG = "netSafe";

    public static boolean Debug = true;
    public static void LOG(String log)
    {
        if (Debug)
        {
            System.out.println(TAG+"："+log);
        }
    }
}
