package se.llbit.chunky.renderer.scene;

public class SceneUtils {

  /**
   * Remove problematic characters from scene name.
   *
   * @return sanitized scene name
   */
  public static String sanitizedSceneName(String name, String fallbackName) {
    name = name.trim();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < name.length(); ++i) {
      char c = name.charAt(i);
      if (isValidSceneNameChar(c)) {
        sb.append(c);
      } else if (c >= '\u0020' && c <= '\u007e') {
        sb.append('_');
      }
    }
    String stripped = sb.toString().trim();
    if (stripped.isEmpty()) {
      return fallbackName;
    } else {
      return stripped;
    }
  }

  /**
   * Remove problematic characters from scene name.
   *
   * @return sanitized scene name
   */
  public static String sanitizedSceneName(String name) {
    return sanitizedSceneName(name, "Scene");
  }

  /**
   * @return <code>false</code> if the character can cause problems on any
   * supported platform.
   */
  public static boolean isValidSceneNameChar(char c) {
    switch (c) {
      case '/':
      case ':':
      case ';':
      case '\\': // Windows file separator.
      case '*':
      case '?':
      case '"':
      case '<':
      case '>':
      case '|':
        return false;
    }
    if (c < '\u0020') {
      return false;
    }
    return c <= '\u007e' || c >= '\u00a0';
  }

  /**
   * Check for scene name validity.
   *
   * @return <code>true</code> if the scene name contains only legal characters
   */
  public static boolean sceneNameIsValid(String name) {
    for (int i = 0; i < name.length(); ++i) {
      if (!isValidSceneNameChar(name.charAt(i))) {
        return false;
      }
    }
    return true;
  }
}
