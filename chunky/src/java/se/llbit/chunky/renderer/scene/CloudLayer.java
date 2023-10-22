package se.llbit.chunky.renderer.scene;

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.block.minecraft.Air;
import se.llbit.chunky.world.Clouds;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.VolumeMaterial;
import se.llbit.chunky.world.material.CloudMaterial;
import se.llbit.chunky.world.material.VolumeCloudMaterial;
import se.llbit.json.JsonObject;
import se.llbit.math.ColorUtil;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.util.JsonSerializable;
import se.llbit.util.Pair;

import java.util.Random;

public class CloudLayer implements JsonSerializable {
  /**
   * Default cloud y-position
   */
  protected static final int DEFAULT_CLOUD_HEIGHT = 128;

  protected static final int DEFAULT_CLOUD_SIZE = 12;

  protected static final double DEFAULT_CLOUD_DENSITY = 0.2;

  private final Vector3 size = new Vector3(DEFAULT_CLOUD_SIZE, 4, DEFAULT_CLOUD_SIZE);
  private final Vector3 offset = new Vector3(0, DEFAULT_CLOUD_HEIGHT, 0);
  private final Vector3 color = new Vector3(1, 1, 1);
  private boolean volumetricClouds = false;
  private double density = DEFAULT_CLOUD_DENSITY;
  private final Material material = new CloudMaterial();
  private final VolumeMaterial volumeMaterial = new VolumeCloudMaterial();

  public Vector3 getCloudColor() {
    return new Vector3(color);
  }

  public void setCloudColor(Vector3 color) {
    this.color.set(color);
  }


  public boolean getVolumetricClouds() {
    return volumetricClouds;
  }

  public void setVolumetricClouds(boolean value) {
    volumetricClouds = value;
  }


  public double getCloudDensity() {
    return density;
  }

  public void setCloudDensity(double value) {
    density = value;
  }


  public double getCloudSizeX() {
    return size.x;
  }

  public void setCloudSizeX(double newValue) {
    if (newValue != size.x) {
      size.x = newValue;
    }
  }


  public double getCloudSizeY() {
    return size.y;
  }

  public void setCloudSizeY(double newValue) {
    if (newValue != size.y) {
      size.y = newValue;
    }
  }


  public double getCloudSizeZ() {
    return size.z;
  }

  public void setCloudSizeZ(double newValue) {
    if (newValue != size.z) {
      size.z = newValue;
    }
  }


  public double getCloudXOffset() {
    return offset.x;
  }

  public void setCloudXOffset(double newValue) {
    if (newValue != offset.x) {
      offset.x = newValue;
    }
  }


  /**
   * @return The current cloud height
   */
  public double getCloudYOffset() {
    return offset.y;
  }

  /**
   * Change the cloud height
   */
  public void setCloudYOffset(double newValue) {
    if (newValue != offset.y) {
      offset.y = newValue;
    }
  }


  public double getCloudZOffset() {
    return offset.z;
  }

  public void setCloudZOffset(double newValue) {
    if (newValue != offset.z) {
      offset.z = newValue;
    }
  }

  public float getEmittance() {
    return volumetricClouds ? volumeMaterial.emittance : material.emittance;
  }

  public void setEmittance(float value) {
    if (volumetricClouds) {
      volumeMaterial.emittance = value;
    } else {
      material.emittance = value;
    }
  }

  public float getSpecular() {
    return material.specular;
  }

  public void setSpecular(float value) {
    material.specular = value;
  }

  public float getSmoothness() {
    return (float) material.getPerceptualSmoothness();
  }

  public void setSmoothness(float value) {
    material.setPerceptualSmoothness(value);
  }

  public float getIor() {
    return material.ior;
  }

  public void setIor(float value) {
    material.ior = value;
  }

  public float getMetalness() {
    return material.metalness;
  }

  public void setMetalness(float value) {
    material.emittance = value;
  }

  public float getAnisotropy() {
    return volumeMaterial.anisotropy;
  }

  public void setAnisotropy(float value) {
    volumeMaterial.anisotropy = value;
  }

