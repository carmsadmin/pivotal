package com.p5solutions.core.utils;

public class BitUtil {
  
  public static String bits(long value) {
    int mask = 0x0000000000000001;

    byte[] buf = new byte[64];
    long t = value;
    for (int i = 63; i >= 0; i--) {
      t = (value >> i) & mask;
      // TODO optimize.
      buf[63-i] = (byte)(48 + t); 
      //System.out.println(i + " + " + t);
      //System.out.print(t);
    }
    return new String(buf);
  }
}
