package se.llbit.chunky.block;

import se.llbit.chunky.model.RailModel;
import se.llbit.chunky.resources.Texture;

public class Rail extends AbstractModelBlock {

  private final String description;

  public Rail(String name, Texture straightTrack, String shape) {
    super(name, Texture.dispenserFront);
    Texture[] texture = new Texture[]{
        straightTrack, straightTrack, straightTrack, straightTrack, straightTrack, straightTrack,
        Texture.railsCurved, Texture.railsCurved, Texture.railsCurved, Texture.railsCurved
    };
    this.description = "shape=" + shape;
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
  public String description() {
    return description;
  }
}
