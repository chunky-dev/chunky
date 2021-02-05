package se.llbit.chunky.renderer.scene;

import org.apache.commons.math3.util.FastMath;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

public class PreethamSky implements SkySimulated {
  private static final double xZenithChroma[][] =
      {{0.00166, -0.00375, 0.00209, 0}, {-0.02903, 0.06377, -0.03203, 0.00394},
          {0.11693, -0.21196, 0.06052, 0.25886},};
  private static final double yZenithChroma[][] =
      {{0.00275, -0.00610, 0.00317, 0}, {-0.04214, 0.08970, -0.04153, 0.00516},
          {0.15346, -0.26756, 0.06670, 0.26688},};
  private static final double mdx[][] =
      {{-0.0193, -0.2592}, {-0.0665, 0.0008}, {-0.0004, 0.2125}, {-0.0641, -0.8989},
          {-0.0033, 0.0452}};
  private static final double mdy[][] =
      {{-0.0167, -0.2608}, {-0.0950, 0.0092}, {-0.0079, 0.2102}, {-0.0441, -1.6537},
          {-0.0109, 0.0529}};
  private static final double mdY[][] =
      {{0.1787, -1.4630}, {-0.3554, 0.4275}, {-0.0227, 5.3251}, {0.1206, -2.5771},
          {-0.0670, 0.3703}};

  private static double turb = 2.5;
  private static double turb2 = turb * turb;
  private static Vector3 A = new Vector3();
  private static Vector3 B = new Vector3();
  private static Vector3 C = new Vector3();
  private static Vector3 D = new Vector3();
  private static Vector3 E = new Vector3();

  static {
    A.x = mdx[0][0] * turb + mdx[0][1];
    B.x = mdx[1][0] * turb + mdx[1][1];
    C.x = mdx[2][0] * turb + mdx[2][1];
    D.x = mdx[3][0] * turb + mdx[3][1];
    E.x = mdx[4][0] * turb + mdx[4][1];

    A.y = mdy[0][0] * turb + mdy[0][1];
    B.y = mdy[1][0] * turb + mdy[1][1];
    C.y = mdy[2][0] * turb + mdy[2][1];
    D.y = mdy[3][0] * turb + mdy[3][1];
    E.y = mdy[4][0] * turb + mdy[4][1];

    A.z = mdY[0][0] * turb + mdY[0][1];
    B.z = mdY[1][0] * turb + mdY[1][1];
    C.z = mdY[2][0] * turb + mdY[2][1];
    D.z = mdY[3][0] * turb + mdY[3][1];
    E.z = mdY[4][0] * turb + mdY[4][1];
  }

  private double zenith_Y;
  private double zenith_x;
  private double zenith_y;
  private double f0_Y;
  private double f0_x;
  private double f0_y;

  private final Vector3 sw = new Vector3();

  private Sun sun;

  /**
   * Create a new sky renderer without an existing sun.
   * TODO: This may cause null pointer exception?
   */
  public PreethamSky() {
    this.sun = null;
  }

  public PreethamSky(Sun sun) {
    updateSun(sun);
  }

  @Override
  public void updateSun(Sun sun) {
    this.sun = sun;

    double theta = sun.getAzimuth();
    double phi = sun.getAltitude();

    double r = QuickMath.abs(FastMath.cos(phi));

    sw.set(FastMath.cos(theta) * r, FastMath.sin(phi), FastMath.sin(theta) * r);

    updateSkylightValues();
  }

  @Override
  public String getName() {
    return "Preetham";
  }

  @Override
  public String getTooltip() {
    return "A fast daytime sky model.";
  }

  @Override
  public Vector3 calcIncidentLight(Ray ray) {
    double cosTheta = ray.d.y;
    if (cosTheta < 0)
      cosTheta = 0;
    double cosGamma = ray.d.dot(sw);
    double gamma = FastMath.acos(cosGamma);
    double cos2Gamma = cosGamma * cosGamma;
    double x = zenith_x * perezF(cosTheta, gamma, cos2Gamma, A.x, B.x, C.x, D.x, E.x) * f0_x;
    double y = zenith_y * perezF(cosTheta, gamma, cos2Gamma, A.y, B.y, C.y, D.y, E.y) * f0_y;
    double z = zenith_Y * perezF(cosTheta, gamma, cos2Gamma, A.z, B.z, C.z, D.z, E.z) * f0_Y;
    if (y <= Ray.EPSILON) {
      return new Vector3(0, 0, 0);
    } else {
      double f = (z / y);
      double x2 = x * f;
      double y2 = z;
      double z2 = (1 - x - y) * f;
      // CIE to RGB M^-1 matrix from http://www.brucelindbloom.com/Eqn_RGB_XYZ_Matrix.html
      Vector3 color = new Vector3(
          2.3706743 * x2 - 0.9000405 * y2 - 0.4706338 * z2,
          -0.513885 * x2 + 1.4253036 * y2 + 0.0885814 * z2,
          0.0052982 * x2 - 0.0146949 * y2 + 1.0093968 * z2
      );
      color.scale(0.045);

      return color;
    }
  }

  private void updateSkylightValues() {
    double sunTheta = Math.PI / 2 - sun.getAltitude();
    double cosTheta = FastMath.cos(sunTheta);
    double cos2Theta = cosTheta * cosTheta;
    double chi = (4.0 / 9.0 - turb / 120.0) * (Math.PI - 2 * sunTheta);
    zenith_Y = (4.0453 * turb - 4.9710) * Math.tan(chi) - 0.2155 * turb + 2.4192;
    zenith_Y = (zenith_Y < 0) ? -zenith_Y : zenith_Y;
    zenith_x = chroma(turb, turb2, sunTheta, xZenithChroma);
    zenith_y = chroma(turb, turb2, sunTheta, yZenithChroma);
    f0_x = 1 / perezF(1, sunTheta, cos2Theta, A.x, B.x, C.x, D.x, E.x);
    f0_y = 1 / perezF(1, sunTheta, cos2Theta, A.y, B.y, C.y, D.y, E.y);
    f0_Y = 1 / perezF(1, sunTheta, cos2Theta, A.z, B.z, C.z, D.z, E.z);
  }

  private double chroma(double turb, double turb2, double sunTheta, double[][] matrix) {

    double t1 = sunTheta;
    double t2 = t1 * t1;
    double t3 = t1 * t2;

    return turb2 * (matrix[0][0] * t3 + matrix[0][1] * t2 + matrix[0][2] * t1 + matrix[0][3]) +
        turb * (matrix[1][0] * t3 + matrix[1][1] * t2 + matrix[1][2] * t1 + matrix[1][3]) +
        (matrix[2][0] * t3 + matrix[2][1] * t2 + matrix[2][2] * t1 + matrix[2][3]);
  }

  private static double perezF(double cosTheta, double gamma, double cos2Gamma, double A, double B,
                 double C, double D, double E) {
    return (1 + A * FastMath.exp(B / cosTheta)) * (1 + C * FastMath.exp(D * gamma) + E * cos2Gamma);
  }
}