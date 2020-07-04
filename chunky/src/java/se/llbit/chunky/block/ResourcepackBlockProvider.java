package se.llbit.chunky.block;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.block.minecraft.Air;
import se.llbit.chunky.block.minecraft.UnknownBlock;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.AnimatedTexture;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.chunky.resources.LayeredResourcePacks;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonMember;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;
import se.llbit.json.JsonParser.SyntaxError;
import se.llbit.json.JsonValue;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.math.primitive.Primitive;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.Tag;
import se.llbit.resources.ImageLoader;

public class ResourcepackBlockProvider implements BlockProvider {
  public static final Map<String, BlockVariants> blocks = new HashMap<>();

  /*
  public void loadBlocks(List<File> files) throws IOException {
    blocks.clear();
    try (MultiFileSystem effectiveResources =
           new MultiFileSystem(
             files.stream()
               .map(
                 f -> {
                   try {
                     return FileSystems.newFileSystem(
                       URI.create("jar:" + f.toURI()), Collections.emptyMap());
                   } catch (IOException e) {
                     throw new RuntimeException("Could not open resource pack " + f, e);
                   }
                 })
               .toArray(FileSystem[]::new))) {

      for (FileSystem resourcePack : effectiveResources.fileSystems) {
        JsonModelLoader modelLoader = new JsonModelLoader();
        Files.list(resourcePack.getPath("assets"))
          .filter(Files::isDirectory)
          .map(assetProvider -> assetProvider.resolve("blockstates"))
          .filter(Files::isDirectory)
          .forEach(
            assets -> {
              final String assetsName = assets.getParent().getFileName().toString();
              try {
                Files.list(assets)
                  .filter(path -> path.getFileName().toString().endsWith(".json"))
                  .forEach(
                    block -> {
                      String blockName = block.getFileName().toString();
                      blockName =
                        blockName.substring(0, blockName.length() - ".json".length());
                      String fqBlockName = assetsName + ":" + blockName;

                      if (blocks.containsKey(fqBlockName)) {
                        // this block was already provided by a different resource pack
                        return;
                      }

                      try (JsonParser parser =
                             new JsonParser(Files.newInputStream(block))) {
                        BlockVariants variants = new BlockVariants();

                        JsonObject blockStates = parser.parse().object();
                        if (blockStates.get("variants").isObject()) {
                          for (JsonMember blockState :
                            blockStates.get("variants").object().members) {
                            // TODO add support for pseudo-random models
                            JsonObject blockDefinition =
                              blockState.getValue().isArray()
                                ? blockState.getValue().array().get(0).object()
                                : blockState.getValue().object();
                            String modelName =
                              blockDefinition.get("model").stringValue("unknown:unknown");
                            if (modelName.equals("minecraft:block/air")) {
                              variants.variants.add(new SimpleBlockVariant(Air.INSTANCE));
                            } else {
                              Block model =
                                modelLoader.loadBlockModel(
                                  effectiveResources, modelName, fqBlockName);
                              if (model instanceof JsonModel) {
                                if (blockDefinition.get("x").doubleValue(0) > 0) {
                                  ((JsonModel) model)
                                    .rotateX(
                                      blockDefinition.get("x").intValue(0),
                                      blockDefinition.get("uvlock").boolValue(false));
                                }
                                if (blockDefinition.get("y").doubleValue(0) > 0) {
                                  ((JsonModel) model)
                                    .rotateY(
                                      blockDefinition.get("y").intValue(0),
                                      blockDefinition.get("uvlock").boolValue(false));
                                }
                                if (blockDefinition.get("z").doubleValue(0) > 0) {
                                  ((JsonModel) model)
                                    .rotateZ(
                                      blockDefinition.get("z").intValue(0),
                                      blockDefinition.get("uvlock").boolValue(false));
                                }
                              }

                              variants.variants.add(
                                new VariantsBlockVariant(blockState.getName(), model));
                            }
                          }
                        } else if (blockStates.get("multipart").isArray()) {
                          BlockVariantMultipart multipartBlockVariant =
                            new BlockVariantMultipart(blockName);
                          for (JsonValue part : blockStates.get("multipart").array()) {
                            JsonObject blockDefinition =
                              part.object().get("apply").isArray()
                                ? part.object().get("apply").array().get(0).object()
                                : part.object().get("apply").object();
                            String modelName =
                              blockDefinition.get("model").stringValue("unknown:unknown");

                            Block model =
                              modelLoader.loadBlockModel(
                                effectiveResources, modelName, fqBlockName);

                            if (model instanceof JsonModel) {
                              if (blockDefinition.get("x").doubleValue(0) > 0) {
                                ((JsonModel) model)
                                  .rotateX(
                                    blockDefinition.get("x").intValue(0),
                                    blockDefinition.get("uvlock").boolValue(false));
                              }
                              if (blockDefinition.get("y").doubleValue(0) > 0) {
                                ((JsonModel) model)
                                  .rotateY(
                                    blockDefinition.get("y").intValue(0),
                                    blockDefinition.get("uvlock").boolValue(false));
                              }
                              if (blockDefinition.get("z").doubleValue(0) > 0) {
                                ((JsonModel) model)
                                  .rotateZ(
                                    blockDefinition.get("z").intValue(0),
                                    blockDefinition.get("uvlock").boolValue(false));
                              }
                            }
                            JsonObject conditions = part.object().get("when").object();
                            if (conditions.get("OR").isArray()) {
                              multipartBlockVariant.addPart(
                                new MultipartBlockVariant(
                                  conditions.get("OR").array(), model));
                            } else {
                              multipartBlockVariant.addPart(
                                new MultipartBlockVariant(conditions, model));
                            }
                          }
                          variants.variants.add(multipartBlockVariant);
                        } else {
                          throw new RuntimeException("Unsupported block " + fqBlockName);
                        }

                        blocks.put(assetsName + ":" + blockName, variants);
                      } catch (IOException | SyntaxError | RuntimeException e) {
                        System.out.println(
                          "Could not load block "
                            + fqBlockName
                            + " from "
                            + resourcePack.getFileStores().iterator().next().name());
                      }
                    });
              } catch (IOException e) {
                System.out.println(
                  "Could not read resource pack "
                    + resourcePack.getFileStores().iterator().next().name());
              }
            });
      }
    }
  }
  */

