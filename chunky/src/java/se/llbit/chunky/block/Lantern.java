package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.LanternModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Lantern extends MinecraftBlockTranslucent implements ModelBlock {
    private final LanternModel model;
    private final boolean hanging;

    public Lantern(String name, Texture texture, boolean hanging) {
        super(name, texture);
        this.hanging = hanging;
        this.model = new LanternModel(texture, hanging);
        localIntersect = true;
    }

    @Override
    public boolean intersect(Ray ray, Scene scene) {
        return model.intersect(ray, scene);
    }

    @Override
    public String description() {
        return "hanging=" + hanging;
    }

    @Override
    public BlockModel getModel() {
        return model;
    }
}
