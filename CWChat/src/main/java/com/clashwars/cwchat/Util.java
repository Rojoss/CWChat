package com.clashwars.cwchat;

import com.clashwars.cwcore.utils.CWUtil;

public class Util {

    public static String formatMsg(String msg) {
        return CWUtil.integrateColor("&8[&4CW Chat&8] &6" + msg);
    }
}
