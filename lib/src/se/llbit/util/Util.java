/* Copyright (c) 2013-2016 Jesper Öqvist <jesper@llbit.se>
 *
 * This file is part of Chunky.
 *
 * Chunky is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chunky is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Chunky.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.llbit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Miscellaneous utility functions.
 */
public class Util {

  private static final char[] B64 =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".toCharArray();

  /**
   * @return the MD5 hash sum of the given file, in hexadecimal format.
   * Returns an error message if there was an error computing the checksum.
   */
  public static String md5sum(File library) {
    try {
      MessageDigest digest = MessageDigest.getInstance("MD5");
      try (FileInputStream in = new FileInputStream(library);
           DigestInputStream dis = new DigestInputStream(in, digest)) {
        byte[] buf = new byte[2048];
        int n;
        do {
          n = dis.read(buf);
        } while (n != -1);
        return byteArrayToHexString(digest.digest());
      } catch (IOException e) {
        return "md5 compute error: " + e.getMessage();
      }
    } catch (NoSuchAlgorithmException e) {
      return "md5 compute error: " + e.getMessage();
    }
  }

  /**
   * @return the SHA256 hash sum of the given file, in hexadecimal format.
   * Returns an error message if there was an error computing the checksum.
   */
  public static String sha256sum(File library) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      try (FileInputStream in = new FileInputStream(library);
           DigestInputStream dis = new DigestInputStream(in, digest)) {
        byte[] buf = new byte[2048];
        int n;
        do {
          n = dis.read(buf);
        } while (n != -1);
        return byteArrayToHexString(digest.digest());
      } catch (IOException e) {
        return "sha256 compute error: " + e.getMessage();
      }
    } catch (NoSuchAlgorithmException e) {
      return "sha256 compute error: " + e.getMessage();
    }
  }

  private static byte[] NIBBLE_TO_HEX = "0123456789ABCDEF".getBytes();

  /**
   * Inspired by Real's How To: http://www.rgagnon.com/javadetails/java-0596.html
   *
   * @return Hexadecimal string representation of the input bytes
   */
  public static String byteArrayToHexString(byte[] array) {
    byte[] hexdigits = new byte[array.length * 2];
    for (int i = 0; i < array.length; ++i) {
      hexdigits[i * 2] = NIBBLE_TO_HEX[(array[i] & 0xF0) >>> 4];
      hexdigits[i * 2 + 1] = NIBBLE_TO_HEX[array[i] & 0xF];
    }
    return new String(hexdigits);
  }

  /**
   * @return A date object representing the given ISO 8601 timestamp. If time is
   * not a ISO 8601 formatted timestamp, the returned object is
   * {@code new Date(0)}.
   */
  public static Date dateFromISO8601(String time) {
    if (time.length() < 6) {
      return new Date(0);
    }
    try {
      // Insert "GMT".
      if (time.endsWith("Z")) {
        time = time.substring(0, time.length() - 1) + "GMT-00:00";
      } else {
        time = time.substring(0, time.length() - 6) + "GMT" +
            time.substring(time.length() - 6, time.length());
      }
      // http://stackoverflow.com/a/10624878/1250278
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
      return dateFormat.parse(time);
    } catch (ParseException e) {
      return new Date(0);
    }
  }

  public static String ISO8601FromDate(Date date) {
    // http://stackoverflow.com/a/3914498/1250278
    // http://stackoverflow.com/a/10624878/1250278
    TimeZone tz = TimeZone.getTimeZone("UTC");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    df.setTimeZone(tz);
    return df.format(date);
  }

  public static boolean isValidISO8601(String time) {
    if (time.length() < 6) {
      return false;
    }
    try {
      // Insert "GMT".
      if (time.endsWith("Z")) {
        time = time.substring(0, time.length() - 1) + "GMT-00:00";
      } else {
        time = time.substring(0, time.length() - 6) + "GMT" +
            time.substring(time.length() - 6, time.length());
      }
      // http://stackoverflow.com/a/10624878/1250278
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
      dateFormat.parse(time);
      return true;
    } catch (ParseException e) {
      return false;
    }
  }

  /**
   * Encode a hash code as a string.
   */
  public static String cacheEncode(int hash) {
    char s1 = B64[(hash & 0xFC000000) >>> 26];
    char s2 = B64[(hash & 0x03F00000) >>> 20];
    char s3 = B64[(hash & 0x000FC000) >>> 14];
    char s4 = B64[(hash & 0x00003F00) >>> 8];
    char s5 = B64[(hash & 0x000000FC) >>> 2];
    char s6 = B64[hash & 0x00000003];
    return "" + s1 + s2 + s3 + s4 + s5 + s6;
  }
}
