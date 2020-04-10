package se.llbit.chunky.block;

import se.llbit.chunky.model.EnchantmentTableModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class EnchantingTable extends MinecraftBlockTranslucent {
  public EnchantingTable() {
    super("enchanting_table", Texture.enchantmentTableSide);
    localIntersect = true;
    solid = false;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return EnchantmentTableModel.intersect(ray);
  }
}
