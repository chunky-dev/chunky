package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.TripwireHookModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class TripwireHook extends MinecraftBlockTranslucent implements ModelBlock {
    private final TripwireHookModel model;
    private final String description;

    public TripwireHook(String facingString, boolean attached, boolean powered) {
        super("tripwire_hook", Texture.tripwire);
        localIntersect = true;
        this.description = String.format("facing=%s,attached=%s,powered=%s", facingString, attached, powered);
        int facing;
        switch (facingString) {
            default:
            case "north":
                facing = 0;
                break;
            case "south":
                facing = 2;
                break;
            case "west":
                facing = 3;
                break;
            case "east":
                facing = 1;
                break;
        }
        this.model = new TripwireHookModel(facing, attached, powered);
    }

    @Override
    public boolean intersect(Ray ray, Scene scene) {
        return model.intersect(ray, scene);
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public BlockModel getModel() {
        return model;
    }
}
