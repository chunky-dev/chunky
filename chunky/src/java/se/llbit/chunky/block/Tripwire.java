package se.llbit.chunky.block;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import static se.llbit.chunky.world.BlockData.CONNECTED_EAST;
import static se.llbit.chunky.world.BlockData.CONNECTED_NORTH;
import static se.llbit.chunky.world.BlockData.CONNECTED_SOUTH;
import static se.llbit.chunky.world.BlockData.CONNECTED_WEST;

public class Tripwire extends MinecraftBlockTranslucent {

  private final String description;
  private final int connections;

  private static final Quad[] tripwire_n = {
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      )
  };

  private static final Quad[] tripwire_ne = {
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
          new Vector3(12 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
          new Vector3(8 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
          new Vector3(12 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
          new Vector3(8 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 14 / 16.0)
      ),
      new Quad(
          new Vector3(12 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
          new Vector3(16 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
          new Vector3(12 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(12 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
          new Vector3(16 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
          new Vector3(12 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 14 / 16.0)
      )
  };

  private static final Quad[] tripwire_ns = {
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 16 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 16 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 16 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      )
  };

  private static final Quad[] tripwire_nse = {
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 16 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 16 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 16 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
          new Vector3(12 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
          new Vector3(8 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
          new Vector3(12 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
          new Vector3(8 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 14 / 16.0)
      ),
      new Quad(
          new Vector3(12 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
          new Vector3(16 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
          new Vector3(12 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(12 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
          new Vector3(16 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
          new Vector3(12 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 14 / 16.0)
      )
  };

  private static final Quad[] tripwire_nsew = {
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 16 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 16 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 16 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
          new Vector3(4 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
          new Vector3(0 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
          new Vector3(4 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
          new Vector3(0 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 14 / 16.0)
      ),
      new Quad(
          new Vector3(4 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
          new Vector3(8 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
          new Vector3(4 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(4 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
          new Vector3(8 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
          new Vector3(4 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 14 / 16.0)
      ),
      new Quad(
          new Vector3(8 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
          new Vector3(12 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
          new Vector3(8 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
          new Vector3(12 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
          new Vector3(8 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 14 / 16.0)
      ),
      new Quad(
          new Vector3(12 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
          new Vector3(16 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
          new Vector3(12 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(12 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
          new Vector3(16 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
          new Vector3(12 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 14 / 16.0)
      )
  };

  private static final Quad[][] quads = new Quad[16][];

  static {
    quads[0] = tripwire_ns; // If no side is elected, render north-south.
    quads[CONNECTED_NORTH | CONNECTED_SOUTH | CONNECTED_EAST | CONNECTED_WEST] = tripwire_nsew;
    quads[CONNECTED_NORTH | CONNECTED_EAST | CONNECTED_SOUTH] = tripwire_nse;
    quads[CONNECTED_EAST | CONNECTED_SOUTH | CONNECTED_WEST] = Model
        .rotateY(quads[CONNECTED_NORTH | CONNECTED_EAST | CONNECTED_SOUTH]);
    quads[CONNECTED_SOUTH | CONNECTED_WEST | CONNECTED_NORTH] = Model
        .rotateY(quads[CONNECTED_EAST | CONNECTED_SOUTH | CONNECTED_WEST]);
    quads[CONNECTED_WEST | CONNECTED_NORTH | CONNECTED_EAST] = Model
        .rotateY(quads[CONNECTED_SOUTH | CONNECTED_WEST | CONNECTED_NORTH]);
    quads[CONNECTED_NORTH | CONNECTED_SOUTH] = tripwire_ns;
    quads[CONNECTED_EAST | CONNECTED_WEST] = Model
        .rotateY(quads[CONNECTED_NORTH | CONNECTED_SOUTH]);
    quads[CONNECTED_NORTH | CONNECTED_EAST] = tripwire_ne;
    quads[CONNECTED_EAST | CONNECTED_SOUTH] = Model
        .rotateY(quads[CONNECTED_NORTH | CONNECTED_EAST]);
    quads[CONNECTED_SOUTH | CONNECTED_WEST] = Model
        .rotateY(quads[CONNECTED_EAST | CONNECTED_SOUTH]);
    quads[CONNECTED_WEST | CONNECTED_NORTH] = Model
        .rotateY(quads[CONNECTED_SOUTH | CONNECTED_WEST]);
    quads[CONNECTED_NORTH] = tripwire_n;
    quads[CONNECTED_EAST] = Model.rotateY(quads[CONNECTED_NORTH]);
    quads[CONNECTED_SOUTH] = Model.rotateY(quads[CONNECTED_EAST]);
    quads[CONNECTED_WEST] = Model.rotateY(quads[CONNECTED_SOUTH]);
  }

  public Tripwire(boolean north, boolean south, boolean east, boolean west) {
    super("tripwire", Texture.tripwire);
    localIntersect = true;
    this.description = String.format("north=%s, south=%s, east=%s, west=%s",
        north, south, east, west);
    int connections = 0;
    if (north) {
      connections |= CONNECTED_NORTH;
    }
    if (south) {
      connections |= CONNECTED_SOUTH;
    }
    if (east) {
      connections |= CONNECTED_EAST;
    }
    if (west) {
      connections |= CONNECTED_WEST;
    }
    this.connections = connections;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    for (Quad quad : quads[connections]) {
      if (quad.intersect(ray)) {
        Texture.tripwire.getColor(ray);
        ray.n.set(quad.n);
        ray.t = ray.tNext;
        hit = true;
      }
    }
    if (hit) {
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
    }
    return hit;
  }

  @Override
  public String description() {
    return description;
  }
}
