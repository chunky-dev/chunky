package se.llbit.chunky.block;

import se.llbit.chunky.model.ScaffoldingModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Scaffolding extends MinecraftBlockTranslucent {
    private final boolean bottom;

    public Scaffolding(boolean bottom) {
        super("scaffolding", Texture.scaffoldingSide);
        this.bottom = bottom;
        localIntersect = true;
    }

    @Override
    public boolean intersect(Ray ray, Scene scene) {
        return ScaffoldingModel.intersect(ray, this.bottom);
    }

    @Override
    public String description() {
        return "bottom=" + bottom;
    }
}
