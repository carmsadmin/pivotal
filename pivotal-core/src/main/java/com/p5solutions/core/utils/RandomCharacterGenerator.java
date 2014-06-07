package com.p5solutions.core.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * RandomCharacterGenerator: Generates random characters that can be used as part of some sort of access or id hash.
 * 
 * @author Sophanara Min (smin)
 */
public class RandomCharacterGenerator {
  private static String Alpha = "ABCDEFGHJKLMNPQRSTUVWXYZ";

  private static String AlphanNum = "0123456789";

  public static String generate(int numberOfChar) {
    try {
      SecureRandom rand = SecureRandom.getInstance("SHA1PRNG");
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < numberOfChar; i++) {
        sb.append(Alpha.charAt(getRandomInteger(1, Alpha.length(), rand) - 1));
      }
      return sb.toString();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return "";

  }

  public static String generateAlphaNum(int numberOfChar) {
    try {
      SecureRandom rand = SecureRandom.getInstance("SHA1PRNG");
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < numberOfChar; i++) {
        sb.append(AlphanNum.charAt(getRandomInteger(1, AlphanNum.length(), rand) - 1));
      }
      return sb.toString();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return "";

  }

  private static Integer getRandomInteger(int aStart, int aEnd, Random aRandom) {
    if (aStart > aEnd) {
      throw new IllegalArgumentException("Start cannot exceed End.");
    }
    // get the range, casting to long to avoid overflow problems
    long range = (long) aEnd - (long) aStart + 1;
    // compute a fraction of the range, 0 <= frac < range
    long fraction = (long) (range * aRandom.nextDouble());
    int randomNumber = (int) (fraction + aStart);
    return randomNumber;
  }
}
