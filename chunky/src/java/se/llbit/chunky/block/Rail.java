package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.RailModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Rail extends MinecraftBlock implements ModelBlock {
  private final RailModel model;
  private final String description;

  public Rail(String name, Texture straightTrack, String shape) {
    super(name, Texture.dispenserFront);
    Texture[] texture = new Texture[] {
        straightTrack, straightTrack, straightTrack, straightTrack, straightTrack, straightTrack,
        Texture.railsCurved, Texture.railsCurved, Texture.railsCurved, Texture.railsCurved
    };
    this.description = "shape=" + shape;
    localIntersect = true;
    opaque = false;
    solid = false;

    int variation;
    switch (shape) {
      default:
      case "north_south":
        variation = 0;
        break;
      case "east_west":
        variation = 1;
        break;
      case "ascending_east":
        variation = 2;
        break;
      case "ascending_west":
        variation = 3;
        break;
      case "ascending_north":
        variation = 4;
        break;
      case "ascending_south":
        variation = 5;
        break;
      case "north_west":
        variation = 8;
        break;
      case "north_east":
        variation = 9;
        break;
      case "south_east":
        variation = 6;
        break;
      case "south_west":
        variation = 7;
        break;
    }
    this.model = new RailModel(texture[variation], variation);
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public BlockModel getModel() {
    return model;
  }
}
