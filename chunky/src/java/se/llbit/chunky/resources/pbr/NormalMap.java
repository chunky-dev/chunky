package se.llbit.chunky.resources.pbr;

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.ColorUtil;
import se.llbit.math.Matrix3;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector2;
import se.llbit.math.Vector3;

public class NormalMap {

  public static final Matrix3 tbnCubeTop =
      getTbn(new Vector3(1, 0, 0), new Vector3(0, 0, 1), new Vector3(0, 1, 0));
  public static final Matrix3 tbnCubeBottom =
      getTbn(new Vector3(1, 0, 0), new Vector3(0, -1, 0), new Vector3(0, -1, 0));
  public static final Matrix3 tbnCubeNorth =
      getTbn(new Vector3(1, 0, 0), new Vector3(0, -1, 0), new Vector3(0, 0, -1));
  public static final Matrix3 tbnCubeEast =
      getTbn(new Vector3(0, 0, 1), new Vector3(0, -1, 0), new Vector3(1, 0, 0));
  public static final Matrix3 tbnCubeSouth =
      getTbn(new Vector3(-1, 0, 0), new Vector3(0, -1, 0), new Vector3(0, 0, 1));
  public static final Matrix3 tbnCubeWest =
      getTbn(new Vector3(0, 0, -1), new Vector3(0, -1, 0), new Vector3(-1, 0, 0));

  private int width;
  private int height;
  private Vector3[] normals;

  public static void apply(Ray ray, Quad quad, Texture texture) {
    if (texture.getNormalMap() != null) {
      texture.getNormalMap().apply(quad, ray.getNormal(), ray.u, ray.v);
    }
  }

  public static void apply(Ray ray, Matrix3 tbn, Texture texture) {
    if (texture.getNormalMap() != null) {
      texture.getNormalMap().apply(tbn, ray.getNormal(), ray.u, ray.v);
    }
  }

  public static void apply(Ray ray, Vector3 t, Vector3 b, Texture texture) {
    if (texture.getNormalMap() != null) {
      texture.getNormalMap().apply(ray.getNormal(), t, b, ray.u, ray.v);
    }
  }

  public void apply(Vector3 vec, Vector3 t, Vector3 b, double u, double v) {
    t.normalize();
    b.normalize();

    Matrix3 tbn = new Matrix3();
    tbn.m11 = t.x;
    tbn.m21 = t.y;
    tbn.m31 = t.z;
    tbn.m12 = b.x;
    tbn.m22 = b.y;
    tbn.m32 = b.z;
    tbn.m13 = vec.x;
    tbn.m23 = vec.y;
    tbn.m33 = vec.z;

    apply(tbn, vec, u, v);
  }

  private void apply(Matrix3 tbn, Vector3 vec, double u, double v) {
    Vector3 n =
        this.normals[
            (int) (v * height - Ray.EPSILON) * width + (int) (u * width - Ray.EPSILON)];
    if (n.lengthSquared() > 0) {
      vec.set(n);
      tbn.transform(vec);
      vec.normalize();
    }
  }

  private void apply(Quad quad, Vector3 vec, double u, double v) {
    Vector3 n =
        this.normals[
            (int) (v * height - Ray.EPSILON) * width + (int) (u * width - Ray.EPSILON)];
    if (n.lengthSquared() > 0) {
      vec.set(n);
      quad.tbn.transform(vec);
      vec.normalize();
    }
  }

  public NormalMap(BitmapImage newImage) {
    width = newImage.width;
    height = newImage.height;
    normals = new Vector3[width * height];
    float[] color = new float[4];
    for (int x = 0; x < newImage.width; x++) {
      for (int y = 0; y < newImage.height; y++) {
        ColorUtil.getRGBAComponents(newImage.getPixel(x, y), color);
        Vector2 xy = new Vector2(color[0] * 2 - 1, color[1] * 2 - 1);
        // xy.normalize();
        normals[width * y + x] =
            new Vector3(color[0] * 2 - 1, color[1] * 2 - 1, FastMath.sqrt(1.0 - xy.dot(xy)));
        normals[width * y + x].normalize();
      }
    }
  }

  public static Matrix3 getTbn(Vector3 t, Vector3 b, Vector3 n) {
    Matrix3 tbn = new Matrix3();
    tbn.m11 = t.x;
    tbn.m21 = t.y;
    tbn.m31 = t.z;
    tbn.m12 = b.x;
    tbn.m22 = b.y;
    tbn.m32 = b.z;
    tbn.m13 = n.x;
    tbn.m23 = n.y;
    tbn.m33 = n.z;
    return tbn;
  }
}