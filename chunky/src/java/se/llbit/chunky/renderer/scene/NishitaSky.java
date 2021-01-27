package se.llbit.chunky.renderer.scene;

import org.apache.commons.math3.util.FastMath;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import static java.lang.Math.PI;

public class NishitaSky {
    private Sun sun;
    private Vector3 sunPosition;

    private double earthRadius = 6360e3;
    private double atmThickness = 100e3;

    private double rayleighScale = 8e3;
    private double mieScale = 1.2e3;

    private Vector3 betaR = new Vector3(3.8e-6, 13.5e-6, 33.1e-6);
    private Vector3 betaM = new Vector3(21e-6, 21e-6, 21e-6);

    private int samples = 16;
    private int samplesLight = 8;

    private double sunIntensity;

    public NishitaSky(Sun sun) {
        this.sun = sun;

        double theta = sun.getAzimuth();
        double phi = sun.getAltitude();
        double r = QuickMath.abs(FastMath.cos(phi));
        sunPosition = new Vector3(FastMath.cos(theta) * r, FastMath.sin(phi), FastMath.sin(theta) * r);

        sunIntensity = sun.getIntensity();
    }

    public Vector3 calcIncidentLight(Ray ray) {
        Vector3 origin = new Vector3(0, ray.o.y + earthRadius + 1, 0);
        Vector3 direction = ray.d;

        double distance = sphereIntersect(origin, direction, earthRadius + atmThickness);
        if (distance == -1) {
            // No intersection, black
            return new Vector3(0, 0, 0);
        }

        double segmentLength = distance / samples;
        double currentDist = 0;

        double optDepthR = 0;
        double optDepthM = 0;

        double mu = direction.dot(sunPosition);
        double phaseR = (3 / (16 * PI)) * (1 + mu*mu);
        double g = 0.76;
        double phaseM = (3 / (8 * PI)) * ((1 - g*g) * ((1 + mu*mu) / (2 + g*g)) * (FastMath.pow(1 + g*g - 2*g*mu, 1.5)));

        Vector3 sumR = new Vector3(0, 0, 0);
        Vector3 sumM = new Vector3(0, 0, 0);

        Vector3 samplePosition = new Vector3();
        double height, hr, hm;

        Vector3 sunSamplePosition = new Vector3();
        double sunLength, sunSegment, sunCurrent, optDepthSunR, optDepthSunM, sunHeight;

        Vector3 tau = new Vector3();
        Vector3 attenuation = new Vector3();

        for (int i = 0; i < samples; i++) {
            samplePosition.set(
                    origin.x + (currentDist + segmentLength/2) * direction.x,
                    origin.y + (currentDist + segmentLength/2) * direction.y,
                    origin.z + (currentDist + segmentLength/2) * direction.z
            );
            height = samplePosition.length() - earthRadius;

            hr = FastMath.exp(-height / rayleighScale) * segmentLength;
            hm = FastMath.exp(-height / mieScale) * segmentLength;
            optDepthR += hr;
            optDepthM += hm;

            // Sun trace
            sunLength = sphereIntersect(samplePosition, sunPosition, earthRadius + atmThickness);
            sunSegment = sunLength / samplesLight;
            sunCurrent = 0;

            optDepthSunR = 0;
            optDepthSunM = 0;

            boolean flag = false;
            for (int j = 0; j < samplesLight; j++) {
                sunSamplePosition.set(
                        samplePosition.x + (sunCurrent + sunSegment/2) * sunPosition.x,
                        samplePosition.y + (sunCurrent + sunSegment/2) * sunPosition.y,
                        samplePosition.z + (sunCurrent + sunSegment/2) * sunPosition.z
                );
                sunHeight = sunSamplePosition.length() - earthRadius;
                if (sunHeight < 0) {
                    flag = true;
                    break;
                }

                optDepthSunR += FastMath.exp(-sunHeight / rayleighScale) * sunSegment;
                optDepthSunM += FastMath.exp(-sunHeight / mieScale) * sunSegment;

                sunCurrent += sunSegment;
            }

            if (!flag) {
                tau.set(
                        betaR.x * (optDepthR + optDepthSunR) + betaM.x * 1.1 * (optDepthM + optDepthSunM),
                        betaR.y * (optDepthR + optDepthSunR) + betaM.y * 1.1 * (optDepthM + optDepthSunM),
                        betaR.z * (optDepthR + optDepthSunR) + betaM.z * 1.1 * (optDepthM + optDepthSunM)
                );

                attenuation.set(
                        FastMath.exp(-1 * tau.x),
                        FastMath.exp(-1 * tau.y),
                        FastMath.exp(-1 * tau.z)
                );

                sumR.add(
                        attenuation.x * hr,
                        attenuation.y * hr,
                        attenuation.z * hr
                );

                sumM.add(
                        attenuation.x * hm,
                        attenuation.y * hm,
                        attenuation.z * hm
                );
            }

            currentDist += segmentLength;
        }

        return new Vector3(
                (sumR.x*betaR.x*phaseR + sumM.x*betaM.x*phaseM) * sunIntensity * 5,
                (sumR.y*betaR.y*phaseR + sumM.y*betaM.y*phaseM) * sunIntensity * 5,
                (sumR.z*betaR.z*phaseR + sumM.z*betaM.z*phaseM) * sunIntensity * 5
        );
    }

    private double sphereIntersect(Vector3 origin, Vector3 direction, double sphere_radius) {
        double a = direction.lengthSquared();
        double b = 2 * direction.dot(origin);
        double c = origin.lengthSquared() - sphere_radius*sphere_radius;

        if (b == 0) {
            if (a == 0) {
                // No intersection
                return -1;
            }

            return FastMath.sqrt(-c / a);
        }

        double disc = b*b - 4*a*c;

        if (disc < 0) {
            // No intersection
            return -1;
        }
        return (-b + FastMath.sqrt(disc)) / (2*a);
    }
}
