package se.llbit.chunky.block;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class SeaPickle extends MinecraftBlockTranslucent {

  private static final Quad[] seaPickle1 = {
      // cube1
      new Quad(
          new Vector3(6 / 16.0, 6 / 16.0, 10 / 16.0),
          new Vector3(10 / 16.0, 6 / 16.0, 10 / 16.0),
          new Vector3(6 / 16.0, 6 / 16.0, 6 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(6 / 16.0, 0, 6 / 16.0),
          new Vector3(10 / 16.0, 0, 6 / 16.0),
          new Vector3(6 / 16.0, 0, 10 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(10 / 16.0, 0, 10 / 16.0),
          new Vector3(10 / 16.0, 0, 6 / 16.0),
          new Vector3(10 / 16.0, 6 / 16.0, 10 / 16.0),
          new Vector4(12 / 16.0, 16 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(6 / 16.0, 0, 6 / 16.0),
          new Vector3(6 / 16.0, 0, 10 / 16.0),
          new Vector3(6 / 16.0, 6 / 16.0, 6 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(10 / 16.0, 0, 6 / 16.0),
          new Vector3(6 / 16.0, 0, 6 / 16.0),
          new Vector3(10 / 16.0, 6 / 16.0, 6 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(6 / 16.0, 0, 10 / 16.0),
          new Vector3(10 / 16.0, 0, 10 / 16.0),
          new Vector3(6 / 16.0, 6 / 16.0, 10 / 16.0),
          new Vector4(0, 4 / 16.0, 5 / 16.0, 11 / 16.0)),
      // cube2
      new Quad(
          new Vector3(6 / 16.0, 5.95 / 16.0, 10 / 16.0),
          new Vector3(10 / 16.0, 5.95 / 16.0, 10 / 16.0),
          new Vector3(6 / 16.0, 5.95 / 16.0, 6 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      // cube3
      new Quad(new Quad(
          new Vector3(8.5 / 16.0, 5.2 / 16.0, 8 / 16.0),
          new Vector3(7.5 / 16.0, 5.2 / 16.0, 8 / 16.0),
          new Vector3(8.5 / 16.0, 8.7 / 16.0, 8 / 16.0),
          new Vector4(1 / 16.0, 3 / 16.0, 11 / 16.0, 16 / 16.0)),
          Transform.NONE.translate(0, 0, 0)
              .rotateY(0.7853981633974483 / 1.0)
              .translate(0, 0, 0)),
      new Quad(new Quad(
          new Vector3(7.5 / 16.0, 5.2 / 16.0, 8 / 16.0),
          new Vector3(8.5 / 16.0, 5.2 / 16.0, 8 / 16.0),
          new Vector3(7.5 / 16.0, 8.7 / 16.0, 8 / 16.0),
          new Vector4(13 / 16.0, 15 / 16.0, 11 / 16.0, 16 / 16.0)),
          Transform.NONE.translate(0, 0, 0)
              .rotateY(0.7853981633974483 / 1.0)
              .translate(0, 0, 0)),
  };
  private static final Quad[] seaPickle1Pickle = Model.join(
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(7.5 / 16.0, 8.7 / 16.0, 8 / 16.0),
              new Vector3(8.5 / 16.0, 8.7 / 16.0, 8 / 16.0),
              new Vector3(7.5 / 16.0, 5.2 / 16.0, 8 / 16.0),
              new Vector4(3 / 16.0, 1 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(8.5 / 16.0, 8.7 / 16.0, 8 / 16.0),
              new Vector3(7.5 / 16.0, 8.7 / 16.0, 8 / 16.0),
              new Vector3(8.5 / 16.0, 5.2 / 16.0, 8 / 16.0),
              new Vector4(1 / 16.0, 3 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(8 / 16.0, 8.7 / 16.0, 8.5 / 16.0),
              new Vector3(8 / 16.0, 8.7 / 16.0, 7.5 / 16.0),
              new Vector3(8 / 16.0, 5.2 / 16.0, 8.5 / 16.0),
              new Vector4(15 / 16.0, 13 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 8.7 / 16.0, 7.5 / 16.0),
              new Vector3(8 / 16.0, 8.7 / 16.0, 8.5 / 16.0),
              new Vector3(8 / 16.0, 5.2 / 16.0, 7.5 / 16.0),
              new Vector4(13 / 16.0, 15 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45)));
  private static final Quad[] seaPickle2 = {
      // cube1
      new Quad(
          new Vector3(3 / 16.0, 6 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 6 / 16.0, 7 / 16.0),
          new Vector3(3 / 16.0, 6 / 16.0, 3 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(3 / 16.0, 0, 3 / 16.0),
          new Vector3(7 / 16.0, 0, 3 / 16.0),
          new Vector3(3 / 16.0, 0, 7 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(7 / 16.0, 0, 7 / 16.0),
          new Vector3(7 / 16.0, 0, 3 / 16.0),
          new Vector3(7 / 16.0, 6 / 16.0, 7 / 16.0),
          new Vector4(12 / 16.0, 16 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(3 / 16.0, 0, 3 / 16.0),
          new Vector3(3 / 16.0, 0, 7 / 16.0),
          new Vector3(3 / 16.0, 6 / 16.0, 3 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(7 / 16.0, 0, 3 / 16.0),
          new Vector3(3 / 16.0, 0, 3 / 16.0),
          new Vector3(7 / 16.0, 6 / 16.0, 3 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(3 / 16.0, 0, 7 / 16.0),
          new Vector3(7 / 16.0, 0, 7 / 16.0),
          new Vector3(3 / 16.0, 6 / 16.0, 7 / 16.0),
          new Vector4(0, 4 / 16.0, 5 / 16.0, 11 / 16.0)),
      // cube2
      new Quad(
          new Vector3(3 / 16.0, 5.95 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 5.95 / 16.0, 7 / 16.0),
          new Vector3(3 / 16.0, 5.95 / 16.0, 3 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      // cube3
      new Quad(
          new Vector3(8 / 16.0, 4 / 16.0, 12 / 16.0),
          new Vector3(12 / 16.0, 4 / 16.0, 12 / 16.0),
          new Vector3(8 / 16.0, 4 / 16.0, 8 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(8 / 16.0, 0, 8 / 16.0),
          new Vector3(12 / 16.0, 0, 8 / 16.0),
          new Vector3(8 / 16.0, 0, 12 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(12 / 16.0, 0, 12 / 16.0),
          new Vector3(12 / 16.0, 0, 8 / 16.0),
          new Vector3(12 / 16.0, 4 / 16.0, 12 / 16.0),
          new Vector4(12 / 16.0, 16 / 16.0, 7 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(8 / 16.0, 0, 8 / 16.0),
          new Vector3(8 / 16.0, 0, 12 / 16.0),
          new Vector3(8 / 16.0, 4 / 16.0, 8 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 7 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(12 / 16.0, 0, 8 / 16.0),
          new Vector3(8 / 16.0, 0, 8 / 16.0),
          new Vector3(12 / 16.0, 4 / 16.0, 8 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 7 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(8 / 16.0, 0, 12 / 16.0),
          new Vector3(12 / 16.0, 0, 12 / 16.0),
          new Vector3(8 / 16.0, 4 / 16.0, 12 / 16.0),
          new Vector4(0, 4 / 16.0, 7 / 16.0, 11 / 16.0)),
      // cube4
      new Quad(
          new Vector3(8 / 16.0, 3.95 / 16.0, 12 / 16.0),
          new Vector3(12 / 16.0, 3.95 / 16.0, 12 / 16.0),
          new Vector3(8 / 16.0, 3.95 / 16.0, 8 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
  };
  private static final Quad[] seaPickle2Pickle = Model.join(
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(4.5 / 16.0, 8.7 / 16.0, 5 / 16.0),
              new Vector3(5.5 / 16.0, 8.7 / 16.0, 5 / 16.0),
              new Vector3(4.5 / 16.0, 5.2 / 16.0, 5 / 16.0),
              new Vector4(3 / 16.0, 1 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(5.5 / 16.0, 8.7 / 16.0, 5 / 16.0),
              new Vector3(4.5 / 16.0, 8.7 / 16.0, 5 / 16.0),
              new Vector3(5.5 / 16.0, 5.2 / 16.0, 5 / 16.0),
              new Vector4(1 / 16.0, 3 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(5 / 16., 5.6 / 16., 5 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(5 / 16.0, 8.7 / 16.0, 5.5 / 16.0),
              new Vector3(5 / 16.0, 8.7 / 16.0, 4.5 / 16.0),
              new Vector3(5 / 16.0, 5.2 / 16.0, 5.5 / 16.0),
              new Vector4(15 / 16.0, 13 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(5 / 16.0, 8.7 / 16.0, 4.5 / 16.0),
              new Vector3(5 / 16.0, 8.7 / 16.0, 5.5 / 16.0),
              new Vector3(5 / 16.0, 5.2 / 16.0, 4.5 / 16.0),
              new Vector4(13 / 16.0, 15 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(5 / 16., 5.6 / 16., 5 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(9.5 / 16.0, 6.7 / 16.0, 10 / 16.0),
              new Vector3(10.5 / 16.0, 6.7 / 16.0, 10 / 16.0),
              new Vector3(9.5 / 16.0, 3.2 / 16.0, 10 / 16.0),
              new Vector4(3 / 16.0, 1 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(10.5 / 16.0, 6.7 / 16.0, 10 / 16.0),
              new Vector3(9.5 / 16.0, 6.7 / 16.0, 10 / 16.0),
              new Vector3(10.5 / 16.0, 3.2 / 16.0, 10 / 16.0),
              new Vector4(1 / 16.0, 3 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(10 / 16., 8 / 16., 10 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(10 / 16.0, 6.7 / 16.0, 10.5 / 16.0),
              new Vector3(10 / 16.0, 6.7 / 16.0, 9.5 / 16.0),
              new Vector3(10 / 16.0, 3.2 / 16.0, 10.5 / 16.0),
              new Vector4(15 / 16.0, 13 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(10 / 16.0, 6.7 / 16.0, 9.5 / 16.0),
              new Vector3(10 / 16.0, 6.7 / 16.0, 10.5 / 16.0),
              new Vector3(10 / 16.0, 3.2 / 16.0, 9.5 / 16.0),
              new Vector4(13 / 16.0, 15 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(10 / 16., 8 / 16., 10 / 16.)));
  private static final Quad[] seaPickle3 = {
      // cube1
      new Quad(
          new Vector3(6 / 16.0, 6 / 16.0, 13 / 16.0),
          new Vector3(10 / 16.0, 6 / 16.0, 13 / 16.0),
          new Vector3(6 / 16.0, 6 / 16.0, 9 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(6 / 16.0, 0, 9 / 16.0),
          new Vector3(10 / 16.0, 0, 9 / 16.0),
          new Vector3(6 / 16.0, 0, 13 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(10 / 16.0, 0, 13 / 16.0),
          new Vector3(10 / 16.0, 0, 9 / 16.0),
          new Vector3(10 / 16.0, 6 / 16.0, 13 / 16.0),
          new Vector4(12 / 16.0, 16 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(6 / 16.0, 0, 9 / 16.0),
          new Vector3(6 / 16.0, 0, 13 / 16.0),
          new Vector3(6 / 16.0, 6 / 16.0, 9 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(10 / 16.0, 0, 9 / 16.0),
          new Vector3(6 / 16.0, 0, 9 / 16.0),
          new Vector3(10 / 16.0, 6 / 16.0, 9 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(6 / 16.0, 0, 13 / 16.0),
          new Vector3(10 / 16.0, 0, 13 / 16.0),
          new Vector3(6 / 16.0, 6 / 16.0, 13 / 16.0),
          new Vector4(0, 4 / 16.0, 5 / 16.0, 11 / 16.0)),
      // cube2
      new Quad(
          new Vector3(6 / 16.0, 5.95 / 16.0, 13 / 16.0),
          new Vector3(10 / 16.0, 5.95 / 16.0, 13 / 16.0),
          new Vector3(6 / 16.0, 5.95 / 16.0, 9 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      // cube3
      new Quad(
          new Vector3(2 / 16.0, 4 / 16.0, 6 / 16.0),
          new Vector3(6 / 16.0, 4 / 16.0, 6 / 16.0),
          new Vector3(2 / 16.0, 4 / 16.0, 2 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(2 / 16.0, 0, 2 / 16.0),
          new Vector3(6 / 16.0, 0, 2 / 16.0),
          new Vector3(2 / 16.0, 0, 6 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(6 / 16.0, 0, 6 / 16.0),
          new Vector3(6 / 16.0, 0, 2 / 16.0),
          new Vector3(6 / 16.0, 4 / 16.0, 6 / 16.0),
          new Vector4(12 / 16.0, 16 / 16.0, 7 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(2 / 16.0, 0, 2 / 16.0),
          new Vector3(2 / 16.0, 0, 6 / 16.0),
          new Vector3(2 / 16.0, 4 / 16.0, 2 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 7 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(6 / 16.0, 0, 2 / 16.0),
          new Vector3(2 / 16.0, 0, 2 / 16.0),
          new Vector3(6 / 16.0, 4 / 16.0, 2 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 7 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(2 / 16.0, 0, 6 / 16.0),
          new Vector3(6 / 16.0, 0, 6 / 16.0),
          new Vector3(2 / 16.0, 4 / 16.0, 6 / 16.0),
          new Vector4(0, 4 / 16.0, 7 / 16.0, 11 / 16.0)),
      // cube4
      new Quad(
          new Vector3(2 / 16.0, 3.95 / 16.0, 6 / 16.0),
          new Vector3(6 / 16.0, 3.95 / 16.0, 6 / 16.0),
          new Vector3(2 / 16.0, 3.95 / 16.0, 2 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      // cube5
      new Quad(
          new Vector3(8 / 16.0, 6 / 16.0, 8 / 16.0),
          new Vector3(12 / 16.0, 6 / 16.0, 8 / 16.0),
          new Vector3(8 / 16.0, 6 / 16.0, 4 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(8 / 16.0, 0, 4 / 16.0),
          new Vector3(12 / 16.0, 0, 4 / 16.0),
          new Vector3(8 / 16.0, 0, 8 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(12 / 16.0, 0, 8 / 16.0),
          new Vector3(12 / 16.0, 0, 4 / 16.0),
          new Vector3(12 / 16.0, 6 / 16.0, 8 / 16.0),
          new Vector4(12 / 16.0, 16 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(8 / 16.0, 0, 4 / 16.0),
          new Vector3(8 / 16.0, 0, 8 / 16.0),
          new Vector3(8 / 16.0, 6 / 16.0, 4 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(12 / 16.0, 0, 4 / 16.0),
          new Vector3(8 / 16.0, 0, 4 / 16.0),
          new Vector3(12 / 16.0, 6 / 16.0, 4 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(8 / 16.0, 0, 8 / 16.0),
          new Vector3(12 / 16.0, 0, 8 / 16.0),
          new Vector3(8 / 16.0, 6 / 16.0, 8 / 16.0),
          new Vector4(0, 4 / 16.0, 5 / 16.0, 11 / 16.0)),
      // cube6
      new Quad(
          new Vector3(8 / 16.0, 5.95 / 16.0, 8 / 16.0),
          new Vector3(12 / 16.0, 5.95 / 16.0, 8 / 16.0),
          new Vector3(8 / 16.0, 5.95 / 16.0, 4 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
  };
  private static final Quad[] seaPickle3Pickle = Model.join(
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(7.5 / 16.0, 8.7 / 16.0, 11 / 16.0),
              new Vector3(8.5 / 16.0, 8.7 / 16.0, 11 / 16.0),
              new Vector3(7.5 / 16.0, 5.2 / 16.0, 11 / 16.0),
              new Vector4(3 / 16.0, 1 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(8.5 / 16.0, 8.7 / 16.0, 11 / 16.0),
              new Vector3(7.5 / 16.0, 8.7 / 16.0, 11 / 16.0),
              new Vector3(8.5 / 16.0, 5.2 / 16.0, 11 / 16.0),
              new Vector4(1 / 16.0, 3 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(8 / 16., 8 / 16., 11 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(8 / 16.0, 8.7 / 16.0, 11.5 / 16.0),
              new Vector3(8 / 16.0, 8.7 / 16.0, 10.5 / 16.0),
              new Vector3(8 / 16.0, 5.2 / 16.0, 11.5 / 16.0),
              new Vector4(15 / 16.0, 13 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 8.7 / 16.0, 10.5 / 16.0),
              new Vector3(8 / 16.0, 8.7 / 16.0, 11.5 / 16.0),
              new Vector3(8 / 16.0, 5.2 / 16.0, 10.5 / 16.0),
              new Vector4(13 / 16.0, 15 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(8 / 16., 8 / 16., 11 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(3.5 / 16.0, 6.7 / 16.0, 4 / 16.0),
              new Vector3(4.5 / 16.0, 6.7 / 16.0, 4 / 16.0),
              new Vector3(3.5 / 16.0, 3.2 / 16.0, 4 / 16.0),
              new Vector4(3 / 16.0, 1 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(4.5 / 16.0, 6.7 / 16.0, 4 / 16.0),
              new Vector3(3.5 / 16.0, 6.7 / 16.0, 4 / 16.0),
              new Vector3(4.5 / 16.0, 3.2 / 16.0, 4 / 16.0),
              new Vector4(1 / 16.0, 3 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(4 / 16., 8 / 16., 4 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(4 / 16.0, 6.7 / 16.0, 4.5 / 16.0),
              new Vector3(4 / 16.0, 6.7 / 16.0, 3.5 / 16.0),
              new Vector3(4 / 16.0, 3.2 / 16.0, 4.5 / 16.0),
              new Vector4(15 / 16.0, 13 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(4 / 16.0, 6.7 / 16.0, 3.5 / 16.0),
              new Vector3(4 / 16.0, 6.7 / 16.0, 4.5 / 16.0),
              new Vector3(4 / 16.0, 3.2 / 16.0, 3.5 / 16.0),
              new Vector4(13 / 16.0, 15 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(4 / 16., 8 / 16., 4 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(9.5 / 16.0, 8.7 / 16.0, 6 / 16.0),
              new Vector3(10.5 / 16.0, 8.7 / 16.0, 6 / 16.0),
              new Vector3(9.5 / 16.0, 5.2 / 16.0, 6 / 16.0),
              new Vector4(3 / 16.0, 1 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(10.5 / 16.0, 8.7 / 16.0, 6 / 16.0),
              new Vector3(9.5 / 16.0, 8.7 / 16.0, 6 / 16.0),
              new Vector3(10.5 / 16.0, 5.2 / 16.0, 6 / 16.0),
              new Vector4(1 / 16.0, 3 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(10 / 16., 8 / 16., 6 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(10 / 16.0, 8.7 / 16.0, 6.5 / 16.0),
              new Vector3(10 / 16.0, 8.7 / 16.0, 5.5 / 16.0),
              new Vector3(10 / 16.0, 5.2 / 16.0, 6.5 / 16.0),
              new Vector4(15 / 16.0, 13 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(10 / 16.0, 8.7 / 16.0, 5.5 / 16.0),
              new Vector3(10 / 16.0, 8.7 / 16.0, 6.5 / 16.0),
              new Vector3(10 / 16.0, 5.2 / 16.0, 5.5 / 16.0),
              new Vector4(13 / 16.0, 15 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(10 / 16., 8 / 16., 6 / 16.))
  );
  private static final Quad[] seaPickle4 = {
      // cube1
      new Quad(
          new Vector3(2 / 16.0, 6 / 16.0, 6 / 16.0),
          new Vector3(6 / 16.0, 6 / 16.0, 6 / 16.0),
          new Vector3(2 / 16.0, 6 / 16.0, 2 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(2 / 16.0, 0, 2 / 16.0),
          new Vector3(6 / 16.0, 0, 2 / 16.0),
          new Vector3(2 / 16.0, 0, 6 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(6 / 16.0, 0, 6 / 16.0),
          new Vector3(6 / 16.0, 0, 2 / 16.0),
          new Vector3(6 / 16.0, 6 / 16.0, 6 / 16.0),
          new Vector4(12 / 16.0, 16 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(2 / 16.0, 0, 2 / 16.0),
          new Vector3(2 / 16.0, 0, 6 / 16.0),
          new Vector3(2 / 16.0, 6 / 16.0, 2 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(6 / 16.0, 0, 2 / 16.0),
          new Vector3(2 / 16.0, 0, 2 / 16.0),
          new Vector3(6 / 16.0, 6 / 16.0, 2 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(2 / 16.0, 0, 6 / 16.0),
          new Vector3(6 / 16.0, 0, 6 / 16.0),
          new Vector3(2 / 16.0, 6 / 16.0, 6 / 16.0),
          new Vector4(0, 4 / 16.0, 5 / 16.0, 11 / 16.0)),
      // cube2
      new Quad(
          new Vector3(2 / 16.0, 5.95 / 16.0, 6 / 16.0),
          new Vector3(6 / 16.0, 5.95 / 16.0, 6 / 16.0),
          new Vector3(2 / 16.0, 5.95 / 16.0, 2 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      // cube3
      new Quad(
          new Vector3(9 / 16.0, 4 / 16.0, 14 / 16.0),
          new Vector3(13 / 16.0, 4 / 16.0, 14 / 16.0),
          new Vector3(9 / 16.0, 4 / 16.0, 10 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(9 / 16.0, 0, 10 / 16.0),
          new Vector3(13 / 16.0, 0, 10 / 16.0),
          new Vector3(9 / 16.0, 0, 14 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(13 / 16.0, 0, 14 / 16.0),
          new Vector3(13 / 16.0, 0, 10 / 16.0),
          new Vector3(13 / 16.0, 4 / 16.0, 14 / 16.0),
          new Vector4(12 / 16.0, 16 / 16.0, 7 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(9 / 16.0, 0, 10 / 16.0),
          new Vector3(9 / 16.0, 0, 14 / 16.0),
          new Vector3(9 / 16.0, 4 / 16.0, 10 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 7 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(13 / 16.0, 0, 10 / 16.0),
          new Vector3(9 / 16.0, 0, 10 / 16.0),
          new Vector3(13 / 16.0, 4 / 16.0, 10 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 7 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(9 / 16.0, 0, 14 / 16.0),
          new Vector3(13 / 16.0, 0, 14 / 16.0),
          new Vector3(9 / 16.0, 4 / 16.0, 14 / 16.0),
          new Vector4(0, 4 / 16.0, 7 / 16.0, 11 / 16.0)),
      // cube4
      new Quad(
          new Vector3(9 / 16.0, 3.95 / 16.0, 14 / 16.0),
          new Vector3(13 / 16.0, 3.95 / 16.0, 14 / 16.0),
          new Vector3(9 / 16.0, 3.95 / 16.0, 10 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      // cube5
      new Quad(
          new Vector3(9 / 16.0, 6 / 16.0, 6 / 16.0),
          new Vector3(13 / 16.0, 6 / 16.0, 6 / 16.0),
          new Vector3(9 / 16.0, 6 / 16.0, 2 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(9 / 16.0, 0, 2 / 16.0),
          new Vector3(13 / 16.0, 0, 2 / 16.0),
          new Vector3(9 / 16.0, 0, 6 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(13 / 16.0, 0, 6 / 16.0),
          new Vector3(13 / 16.0, 0, 2 / 16.0),
          new Vector3(13 / 16.0, 6 / 16.0, 6 / 16.0),
          new Vector4(12 / 16.0, 16 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(9 / 16.0, 0, 2 / 16.0),
          new Vector3(9 / 16.0, 0, 6 / 16.0),
          new Vector3(9 / 16.0, 6 / 16.0, 2 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(13 / 16.0, 0, 2 / 16.0),
          new Vector3(9 / 16.0, 0, 2 / 16.0),
          new Vector3(13 / 16.0, 6 / 16.0, 2 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(9 / 16.0, 0, 6 / 16.0),
          new Vector3(13 / 16.0, 0, 6 / 16.0),
          new Vector3(9 / 16.0, 6 / 16.0, 6 / 16.0),
          new Vector4(0, 4 / 16.0, 5 / 16.0, 11 / 16.0)),
      // cube6
      new Quad(
          new Vector3(9 / 16.0, 5.95 / 16.0, 6 / 16.0),
          new Vector3(13 / 16.0, 5.95 / 16.0, 6 / 16.0),
          new Vector3(9 / 16.0, 5.95 / 16.0, 2 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      // cube7
      new Quad(
          new Vector3(2 / 16.0, 7 / 16.0, 12 / 16.0),
          new Vector3(6 / 16.0, 7 / 16.0, 12 / 16.0),
          new Vector3(2 / 16.0, 7 / 16.0, 8 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(2 / 16.0, 0, 8 / 16.0),
          new Vector3(6 / 16.0, 0, 8 / 16.0),
          new Vector3(2 / 16.0, 0, 12 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(6 / 16.0, 0, 12 / 16.0),
          new Vector3(6 / 16.0, 0, 8 / 16.0),
          new Vector3(6 / 16.0, 7 / 16.0, 12 / 16.0),
          new Vector4(12 / 16.0, 16 / 16.0, 4 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(2 / 16.0, 0, 8 / 16.0),
          new Vector3(2 / 16.0, 0, 12 / 16.0),
          new Vector3(2 / 16.0, 7 / 16.0, 8 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 4 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(6 / 16.0, 0, 8 / 16.0),
          new Vector3(2 / 16.0, 0, 8 / 16.0),
          new Vector3(6 / 16.0, 7 / 16.0, 8 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 4 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(2 / 16.0, 0, 12 / 16.0),
          new Vector3(6 / 16.0, 0, 12 / 16.0),
          new Vector3(2 / 16.0, 7 / 16.0, 12 / 16.0),
          new Vector4(0, 4 / 16.0, 4 / 16.0, 11 / 16.0)),
      // cube8
      new Quad(
          new Vector3(2 / 16.0, 6.95 / 16.0, 12 / 16.0),
          new Vector3(6 / 16.0, 6.95 / 16.0, 12 / 16.0),
          new Vector3(2 / 16.0, 6.95 / 16.0, 8 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
  };
  private static final Quad[] seaPickle4Pickle = Model.join(
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(3.5 / 16.0, 8.7 / 16.0, 4 / 16.0),
              new Vector3(4.5 / 16.0, 8.7 / 16.0, 4 / 16.0),
              new Vector3(3.5 / 16.0, 5.2 / 16.0, 4 / 16.0),
              new Vector4(3 / 16.0, 1 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(4.5 / 16.0, 8.7 / 16.0, 4 / 16.0),
              new Vector3(3.5 / 16.0, 8.7 / 16.0, 4 / 16.0),
              new Vector3(4.5 / 16.0, 5.2 / 16.0, 4 / 16.0),
              new Vector4(1 / 16.0, 3 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(4 / 16., 8 / 16., 4 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(4 / 16.0, 8.7 / 16.0, 4.5 / 16.0),
              new Vector3(4 / 16.0, 8.7 / 16.0, 3.5 / 16.0),
              new Vector3(4 / 16.0, 5.2 / 16.0, 4.5 / 16.0),
              new Vector4(15 / 16.0, 13 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(4 / 16.0, 8.7 / 16.0, 3.5 / 16.0),
              new Vector3(4 / 16.0, 8.7 / 16.0, 4.5 / 16.0),
              new Vector3(4 / 16.0, 5.2 / 16.0, 3.5 / 16.0),
              new Vector4(13 / 16.0, 15 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(4 / 16., 8 / 16., 4 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(10.5 / 16.0, 6.7 / 16.0, 12 / 16.0),
              new Vector3(11.5 / 16.0, 6.7 / 16.0, 12 / 16.0),
              new Vector3(10.5 / 16.0, 3.2 / 16.0, 12 / 16.0),
              new Vector4(3 / 16.0, 1 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(11.5 / 16.0, 6.7 / 16.0, 12 / 16.0),
              new Vector3(10.5 / 16.0, 6.7 / 16.0, 12 / 16.0),
              new Vector3(11.5 / 16.0, 3.2 / 16.0, 12 / 16.0),
              new Vector4(1 / 16.0, 3 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(11 / 16., 8 / 16., 12 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(11 / 16.0, 6.7 / 16.0, 12.5 / 16.0),
              new Vector3(11 / 16.0, 6.7 / 16.0, 11.5 / 16.0),
              new Vector3(11 / 16.0, 3.2 / 16.0, 12.5 / 16.0),
              new Vector4(15 / 16.0, 13 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(11 / 16.0, 6.7 / 16.0, 11.5 / 16.0),
              new Vector3(11 / 16.0, 6.7 / 16.0, 12.5 / 16.0),
              new Vector3(11 / 16.0, 3.2 / 16.0, 11.5 / 16.0),
              new Vector4(13 / 16.0, 15 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(11 / 16., 8 / 16., 12 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(10.5 / 16.0, 8.7 / 16.0, 4 / 16.0),
              new Vector3(11.5 / 16.0, 8.7 / 16.0, 4 / 16.0),
              new Vector3(10.5 / 16.0, 5.2 / 16.0, 4 / 16.0),
              new Vector4(3 / 16.0, 1 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(11.5 / 16.0, 8.7 / 16.0, 4 / 16.0),
              new Vector3(10.5 / 16.0, 8.7 / 16.0, 4 / 16.0),
              new Vector3(11.5 / 16.0, 5.2 / 16.0, 4 / 16.0),
              new Vector4(1 / 16.0, 3 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(11 / 16., 8 / 16., 4 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(11 / 16.0, 8.7 / 16.0, 4.5 / 16.0),
              new Vector3(11 / 16.0, 8.7 / 16.0, 3.5 / 16.0),
              new Vector3(11 / 16.0, 5.2 / 16.0, 4.5 / 16.0),
              new Vector4(15 / 16.0, 13 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(11 / 16.0, 8.7 / 16.0, 3.5 / 16.0),
              new Vector3(11 / 16.0, 8.7 / 16.0, 4.5 / 16.0),
              new Vector3(11 / 16.0, 5.2 / 16.0, 3.5 / 16.0),
              new Vector4(13 / 16.0, 15 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(11 / 16., 8 / 16., 4 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(3.5 / 16.0, 9.7 / 16.0, 10 / 16.0),
              new Vector3(4.5 / 16.0, 9.7 / 16.0, 10 / 16.0),
              new Vector3(3.5 / 16.0, 6.2 / 16.0, 10 / 16.0),
              new Vector4(3 / 16.0, 1 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(4.5 / 16.0, 9.7 / 16.0, 10 / 16.0),
              new Vector3(3.5 / 16.0, 9.7 / 16.0, 10 / 16.0),
              new Vector3(4.5 / 16.0, 6.2 / 16.0, 10 / 16.0),
              new Vector4(1 / 16.0, 3 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(4 / 16., 8 / 16., 10 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(4 / 16.0, 9.7 / 16.0, 10.5 / 16.0),
              new Vector3(4 / 16.0, 9.7 / 16.0, 9.5 / 16.0),
              new Vector3(4 / 16.0, 6.2 / 16.0, 10.5 / 16.0),
              new Vector4(15 / 16.0, 13 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(4 / 16.0, 9.7 / 16.0, 9.5 / 16.0),
              new Vector3(4 / 16.0, 9.7 / 16.0, 10.5 / 16.0),
              new Vector3(4 / 16.0, 6.2 / 16.0, 9.5 / 16.0),
              new Vector4(13 / 16.0, 15 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(4 / 16., 8 / 16., 10 / 16.))
  );

  private static final Quad[][] pickleModles = {
      seaPickle1,
      seaPickle2,
      seaPickle3,
      seaPickle4,
  };

  private static final Quad[][] picklePickle = {
      seaPickle1Pickle,
      seaPickle2Pickle,
      seaPickle3Pickle,
      seaPickle4Pickle,
  };

  private final String description;
  public final boolean live;
  public final int pickles;

  public SeaPickle(int pickles, boolean live) {
    super("sea_pickle", Texture.seaPickle);
    pickles = Math.max(1, Math.min(4, pickles));
    this.description = String.format("pickles=%d", pickles);
    this.pickles = pickles;
    this.live = live;
    localIntersect = true;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    for (Quad quad : pickleModles[pickles - 1]) {
      if (quad.intersect(ray)) {
        float[] color = texture.getColor(ray.u, ray.v);
        if (color[3] > Ray.EPSILON) {
          ray.color.set(color);
          texture.getColor(ray);
          ray.n.set(quad.n);
          ray.t = ray.tNext;
          hit = true;
        }
      }
    }
    if (live) {
      for (Quad quad : picklePickle[pickles - 1]) {
        if (quad.intersect(ray)) {
          float[] color = texture.getColor(ray.u, ray.v);
          if (color[3] > Ray.EPSILON) {
            ray.color.set(color);
            ray.n.set(quad.n);
            ray.t = ray.tNext;
            hit = true;
          }
        }
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
