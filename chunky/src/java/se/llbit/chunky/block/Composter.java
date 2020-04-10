package se.llbit.chunky.block;

import se.llbit.chunky.model.CactusModel;
import se.llbit.chunky.model.ComposterModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Composter extends MinecraftBlockTranslucent {
    private final int level;

    public Composter(int level) {
        super("composter", Texture.composterSide);
        this.level = level;
        localIntersect = true;
    }

    @Override
    public boolean intersect(Ray ray, Scene scene) {
        return ComposterModel.intersect(ray, this.level);
    }
}
