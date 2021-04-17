package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.ComposterModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Composter extends MinecraftBlockTranslucent implements ModelBlock {
    private final ComposterModel model;

    public Composter(int level) {
        super("composter", Texture.composterSide);
        this.model = new ComposterModel(level);
        localIntersect = true;
    }

    @Override
    public boolean intersect(Ray ray, Scene scene) {
        return model.intersect(ray, scene);
    }

    @Override
    public BlockModel getModel() {
        return model;
    }
}
