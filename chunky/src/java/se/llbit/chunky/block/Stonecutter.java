package se.llbit.chunky.block;

import se.llbit.chunky.model.StonecutterModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Stonecutter extends MinecraftBlockTranslucent {
    private final String facing;

    public Stonecutter(String facing) {
        super("stonecutter", Texture.stonecutterSide);
        localIntersect = true;
        this.facing = facing;
    }

    @Override
    public boolean intersect(Ray ray, Scene scene) {
        return StonecutterModel.intersect(ray, facing);
    }
}