  private Pair<Double, Double> getCloudDistance(Scene scene, Ray ray) {
    Pair<Boolean, Boolean> cloudIntersectionTest = cloudIntersection(scene, ray);
    boolean hitCloud = cloudIntersectionTest.thing1;
    boolean insideCloud = cloudIntersectionTest.thing2;
    if (!hitCloud) {
      return new Pair<>(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    } else {
      if (!volumetricClouds) {
        double t = ray.t;
        double tExit;
        if (insideCloud) {
          tExit = 1d;
        } else {
          tExit = 0d;
        }
        return new Pair<>(t, tExit);
      } else {
        if (insideCloud) {
          double t;
          double tExit;
          t = 0;
          tExit = ray.t;
          return new Pair<>(t, tExit);
        } else {
          double t = ray.t;
          double tExit;
          double depth = 0;
          while (true) {
            if (depth >= 1000) {
              return new Pair<>(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
            }
            Ray nextIntersection = new Ray(ray);
            nextIntersection.t = Double.POSITIVE_INFINITY;
            nextIntersection.o.scaleAdd(t + Ray.OFFSET, ray.d);
            Pair<Boolean, Boolean> testSecondIntersection = cloudIntersection(scene, nextIntersection);
            if (!testSecondIntersection.thing1) {
              return new Pair<>(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
            } else {
              if (testSecondIntersection.thing2) {
                tExit = t + Ray.OFFSET + nextIntersection.t;
                break;
              } else {
                t += Ray.OFFSET + nextIntersection.t;
                depth++;
              }
            }
          }
          return new Pair<>(t, tExit);
        }
      }
    }
  }

  public boolean intersect(Scene scene, Ray ray, Random random) {
    if (random == null && volumetricClouds) {
      return false;
    }
    Ray test = new Ray(ray);
    test.t = ray.t;
    Pair<Double, Double> cloudDistance = getCloudDistance(scene, test);
    double firstIntersection = cloudDistance.thing1;
    if (firstIntersection == Double.POSITIVE_INFINITY) {
      return false;
    }
    double secondIntersection = cloudDistance.thing2;
    double t;
    if (volumetricClouds) {
      double testFirstIntersection = firstIntersection;
      double testSecondIntersection = secondIntersection;
      int depth = 0;
      while (true) {
        if (depth >= 1000) {
          return false;
        }
        double fogPenetrated = -FastMath.log(1 - random.nextDouble());
        double fogDistance = fogPenetrated / density;
        if (testFirstIntersection + fogDistance < testSecondIntersection) {
          t = testFirstIntersection + fogDistance;

          if (t >= ray.t) {
            return false;
          }

          volumeMaterial.setRandomSphericalNormal(ray, random);

          ray.setCurrentMaterial(volumeMaterial);
          ray.specular = false;
          break;
        } else {
          Ray test2 = new Ray(ray);
          test2.t = Double.POSITIVE_INFINITY;
          test2.o.scaleAdd(testSecondIntersection + Ray.OFFSET, ray.d);
          Pair<Double, Double> testCloudDistance = getCloudDistance(scene, test2);
          if (testCloudDistance.thing1 == Double.POSITIVE_INFINITY) {
            return false;
          }
          testFirstIntersection += (testSecondIntersection - testFirstIntersection) + Ray.OFFSET + testCloudDistance.thing1;
          testSecondIntersection += Ray.OFFSET + testCloudDistance.thing2;
          depth++;
        }
      }
    } else {
      t = firstIntersection;
      if (t >= ray.t) {
        return false;
      }
      ray.setNormal(test.getNormal());
      if (secondIntersection == 1) {
        ray.setCurrentMaterial(Air.INSTANCE);
      } else {
        ray.setCurrentMaterial(material);
      }
    }
    ray.t = t;
    ray.color.set(color.x, color.y, color.z, 1);
    return true;
  }

  /**
   * Test for a cloud intersection. If the ray intersects a cloud,
   * the distance to the intersection is stored in <code>ray.t</code>.
   * @param ray Ray with which to test for cloud intersection.
   * @return {@link se.llbit.util.Pair} of Booleans.
   * <code>pair.thing1</code>: <code>true</code> if the ray intersected a cloud.
   * <code>pair.thing2</code>: <code>true</code> if the ray origin is inside a cloud.
   */
  private Pair<Boolean, Boolean> cloudIntersection(Scene scene, Ray ray) {
    double ox = ray.o.x + scene.origin.x;
    double oy = ray.o.y + scene.origin.y;
    double oz = ray.o.z + scene.origin.z;
    double offsetX = offset.x;
    double offsetY = offset.y;
    double offsetZ = offset.z;
    double invSizeX = 1 / size.x;
    double invSizeZ = 1 / size.z;
    double cloudTop = offsetY + size.y;
    int target = 1;
    double t_offset = 0;
    if (oy < offsetY || oy > cloudTop) {
      if (ray.d.y > 0) {
        t_offset = (offsetY - oy) / ray.d.y;
      } else {
        t_offset = (cloudTop - oy) / ray.d.y;
      }
      if (t_offset < 0) {
        return new Pair<>(false, false);
      }
      // Ray is entering cloud.
      if (inCloud((ray.d.x * t_offset + ox) * invSizeX + offsetX,
        (ray.d.z * t_offset + oz) * invSizeZ + offsetZ)) {
        ray.setNormal(0, -Math.signum(ray.d.y), 0);
        ray.t = t_offset;
        return new Pair<>(true, false);
      }
    } else if (inCloud(ox * invSizeX + offsetX, oz * invSizeZ + offsetZ)) {
      target = 0;
    }
    double tExit;
    if (ray.d.y > 0) {
      tExit = (cloudTop - oy) / ray.d.y - t_offset;
    } else {
      tExit = (offsetY - oy) / ray.d.y - t_offset;
    }
    if (ray.t < tExit) {
      tExit = ray.t;
    }
    double x0 = (ox + ray.d.x * t_offset) * invSizeX + offsetX;
    double z0 = (oz + ray.d.z * t_offset) * invSizeZ + offsetZ;
    double xp = x0;
    double zp = z0;
    int ix = (int) Math.floor(xp);
    int iz = (int) Math.floor(zp);
    int xmod = (int) Math.signum(ray.d.x), zmod = (int) Math.signum(ray.d.z);
    int xo = (1 + xmod) / 2, zo = (1 + zmod) / 2;
    double dx = Math.abs(ray.d.x) * invSizeX;
    double dz = Math.abs(ray.d.z) * invSizeZ;
    double t = 0;
    int i = 0;
    int nx = 0, nz = 0;
    if (dx > dz) {
      double m = dz / dx;
      double xrem = xmod * (ix + xo - xp);
      double zlimit = xrem * m;
      while (t < tExit) {
        double zrem = zmod * (iz + zo - zp);
        if (zrem < zlimit) {
          iz += zmod;
          if (Clouds.getCloud(ix, iz) == target) {
            t = i / dx + zrem / dz;
            nx = 0;
            nz = -zmod;
            break;
          }
          ix += xmod;
          if (Clouds.getCloud(ix, iz) == target) {
            t = (i + xrem) / dx;
            nx = -xmod;
            nz = 0;
            break;
          }
        } else {
          ix += xmod;
          if (Clouds.getCloud(ix, iz) == target) {
            t = (i + xrem) / dx;
            nx = -xmod;
            nz = 0;
            break;
          }
          if (zrem <= m) {
            iz += zmod;
            if (Clouds.getCloud(ix, iz) == target) {
              t = i / dx + zrem / dz;
              nx = 0;
              nz = -zmod;
              break;
            }
          }
        }
        t = i / dx;
        i += 1;
        zp = z0 + zmod * i * m;
      }
    } else {
      double m = dx / dz;
      double zrem = zmod * (iz + zo - zp);
      double xlimit = zrem * m;
      while (t < tExit) {
        double xrem = xmod * (ix + xo - xp);
        if (xrem < xlimit) {
          ix += xmod;
          if (Clouds.getCloud(ix, iz) == target) {
            t = i / dz + xrem / dx;
            nx = -xmod;
            nz = 0;
            break;
          }
          iz += zmod;
          if (Clouds.getCloud(ix, iz) == target) {
            t = (i + zrem) / dz;
            nx = 0;
            nz = -zmod;
            break;
          }
        } else {
          iz += zmod;
          if (Clouds.getCloud(ix, iz) == target) {
            t = (i + zrem) / dz;
            nx = 0;
            nz = -zmod;
            break;
          }
          if (xrem <= m) {
            ix += xmod;
            if (Clouds.getCloud(ix, iz) == target) {
              t = i / dz + xrem / dx;
              nx = -xmod;
              nz = 0;
              break;
            }
          }
        }
        t = i / dz;
        i += 1;
        xp = x0 + xmod * i * m;
      }
    }
    int ny = 0;
    if (target == 1) {
      if (t > tExit) {
        return new Pair<>(false, false);
      }
      if (nx == 0 && ny == 0 && nz == 0) {
        // fix ray.n being set to zero (issue #643)
        return new Pair<>(false, false);
      }
      ray.setNormal(nx, ny, nz);
      ray.t = t + t_offset;
      return new Pair<>(true, false);
    } else {
      if (t > tExit) {
        nx = 0;
        ny = (int) Math.signum(ray.d.y);
        nz = 0;
        t = tExit;
      } else {
        nx = -nx;
        nz = -nz;
      }
      if (nx == 0 && ny == 0 && nz == 0) {
        // fix ray.n being set to zero (issue #643)
        return new Pair<>(false, false);
      }
      ray.setNormal(nx, ny, nz);
      ray.t = t;
      return new Pair<>(true, true);
    }
  }

  private static boolean inCloud(double x, double z) {
    return Clouds.getCloud((int) Math.floor(x), (int) Math.floor(z)) == 1;
  }

  public JsonObject toJson() {
    JsonObject cloudLayerJson = new JsonObject();
    cloudLayerJson.add("size", size.toJson());
    cloudLayerJson.add("offset", offset.toJson());
    cloudLayerJson.add("color", ColorUtil.rgbToJson(color));
    cloudLayerJson.add("volumetricClouds", volumetricClouds);
    cloudLayerJson.add("density", density);
    cloudLayerJson.add("flatMaterialProperties", flatMaterialToJson());
    cloudLayerJson.add("volumeMaterialProperties", volumeMaterialToJson());
    return cloudLayerJson;
  }

  private JsonObject flatMaterialToJson() {
    JsonObject flatMaterial = new JsonObject();
    flatMaterial.add("emittance", material.emittance);
    flatMaterial.add("specular", material.specular);
    flatMaterial.add("roughness", material.roughness);
    flatMaterial.add("ior", material.ior);
    flatMaterial.add("metalness", material.metalness);
    return flatMaterial;
  }

  private JsonObject volumeMaterialToJson() {
    JsonObject volumeMaterialObject = new JsonObject();
    volumeMaterialObject.add("emittance", volumeMaterial.emittance);
    volumeMaterialObject.add("anisotropy", volumeMaterial.anisotropy);
    return volumeMaterialObject;
  }

  public void importFromJson(JsonObject json) {
    size.fromJson(json.get("size").asObject());
    offset.fromJson(json.get("offset").asObject());
    color.set(ColorUtil.jsonToRGB(json.get("color").asObject()));
    volumetricClouds = json.get("volumetricClouds").boolValue(volumetricClouds);
    density = json.get("density").doubleValue(density);
    JsonObject flatMaterialProperties = json.get("flatMaterialProperties").asObject();
    importFlatMaterial(flatMaterialProperties);
    JsonObject volumeMaterialProperties = json.get("volumeMaterialProperties").asObject();
    importVolumeMaterial(volumeMaterialProperties);
  }

  private void importFlatMaterial(JsonObject json) {
    material.emittance = json.get("emittance").floatValue(material.emittance);
    material.specular = json.get("specular").floatValue(material.specular);
    material.roughness = json.get("roughness").floatValue(material.roughness);
    material.ior = json.get("ior").floatValue(material.ior);
    material.metalness = json.get("metalness").floatValue(material.metalness);
  }

  private void importVolumeMaterial(JsonObject json) {
    volumeMaterial.emittance = json.get("emittance").floatValue(volumeMaterial.emittance);
    volumeMaterial.anisotropy = json.get("anisotropy").floatValue(volumeMaterial.anisotropy);
  }
}
