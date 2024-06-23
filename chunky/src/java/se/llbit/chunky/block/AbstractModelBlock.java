package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.primitive.Primitive;

import java.util.Collection;
import java.util.Random;

@PluginApi
public abstract class AbstractModelBlock extends MinecraftBlock implements ModelBlock {

  protected BlockModel model;

  public AbstractModelBlock(String name, Texture texture) {
    super(name, texture);
    localIntersect = true;
    opaque = false;
    solid = false;
  }

  @Override
  public int faceCount() {
    return model.faceCount();
  }

  @Override
  public void sample(int face, Vector3 loc, Random rand) {
    model.sample(face, loc, rand);
  }

  @Override
  public double surfaceArea(int face) {
    return model.faceSurfaceArea(face);
  }

  @Override
  public BlockModel getModel() {
    return model;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }

  @Override
  public boolean isBiomeDependant() {
    return super.isBiomeDependant() || model.isBiomeDependant();
  }

  @Override
  public Collection<Primitive> getPrimitives(Transform transform) {
    return getModel().getPrimitives(transform);
  }
}
