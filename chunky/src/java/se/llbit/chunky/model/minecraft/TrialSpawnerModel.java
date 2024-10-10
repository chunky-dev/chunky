package se.llbit.chunky.model.minecraft;

import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.resources.texture.AbstractTexture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class TrialSpawnerModel extends QuadModel {
  private static final Quad[] quads = new Quad[]{
    new Quad(
      new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(15.998 / 16.0, 15.998 / 16.0, 15.998 / 16.0),
      new Vector3(0.002 / 16.0, 15.998 / 16.0, 15.998 / 16.0),
      new Vector3(15.998 / 16.0, 15.998 / 16.0, 0.002 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(15.998 / 16.0, 0.002 / 16.0, 0.002 / 16.0),
      new Vector3(0.002 / 16.0, 0.002 / 16.0, 0.002 / 16.0),
      new Vector3(15.998 / 16.0, 0.002 / 16.0, 15.998 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(15.998 / 16.0, 15.998 / 16.0, 15.998 / 16.0),
      new Vector3(15.998 / 16.0, 15.998 / 16.0, 0.002 / 16.0),
      new Vector3(15.998 / 16.0, 0.002 / 16.0, 15.998 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0.002 / 16.0, 15.998 / 16.0, 0.002 / 16.0),
      new Vector3(0.002 / 16.0, 15.998 / 16.0, 15.998 / 16.0),
      new Vector3(0.002 / 16.0, 0.002 / 16.0, 0.002 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(15.998 / 16.0, 15.998 / 16.0, 0.002 / 16.0),
      new Vector3(0.002 / 16.0, 15.998 / 16.0, 0.002 / 16.0),
      new Vector3(15.998 / 16.0, 0.002 / 16.0, 0.002 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0.002 / 16.0, 15.998 / 16.0, 15.998 / 16.0),
      new Vector3(15.998 / 16.0, 15.998 / 16.0, 15.998 / 16.0),
      new Vector3(0.002 / 16.0, 0.002 / 16.0, 15.998 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)
    )
  };

  private final AbstractTexture[] textures;

  public TrialSpawnerModel(
    boolean ominous, String trialSpawnerState
  ) {
    AbstractTexture top = getTopTexture(ominous, trialSpawnerState);
    AbstractTexture bottom = Texture.trialSpawnerBottom;
    AbstractTexture side;
    switch (trialSpawnerState) {
      case "active":
      case "waiting_for_players":
      case "waiting_for_reward_ejection":
      case "ejecting_reward":
        side = ominous ? Texture.trialSpawnerSideActiveOminous : Texture.trialSpawnerSideActive;
        break;
      case "inactive":
      case "cooldown":
      default:
        side = ominous ? Texture.trialSpawnerSideInactiveOminous : Texture.trialSpawnerSideInactive;
        break;
    }
    textures = new AbstractTexture[]{
      top, bottom, side, side, side, side, top, bottom, side, side, side, side
    };
  }

  public static AbstractTexture getTopTexture(boolean ominous, String trialSpawnerState) {
    switch (trialSpawnerState) {
      case "active":
      case "waiting_for_players":
      case "waiting_for_reward_ejection":
        return ominous ? Texture.trialSpawnerTopActiveOminous : Texture.trialSpawnerTopActive;
      case "ejecting_reward":
        return ominous ? Texture.trialSpawnerTopEjectingRewardOminous : Texture.trialSpawnerTopEjectingReward;
      case "inactive":
      case "cooldown":
      default:
        return ominous ? Texture.trialSpawnerTopInactiveOminous : Texture.trialSpawnerTopInactive;
    }
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public AbstractTexture[] getTextures() {
    return textures;
  }
}
