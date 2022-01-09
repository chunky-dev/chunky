package se.llbit.chunky.block;

import se.llbit.chunky.model.SlimeBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Slime extends MinecraftBlockTranslucent {
    public Slime() {
        super("slime_block", Texture.slime);
        localIntersect = true;
        opaque = false;
        ior = 1.516f; // gelatin, according to https://study.com/academy/answer/what-is-the-refractive-index-of-gelatin.html
        solid = false;
        refractive = true;
    }

    @Override
    public boolean intersect(Ray ray, Scene scene) {
        return SlimeBlockModel.intersect(ray);
    }
}