  @Override
  public Block getBlockByTag(String name, Tag tag) {
    BlockVariants variants = blocks.get(name);
    return variants != null ? variants.getBlock(tag) : null;
  }

  @Override
  public Collection<String> getSupportedBlocks() {
    return blocks.keySet();
  }

  public static class BlockVariants {
    public final List<BlockVariant> variants = new ArrayList<>();

    public Block getBlock(Tag tag) {
      Tag properties = tag.get("Properties");
      for (BlockVariant variant : variants) {
        if (variant.isMatch(properties)) {
          return variant.getBlock(properties);
        }
      }
      // throw new RuntimeException("Could not find block model for " + properties.toString());
      return UnknownBlock.UNKNOWN;
    }
  }

  private interface BlockVariant {
    boolean isMatch(Tag properties);

    Block getBlock(Tag properties);
  }

  public static class SimpleBlockVariant implements BlockVariant {
    private final Block block;

    public SimpleBlockVariant(Block block) {
      this.block = block;
    }

    @Override
    public boolean isMatch(Tag properties) {
      return true;
    }

    @Override
    public Block getBlock(Tag properties) {
      return block;
    }
  }

  private interface Condition {
    boolean isSatisfied(Tag properties);
  }

  private static class BlockStateCondition implements Condition {
    protected final Map<String, String> conditions = new HashMap<>();

    private BlockStateCondition(String conditions) {
      for (String condition : conditions.split(",")) {
        String[] parts = condition.trim().split("=");
        if (parts.length < 2) {
          break;
        }
        this.conditions.put(parts[0], parts[1]);
      }
    }

    public BlockStateCondition(JsonObject conditions) {
      for (JsonMember member : conditions.members) {
        this.conditions.put(member.getName(), member.getValue().stringValue(""));
      }
    }

    @Override
    public boolean isSatisfied(Tag properties) {
      for (Entry<String, String> property : conditions.entrySet()) {
        if (Arrays.stream(property.getValue().split("\\|"))
          .noneMatch(value -> properties.get(property.getKey()).stringValue("").equals(value))) {
          return false;
        }
      }
      return true;
    }
  }

  private static class OrCondition implements Condition {
    private final List<Condition> children;

    private OrCondition(List<Condition> children) {
      this.children = children;
    }

    @Override
    public boolean isSatisfied(Tag properties) {
      return children.stream().anyMatch(c -> c.isSatisfied(properties));
    }
  }

