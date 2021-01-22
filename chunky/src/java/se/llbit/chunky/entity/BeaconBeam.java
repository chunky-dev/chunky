package se.llbit.chunky.entity;

import se.llbit.chunky.block.Beacon;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.BeaconBeamMaterial;
import se.llbit.json.JsonMember;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.ColorUtil;
import se.llbit.math.Octree;
import se.llbit.math.Quad;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector3i;
import se.llbit.math.Vector4;
import se.llbit.math.primitive.Primitive;
import se.llbit.util.JsonUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BeaconBeam extends Entity implements Poseable {

  private static final Quad[] beam = new Quad[]{
      new Quad(
          new Vector3(5 / 16.0, 0.0, 5 / 16.0),
          new Vector3(5 / 16.0, 0.0, 11 / 16.0),
          new Vector3(5 / 16.0, 1.0, 5 / 16.0),
          new Vector4(1.0, 0.0, 1.0, 0.0)
      ),
      new Quad(
          new Vector3(11 / 16.0, 0.0, 11 / 16.0),
          new Vector3(11 / 16.0, 0.0, 5 / 16.0),
          new Vector3(11 / 16.0, 1.0, 11 / 16.0),
          new Vector4(1.0, 0.0, 1.0, 0.0)
      ),
      new Quad(
          new Vector3(5 / 16.0, 1.0, 5 / 16.0),
          new Vector3(11 / 16.0, 1.0, 5 / 16.0),
          new Vector3(5 / 16.0, 0.0, 5 / 16.0),
          new Vector4(1.0, 0.0, 1.0, 0.0)
      ),
      new Quad(
          new Vector3(11 / 16.0, 1.0, 11 / 16.0),
          new Vector3(5 / 16.0, 1.0, 11 / 16.0),
          new Vector3(11 / 16.0, 0.0, 11 / 16.0),
          new Vector4(1.0, 0.0, 1.0, 0.0)
      )
  };

  private final JsonObject pose;
  private double scale = 1;
  private int height = 256;
  private final Map<Integer, BeaconBeamMaterial> materials = new HashMap<>();

  public BeaconBeam(Vector3 position) {
    super(position);
    this.pose = new JsonObject();
    pose.add("all", JsonUtil.vec3ToJson(new Vector3(0, 0, 0)));
  }

  public BeaconBeam(JsonObject json) {
    super(JsonUtil.vec3FromJsonObject(json.get("position")));
    this.scale = json.get("scale").asDouble(1);
    this.height = json.get("height").asInt(256);
    this.pose = json.get("pose").object();

    JsonObject materialsList = json.get("beamMaterials").object();
    for (JsonMember obj : materialsList.members) {
      BeaconBeamMaterial mat = new BeaconBeamMaterial(BeaconBeamMaterial.DEFAULT_COLOR);
      mat.loadMaterialProperties(obj.value.object());
      materials.put(Integer.parseInt(obj.name), mat);
    }
  }

  @Override
  public void loadDataFromOctree(Octree octree, BlockPalette palette, Vector3i origin) {
    int firstColor = BeaconBeamMaterial.DEFAULT_COLOR;
    boolean foundFirst = false;
    this.materials.put(0, new BeaconBeamMaterial(BeaconBeamMaterial.DEFAULT_COLOR));

    //Start i at 1 so the first beacon is not checked.
    //Stop i at 256 even if the beam is taller because the Octree will wrap the coordinates.
    for (int i = 1; i < height && (i + position.y) < 256; i++) {
      Material blockMaterial = octree.getMaterial((int)(position.x - origin.x), (int)(position.y + i - origin.y), (int)(position.z - origin.z), palette);
      int color = colorfromMaterial(blockMaterial);
      if(color != -1) {
        if (!foundFirst) {
          this.materials.put(i, new BeaconBeamMaterial(color));
          firstColor = color;
          foundFirst = true;
        } else {
          float[] foundColor = new float[3];
          float[] baseColor = new float[3];

          ColorUtil.getRGBComponents(color, foundColor);
          ColorUtil.getRGBComponents(firstColor, baseColor);

          int newColor = ColorUtil.getRGB((foundColor[0] + baseColor[0]) / 2.0f, (foundColor[1] + baseColor[1]) / 2.0f, (foundColor[2] + baseColor[2]) / 2.0f);
          this.materials.put(i, new BeaconBeamMaterial(newColor));
        }
      }
    }
  }

  private int colorfromMaterial(Material blockMaterial) {
    if(blockMaterial instanceof Beacon) {
      return 0xF9FFFE;
    }
    if(blockMaterial.name.endsWith("_stained_glass") || blockMaterial.name.endsWith("_stained_glass_pane")) {
      String prefix;
      if(blockMaterial.name.endsWith("_stained_glass")) {
        prefix = blockMaterial.name.substring(10, blockMaterial.name.length() - 14);
      } else {
        prefix = blockMaterial.name.substring(10, blockMaterial.name.length() - 19);
      }
      switch (prefix) {
        default:
        case "white": {
          return 0xF9FFFE;
        }
        case "orange": {
          return 0xF9801D;
        }
        case "magenta": {
          return 0xC74EBD;
        }
        case "light_blue": {
          return 0x3AB3DA;
        }
        case "yellow": {
          return 0xFED83D;
        }
        case "lime": {
          return 0x80C71F;
        }
        case "pink": {
          return 0xF38BAA;
        }
        case "gray": {
          return 0x474F52;
        }
        case "light_gray": {
          return 0x9D9D97;
        }
        case "cyan": {
          return 0x169C9C;
        }
        case "purple": {
          return 0x8932B8;
        }
        case "blue": {
          return 0x3C44AA;
        }
        case "brown": {
          return 0x835432;
        }
        case "green": {
          return 0x5E7C16;
        }
        case "red": {
          return 0xB02E26;
        }
        case "black": {
          return 0x1D1D21;
        }
      }
    }
    return -1;
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    Vector3 allPose = JsonUtil.vec3FromJsonArray(this.pose.get("all"));
    ArrayList<Primitive> faces = new ArrayList<>();
    BeaconBeamMaterial using = new BeaconBeamMaterial(BeaconBeamMaterial.DEFAULT_COLOR);
    //Have 1 block tall model and repeat it for height * scale.
    //This addresses the texture stretching problem and
    //allows for the height to be changed.
    for (int i = 0; i < height; i++) {
      if (materials.containsKey(i)) {
        using = materials.get(i);
      }

      for (Quad quad : beam) {
        quad.addTriangles(faces, using,
            Transform.NONE.translate(-0.5, -0.5, -0.5)
                .scale(scale)
                .translate(0.0, i * scale, 0.0)
                .rotateX(allPose.x)
                .rotateY(allPose.y)
                .rotateZ(allPose.z)
                .translate(0.5, 0.5, 0.5)
                .translate(position.x + offset.x, position.y + offset.y, position.z + offset.z)
        );
      }
    }
    return faces;
  }

  @Override
  public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "beaconBeam");
    json.add("position", position.toJson());
    json.add("height", height);
    json.add("scale", getScale());
    json.add("pose", pose);

    JsonObject materialsList = new JsonObject();
    for (int i : materials.keySet()) {
      BeaconBeamMaterial material = materials.get(i);
      JsonObject object = new JsonObject(materials.size());
      material.saveMaterialProperties(object);
      materialsList.add(String.valueOf(i), object);
    }

    json.add("beamMaterials", materialsList);
    return json;
  }

  public static BeaconBeam fromJson(JsonObject json) {
    return new BeaconBeam(json);
  }

  @Override
  public String[] partNames() {
    return new String[]{"all"};
  }

  @Override
  public double getScale() {
    return scale;
  }

  @Override
  public void setScale(double value) {
    this.scale = value;
  }

  @Override
  public JsonObject getPose() {
    return pose;
  }

  @Override
  public boolean hasHead() {
    return false;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public Map<Integer, BeaconBeamMaterial> getMaterials() {
    return materials;
  }
}
