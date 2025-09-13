package se.llbit.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ColorUtilTest {
  @Test
  void fromHexString() {
    Vector3 color = new Vector3();
    ColorUtil.fromHexString("#c0ffee", color);
    assertEquals(192 / 255., color.x, Constants.EPSILON);
    assertEquals(255 / 255., color.y, Constants.EPSILON);
    assertEquals(238 / 255., color.z, Constants.EPSILON);

    ColorUtil.fromHexString("#123", color);
    assertEquals(17 / 255., color.x, Constants.EPSILON);
    assertEquals(34 / 255., color.y, Constants.EPSILON);
    assertEquals(51 / 255., color.z, Constants.EPSILON);

    ColorUtil.fromHexString("0ff1ce", color);
    assertEquals(15 / 255., color.x, Constants.EPSILON);
    assertEquals(241 / 255., color.y, Constants.EPSILON);
    assertEquals(206 / 255., color.z, Constants.EPSILON);

    ColorUtil.fromHexString("456", color);
    assertEquals(68 / 255., color.x, Constants.EPSILON);
    assertEquals(85 / 255., color.y, Constants.EPSILON);
    assertEquals(102 / 255., color.z, Constants.EPSILON);

    assertThrows(IllegalArgumentException.class, () -> {
      ColorUtil.fromHexString("1234", color);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      ColorUtil.fromHexString("#1234567", color);
    });
    assertThrows(NumberFormatException.class, () -> {
      ColorUtil.fromHexString("#badhex", color);
    });
  }
}