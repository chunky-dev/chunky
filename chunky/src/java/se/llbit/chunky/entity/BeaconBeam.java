package se.llbit.chunky.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import se.llbit.chunky.block.minecraft.Beacon;
import se.llbit.chunky.block.Block;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.IntegerAdjuster;
import se.llbit.chunky.ui.IntegerTextField;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.BeaconBeamMaterial;
import se.llbit.fx.LuxColorPicker;
import se.llbit.json.JsonMember;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.log.Log;
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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
  private final Int2ObjectOpenHashMap<BeaconBeamMaterial> materials = new Int2ObjectOpenHashMap<>();

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

    //Start i at 1 so the first beacon block is not checked. This would cause the base beam color to always be white.
    //Stop iterating if the we get outside octree.
    for (int i = 1; i < height && octree.isInside(new Vector3((position.x - origin.x), (position.y + i - origin.y), (position.z - origin.z))); i++) {
      Material blockMaterial = octree.getMaterial((int)(position.x - origin.x), (int)(position.y + i - origin.y), (int)(position.z - origin.z), palette);
      int color = getColorFromBlock((Block)blockMaterial);
      if(color != -1) {
        if (!foundFirst) {
          this.materials.put(i, new BeaconBeamMaterial(color));
          firstColor = color;
          foundFirst = true;
        } else {
          //If this is not the first block to change the color, the new beam color is the average of the first color and the color of this block
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

  private int getColorFromBlock(Block blockMaterial) {
    if(blockMaterial instanceof Beacon) {
      return BeaconBeamMaterial.DEFAULT_COLOR;
    }
    Pattern stainedGlassPattern = Pattern.compile("(minecraft:)?(.+?)_stained_glass(_pane)?");
    Matcher matcher = stainedGlassPattern.matcher(blockMaterial.name);
    if (matcher.find()) {
      String prefix = matcher.group(2);
      switch (prefix) {
        default: {
          Log.warn("Unknown stained glass type found: " + prefix);
        }
        case "white": {
          return BeaconBeamMaterial.DEFAULT_COLOR;
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
    return -1; // Return -1 if block doesn't change beam color
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

  @Override
  public VBox getControls(Node tab, Scene scene) {
    VBox controls = new VBox();

    IntegerAdjuster height = new IntegerAdjuster();
    height.setName("Height");
    height.setTooltip("Modifies the height of the beam. Useful if your scene is taller than the world height.");
    height.set(getHeight());
    height.setRange(1, 512);
    height.onValueChange(value -> {
      setHeight(value);
      scene.rebuildActorBvh();
    });
    controls.getChildren().add(height);

    HBox beamColor = new HBox();
    VBox listControls = new VBox();
    VBox propertyControls = new VBox();

    listControls.setMaxWidth(200);
    beamColor.setPadding(new Insets(10));
    beamColor.setSpacing(15);
    propertyControls.setSpacing(10);

    DoubleAdjuster emittance = new DoubleAdjuster();
    emittance.setName("Emittance");
    emittance.setRange(0, 100);
    emittance.clampBoth();

    DoubleAdjuster specular = new DoubleAdjuster();
    specular.setName("Specular");
    specular.setRange(0, 1);
    specular.clampBoth();

    DoubleAdjuster ior = new DoubleAdjuster();
    ior.setName("IoR");
    ior.setRange(0, 5);
    ior.clampMin();

    DoubleAdjuster perceptualSmoothness = new DoubleAdjuster();
    perceptualSmoothness.setName("Smoothness");
    perceptualSmoothness.setRange(0, 1);
    perceptualSmoothness.clampBoth();

    DoubleAdjuster metalness = new DoubleAdjuster();
    metalness.setName("Metalness");
    metalness.setRange(0, 1);
    metalness.clampBoth();

    LuxColorPicker beamColorPicker = new LuxColorPicker();

    ObservableList<Integer> colorHeights = FXCollections.observableArrayList();
    colorHeights.addAll(getMaterials().keySet());
    ListView<Integer> colorHeightList = new ListView<>(colorHeights);
    colorHeightList.setMaxHeight(150.0);
    colorHeightList.getSelectionModel().selectedItemProperty().addListener(
      (observable, oldValue, heightIndex) -> {

        BeaconBeamMaterial beamMat = getMaterials().get(heightIndex);
        emittance.set(beamMat.emittance);
        specular.set(beamMat.specular);
        ior.set(beamMat.ior);
        perceptualSmoothness.set(beamMat.getPerceptualSmoothness());
        metalness.set(beamMat.metalness);
        beamColorPicker.setColor(ColorUtil.toFx(beamMat.getColorInt()));

        emittance.onValueChange(value -> {
          beamMat.emittance = value.floatValue();
          scene.refresh();
        });
        specular.onValueChange(value -> {
          beamMat.specular = value.floatValue();
          scene.refresh();
        });
        ior.onValueChange(value -> {
          beamMat.ior = value.floatValue();
          scene.refresh();
        });
        perceptualSmoothness.onValueChange(value -> {
          beamMat.setPerceptualSmoothness(value);
          scene.refresh();
        });
        metalness.onValueChange(value -> {
          beamMat.metalness = value.floatValue();
          scene.refresh();
        });
      }
    );
    beamColorPicker.colorProperty().addListener(
      (observableColor, oldColorValue, newColorValue) -> {
        Integer index = colorHeightList.getSelectionModel().getSelectedItem();
        if (index != null) {
          getMaterials().get(index).updateColor(ColorUtil.getRGB(ColorUtil.fromFx(newColorValue)));
          scene.rebuildActorBvh();
        }
      }
    );

    HBox listButtons = new HBox();
    listButtons.setPadding(new Insets(10));
    listButtons.setSpacing(15);
    Button deleteButton = new Button("Delete");
    deleteButton.setOnAction(e -> {
      Integer index = colorHeightList.getSelectionModel().getSelectedItem();
      if (index != null && index != 0) { //Prevent removal of the bottom layer
        getMaterials().remove(index);
        colorHeightList.getItems().removeAll(index);
        scene.rebuildActorBvh();
      }
    });
    IntegerTextField layerInput = new IntegerTextField();
    layerInput.setMaxWidth(50);
    Button addButton = new Button("Add");
    addButton.setOnAction(e -> {
      if (!getMaterials().containsKey(layerInput.valueProperty().get())) { //Don't allow duplicate indices
        getMaterials().put(layerInput.valueProperty().get(), new BeaconBeamMaterial(BeaconBeamMaterial.DEFAULT_COLOR));
        colorHeightList.getItems().add(layerInput.valueProperty().get());
        scene.rebuildActorBvh();
      }
    });

    listButtons.getChildren().addAll(deleteButton, layerInput, addButton);
    propertyControls.setAlignment(Pos.TOP_RIGHT);
    propertyControls.getChildren().addAll(emittance, specular, perceptualSmoothness, ior, metalness, beamColorPicker);
    listControls.getChildren().addAll(new Label("Start Height:"), colorHeightList, listButtons);
    beamColor.getChildren().addAll(listControls, propertyControls);
    controls.getChildren().add(beamColor);

    controls.setSpacing(10);

    return controls;
  }
}
