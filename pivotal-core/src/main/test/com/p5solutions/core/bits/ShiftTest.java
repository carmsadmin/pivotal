package com.p5solutions.core.bits;

import java.util.Date;

import com.p5solutions.core.utils.BitUtil;

import junit.framework.TestCase;

public class ShiftTest extends TestCase {


  public void testBitsTimeRange() {
    System.out.println();
    System.out.println("--------------------------------");
    long time = new Date().getTime() / 1000;
    System.out.println("T0: " + BitUtil.bits(time));
    time = (time << 32);
    System.out.println("T1: " + BitUtil.bits(time));
    time = time | 0xFFFFFFFFl;
    //time = time | Integer.MAX_VALUE;
    System.out.println("T2: " + BitUtil.bits(time));
    System.out.println("--------------------------------");
  }
  
  public void testBit() {
    Date now = new Date();
    int sequence = Integer.MAX_VALUE;
    //int sequence = 2038495161;
    //int sequence = 238495161;
    
    // time in ms
    //long t_now = now.getTime() / 1000;
    //long t_now = Integer.MIN_VALUE;
    long t_now = ((long)Integer.MAX_VALUE) + 86400L;
    
    
    System.out.println("TIME BITS: " + t_now);
    System.out.print("--- ");
    System.out.print(BitUtil.bits(t_now));
    System.out.println();
    
    // shift 32 bits left and 
    long t_now1 = (t_now << 32);
    System.out.println("TIME BITS <<32: " + t_now1);
    System.out.print("--- ");
    System.out.print(BitUtil.bits(t_now1));
    System.out.println();
    
    t_now1 = t_now1 | sequence;
    System.out.println("TIME BITS XOR SEQ: " + t_now1);
    System.out.print("--- ");
    System.out.print(BitUtil.bits(t_now1));
    System.out.println();

    // zero out and shift 32 bits to the right
    long t_original = (t_now1 >>> 32);
    System.out.println("TIME BITS >>>32: " + t_original);
    System.out.print("--- ");
    System.out.print(BitUtil.bits(t_original));
    System.out.println();
    
    long seq_original = (t_now1 << 32 >>> 32);
    
    System.out.println("Now: " + t_now);
    System.out.println("Now1: " + t_now1);
    System.out.println("Now2: " + t_original);
    System.out.println("Sequence: " + seq_original + " == " + sequence);
    //System.out.println("Today: " + t_today);
  
    int mask = 0x00000001;

    System.out.println(Integer.toBinaryString(sequence));
    long t = sequence;
    for (int i = 31; i >= 0; i--) {
      t = (sequence >> i) & mask;
      //System.out.println(i + " + " + t);
      System.out.print(t);
    }
  }
}
