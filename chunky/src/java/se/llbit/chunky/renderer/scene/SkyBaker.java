package se.llbit.chunky.renderer.scene;

import org.apache.commons.math3.util.FastMath;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import static java.lang.Math.PI;

public class SkyBaker {
  private double[][][] skyTexture;
  private int skyResolution = 1024;
  private SimulatedSky simSky;
  private Sky sky;

  public SkyBaker(Sky sky) {
    this.sky = sky;
    skyTexture = new double[skyResolution+1][skyResolution+1][3];
    reset();
  }

  public void reset() {
    simSky = sky.getSimulatedSky();
    for (int i = 0; i < skyResolution+1; i++) {
      for (int j = 0; j < skyResolution+1; j++) {
        for (int k = 0; k < 3; k++) {
          skyTexture[i][j][k] = -1;
        }
      }
    }
  }

  public void setSkyResolution(int skyResolution) {
    this.skyResolution = skyResolution;
    skyTexture = new double[skyResolution+1][skyResolution+1][3];
    reset();
  }

  public int getSkyResolution() {
    return this.skyResolution;
  }

  public Vector3 calcIncidentLight(Ray ray, double horizonOffset) {
    double theta = FastMath.atan2(ray.d.z, ray.d.x);
    theta /= PI*2;
    theta = ((theta % 1) + 1) % 1;
    double phi = (FastMath.asin(QuickMath.clamp(ray.d.y + horizonOffset, -1, 1)) + PI/2) / PI;

    return getColorInterpolated(theta, phi);
  }

  private double interp1D(double x, double x0, double x1, double y0, double y1) {
    return y0 + (x - x0)*(y1-y0)/(x1-x0);
  }

  private Vector3 getColorInterpolated(double normX, double normY) {
    double x = normX * skyResolution;
    double y = normY * skyResolution;
    int floorX = (int) QuickMath.clamp(x, 0, skyResolution-1);
    int floorY = (int) QuickMath.clamp(y, 0, skyResolution-1);

    if (skyTexture[floorX][floorY][0] == -1) bake(floorX, floorY);
    if (skyTexture[floorX][floorY+1][0] == -1) bake(floorX, floorY+1);
    if (skyTexture[floorX+1][floorY][0] == -1) bake(floorX+1, floorY);
    if (skyTexture[floorX+1][floorY+1][0] == -1) bake(floorX+1, floorY+1);

    double[] color = new double[3];
    for (int i = 0; i < 3; i++) {
      double y0 = interp1D(x, floorX, floorX + 1, skyTexture[floorX][floorY][i], skyTexture[floorX+1][floorY][i]);
      double y1 = interp1D(x, floorX, floorX + 1, skyTexture[floorX][floorY+1][i], skyTexture[floorX+1][floorY+1][i]);
      color[i] = interp1D(y, floorY, floorY + 1, y0, y1);
    }

    return new Vector3(color[0], color[1], color[2]);
  }

  private void bake(int x, int y) {
    Ray ray = new Ray();

    int i = x;
    int j = y;

    double theta = ((double) i / skyResolution) * 2 * PI;
    double phi = ((double) j / skyResolution) * PI - PI/2;
    double r = FastMath.cos(phi);
    ray.d.set(FastMath.cos(theta) * r, FastMath.sin(phi), FastMath.sin(theta) * r);

    Vector3 color = simSky.calcIncidentLight(ray, 0);
    skyTexture[x][y][0] = color.x;
    skyTexture[x][y][1] = color.y;
    skyTexture[x][y][2] = color.z;
  }
}
