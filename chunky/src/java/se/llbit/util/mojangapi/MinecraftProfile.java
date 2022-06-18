package se.llbit.util.mojangapi;

import com.google.gson.annotations.SerializedName;

import java.util.Optional;

public class MinecraftProfile {
  public String id;
  public String name;
  public Property[] properties;

  public Optional<MinecraftSkin> getSkin() {
    return getProperty("textures").flatMap(MinecraftSkin::getSkinFromEncodedTextures);
  }

  public Optional<String> getProperty(String name) {
    if (properties != null) {
      for (Property property : properties) {
        if (name.equals(property.name)) {
          return Optional.of(property.value);
        }
      }
    }
    return Optional.empty();
  }

  public static class Property {
    public String name;
    public String value;
  }

  public static class Textures {
    @SerializedName("SKIN")
    public SkinUrl skin;

    @SerializedName("CAPE")
    public CapeUrl cape;

    public static class SkinUrl {
      public String url;

      public Metadata metadata;

      public static class Metadata {
        public String model;
      }
    }

    public static class CapeUrl {
      public String url;
    }
  }
}
