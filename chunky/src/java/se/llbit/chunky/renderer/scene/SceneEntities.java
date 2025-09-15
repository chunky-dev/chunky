package se.llbit.chunky.renderer.scene;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.chunk.ChunkData;
import se.llbit.chunky.entity.*;
import se.llbit.chunky.world.Dimension;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.log.Log;
import se.llbit.math.*;
import se.llbit.math.bvh.BVH;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.ListTag;
import se.llbit.nbt.Tag;
import se.llbit.util.NbtUtil;
import se.llbit.util.TaskTracker;
import se.llbit.util.mojangapi.MinecraftProfile;
import se.llbit.util.mojangapi.MinecraftSkin;
import se.llbit.util.mojangapi.MojangApi;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

/**
 * Encapsulates entity handling for a scene.
 */
public class SceneEntities implements Intersectable {

  private EntityLoadingPreferences entityLoadingPreferences = new EntityLoadingPreferences();

  /**
   * Entities in the scene.
   */
  private ArrayList<Entity> entities = new ArrayList<>();

  /**
   * Poseable entities in the scene.
   */
  private ArrayList<Entity> actors = new ArrayList<>();

  protected boolean renderActors = true;

  /**
   * Poseable entities in the scene.
   */
  private Map<PlayerEntity, MinecraftProfile> profiles = new HashMap<>();

  private BVH bvh = BVH.EMPTY;
  private BVH actorBvh = BVH.EMPTY;

  /**
   * The BVH implementation to use
   */
  private String bvhImplementation = PersistentSettings.getBvhMethod();

  public void copyState(SceneEntities other) {
    entityLoadingPreferences = other.entityLoadingPreferences;
    entities = other.entities;

    actors.clear();
    actors.addAll(other.actors); // Create a copy so that entity changes can be reset.
    actors.trimToSize();
    renderActors = other.renderActors;

    profiles = other.profiles;

    bvh = other.bvh;
    actorBvh = other.actorBvh;

    bvhImplementation = other.bvhImplementation;
  }

  @Override
  public boolean closestIntersection(Ray ray, IntersectionRecord intersectionRecord, Scene scene, Random random) {
    boolean hit = bvh.closestIntersection(ray, intersectionRecord, scene);

    if (renderActors) {
      IntersectionRecord intersectionTest = new IntersectionRecord();
      if (actorBvh.closestIntersection(ray, intersectionTest, scene) && intersectionTest.distance < intersectionRecord.distance - Constants.EPSILON) {
        hit = true;
        intersectionRecord.distance = intersectionTest.distance;
        intersectionRecord.setNormal(intersectionTest);
        intersectionRecord.color.set(intersectionTest.color);
        intersectionRecord.material = intersectionTest.material;
        intersectionRecord.flags = intersectionTest.flags;
      }
    }

    return hit;
  }

  public void loadPlayers(TaskTracker.Task task, Dimension dimension) {
    entities.clear();
    if (actors.isEmpty() && PersistentSettings.getLoadPlayers()) {
      // We don't load actor entities if some already exists. Loading actor entities
      // risks resetting posed actors when reloading chunks for an existing scene.
      actors.clear();
      profiles = new HashMap<>();
      Collection<PlayerEntity> players = dimension.getPlayerEntities();
      int done = 1;
      int target = players.size();
      for (PlayerEntity entity : players) {
        entity.randomPose();
        task.update(target, done);
        done += 1;
        MinecraftProfile profile;
        try {
          profile = MojangApi.fetchProfile(entity.uuid);
          Optional<MinecraftSkin> skin = profile.getSkin();
          if (skin.isPresent()) {
            String skinUrl = skin.get().getSkinUrl();
            if (skinUrl != null) {
              entity.skin = MojangApi.downloadSkin(skinUrl).getAbsolutePath();
            }
            entity.model = skin.get().getPlayerModel();
          }
        } catch (IOException e) {
          Log.error(e);
          profile = new MinecraftProfile();
        }
        profiles.put(entity, profile);
        actors.add(entity);
      }
    }
    finalizeLoading();
  }

