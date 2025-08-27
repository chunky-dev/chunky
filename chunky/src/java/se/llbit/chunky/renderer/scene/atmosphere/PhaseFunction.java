package se.llbit.chunky.renderer.scene.atmosphere.phasefunction;

import static se.llbit.chunky.renderer.scene.atmosphere.Util.lutLerp;

import org.apache.commons.math3.util.FastMath;
import se.llbit.math.Constants;
import se.llbit.math.Vector3;

@FunctionalInterface
public interface PhaseFunction {
  double p(Vector3 wo, Vector3 wi, double wl);

  final class Isotropic implements PhaseFunction {
    @Override
    public double p(Vector3 wo, Vector3 wi, double wl) {
      return Constants.INV_4_PI;
    }
  }

  final class HenyeyGreenstein implements PhaseFunction {
    private double g;
    private double gSquared;

    public HenyeyGreenstein(double g) {
      this.g = g;
      this.gSquared = g * g;
    }

    @Override
    public double p(Vector3 wo, Vector3 wi, double wl) {
      double cosTheta = wo.dot(wi);
      double denom = 1 + gSquared + 2 * g * cosTheta;
      return Constants.INV_4_PI * (1 - gSquared) / (denom * FastMath.sqrt(denom));
    }
  }

  final class RayleighPhase implements PhaseFunction {
    private static final double RAYLEIGH_PHASE_SCALE = (3.0 / 16.0) * Constants.INV_PI;

    @Override
    public double p(Vector3 wo, Vector3 wi, double wl) {
      double cosTheta = wo.dot(wi);
      return RAYLEIGH_PHASE_SCALE * (1 + cosTheta * cosTheta);
    }
  }

  final class ChandrasekharPhase implements PhaseFunction {
    private static final double[][] gammaLut = new double[][] {
        {200.0, 0.02326},
        {205.0, 0.02241},
        {210.0, 0.02100},
        {215.0, 0.02043},
        {220.0, 0.01986},
        {225.0, 0.01930},
        {240.0, 0.01872},
        {260.0, 0.01758},
        {270.0, 0.01729},
        {280.0, 0.01672},
        {290.0, 0.01643},
        {300.0, 0.01614},
        {310.0, 0.01614},
        {320.0, 0.01586},
        {330.0, 0.01557},
        {340.0, 0.01557},
        {350.0, 0.01528},
        {360.0, 0.01528},
        {370.0, 0.01528},
        {380.0, 0.01499},
        {390.0, 0.01499},
        {400.0, 0.01499},
        {450.0, 0.01471},
        {500.0, 0.01442},
        {550.0, 0.01442},
        {600.0, 0.01413},
        {650.0, 0.01413},
        {700.0, 0.01413},
        {750.0, 0.01413},
        {800.0, 0.01384},
        {850.0, 0.01384},
        {900.0, 0.01384},
        {950.0, 0.01384},
        {1000.0, 0.01384},
    };

    @Override
    public double p(Vector3 wo, Vector3 wi, double wl) {
      double cosTheta = wo.dot(wi);
      double gamma = lutLerp(gammaLut, wl);
      return (RayleighPhase.RAYLEIGH_PHASE_SCALE / (1 + 2 * gamma)) * (1 + 3 * gamma + (1 - gamma) * cosTheta * cosTheta);
    }
  }
}