  public static class VariantsBlockVariant implements BlockVariant {
    private final Block model;
    private final Condition condition;

    public VariantsBlockVariant(String conditions, Block model) {
      this(new BlockStateCondition(conditions), model);
    }

    public VariantsBlockVariant(Condition condition, Block model) {
      this.condition = condition;
      this.model = model;
    }

    @Override
    public boolean isMatch(Tag properties) {
      return this.condition.isSatisfied(properties);
    }

    @Override
    public Block getBlock(Tag properties) {
      return model;
    }
  }

  public static class BlockVariantMultipart implements BlockVariant {
    private final String name;
    private final List<VariantsBlockVariant> parts = new ArrayList<>();

    public BlockVariantMultipart(String name) {
      this.name = name;
    }

    public void addPart(VariantsBlockVariant part) {
      parts.add(part);
    }

    @Override
    public boolean isMatch(Tag properties) {
      return true;
    }

    @Override
    public Block getBlock(Tag properties) {
      List<JsonModel> applicableParts = new ArrayList<>();
      for (VariantsBlockVariant part : parts) {
        if (part.isMatch(properties)) {
          Block partBlock = part.getBlock(properties);
          if (partBlock instanceof JsonModel) {
            applicableParts.add((JsonModel) partBlock);
          } else {
            throw new RuntimeException("Multipart model part is not a JsonModel");
          }
        }
      }
      /*if (applicableParts.isEmpty()) {
        Log.warn("Empty multipart model for block " + name + " (" + properties.dumpTree() + ")");
      }*/
      return new MultipartJsonModel(name, applicableParts.toArray(new JsonModel[0]));
    }
  }

  public static class MultipartBlockVariant extends VariantsBlockVariant {
    public MultipartBlockVariant(JsonObject when, Block model) {
      super(new BlockStateCondition(when), model);
    }

    public MultipartBlockVariant(JsonArray when, Block model) {
      super(
        new OrCondition(
          when.elements.stream()
            .map(condition -> new BlockStateCondition(when.object()))
            .collect(Collectors.toList())),
        model);
    }
  }

  public static class JsonModelLoader {
    private final Map<String, JsonObject> models = new HashMap<>();
    private final Map<String, Texture> textures = new HashMap<>();

    public Texture getTexture(LayeredResourcePacks fs, String textureName) {
      Texture texture = textures.get(textureName);
      if (texture == null) {
        String[] parts = textureName.split(":");
        if (parts.length < 2) {
          parts = new String[]{"minecraft", parts[0]};
        }
        // TODO <= 1.12 texture paths are prefixed
        try (InputStream inputStream =
               fs.getInputStream(String.join("/", "assets", parts[0], "textures", parts[1] + ".png")).get()) {
          BitmapImage image = ImageLoader.read(inputStream);
          if (image.width == image.height) {
            // textures are always squared...
            texture = new Texture(image);
          } else {
            // ...unless they are animated
            texture = new AnimatedTexture(image);
          }
          textures.put(textureName, texture);
        } catch (IOException e) {
          throw new RuntimeException("Could not load texture " + textureName, e);
          // textures.put(textureName, Texture.unknown);
          // return Texture.unknown;
        }
      }
      return texture;
    }

    private JsonObject getModel(LayeredResourcePacks resourcePacks, String modelName) {
      JsonObject model = models.get(modelName);
      if (model == null) {
        String[] parts = modelName.split(":");
        if (parts.length < 2) {
          parts = new String[]{"minecraft", parts[0]};
        }
        // TODO <= 1.12 model paths are prefixed
        List<String> path = new ArrayList<>();
        path.add("assets");
        path.add(parts[0]);
        path.add("models");
        path.addAll(Arrays.stream((parts[1] + ".json").split("/")).toList());
        try (JsonParser parser =
               new JsonParser(resourcePacks.getInputStream(String.join("/", path)).get())) {
          model = parser.parse().object();
          models.put(modelName, model);
        } catch (IOException | SyntaxError e) {
          System.out.println("MODEL NOT FOUND: " + path);
          throw new RuntimeException("Could not load block model " + modelName, e);
        }
      }
      return model;
    }

