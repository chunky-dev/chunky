package se.llbit.chunky.block;

import se.llbit.chunky.model.TripwireHookModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class TripwireHook extends MinecraftBlockTranslucent {
    private final int facing;
    private final boolean attached, powered;

    public TripwireHook(String facing, boolean attached, boolean powered) {
        super("tripwire_hook", Texture.tripwire);
        localIntersect = true;
        this.attached = attached;
        this.powered = powered;
        switch (facing) {
            default:
            case "north":
                this.facing = 0;
                break;
            case "south":
                this.facing = 2;
                break;
            case "west":
                this.facing = 3;
                break;
            case "east":
                this.facing = 1;
                break;
        }
    }

    @Override
    public boolean intersect(Ray ray, Scene scene) {
        return TripwireHookModel.intersect(ray, facing, attached, powered);
    }

    @Override
    public String description() {
        return String.format("facing=%s,attached=%s,powered=%s", facing, attached, powered);
    }

    public String getFacing() {
        return new String[]{"north", "east", "south", "west"}[facing];
    }
}
