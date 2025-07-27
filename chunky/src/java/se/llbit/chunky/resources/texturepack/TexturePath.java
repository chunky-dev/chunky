package se.llbit.chunky.resources.texturepack;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TexturePath {
  String value();
  String[] alternatives() default {};
}
