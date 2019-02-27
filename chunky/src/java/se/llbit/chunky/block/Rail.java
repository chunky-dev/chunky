package se.llbit.chunky.block;

import se.llbit.chunky.model.RailModel;
import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Rail extends MinecraftBlock {
  private final int shape;
  private final String description;

  private final Texture[] texture;

  public Rail(String name, Texture straightTrack, String shape) {
    super(name, Texture.dispenserFront);
    texture = new Texture[] {
        straightTrack, straightTrack, straightTrack, straightTrack, straightTrack, straightTrack,
        Texture.railsCurved, Texture.railsCurved, Texture.railsCurved, Texture.railsCurved
    };
    this.description = "shape=" + shape;
    localIntersect = true;
    switch (shape) {
      default:
      case "north_south":
        this.shape = 0;
        break;
      case "east_west":
        this.shape = 1;
        break;
      case "ascending_east":
        this.shape = 2;
        break;
      case "ascending_west":
        this.shape = 3;
        break;
      case "ascending_north":
        this.shape = 4;
        break;
      case "ascending_south":
        this.shape = 5;
        break;
      case "north_west":
        this.shape = 8;
        break;
      case "north_east":
        this.shape = 9;
        break;
      case "south_east":
        this.shape = 6;
        break;
      case "south_west":
        this.shape = 7;
        break;
    }
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return RailModel.intersect(ray, texture[shape], shape);
  }

  @Override public String description() {
    return description;
  }
}
