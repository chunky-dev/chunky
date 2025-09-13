package se.llbit.testutil;

import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestUtils {
  public static <T extends Throwable> T assertThrowsWithExpectedMessage(Class<T> expectedType, Executable executable, String expectedMessage) {
    T t = assertThrows(expectedType, executable);
    assertEquals(t.getMessage(), expectedMessage, "Error message differs from expected");
    return t;
  }
}