    public Block loadBlockModel(LayeredResourcePacks resourcePacks, String model, String blockName) {
      if (model.equals("unknown:unknown")) {
        throw new RuntimeException("unknown block model for " + blockName);
        // return UnknownBlock.UNKNOWN;
      }

      JsonModel block = new JsonModel(blockName, Texture.air);
      JsonObject blockDefinition = this.getModel(resourcePacks, model);
      block.applyDefinition(blockDefinition, name -> this.getTexture(resourcePacks, name));
      try {
        while (!blockDefinition.get("parent").isUnknown()) {
          String parentName = blockDefinition.get("parent").stringValue("block/block");
          blockDefinition = this.getModel(resourcePacks, parentName);
          block.applyDefinition(blockDefinition, name -> this.getTexture(resourcePacks, name));
          block.texture = block.textures.get("particle");
          if (parentName.equals("block/cube_all")) {
            block.localIntersect = false;
            break;
          }
        }
      } catch (RuntimeException e) {
        System.out.println("Parent chain could not be applied");
      }

      // TODO resolve parents up to block/block
      return block;
    }
  }

  private static class JsonModelFace {
    private Quad quad;
    private String texture;
    private int tintindex;

    public JsonModelFace(String direction, JsonObject face, Vector3 from, Vector3 to) {
      if (face.get("texture").stringValue("").length() < 2) {
        throw new RuntimeException(face.toCompactString());
      }
      this.texture = face.get("texture").stringValue("").substring(1);
      this.tintindex = face.get("tintindex").intValue(-1);
      int rotation = face.get("rotation").intValue(0);
      JsonArray uv = face.get("uv").isArray() ? face.get("uv").array() : null;
      // TODO cullface, uvlock of the parent

      if (direction.equals("up")) {
        this.quad =
          new Quad(
            new Vector3(from.x / 16, to.y / 16, to.z / 16),
            new Vector3(to.x / 16, to.y / 16, to.z / 16),
            new Vector3(from.x / 16, to.y / 16, from.z / 16),
            uv != null
              ? new Vector4(
              uv.get(0).doubleValue(from.x) / 16,
              uv.get(2).doubleValue(to.x) / 16,
              1 - uv.get(3).doubleValue(to.z) / 16,
              1 - uv.get(1).doubleValue(from.z) / 16)
              : new Vector4(from.x / 16, to.x / 16, 1 - to.z / 16, 1 - from.z / 16));

        if (rotation > 0) {
          quad.textureRotation = rotation; // -angle or +angle?
        }
      } else if (direction.equals("down")) {
        this.quad =
          new Quad(
            new Vector3(from.x / 16, from.y / 16, from.z / 16),
            new Vector3(to.x / 16, from.y / 16, from.z / 16),
            new Vector3(from.x / 16, from.y / 16, to.z / 16),
            uv != null
              ? new Vector4(
              uv.get(0).doubleValue(from.x) / 16,
              uv.get(2).doubleValue(to.x) / 16,
              1 - uv.get(3).doubleValue(to.z) / 16,
              1 - uv.get(1).doubleValue(from.z) / 16)
              : new Vector4(from.x / 16, to.x / 16, 1 - to.z / 16, 1 - from.z / 16));

        if (rotation > 0) {
          quad.textureRotation = rotation; // -angle or +angle?
        }
      } else if (direction.equals("west")) {
        this.quad =
          new Quad(
            new Vector3(from.x / 16, to.y / 16, to.z / 16),
            new Vector3(from.x / 16, to.y / 16, from.z / 16),
            new Vector3(from.x / 16, from.y / 16, to.z / 16),
            uv != null
              ? new Vector4(
              uv.get(2).doubleValue(from.z) / 16,
              uv.get(0).doubleValue(to.z) / 16,
              1 - uv.get(1).doubleValue(from.y) / 16,
              1 - uv.get(3).doubleValue(to.y) / 16)
              : new Vector4(from.z / 16, to.z / 16, to.y / 16, from.y / 16));

        if (rotation > 0) {
          quad.textureRotation = rotation;
        }
      } else if (direction.equals("east")) {
        this.quad =
          new Quad(
            new Vector3(to.x / 16, to.y / 16, from.z / 16),
            new Vector3(to.x / 16, to.y / 16, to.z / 16),
            new Vector3(to.x / 16, from.y / 16, from.z / 16),
            uv != null
              ? new Vector4(
              uv.get(2).doubleValue(to.z) / 16,
              uv.get(0).doubleValue(from.z) / 16,
              1 - uv.get(1).doubleValue(from.y) / 16,
              1 - uv.get(3).doubleValue(to.y) / 16)
              : new Vector4(to.z / 16, from.z / 16, to.y / 16, from.y / 16));

        if (rotation > 0) {
          quad.textureRotation = rotation;
        }
      } else if (direction.equals("north")) {
        this.quad =
          new Quad(
            new Vector3(from.x / 16, to.y / 16, from.z / 16),
            new Vector3(to.x / 16, to.y / 16, from.z / 16),
            new Vector3(from.x / 16, from.y / 16, from.z / 16),
            uv != null
              ? new Vector4(
              uv.get(0).doubleValue(to.x) / 16,
              uv.get(2).doubleValue(from.x) / 16,
              1 - uv.get(1).doubleValue(from.y) / 16,
              1 - uv.get(3).doubleValue(to.y) / 16)
              : new Vector4(to.x / 16, from.x / 16, to.y / 16, from.y / 16));

        if (rotation > 0) {
          quad.textureRotation = rotation; // -angle or +angle?
        }
      } else if (direction.equals("south")) {
        this.quad =
          new Quad(
            new Vector3(to.x / 16, to.y / 16, to.z / 16),
            new Vector3(from.x / 16, to.y / 16, to.z / 16),
            new Vector3(to.x / 16, from.y / 16, to.z / 16),
            uv != null
              ? new Vector4(
              uv.get(0).doubleValue(to.x) / 16,
              uv.get(2).doubleValue(from.x) / 16,
              1 - uv.get(1).doubleValue(from.y) / 16,
              1 - uv.get(3).doubleValue(to.y) / 16)
              : new Vector4(to.x / 16, from.x / 16, to.y / 16, from.y / 16));

        if (rotation > 0) {
          quad.textureRotation = rotation; // -angle or +angle?
        }
      }
    }

