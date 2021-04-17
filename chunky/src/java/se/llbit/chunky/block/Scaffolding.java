package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.ScaffoldingModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Scaffolding extends MinecraftBlockTranslucent implements ModelBlock {
    private final ScaffoldingModel model;
    private final boolean bottom;

    public Scaffolding(boolean bottom) {
        super("scaffolding", Texture.scaffoldingSide);
        localIntersect = true;
        this.model = new ScaffoldingModel(bottom);
        this.bottom = bottom;
    }

    @Override
    public boolean intersect(Ray ray, Scene scene) {
        return model.intersect(ray, scene);
    }

    @Override
    public String description() {
        return "bottom=" + bottom;
    }

    @Override
    public BlockModel getModel() {
        return model;
    }
}
