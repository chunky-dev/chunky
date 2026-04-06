package se.llbit.util;

import java.util.UUID;

public class UuidUtil {
  /**
   * Converts four integers (ordered from MSB to LSB) to a UUID.
   *
   * @param ints Four integers, ordered from MSB to LSB
   * @return UUID
   */
  public static UUID intsToUuid(int[] ints) {
    return new UUID(
      ((long) ints[0]) << 32 | ((long) ints[1] & 0xFFFFFFFFL),
      ((long) ints[2]) << 32 | ((long) ints[3] & 0xFFFFFFFFL)
    );
  }

  public static UUID stringToUuid(String laxUuid) {
    try {
      return UUID.fromString(laxUuid);
    } catch (IllegalArgumentException e) {
      laxUuid = laxUuid.replaceAll("[^0-9A-Za-z]", "");
      laxUuid = laxUuid.substring(0, 8) + "-" +
        laxUuid.substring(8, 12) + "-" +
        laxUuid.substring(12, 16) + "-" +
        laxUuid.substring(16, 20) + "-" +
        laxUuid.substring(20);
      return UUID.fromString(laxUuid);
    }
  }
}
