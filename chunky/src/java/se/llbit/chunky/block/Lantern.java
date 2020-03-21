package se.llbit.chunky.block;

import se.llbit.chunky.model.LanternModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Lantern extends MinecraftBlockTranslucent {
    private final boolean hanging;

    public Lantern(String name, Texture texture, boolean hanging) {
        super(name, texture);
        this.hanging = hanging;
        localIntersect = true;
    }

    @Override
    public boolean intersect(Ray ray, Scene scene) {
        return LanternModel.intersect(ray, this.hanging, this.texture);
    }

    @Override
    public String description() {
        return "hanging=" + hanging;
    }
}
