package com.dalmirdasilva.androidmessagingprotocol.device;

import java.util.Formatter;

/**
 * Set of useful methods.
 */
public class Util {

  /**
   * Parses a array of bytes and return if hexadecimal representation as a string.
   *
   * @param array
   * @return
   */
  public static String formatHex(byte[] array) {
    Formatter formatter = new Formatter();
    for (byte b : array) {
      formatter.format("%02x", b);
    }
    return formatter.toString();
  }
}