    public float[] getColor(Ray ray, Scene scene, Texture texture) {
      float[] color;
      color = texture.getColor(ray.u, ray.v);

      if (tintindex == 0) {
        float[] biomeColor = ray.getBiomeGrassColor(scene);
        color = color.clone();
        if (color[3] > Ray.EPSILON) {
          color[0] *= biomeColor[0];
          color[1] *= biomeColor[1];
          color[2] *= biomeColor[2];
        }
      }

      return color;
    }
  }

  private static class JsonModelElement {
    private JsonModel model;
    private JsonModelFace[] faces = new JsonModelFace[6]; // up,down,north,east,south,west

    public JsonModelElement(JsonModel model, JsonObject element) {
      this.model = model;

      Vector3 from =
        new Vector3(
          element.get("from").asArray().get(0).asDouble(0),
          element.get("from").asArray().get(1).asDouble(0),
          element.get("from").asArray().get(2).asDouble(0));
      Vector3 to =
        new Vector3(
          element.get("to").asArray().get(0).asDouble(0),
          element.get("to").asArray().get(1).asDouble(0),
          element.get("to").asArray().get(2).asDouble(0));

      for (JsonMember face : element.get("faces").object().members) {
        JsonModelFace modelFace =
          new JsonModelFace(face.getName(), face.getValue().object(), from, to);
        switch (face.getName()) {
          case "up":
            faces[0] = modelFace;
            break;
          case "down":
            faces[1] = modelFace;
            break;
          case "north":
            faces[2] = modelFace;
            break;
          case "east":
            faces[3] = modelFace;
            break;
          case "south":
            faces[4] = modelFace;
            break;
          case "west":
            faces[5] = modelFace;
            break;
        }
      }

      if (element.get("rotation").isObject()) {
        JsonObject rotation = element.get("rotation").object();
        double angle = FastMath.toRadians(rotation.get("angle").doubleValue(0));
        Vector3 origin =
          new Vector3(
            rotation.get("origin").array().get(0).doubleValue(0) / 16,
            rotation.get("origin").array().get(1).doubleValue(0) / 16,
            rotation.get("origin").array().get(2).doubleValue(0) / 16);
        Transform transform = null;
        switch (rotation.get("axis").stringValue("y")) {
          case "x":
            transform =
              Transform.NONE
                .translate(-origin.x + 0.5, -origin.y + 0.5, -origin.z + 0.5)
                .rotateX(angle)
                .translate(origin.x - 0.5, origin.y - 0.5, origin.z - 0.5);
            if (rotation.get("rescale").boolValue(false)) {
              double factor = 1 / FastMath.cos(angle);
              transform = transform.scale(1, factor, factor);
            }
            break;
          case "y":
            transform =
              Transform.NONE
                .translate(-origin.x + 0.5, -origin.y + 0.5, -origin.z + 0.5)
                .rotateY(angle)
                .translate(origin.x - 0.5, origin.y - 0.5, origin.z - 0.5);
            if (rotation.get("rescale").boolValue(false)) {
              double factor = 1 / FastMath.cos(angle);
              transform = transform.scale(factor, 1, factor);
            }
            break;
          case "z":
            transform =
              Transform.NONE
                .translate(-origin.x + 0.5, -origin.y + 0.5, -origin.z + 0.5)
                .rotateZ(angle)
                .translate(origin.x - 0.5, origin.y - 0.5, origin.z - 0.5);
            if (rotation.get("rescale").boolValue(false)) {
              double factor = 1 / FastMath.cos(angle);
              transform = transform.scale(factor, factor, 1);
            }
            break;
        }

        for (JsonModelFace face : faces) {
          if (face != null) {
            face.quad = face.quad.transform(transform);
          }
        }
      }
    }

