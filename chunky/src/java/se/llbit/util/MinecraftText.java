package se.llbit.util;

public class MinecraftText {
  public static String removeFormatChars(String input) {
    return input.replaceAll("\\u00c2?§[0-9a-fklmnor]", "");
  }
}
