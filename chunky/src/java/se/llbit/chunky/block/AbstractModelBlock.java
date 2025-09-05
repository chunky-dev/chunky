package se.llbit.chunky.block;

import se.llbit.chunky.block.minecraft.Air;
import se.llbit.chunky.block.minecraft.Water;
import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.minecraft.WaterModel;
import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.*;

import java.util.Random;

@PluginApi
public abstract class AbstractModelBlock extends MinecraftBlock implements ModelBlock {

  protected BlockModel model;

  public AbstractModelBlock(String name, Texture texture) {
    super(name, texture);
    localIntersect = true;
    opaque = false;
    solid = false;
  }

  @Override
  public int faceCount() {
    return model.faceCount();
  }

  @Override
  public void sample(int face, Vector3 loc, Random rand) {
    model.sample(face, loc, rand);
  }

  @Override
  public double surfaceArea(int face) {
    return model.faceSurfaceArea(face);
  }

  @Override
  public BlockModel getModel() {
    return model;
  }

  public static boolean onEdge(Vector3 o, Vector3 d, double distance) {
    o.scaleAdd(distance, d);
    double ix = o.x - QuickMath.floor(o.x);
    double iy = o.y - QuickMath.floor(o.y);
    double iz = o.z - QuickMath.floor(o.z);
    return !(Constants.EPSILON < ix && ix < 1 - Constants.EPSILON &&
      Constants.EPSILON < iy && iy < 1 - Constants.EPSILON &&
      Constants.EPSILON < iz && iz < 1 - Constants.EPSILON);
  }

  @Override
  public boolean intersect(Ray ray, IntersectionRecord intersectionRecord, Scene scene) {
    Intersectable waterModel = null;

    // TODO this shouldn't be checked at intersection time, but rather when loading chunks.
    boolean isWaterloggedFull = false;
    if (waterlogged) {
      int x = (int) QuickMath.floor(ray.o.x + ray.d.x * Constants.OFFSET);
      int y = (int) QuickMath.floor(ray.o.y + ray.d.y * Constants.OFFSET);
      int z = (int) QuickMath.floor(ray.o.z + ray.d.z * Constants.OFFSET);
      isWaterloggedFull = scene.getWorldOctree().getMaterial(x, y + 1, z, scene.getPalette()).isWaterFilled();
      if (ray.getCurrentMedium().isWater()) {
        if (!isWaterloggedFull) {
          waterModel = WaterModel.WATER_TOP;
        }
      } else {
        waterModel = (isWaterloggedFull) ? Block.FULL_BLOCK : WaterModel.NOT_FULL_BLOCK;
      }
    }

    IntersectionRecord modelIntersect = new IntersectionRecord();
    IntersectionRecord waterIntersect = new IntersectionRecord();

    boolean modelHit = model.intersect(ray, modelIntersect, scene);
    boolean waterHit = false;
    if (waterModel != null) {
      waterHit = waterModel.closestIntersection(ray, waterIntersect, scene);
    }

    boolean hitTop = waterHit && !isWaterloggedFull && waterIntersect.n.y > 0 && ray.d.dot(waterIntersect.n) > 0;

    if (ray.getCurrentMedium() == this) {
      if (modelHit) {
        intersectionRecord.setNormal(modelIntersect);
        if (ray.d.dot(intersectionRecord.n) > 0) {
          Vector3 o = new Vector3(ray.o);
          if (onEdge(ray.o, ray.d, modelIntersect.distance)) {
            return false;
          }
          intersectionRecord.n.scale(-1);
          intersectionRecord.shadeN.scale(-1);

          Block waterPlaneMaterial = scene.waterPlaneMaterial(ray.o.rScaleAdd(intersectionRecord.distance, ray.d));
          if (waterlogged) {
            if (isWaterloggedFull || o.y < 1 - WaterModel.TOP_BLOCK_GAP) {
              intersectionRecord.material = scene.getPalette().water;
              Water.INSTANCE.getColor(intersectionRecord);
            } else {
              intersectionRecord.material = waterPlaneMaterial;
              waterPlaneMaterial.getColor(intersectionRecord);
            }
          } else {
            intersectionRecord.material = waterPlaneMaterial;
            waterPlaneMaterial.getColor(intersectionRecord);
          }
        } else {
          intersectionRecord.color.set(modelIntersect.color);
        }
        intersectionRecord.distance = modelIntersect.distance;
        intersectionRecord.flags = modelIntersect.flags;
        return true;
      } else {
        return false;
      }
    } else if (ray.getCurrentMedium().isWater()) {
      if (!waterlogged && (!modelHit || modelIntersect.distance > Constants.EPSILON)) {
        intersectionRecord.distance = 0;
        intersectionRecord.material = Air.INSTANCE;
        Air.INSTANCE.getColor(intersectionRecord);
        return true;
      }
      if (modelHit && modelIntersect.distance < waterIntersect.distance - Constants.EPSILON) {
        intersectionRecord.distance = modelIntersect.distance;
        intersectionRecord.setNormal(modelIntersect);
        intersectionRecord.color.set(modelIntersect.color);
        intersectionRecord.flags = modelIntersect.flags;
        return true;
      } else if (hitTop) {
        intersectionRecord.distance = waterIntersect.distance;
        intersectionRecord.setNormal(waterIntersect);

        Ray testRay = new Ray(ray);
        testRay.o.scaleAdd(intersectionRecord.distance, testRay.d);
        intersectionRecord.material = scene.waterPlaneMaterial(testRay.o);
        intersectionRecord.material.getColor(intersectionRecord);

        Vector3 shadeNormal = scene.getCurrentWaterShader().doWaterShading(testRay, intersectionRecord, scene.getAnimationTime());
        intersectionRecord.shadeN.set(shadeNormal);

        intersectionRecord.n.scale(-1);
        intersectionRecord.shadeN.scale(-1);
        return true;
      } else {
        return false;
      }
    } else {
      if (!waterlogged && (!modelHit || modelIntersect.distance > Constants.EPSILON)) {
        Block waterPlaneMaterial;
        if (ray.getCurrentMedium() != (waterPlaneMaterial = scene.waterPlaneMaterial(ray.o))) {
          intersectionRecord.distance = 0;
          intersectionRecord.material = waterPlaneMaterial;
          waterPlaneMaterial.getColor(intersectionRecord);
          return true;
        }
      }
      if (modelHit && modelIntersect.distance < waterIntersect.distance + Constants.EPSILON) {
        intersectionRecord.distance = modelIntersect.distance;
        intersectionRecord.setNormal(modelIntersect);
        intersectionRecord.color.set(modelIntersect.color);
        intersectionRecord.flags = modelIntersect.flags;
        return true;
      } else if (waterHit) {
        intersectionRecord.distance = waterIntersect.distance;
        intersectionRecord.setNormal(waterIntersect);
        Water.INSTANCE.getColor(intersectionRecord);
        intersectionRecord.material = scene.getPalette().water;
        intersectionRecord.flags = 0;

        if (intersectionRecord.n.y > 0) {
          Ray testRay = new Ray(ray);
          testRay.o.scaleAdd(intersectionRecord.distance, testRay.d);
          Vector3 shadeNormal = scene.getCurrentWaterShader().doWaterShading(testRay, intersectionRecord, scene.getAnimationTime());
          intersectionRecord.shadeN.set(shadeNormal);
        }
        return true;
      } else {
        return false;
      }
    }
  }

  @Override
  public boolean isInside(Ray ray) {
    return model.isInside(ray);
  }
}
