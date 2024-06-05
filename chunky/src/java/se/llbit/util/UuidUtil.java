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
}
