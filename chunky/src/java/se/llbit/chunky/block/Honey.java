package se.llbit.chunky.block;

import se.llbit.chunky.model.HoneyBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Honey extends MinecraftBlockTranslucent {
    public Honey() {
        super("honey_block", Texture.honeyBlockSide);
        localIntersect = true;
        opaque = false;
        ior = 1.474f; // according to https://study.com/academy/answer/what-is-the-refractive-index-of-honey.html
        solid = false;
        refractive = true;
    }

    @Override
    public boolean intersect(Ray ray, Scene scene) {
        return HoneyBlockModel.intersect(ray);
    }
}
