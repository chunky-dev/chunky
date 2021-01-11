package se.llbit.chunky.world.material;

import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.json.JsonObject;
import se.llbit.math.ColorUtil;
import se.llbit.math.Ray;

public class BeaconBeamMaterial extends Material {

    private int color;
    private float[] beamColor = new float[4];

    public BeaconBeamMaterial(int color) {
        super("beacon_beam", Texture.beaconBeam);
        this.emittance = 1.0f;
        updateColor(color);
    }

    private void updateColor(int color) {
        this.color = color;
        ColorUtil.getRGBAComponents(color, beamColor);
        ColorUtil.toLinear(beamColor);
    }

    @Override
    public void getColor(Ray ray) {
        super.getColor(ray);
        if (ray.color.w > Ray.EPSILON) {
            ray.color.x *= beamColor[0];
            ray.color.y *= beamColor[1];
            ray.color.z *= beamColor[2];
        }
    }

    @Override
    public float[] getColor(double u, double v) {
        float[] color = super.getColor(u, v);
        if (color[3] > Ray.EPSILON) {
            color = color.clone();
            color[0] *= beamColor[0];
            color[1] *= beamColor[1];
            color[2] *= beamColor[2];
        }
        return color;
    }

    @Override
    public void loadMaterialProperties(JsonObject json) {
        super.loadMaterialProperties(json);
        updateColor(json.get("color").asInt(0xFFFFFF));
    }

    public void saveMaterialProperties(JsonObject json) {
        json.add("ior", this.ior);
        json.add("specular", this.specular);
        json.add("emittance", this.emittance);
        json.add("roughness", this.roughness);
        json.add("color", this.color);
    }
}