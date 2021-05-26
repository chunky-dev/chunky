package se.llbit.util;

import se.llbit.chunky.renderer.scene.PlayerModel;

public class Skin {

  private final String url;
  private final PlayerModel model;

  public Skin(String url, PlayerModel model) {
    this.url = url;
    this.model = model;
  }

  public String getUrl() {
    return url;
  }

  public PlayerModel getModel() {
    return model;
  }
}
