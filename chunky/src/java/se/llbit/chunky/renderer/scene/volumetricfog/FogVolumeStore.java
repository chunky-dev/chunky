package se.llbit.chunky.renderer.scene.volumetricfog;

import java.util.Objects;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.*;
import se.llbit.math.bvh.BVH;
import se.llbit.util.JsonSerializable;
import se.llbit.util.TaskTracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Wrapper for fog volumes.
 */
public class FogVolumeStore implements Intersectable, JsonSerializable {
  private static final float DEFAULT_DENSITY = 0.1f;

  private final ArrayList<ContinuousFogVolume> continuousFogVolumes = new ArrayList<>(0);
  private final ArrayList<DiscreteFogVolume> discreteFogVolumes = new ArrayList<>(0);
  private BVH fogVolumeBVH = BVH.EMPTY;

  public FogVolumeStore() {
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    JsonArray continuousFogVolumesArray = new JsonArray();
    continuousFogVolumes.stream()
        .map(FogVolume::toJson)
        .filter(Objects::nonNull)
        .forEach(continuousFogVolumesArray::add);
    json.add("continuousFogVolumes", continuousFogVolumesArray);
    JsonArray discreteFogVolumesArray = new JsonArray();
    discreteFogVolumes.stream()
        .map(FogVolume::toJson)
        .filter(Objects::nonNull)
        .forEach(discreteFogVolumesArray::add);
    json.add("discreteFogVolumes", discreteFogVolumesArray);
    return json;
  }

  public void fromJson(JsonObject json) {
    clear();
    JsonArray continuousFogVolumesArray = json.get("continuousFogVolumes").array();
    for (JsonValue element : continuousFogVolumesArray) {
      JsonObject fogVolumeJson = element.asObject();
      FogVolumeShape shape = FogVolumeShape.valueOf(fogVolumeJson.get("shape").stringValue(""));
      FogVolume fogVolume = fromShape(shape);
      if (fogVolume != null) {
        fogVolume.fromJson(fogVolumeJson);
        addVolume(fogVolume);
      }
    }
    JsonArray discreteFogVolumesArray = json.get("discreteFogVolumes").array();
    for (JsonValue element : discreteFogVolumesArray) {
      JsonObject fogVolumeJson = element.asObject();
      FogVolumeShape shape = FogVolumeShape.valueOf(fogVolumeJson.get("shape").stringValue(""));
      FogVolume fogVolume = fromShape(shape);
      if (fogVolume != null) {
        fogVolume.fromJson(fogVolumeJson);
        addVolume(fogVolume);
      }
    }
  }

  public void copyState(FogVolumeStore other) {
    this.continuousFogVolumes.clear();
    this.continuousFogVolumes.addAll(other.continuousFogVolumes);
    this.discreteFogVolumes.clear();
    this.discreteFogVolumes.addAll(other.discreteFogVolumes);
    this.fogVolumeBVH = other.fogVolumeBVH;
  }

  public List<FogVolume> listFogVolumes() {
    return Stream.concat(this.continuousFogVolumes.stream(), this.discreteFogVolumes.stream()).toList();
  }

  private FogVolume fromShape(FogVolumeShape shape) {
    switch (shape) {
      case EXPONENTIAL: {
        ExponentialFogVolume fogVolume = new ExponentialFogVolume();
        fogVolume.material.volumeDensity = DEFAULT_DENSITY;
        return fogVolume;
      }
      case LAYER: {
        LayerFogVolume fogVolume = new LayerFogVolume();
        fogVolume.material.volumeDensity = DEFAULT_DENSITY;
        return fogVolume;
      }
      case CUBOID: {
        CuboidFogVolume fogVolume = new CuboidFogVolume();
        fogVolume.material.volumeDensity = DEFAULT_DENSITY;
        return fogVolume;
      }
      case SPHERE: {
        SphericalFogVolume fogVolume = new SphericalFogVolume();
        fogVolume.material.volumeDensity = DEFAULT_DENSITY;
        return fogVolume;
      }
      default:
        return null;
    }
  }

  /**
   * Adds a fog volume to the fog volume store.
   * @param shape The shape of the fog volume to add
   * @return Whether to rebuild the fog volume BVH
   */
  public boolean addVolume(FogVolumeShape shape) {
    FogVolume fogVolume = fromShape(shape);
    if (fogVolume != null) {
      return addVolume(fogVolume);
    }
    return false;
  }

  private boolean addVolume(FogVolume fogVolume) {
    if (fogVolume.isDiscrete()) {
      discreteFogVolumes.add((DiscreteFogVolume) fogVolume);
      return true;
    } else {
      continuousFogVolumes.add((ContinuousFogVolume) fogVolume);
      return false;
    }
  }

  public boolean removeVolume(int index) {
    if (index < continuousFogVolumes.size()) {
      continuousFogVolumes.remove(index);
      return false;
    } else {
      discreteFogVolumes.remove(index - continuousFogVolumes.size());
      return true;
    }
  }

  public void clear() {
    this.continuousFogVolumes.clear();
    this.discreteFogVolumes.clear();
  }

  public void finalizeLoading() {
    this.continuousFogVolumes.trimToSize();
    this.discreteFogVolumes.trimToSize();
  }

  @Override
  public boolean closestIntersection(Ray2 ray, IntersectionRecord intersectionRecord, Scene scene, Random random) {
    boolean hit = fogVolumeBVH.closestIntersection(ray, intersectionRecord, scene, random);

    IntersectionRecord intersectionTest = new IntersectionRecord();
    for (ContinuousFogVolume fogVolume : continuousFogVolumes) {
      if (fogVolume.closestIntersection(ray, intersectionTest, scene, random) && intersectionTest.distance < intersectionRecord.distance) {
        hit = true;
        intersectionRecord.distance = intersectionTest.distance;
        intersectionRecord.material = intersectionTest.material;
        intersectionRecord.color.set(intersectionTest.color);
        intersectionRecord.flags = intersectionTest.flags;
      }
      intersectionTest.reset();
    }

    return hit;
  }

  public void buildBvh(TaskTracker.Task task, Vector3i origin) {
    Vector3 worldOffset = new Vector3(-origin.x, -origin.y, -origin.z);
    fogVolumeBVH = BVH.Factory.create("SAH_MA", Collections.unmodifiableList(discreteFogVolumes), worldOffset, task);
  }
}
