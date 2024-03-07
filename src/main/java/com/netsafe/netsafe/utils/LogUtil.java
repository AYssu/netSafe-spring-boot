package com.netsafe.netsafe.utils;

public class LogUtil {
    private static final String TAG = "netSafe";
    private static final boolean isPrint = true;
    public static void LOG(String log)
    {
        if (isPrint)
        {
            System.out.println(TAG+"："+log);
        }
    }
}
