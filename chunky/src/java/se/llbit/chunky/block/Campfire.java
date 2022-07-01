package se.llbit.chunky.block;

import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.nbt.CompoundTag;

import java.util.Random;

public class Campfire extends MinecraftBlockTranslucent {
    private final se.llbit.chunky.entity.Campfire.Kind kind;
    private final String facing;
    public final boolean isLit;

    public Campfire(String name, se.llbit.chunky.entity.Campfire.Kind kind, String facing, boolean lit) {
        super(name, Texture.campfireLog);
        invisible = true;
        opaque = false;
        localIntersect = true;
        this.kind = kind;
        this.facing = facing;
        this.isLit = lit;
    }

    @Override
    public boolean intersect(Ray ray, Scene scene) {
        return false;
    }

    @Override
    public boolean isBlockEntity() {
        return true;
    }

    @Override
    public Entity toBlockEntity(Vector3 position, CompoundTag entityTag) {
        return new se.llbit.chunky.entity.Campfire(this.kind, position, this.facing, this.isLit, this);
    }

    @Override
    public int faceCount() {
        return se.llbit.chunky.entity.Campfire.faceCount();
    }

    @Override
    public void sample(int face, Vector3 loc, Random rand) {
        se.llbit.chunky.entity.Campfire.sample(face, loc, rand);
    }

    @Override
    public double surfaceArea(int face) {
        return se.llbit.chunky.entity.Campfire.surfaceArea(face);
    }
}
