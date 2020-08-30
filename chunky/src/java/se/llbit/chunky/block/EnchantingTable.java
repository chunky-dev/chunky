package se.llbit.chunky.block;

import se.llbit.chunky.entity.Book;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.model.EnchantmentTableModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.nbt.CompoundTag;

public class EnchantingTable extends MinecraftBlockTranslucent {

  public EnchantingTable() {
    super("enchanting_table", Texture.enchantmentTableSide);
    localIntersect = true;
    solid = false;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return EnchantmentTableModel.intersect(ray);
  }

  @Override
  public boolean isBlockEntity() {
    return true;
  }

  @Override
  public Entity toBlockEntity(Vector3 position, CompoundTag entityTag) {
    Vector3 newPosition = new Vector3(position);
    newPosition.add(0, 0.35, 0);
    Book book = new Book(
        newPosition,
        Math.PI - Math.PI / 16,
        Math.toRadians(30),
        Math.toRadians(180 - 30));
    book.setPitch(Math.toRadians(80));
    book.setYaw(Math.toRadians(45));
    return book;
  }
}
