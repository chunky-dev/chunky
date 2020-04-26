package se.llbit.chunky.block;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.AnimatedTexture;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonMember;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;
import se.llbit.json.JsonParser.SyntaxError;
import se.llbit.json.JsonValue;
import se.llbit.log.Log;
import se.llbit.math.AABB;
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
  private final Map<String, BlockVariants> blocks = new HashMap<>();

  public ResourcepackBlockProvider(File file) throws IOException {
    Log.info("Loading blocks from " + file.getAbsolutePath());
    try (FileSystem zip =
        FileSystems.newFileSystem(URI.create("jar:" + file.toURI()), Collections.emptyMap())) {
      JsonModelLoader modelLoader = new JsonModelLoader();
      Files.list(zip.getPath("assets"))
          .filter(Files::isDirectory)
          .map(assetProvider -> assetProvider.resolve("blockstates"))
          .filter(Files::isDirectory)
          .forEach(
              assets -> {
                final String assetsName = assets.getParent().getFileName().toString();
                try {
                  Files.list(assets)
                      .forEach(
                          block -> {
                            String blockName = block.getFileName().toString();
                            blockName = blockName.substring(0, blockName.length() - 5);

                            BlockVariants variants = new BlockVariants();
                            try {
                              JsonObject blockStates =
                                  new JsonParser(Files.newInputStream(block)).parse().object();
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
                                        modelLoader.loadBlockModel(zip, modelName, blockName);
                                    if (model instanceof JsonModel) {
                                      if (blockDefinition.get("y").doubleValue(0) > 0) {
                                        ((JsonModel) model)
                                            .rotateY(
                                                blockDefinition.get("y").intValue(0),
                                                blockDefinition.get("uvlock").boolValue(false));
                                      }
                                      // TODO rotateX, rotateZ
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
                                      modelLoader.loadBlockModel(zip, modelName, blockName);
                                  if (model instanceof JsonModel) {
                                    if (blockDefinition.get("y").doubleValue(0) > 0) {
                                      ((JsonModel) model)
                                          .rotateY(
                                              blockDefinition.get("y").intValue(0),
                                              blockDefinition.get("uvlock").boolValue(false));
                                    }
                                    // TODO rotateX, rotateZ
                                  }
                                  multipartBlockVariant.addPart(
                                      new MultipartBlockVariant(
                                          part.object().get("when").object(), model));
                                }
                                variants.variants.add(multipartBlockVariant);
                              } else {
                                throw new Error("Unsupported block " + blockName);
                              }
                            } catch (IOException | SyntaxError e) {
                              throw new Error(e);
                            }

                            blocks.put(assetsName + ":" + blockName, variants);
                          });
                } catch (IOException e) {
                  throw new Error(e);
                }
              });
    }
  }

  @Override
  public Block getBlockByTag(String name, Tag tag) {
    BlockVariants variants = blocks.get(name);
    return variants != null ? variants.getBlock(tag) : null;
  }

  @Override
  public Collection<String> getSupportedBlocks() {
    return blocks.keySet();
  }

  private static class BlockVariants {
    private final List<BlockVariant> variants = new ArrayList<>();

    public Block getBlock(Tag tag) {
      Tag properties = tag.get("Properties");
      for (BlockVariant variant : variants) {
        if (variant.isMatch(properties)) {
          return variant.getBlock(properties);
        }
      }
      return UnknownBlock.UNKNOWN;
    }
  }

  private interface BlockVariant {
    boolean isMatch(Tag properties);

    Block getBlock(Tag properties);
  }

  private static class SimpleBlockVariant implements BlockVariant {
    private final Block block;

    private SimpleBlockVariant(Block block) {
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

  private static class VariantsBlockVariant implements BlockVariant {
    protected final Map<String, String> conditions = new HashMap<>();
    private final Block model;

    private VariantsBlockVariant(String conditions, Block model) {
      this.model = model;

      for (String condition : conditions.split(",")) {
        String[] parts = condition.trim().split("=");
        if (parts.length < 2) {
          break;
        }
        this.conditions.put(parts[0], parts[1]);
      }

      // TODO handle rotation
      // this.model = model.rotate(x, y, z);
    }

    protected VariantsBlockVariant(Block model) {
      this.model = model;
    }

    @Override
    public boolean isMatch(Tag properties) {
      for (Entry<String, String> property : conditions.entrySet()) {
        if (!properties.get(property.getKey()).stringValue("").equals(property.getValue())) {
          return false;
        }
      }
      return true;
    }

    @Override
    public Block getBlock(Tag properties) {
      return model;
    }
  }

  private static class BlockVariantMultipart implements BlockVariant {
    private final String name;
    private final List<VariantsBlockVariant> parts = new ArrayList<>();

    private BlockVariantMultipart(String name) {
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
            throw new Error("Multipart model part is not a JsonModel");
          }
        }
      }
      /*if (applicableParts.isEmpty()) {
        Log.warn("Empty multipart model for block " + name + " (" + properties.dumpTree() + ")");
      }*/
      return new MultipartJsonModel(name, applicableParts.toArray(new JsonModel[0]));
    }
  }

  private static class MultipartBlockVariant extends VariantsBlockVariant {
    private MultipartBlockVariant(JsonObject when, Block model) {
      super(model);
      for (JsonMember condition : when.members) {
        this.conditions.put(condition.getName(), condition.getValue().stringValue(""));
      }
    }
  }

  private static class JsonModelLoader {
    private final Map<String, JsonObject> models = new HashMap<>();
    private final Map<String, Texture> textures = new HashMap<>();

    public Texture getTexture(FileSystem zip, String textureName) {
      Texture texture = textures.get(textureName);
      if (texture == null) {
        String[] parts = textureName.split(":");
        if (parts.length < 2) {
          parts = new String[] {"minecraft", parts[0]};
        }
        // TODO <= 1.12 texture paths are prefixed
        try (InputStream inputStream =
            Files.newInputStream(zip.getPath("assets", parts[0], "textures", parts[1] + ".png"))) {
          BitmapImage image = ImageLoader.read(inputStream);
          if (image.width == image.height) {
            // textures are always squared...
            texture = new Texture(image);
          } else {
            // ...unless they are animated
            texture = new AnimatedTexture(image);
          }
        } catch (IOException e) {
          throw new Error("Could not load texture " + textureName, e);
        }
      }
      return texture;
    }

    private JsonObject getModel(FileSystem zip, String modelName) {
      JsonObject model = models.get(modelName);
      if (model == null) {
        String[] parts = modelName.split(":");
        if (parts.length < 2) {
          parts = new String[] {"minecraft", parts[0]};
        }
        // TODO <= 1.12 model paths are prefixed
        try (InputStream inputStream =
            Files.newInputStream(zip.getPath("assets", parts[0], "models", parts[1] + ".json"))) {
          model = new JsonParser(inputStream).parse().object();
          models.put(modelName, model);
        } catch (IOException | SyntaxError e) {
          throw new Error("Could not load block model " + modelName, e);
        }
      }
      return model;
    }

    public Block loadBlockModel(FileSystem zip, String model, String blockName) {
      if (model.equals("unknown:unknown")) {
        return UnknownBlock.UNKNOWN;
      }

      JsonModel block = new JsonModel(blockName, Texture.air);
      JsonObject blockDefinition = getModel(zip, model);
      block.applyDefinition(blockDefinition, name -> this.getTexture(zip, name));
      while (!blockDefinition.get("parent").stringValue("block/block").equals("block/block")) {
        String parentName = blockDefinition.get("parent").stringValue("block/block");
        blockDefinition = this.getModel(zip, parentName);
        block.applyDefinition(blockDefinition, name -> this.getTexture(zip, name));
        if (parentName.equals("block/cube_all")) {
          block.texture = block.textures.get("all");
          block.localIntersect = false;
        }
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
        throw new Error(face.toCompactString());
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
          quad.textureRotation = FastMath.toRadians(rotation); // -angle or +angle?
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
          quad.textureRotation = FastMath.toRadians(rotation); // -angle or +angle?
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
                    : new Vector4(from.z / 16, to.z / 16, from.y / 16, to.y / 16));

        if (rotation > 0) {
          quad.textureRotation = FastMath.toRadians(rotation);
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
                    : new Vector4(to.z / 16, from.z / 16, 1 - from.y / 16, 1 - to.y / 16));

        if (rotation > 0) {
          quad.textureRotation = FastMath.toRadians(rotation);
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
                    : new Vector4(to.x / 16, from.x / 16, 1 - from.y / 16, 1 - to.y / 16));

        if (rotation > 0) {
          quad.textureRotation = FastMath.toRadians(rotation); // -angle or +angle?
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
                    : new Vector4(to.x / 16, from.x / 16, 1 - from.y / 16, 1 - to.y / 16));

        if (rotation > 0) {
          quad.textureRotation = FastMath.toRadians(rotation); // -angle or +angle?
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
    private AABB box;
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
            break;
          case "y":
            transform =
                Transform.NONE
                    .translate(-origin.x + 0.5, -origin.y + 0.5, -origin.z + 0.5)
                    .rotateY(angle)
                    .translate(origin.x - 0.5, origin.y - 0.5, origin.z - 0.5);
            break;
          case "z":
            transform =
                Transform.NONE
                    .translate(-origin.x + 0.5, -origin.y + 0.5, -origin.z + 0.5)
                    .rotateZ(angle)
                    .translate(origin.x - 0.5, origin.y - 0.5, origin.z - 0.5);
            break;
        }

        for (JsonModelFace face : faces) {
          if (face != null) {
            face.quad = face.quad.transform(transform);
            // TODO rescale
          }
        }
      }
    }

    public boolean intersect(Ray ray, Scene scene) {
      if (this.box != null) {
        boolean hit = false;
        if (box.intersect(ray)) {
          int faceIndex = -1;
          Vector3 rayNormal = ray.getNormal();
          if (rayNormal.y > 0) {
            faceIndex = 0;
          } else if (rayNormal.y < 0) {
            faceIndex = 1;
          } else if (rayNormal.x < 0) {
            faceIndex = 5;
          } else if (rayNormal.x > 0) {
            faceIndex = 3;
          } else if (rayNormal.z < 0) {
            faceIndex = 2;
          } else if (rayNormal.z > 0) {
            faceIndex = 4;
          }

          if (faceIndex >= 0) {
            JsonModelFace face = faces[faceIndex];
            if (face != null) {
              float[] color = face.getColor(ray, scene, model.textures.get(face.texture));
              if (color[3] > Ray.EPSILON) {
                ray.color.set(color);
                hit = true;
              }
            }
          }

          if (hit) {
            ray.t = ray.tNext;
            return true;
          }
        }
      } else {
        // TODO quad based models for everything that's not a simple opaque cube?
        boolean hit = false;

        for (int faceIndex = 0; faceIndex < 6; faceIndex++) {
          JsonModelFace face = faces[faceIndex];
          if (face != null && face.quad != null && face.quad.intersect(ray)) {
            float[] color = face.getColor(ray, scene, model.textures.get(face.texture));
            if (color[3] > Ray.EPSILON) {
              ray.color.set(color);
              ray.n.set(face.quad.n);
              ray.t = ray.tNext;
              hit = true;
            }
          }
        }

        if (hit) {
          return true;
        }
      }

      return false;
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

  private static class JsonModel extends Block {
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
              throw new Error("Unknown referenced texture " + texture.getValue().stringValue(""));
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
      if (isBlockEntity()) {
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
    public boolean isBlockEntity() {
      return isBlockEntity;
    }

    @Override
    public Entity toBlockEntity(Vector3 position, CompoundTag entityTag) {
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
                face.quad.addTriangles(
                    faces, new TextureMaterial(textures.get(face.texture)), transform);
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

    public void rotateY(int angle, boolean uvlock) {
      for (JsonModelElement element : elements) {
        for (JsonModelFace face : element.faces) {
          if (face != null && face.quad != null)
            face.quad = face.quad.transform(Transform.NONE.rotateY(-Math.toRadians(angle)));
        }
        if (uvlock) {
          if (element.faces[0] != null && element.faces[0].quad != null) {
            element.faces[0].quad.textureRotation -= Math.toRadians(angle);
          }
          if (element.faces[1] != null && element.faces[1].quad != null) {
            element.faces[1].quad.textureRotation -= Math.toRadians(angle);
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
    public boolean isBlockEntity() {
      for (JsonModel part : parts) {
        if (part.isBlockEntity()) {
          return true;
        }
      }
      return false;
    }

    @Override
    public Entity toBlockEntity(Vector3 position, CompoundTag entityTag) {
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
                face.quad.addTriangles(
                    faces, new TextureMaterial(part.textures.get(face.texture)), transform);
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
