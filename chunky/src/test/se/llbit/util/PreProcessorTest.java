package se.llbit.util;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.truth.Truth.assertThat;

public class PreProcessorTest {
  @Test
  public void testRegex() throws NoSuchFieldException, IllegalAccessException {
    Field regexField = JsonPreprocessor.class.getDeclaredField("COMMENT_STRIP_MATCHER");
    regexField.setAccessible(true);
    Pattern pattern = (Pattern) regexField.get(null);

    assertThat(doRegex(pattern, "\"key2\": \"hey // not a comment\" // This is a comment"))
      .isEqualTo("\"key2\": \"hey // not a comment\" ");
    assertThat(doRegex(pattern, "test / test"))
      .isEqualTo("test / test");
    assertThat(doRegex(pattern, "\" \\\" // not a comment\""))
      .isEqualTo("\" \\\" // not a comment\"");
    assertThat(doRegex(pattern, "\t\"Normal Json\""))
      .isEqualTo("\t\"Normal Json\"");
  }

  private static String doRegex(Pattern pattern, String input) {
    Matcher m = pattern.matcher(input);
    assertThat(m.lookingAt()).isTrue();
    return m.group("json");
  }
}
