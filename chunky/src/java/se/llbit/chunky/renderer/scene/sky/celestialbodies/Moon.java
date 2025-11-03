package se.llbit.chunky.renderer.scene.sky.celestialbodies;

import se.llbit.chunky.resources.BitmapImage;
import se.llbit.chunky.resources.Texture;
import se.llbit.json.JsonObject;
import se.llbit.log.Log;

public class Moon extends CelestialBodyType {
  public static final String ID = "MOON";

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public String getName() {
    return "Moon";
  }

  public enum Phase {
    NEW_MOON(0, 1),
    WAXING_CRESCENT(1, 1),
    FIRST_QUARTER(2, 1),
    WAXING_GIBBOUS(3, 1),
    FULL_MOON(0, 0),
    WANING_GIBBOUS(1, 0),
    LAST_QUARTER(2, 0),
    WANING_CRESCENT(3, 0);

    final byte textureAtlasPosX, textureAtlasPosY;

    Phase(int x, int y) {
      textureAtlasPosX = (byte) x;
      textureAtlasPosY = (byte) y;
    }
  }

  public static final Texture textureAtlas = new Texture();
  private final Texture texture = new Texture();
  private Phase phase;

  public Moon() {
    setPhase(Phase.FULL_MOON);
  }

  @Override
  protected void importFromJson(JsonObject obj) {
    setPhase(Phase.valueOf(obj.get("moonPhase").asString(Phase.FULL_MOON.toString())));
  }

  @Override
  public void appendToConfig(JsonObject obj) {
    super.appendToConfig(obj);
    obj.add("moonPhase", phase.name());
  }

  @Override
  public Texture getTexture() {
    return texture;
  }

  public Phase getPhase() {
    return phase;
  }

  public void setPhase(Phase phase) {
    this.phase = phase;

    if (textureAtlas.getWidth() == textureAtlas.getHeight()) {
      // only has 1 phase (phases atlas not found, fallback to default moon texture)
      Log.info("Moon texture did not contain multiple phases");
      texture.setTexture(textureAtlas);
      return;
    }

    int textureSize = textureAtlas.getHeight() / 2;
    BitmapImage phaseTexture = new BitmapImage(textureSize, textureSize);
    phaseTexture.blit(
      textureAtlas.getBitmap(),
      0, 0,
      phase.textureAtlasPosX * textureSize,
      phase.textureAtlasPosY * textureSize,
      (phase.textureAtlasPosX + 1) * textureSize,
      (phase.textureAtlasPosY + 1) * textureSize
    );
    texture.setTexture(phaseTexture.rotated270());
  }
}
