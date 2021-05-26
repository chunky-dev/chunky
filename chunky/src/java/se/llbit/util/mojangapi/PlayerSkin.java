package se.llbit.util.mojangapi;

import se.llbit.chunky.renderer.scene.PlayerModel;

public class PlayerSkin {

  private final String url;
  private final PlayerModel model;

  public PlayerSkin(String url, PlayerModel model) {
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