  public void loadEntitiesInChunk(Scene scene, ChunkData chunkData) {
    for (CompoundTag tag : chunkData.getEntities()) {
      Tag posTag = tag.get("Pos");
      if (posTag.isList()) {
        ListTag pos = posTag.asList();
        double x = pos.get(0).doubleValue();
        double y = pos.get(1).doubleValue();
        double z = pos.get(2).doubleValue();

        if (y >= scene.yClipMin && y < scene.yClipMax) {
          String id = tag.get("id").stringValue("");
          // Before 1.12 paintings had id=Painting.
          // After 1.12 paintings had id=minecraft:painting.
          if ((id.equals("minecraft:painting") || id.equals("Painting")) && entityLoadingPreferences.shouldLoadClass(PaintingEntity.class)) {
            Tag paintingVariant = NbtUtil.getTagFromNames(tag, "Motive", "variant");
            int facing = (tag.get("facing").isError())
              ? tag.get("Facing").byteValue(0) // pre 1.17
              : tag.get("facing").byteValue(0); // 1.17+
            addEntity(new PaintingEntity(
              new Vector3(x, y, z),
              paintingVariant.stringValue(),
              facing
            ));
          } else if (id.equals("minecraft:armor_stand") && entityLoadingPreferences.shouldLoadClass(ArmorStand.class)) {
            addActor(new ArmorStand(
              new Vector3(x, y, z),
              tag
            ));
          } else if ((id.equals("minecraft:sheep") || id.equals("Sheep")) && entityLoadingPreferences.shouldLoadClass(SheepEntity.class)) {
            addActor(new SheepEntity(new Vector3(x, y, z), tag));
          } else if ((id.equals("minecraft:cow") || id.equals("Cow")) && entityLoadingPreferences.shouldLoadClass(CowEntity.class)) {
            addActor(new CowEntity(new Vector3(x, y, z), tag));
          } else if ((id.equals("minecraft:chicken") || id.equals("Chicken")) && entityLoadingPreferences.shouldLoadClass(ChickenEntity.class)) {
            addActor(new ChickenEntity(new Vector3(x, y, z), tag));
          } else if ((id.equals("minecraft:pig") || id.equals("Pig")) && entityLoadingPreferences.shouldLoadClass(PigEntity.class)) {
            addActor(new PigEntity(new Vector3(x, y, z), tag));
          } else if ((id.equals("minecraft:mooshroom") || id.equals("MushroomCow")) && entityLoadingPreferences.shouldLoadClass(MooshroomEntity.class)) {
            addActor(new MooshroomEntity(new Vector3(x, y, z), tag));
          } else if ((id.equals("minecraft:squid") || id.equals("Squid")) && entityLoadingPreferences.shouldLoadClass(SquidEntity.class)) {
            addActor(new SquidEntity(new Vector3(x, y, z), tag));
          }
        }
      }
    }
  }

  public boolean shouldLoad(Entity entity) {
    return entityLoadingPreferences.shouldLoad(entity);
  }

  public void addEntity(Entity entity) {
    entities.add(entity);
  }

  public void addActor(Entity entity) {
    // don't add the actor again if it was already loaded from json
    if (actors.stream().noneMatch(actor -> {
      if (actor.getClass().equals(entity.getClass())) {
        Vector3 distance = new Vector3(actor.position);
        distance.sub(entity.position);
        return distance.lengthSquared() < Constants.EPSILON;
      }
      return false;
    })) {
      actors.add(entity);
    }
  }

  public void addPlayer(PlayerEntity playerEntity) {
    if (!actors.contains(playerEntity)) {
      profiles.put(playerEntity, new MinecraftProfile());
      actors.add(playerEntity);
    } else {
      Log.warn("Failed to add player: entity already exists (" + playerEntity + ")");
    }
    actors.trimToSize();
  }

