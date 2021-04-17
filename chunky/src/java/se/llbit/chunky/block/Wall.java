package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.WallModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Wall extends MinecraftBlockTranslucent implements ModelBlock {
    private final WallModel model;
    private final String description;

    public Wall(String name, Texture texture, String north, String south, String east, String west, boolean up) {
        super(name, texture);
        localIntersect = true;
        this.description = String.format("north=%s, south=%s, east=%s, west=%s, up=%s", north, south, east, west, up);
        this.model = new WallModel(texture,
            new int[]{getConnection(north), getConnection(east), getConnection(south), getConnection(west)}, up);
    }

    @Override
    public boolean intersect(Ray ray, Scene scene) {
        return model.intersect(ray, scene);
    }

    @Override
    public String description() {
        return description;
    }

    private static int getConnection(String state) {
        switch (state) {
            case "true": // < 20w06a
            case "low": // >= 20w06a
                return 1;
            case "tall": // >= 20w06a
                return 2;
            default:
                return 0;
        }
    }

    @Override
    public BlockModel getModel() {
        return model;
    }
}
