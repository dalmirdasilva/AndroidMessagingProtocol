package com.dalmirdasilva.androidmessagingprotocol.device;

import java.util.Formatter;

public class Util {

    public static String formatHex(byte[] array) {
        Formatter formatter = new Formatter();
        for (byte b : array) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
}