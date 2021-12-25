package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import java.util.List;
import java.util.Random;

@PluginApi
public abstract class AbstractModelBlock extends MinecraftBlock implements ModelBlock {

  protected BlockModel model;

  public AbstractModelBlock(String name, Texture texture) {
    super(name, texture);
    localIntersect = true;
    opaque = false;
  }

  @Override
  public void sample(Vector3 loc, Random rand) {
    model.sample(loc, rand);
  }

  @Override
  public List<Vector3> sampleAll(Random rand) {
    return model.sampleAll(rand);
  }

  @Override
  public BlockModel getModel() {
    return model;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }
}