  public List<Entity> getEntities() {
    return Collections.unmodifiableList(entities);
  }

  public List<Entity> getActors() {
    return Collections.unmodifiableList(actors);
  }

  public Stream<PlayerEntity> getPlayers() {
    return getActors()
      .stream()
      .filter(PlayerEntity.class::isInstance)
      .map(PlayerEntity.class::cast);
  }

  public MinecraftProfile getAssociatedProfile(PlayerEntity player) {
    return profiles.get(player);
  }

  public EntityLoadingPreferences getEntityLoadingPreferences() {
    return entityLoadingPreferences;
  }

  public String getBvhImplementation() {
    return bvhImplementation;
  }

  public void setBvhImplementation(String bvhImplementation) {
    this.bvhImplementation = bvhImplementation;
  }

  public void loadDataFromOctree(
    Octree worldOctree,
    BlockPalette palette,
    Vector3i origin
  ) {
    for (Entity entity : actors) {
      entity.loadDataFromOctree(worldOctree, palette, origin);
    }
    for (Entity entity : entities) {
      entity.loadDataFromOctree(worldOctree, palette, origin);
    }
  }

  public void buildBvh(TaskTracker.Task task, Vector3i origin) {
    Vector3 worldOffset = new Vector3(-origin.x, -origin.y, -origin.z);
    bvh = BVH.Factory.create(bvhImplementation, Collections.unmodifiableList(entities), worldOffset, task);
  }

  public void buildActorBvh(TaskTracker.Task task, Vector3i origin) {
    Vector3 worldOffset = new Vector3(-origin.x, -origin.y, -origin.z);
    actorBvh = BVH.Factory.create(bvhImplementation, Collections.unmodifiableList(actors), worldOffset, task);
  }

  public void finalizeLoading() {
    entities.trimToSize();
    actors.trimToSize();
  }

  public void removeEntity(Entity entity) {
    if (entity instanceof PlayerEntity) {
      profiles.remove(entity);
    }
    actors.remove(entity);
  }

  public void clear() {
    entities.clear();
    actors.clear();
  }

  public void writeJsonData(JsonObject json) {
    json.add("renderActors", renderActors);

    JsonArray entityArray = new JsonArray();
    entities.stream()
      .map(Entity::toJson)
      .filter(Objects::nonNull)
      .forEach(entityArray::add);
    if (!entityArray.isEmpty()) {
      json.add("entities", entityArray);
    }

    JsonArray actorArray = new JsonArray();
    actors.stream()
      .map(Entity::toJson)
      .filter(Objects::nonNull)
      .forEach(actorArray::add);
    if (!actorArray.isEmpty()) {
      json.add("actors", actorArray);
    }

    json.add("entityLoadingPreferences", entityLoadingPreferences.toJson());
    json.add("bvhImplementation", bvhImplementation);
  }

  public void importJsonData(JsonObject json) {
    renderActors = json.get("renderActors").boolValue(renderActors);

    entityLoadingPreferences.fromJson(json.get("entityLoadingPreferences"));

    bvhImplementation = json.get("bvhImplementation").asString(PersistentSettings.getBvhMethod());

    if (json.get("entities").isArray() || json.get("actors").isArray()) {
      clear();
      // Previously poseable entities were stored in the entities array
      // rather than the actors array. In future versions only the actors
      // array should contain poseable entities.
      for (JsonValue element : json.get("entities").array()) {
        Entity entity = Entity.loadFromJson(element.object());
        if (entity != null) {
          if (entity instanceof PlayerEntity) {
            actors.add(entity);
          } else {
            entities.add(entity);
          }
        }
      }
      for (JsonValue element : json.get("actors").array()) {
        Entity entity = Entity.loadFromJson(element.object());
        actors.add(entity);
      }
    }

    finalizeLoading();
  }
}