    public boolean intersect(Ray ray, Scene scene) {
      boolean hit = false;

      for (int faceIndex = 0; faceIndex < 6; faceIndex++) {
        JsonModelFace face = faces[faceIndex];
        if (face != null && face.quad != null && face.quad.intersect(ray)) {
          float[] color = face.getColor(ray, scene, model.textures.get(face.texture));
          if (color[3] > Ray.EPSILON) {
            ray.color.set(color);
            ray.setNormal(face.quad.n);
            ray.t = ray.tNext;
            hit = true;
          }
        }
      }

      return hit;
    }

    public boolean requiresBlockEntity() {
      for (JsonModelFace face : faces) {
        if (face != null && face.quad != null && !face.quad.fitsInBlock()) {
          return true;
        }
      }
      return false;
    }
  }

  public static class JsonModel extends Block {
    private Map<String, Texture> textures = new HashMap<>();
    private List<JsonModelElement> elements = new ArrayList<>();
    private boolean isBlockEntity = false;

    public JsonModel(String name, Texture texture) {
      super(name, texture);
      localIntersect = true;
      opaque = false;
    }

    public void applyDefinition(JsonObject modelDefinition, Function<String, Texture> getTexture) {
      if (modelDefinition.get("textures").isObject()) {
        for (JsonMember texture : modelDefinition.get("textures").object().members) {
          if (texture.getValue().stringValue("").charAt(0) == '#') {
            Texture referencedTexture =
              textures.get(texture.getValue().stringValue("").substring(1));
            if (referencedTexture == null) {
              referencedTexture = Texture.unknown;
              // throw new RuntimeException("Unknown referenced texture " +
              // texture.getValue().stringValue(""));
            }
            textures.put(texture.getName(), referencedTexture);
          } else {
            textures.put(texture.getName(), getTexture.apply(texture.getValue().stringValue("")));
          }
        }
      }

      if (modelDefinition.get("elements").isArray()) {
        for (JsonValue e : modelDefinition.get("elements").array()) {
          JsonModelElement element = new JsonModelElement(this, e.asObject());
          elements.add(element);
          if (element.requiresBlockEntity()) {
            this.isBlockEntity = true;
          }
        }
      }

      // TODO this block is opaque if and only if all faces have cullface set
    }

    @Override
    public boolean intersect(Ray ray, Scene scene) {
      if (isEntity()) {
        return false;
      }
      boolean hit = false;
      ray.t = Double.POSITIVE_INFINITY;
      for (JsonModelElement element : elements) {
        hit |= element.intersect(ray, scene);
      }
      if (hit) {
        ray.color.w = 1;
        ray.distance += ray.t;
        ray.o.scaleAdd(ray.t, ray.d);
      }
      return hit;
    }

    @Override
    public boolean isEntity() {
      return isBlockEntity;
    }

    @Override
    public Entity toEntity(Vector3 position) {
      return new Entity(position) {
        @Override
        public Collection<Primitive> primitives(Vector3 offset) {
          Collection<Primitive> faces = new LinkedList<>();
          Transform transform =
            Transform.NONE.translate(
              position.x + offset.x, position.y + offset.y, position.z + offset.z);
          for (JsonModelElement element : elements) {
            for (JsonModelFace face : element.faces) {
              if (face != null && face.quad != null) {
                Texture texture = textures.get(face.texture);
                if (texture != null) {
                  face.quad.addTriangles(faces, new TextureMaterial(texture), transform);
                } else {
                  System.out.println("Missing texture " + face.texture + " for " + name);
                }
              }
            }
          }
          return faces;
        }

        @Override
        public JsonValue toJson() {
          // TODO
          return new JsonObject();
        }
      };
    }

