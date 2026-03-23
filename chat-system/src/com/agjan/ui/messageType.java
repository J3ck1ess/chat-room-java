package com.agjan.ui;

public class messageType {
    public static final int LOGIN = 1;
    public static final int GROUP_MSG = 2;
    public static final int ONLINE_LIST = 3;
    public static final int SYSTEM_MSG = 4;
    public static final int PRIVATE_MSG = 5;

    public static String getName(int type) {
        switch (type) {
            case LOGIN: return "LOGIN";
            case GROUP_MSG: return "GROUP_MSG";
            case ONLINE_LIST: return "ONLINE_LIST";
            case SYSTEM_MSG: return "SYSTEM_MSG";
            case PRIVATE_MSG: return "PRIVATE_MSG";
            default: return "UNKNOWN";
        }
    }
}
