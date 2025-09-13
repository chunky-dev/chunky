package se.llbit.chunky.world.material;

import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.json.JsonNumber;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.ColorUtil;
import se.llbit.math.Constants;
import se.llbit.math.IntersectionRecord;
import se.llbit.util.JsonUtil;

public class BeaconBeamMaterial extends Material {

    public static final int DEFAULT_COLOR = 0xF9FFFE;
    private int color;
    private final float[] beamColor = new float[4];

    public BeaconBeamMaterial(int color) {
        super("beacon_beam", Texture.beaconBeam);
        this.emittance = 1.0f;
        updateColor(color);
    }

    public void updateColor(int color) {
        this.color = color;
        ColorUtil.getRGBComponents(color, beamColor);
        ColorUtil.toLinear(beamColor);
    }

    public int getColorInt() {
        return color;
    }

    @Override
    public void getColor(IntersectionRecord intersectionRecord) {
        super.getColor(intersectionRecord);
        if (intersectionRecord.color.w > Constants.EPSILON) {
            intersectionRecord.color.x *= beamColor[0];
            intersectionRecord.color.y *= beamColor[1];
            intersectionRecord.color.z *= beamColor[2];
        }
    }

    @Override
    public float[] getColor(double u, double v) {
        float[] color = super.getColor(u, v);
        if (color[3] > Constants.EPSILON) {
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
        JsonValue color = json.get("color");
        if (color instanceof JsonNumber) {
            // compatibility with older scene files
            updateColor(color.asInt(DEFAULT_COLOR));
        } else {
            updateColor(ColorUtil.getRGB(JsonUtil.rgbFromJson(color)));
        }
    }

    @Override
    public JsonObject saveMaterialProperties() {
        JsonObject json = super.saveMaterialProperties();
        json.add("color", this.color);
        return json;
    }
}