    public void rotateX(int angle, boolean uvlock) {
      for (JsonModelElement element : elements) {
        for (JsonModelFace face : element.faces) {
          if (face != null && face.quad != null) {
            // TODO angle sign might be wrong
            face.quad = face.quad.transform(Transform.NONE.rotateX(-Math.toRadians(angle)));
          }
        }
        if (uvlock) {
          // TODO angle sign might be wrong
          if (element.faces[3] != null && element.faces[3].quad != null) {
            element.faces[3].quad.textureRotation -= angle;
          }
          if (element.faces[5] != null && element.faces[5].quad != null) {
            element.faces[5].quad.textureRotation -= angle;
          }
        }
      }
    }

    public void rotateY(int angle, boolean uvlock) {
      for (JsonModelElement element : elements) {
        for (JsonModelFace face : element.faces) {
          if (face != null && face.quad != null) {
            face.quad = face.quad.transform(Transform.NONE.rotateY(-Math.toRadians(angle)));
          }
        }
        if (uvlock) {
          if (element.faces[0] != null && element.faces[0].quad != null) {
            element.faces[0].quad.textureRotation -= angle;
          }
          if (element.faces[1] != null && element.faces[1].quad != null) {
            element.faces[1].quad.textureRotation -= angle;
          }
        }
      }
    }

    public void rotateZ(int angle, boolean uvlock) {
      for (JsonModelElement element : elements) {
        for (JsonModelFace face : element.faces) {
          if (face != null && face.quad != null) {
            // TODO angle sign might be wrong
            face.quad = face.quad.transform(Transform.NONE.rotateZ(-Math.toRadians(angle)));
          }
        }
        if (uvlock) {
          // TODO angle sign might be wrong
          if (element.faces[2] != null && element.faces[2].quad != null) {
            element.faces[2].quad.textureRotation -= angle;
          }
          if (element.faces[4] != null && element.faces[4].quad != null) {
            element.faces[4].quad.textureRotation -= angle;
          }
        }
      }
    }
  }

  private static class MultipartJsonModel extends Block {
    private final JsonModel[] parts;

    public MultipartJsonModel(String name, JsonModel[] parts) {
      super(name, parts.length > 0 ? parts[0].texture : Texture.EMPTY_TEXTURE);
      localIntersect = true;
      opaque = false;
      this.parts = parts;

      // TODO this block is opaque if one of the parts is opaque
    }

    @Override
    public boolean intersect(Ray ray, Scene scene) {
      if (isEntity()) {
        return false;
      }
      boolean hit = false;
      ray.t = Double.POSITIVE_INFINITY;
      for (JsonModel part : parts) {
        for (JsonModelElement element : part.elements) {
          hit |= element.intersect(ray, scene);
        }
      }
      if (hit) {
        ray.color.w = 1;
        ray.distance += ray.t;
        ray.o.scaleAdd(ray.t, ray.d);
      }
      return hit;
    }

    @Override
    public boolean isEntity() {
      for (JsonModel part : parts) {
        if (part.isEntity()) {
          return true;
        }
      }
      return true;
    }

    @Override
    public Entity toEntity(Vector3 position) {
      return new Entity(position) {
        @Override
        public Collection<Primitive> primitives(Vector3 offset) {
          Collection<Primitive> faces = new LinkedList<>();
          Transform transform =
            Transform.NONE.translate(
              position.x + offset.x, position.y + offset.y, position.z + offset.z);
          for (JsonModel part : parts) {
            for (JsonModelElement element : part.elements) {
              for (JsonModelFace face : element.faces) {
                if (face != null && face.quad != null) {
                  Texture texture = part.textures.get(face.texture);
                  if (texture != null) {
                    face.quad.addTriangles(faces, new TextureMaterial(texture), transform);
                  } else {
                    System.out.println("Missing texture " + face.texture + " for " + name);
                  }
                }
              }
            }
          }
          return faces;
        }

        @Override
        public JsonValue toJson() {
          return new JsonObject();
        }
      };
    }
  }
}
