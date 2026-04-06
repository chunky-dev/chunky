package se.llbit.util;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UuidUtilTest {

  @Test
  void intsToUuid() {
    assertEquals(
      UUID.fromString("61699b2e-d327-4a01-9f1e-0ea8c3f06bc6"),
      UuidUtil.intsToUuid(new int[]{
        1634310958,
        -752399871,
        -1625420120,
        -1007653946
      })
    );
  }

  @Test
  void stringToUuid() {
    assertEquals(
      UUID.fromString("f79a267d-42af-4fde-8ce1-3f06717cac07"),
      UuidUtil.stringToUuid("F79A267D42AF4FDE8CE13F06717CAC07"));
    assertEquals(
      UUID.fromString("f79a267d-42af-4fde-8ce1-3f06717cac07"),
      UuidUtil.stringToUuid("f79A267D-42aF4FDE8ce13F06717cac07"));
  }
}
