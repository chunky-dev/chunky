package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.Arrays;

public class CandleModel extends QuadModel {

  //region Candle Model
  private static final Quad[][] candleModel = new Quad[][]{
      Model.join(
          new Quad[]{
              new Quad(
                  new Vector3(7 / 16.0, 6 / 16.0, 9 / 16.0),
                  new Vector3(9 / 16.0, 6 / 16.0, 9 / 16.0),
                  new Vector3(7 / 16.0, 6 / 16.0, 7 / 16.0),
                  new Vector4(0 / 16.0, 2 / 16.0, 8 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
                  new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
                  new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
                  new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 2 / 16.0)
              ),
              new Quad(
                  new Vector3(7 / 16.0, 6 / 16.0, 9 / 16.0),
                  new Vector3(7 / 16.0, 6 / 16.0, 7 / 16.0),
                  new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 2 / 16.0)
              ),
              new Quad(
                  new Vector3(9 / 16.0, 6 / 16.0, 7 / 16.0),
                  new Vector3(9 / 16.0, 6 / 16.0, 9 / 16.0),
                  new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 2 / 16.0)
              ),
              new Quad(
                  new Vector3(7 / 16.0, 6 / 16.0, 7 / 16.0),
                  new Vector3(9 / 16.0, 6 / 16.0, 7 / 16.0),
                  new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 2 / 16.0)
              ),
              new Quad(
                  new Vector3(9 / 16.0, 6 / 16.0, 9 / 16.0),
                  new Vector3(7 / 16.0, 6 / 16.0, 9 / 16.0),
                  new Vector3(9 / 16.0, 0 / 16.0, 9 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 2 / 16.0)
              ),
          },
          Model.rotateY(new Quad[]{
              new Quad(
                  new Vector3(7.5 / 16.0, 7 / 16.0, 8 / 16.0),
                  new Vector3(8.5 / 16.0, 7 / 16.0, 8 / 16.0),
                  new Vector3(7.5 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(8.5 / 16.0, 7 / 16.0, 8 / 16.0),
                  new Vector3(7.5 / 16.0, 7 / 16.0, 8 / 16.0),
                  new Vector3(8.5 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              )
          }, Math.toRadians(45)),
          Model.rotateY(new Quad[]{
              new Quad(
                  new Vector3(7.5 / 16.0, 7 / 16.0, 8 / 16.0),
                  new Vector3(8.5 / 16.0, 7 / 16.0, 8 / 16.0),
                  new Vector3(7.5 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(8.5 / 16.0, 7 / 16.0, 8 / 16.0),
                  new Vector3(7.5 / 16.0, 7 / 16.0, 8 / 16.0),
                  new Vector3(8.5 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              )
          }, Math.toRadians(-45))
      ),
      Model.join(
          new Quad[]{
              new Quad(
                  new Vector3(5 / 16.0, 5 / 16.0, 9 / 16.0),
                  new Vector3(7 / 16.0, 5 / 16.0, 9 / 16.0),
                  new Vector3(5 / 16.0, 5 / 16.0, 7 / 16.0),
                  new Vector4(0 / 16.0, 2 / 16.0, 8 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(5 / 16.0, 0 / 16.0, 7 / 16.0),
                  new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
                  new Vector3(5 / 16.0, 0 / 16.0, 9 / 16.0),
                  new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 2 / 16.0)
              ),
              new Quad(
                  new Vector3(5 / 16.0, 5 / 16.0, 9 / 16.0),
                  new Vector3(5 / 16.0, 5 / 16.0, 7 / 16.0),
                  new Vector3(5 / 16.0, 0 / 16.0, 9 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 3 / 16.0)
              ),
              new Quad(
                  new Vector3(7 / 16.0, 5 / 16.0, 7 / 16.0),
                  new Vector3(7 / 16.0, 5 / 16.0, 9 / 16.0),
                  new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 3 / 16.0)
              ),
              new Quad(
                  new Vector3(5 / 16.0, 5 / 16.0, 7 / 16.0),
                  new Vector3(7 / 16.0, 5 / 16.0, 7 / 16.0),
                  new Vector3(5 / 16.0, 0 / 16.0, 7 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 3 / 16.0)
              ),
              new Quad(
                  new Vector3(7 / 16.0, 5 / 16.0, 9 / 16.0),
                  new Vector3(5 / 16.0, 5 / 16.0, 9 / 16.0),
                  new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 3 / 16.0)
              )
          },
          Model.rotateY(new Quad[]{
              new Quad(
                  new Vector3(5.5 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(6.5 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(5.5 / 16.0, 5 / 16.0, 8 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(6.5 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(5.5 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(6.5 / 16.0, 5 / 16.0, 8 / 16.0),
                  new Vector4(0 / 16.0, 1 / 16.0, 11 / 16.0, 10 / 16.0)
              )
          }, Math.toRadians(45), new Vector3(6 / 16.0, 0, 0.5)),
          Model.rotateY(new Quad[]{
              new Quad(
                  new Vector3(5.5 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(6.5 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(5.5 / 16.0, 5 / 16.0, 8 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(6.5 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(5.5 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(6.5 / 16.0, 5 / 16.0, 8 / 16.0),
                  new Vector4(0 / 16.0, 1 / 16.0, 11 / 16.0, 10 / 16.0)
              )
          }, Math.toRadians(-45), new Vector3(6 / 16.0, 0, 0.5)),
          new Quad[]{
              new Quad(
                  new Vector3(9 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(11 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(9 / 16.0, 6 / 16.0, 6 / 16.0),
                  new Vector4(0 / 16.0, 2 / 16.0, 8 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(9 / 16.0, 0 / 16.0, 6 / 16.0),
                  new Vector3(11 / 16.0, 0 / 16.0, 6 / 16.0),
                  new Vector3(9 / 16.0, 0 / 16.0, 8 / 16.0),
                  new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 2 / 16.0)
              ),
              new Quad(
                  new Vector3(9 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(9 / 16.0, 6 / 16.0, 6 / 16.0),
                  new Vector3(9 / 16.0, 0 / 16.0, 8 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 2 / 16.0)
              ),
              new Quad(
                  new Vector3(11 / 16.0, 6 / 16.0, 6 / 16.0),
                  new Vector3(11 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(11 / 16.0, 0 / 16.0, 6 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 2 / 16.0)
              ),
              new Quad(
                  new Vector3(9 / 16.0, 6 / 16.0, 6 / 16.0),
                  new Vector3(11 / 16.0, 6 / 16.0, 6 / 16.0),
                  new Vector3(9 / 16.0, 0 / 16.0, 6 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 2 / 16.0)
              ),
              new Quad(
                  new Vector3(11 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(9 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(11 / 16.0, 0 / 16.0, 8 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 2 / 16.0)
              )
          },
          Model.rotateY(new Quad[]{
              new Quad(
                  new Vector3(9.5 / 16.0, 7 / 16.0, 7 / 16.0),
                  new Vector3(10.5 / 16.0, 7 / 16.0, 7 / 16.0),
                  new Vector3(9.5 / 16.0, 6 / 16.0, 7 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(10.5 / 16.0, 7 / 16.0, 7 / 16.0),
                  new Vector3(9.5 / 16.0, 7 / 16.0, 7 / 16.0),
                  new Vector3(10.5 / 16.0, 6 / 16.0, 7 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              )
          }, Math.toRadians(45), new Vector3(10 / 16.0, 0, 7 / 16.0)),
          Model.rotateY(new Quad[]{
              new Quad(
                  new Vector3(9.5 / 16.0, 7 / 16.0, 7 / 16.0),
                  new Vector3(10.5 / 16.0, 7 / 16.0, 7 / 16.0),
                  new Vector3(9.5 / 16.0, 6 / 16.0, 7 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(10.5 / 16.0, 7 / 16.0, 7 / 16.0),
                  new Vector3(9.5 / 16.0, 7 / 16.0, 7 / 16.0),
                  new Vector3(10.5 / 16.0, 6 / 16.0, 7 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              )
          }, Math.toRadians(-45), new Vector3(10 / 16.0, 0, 7 / 16.0))
      ),
      Model.join(
          new Quad[]{
              new Quad(
                  new Vector3(7 / 16.0, 3 / 16.0, 11 / 16.0),
                  new Vector3(9 / 16.0, 3 / 16.0, 11 / 16.0),
                  new Vector3(7 / 16.0, 3 / 16.0, 9 / 16.0),
                  new Vector4(0 / 16.0, 2 / 16.0, 8 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
                  new Vector3(9 / 16.0, 0 / 16.0, 9 / 16.0),
                  new Vector3(7 / 16.0, 0 / 16.0, 11 / 16.0),
                  new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 2 / 16.0)
              ),
              new Quad(
                  new Vector3(7 / 16.0, 3 / 16.0, 11 / 16.0),
                  new Vector3(7 / 16.0, 3 / 16.0, 9 / 16.0),
                  new Vector3(7 / 16.0, 0 / 16.0, 11 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 5 / 16.0)
              ),
              new Quad(
                  new Vector3(9 / 16.0, 3 / 16.0, 9 / 16.0),
                  new Vector3(9 / 16.0, 3 / 16.0, 11 / 16.0),
                  new Vector3(9 / 16.0, 0 / 16.0, 9 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 5 / 16.0)
              ),
              new Quad(
                  new Vector3(7 / 16.0, 3 / 16.0, 9 / 16.0),
                  new Vector3(9 / 16.0, 3 / 16.0, 9 / 16.0),
                  new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 5 / 16.0)
              ),
              new Quad(
                  new Vector3(9 / 16.0, 3 / 16.0, 11 / 16.0),
                  new Vector3(7 / 16.0, 3 / 16.0, 11 / 16.0),
                  new Vector3(9 / 16.0, 0 / 16.0, 11 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 5 / 16.0)
              )
          },
          Model.rotateY(new Quad[]{
              new Quad(
                  new Vector3(7.5 / 16.0, 4 / 16.0, 10 / 16.0),
                  new Vector3(8.5 / 16.0, 4 / 16.0, 10 / 16.0),
                  new Vector3(7.5 / 16.0, 3 / 16.0, 10 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(8.5 / 16.0, 4 / 16.0, 10 / 16.0),
                  new Vector3(7.5 / 16.0, 4 / 16.0, 10 / 16.0),
                  new Vector3(8.5 / 16.0, 3 / 16.0, 10 / 16.0),
                  new Vector4(0 / 16.0, 1 / 16.0, 11 / 16.0, 10 / 16.0)
              )
          }, Math.toRadians(45), new Vector3(8 / 16.0, 0, 10 / 16.0)),
          Model.rotateY(new Quad[]{
              new Quad(
                  new Vector3(7.5 / 16.0, 4 / 16.0, 10 / 16.0),
                  new Vector3(8.5 / 16.0, 4 / 16.0, 10 / 16.0),
                  new Vector3(7.5 / 16.0, 3 / 16.0, 10 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(8.5 / 16.0, 4 / 16.0, 10 / 16.0),
                  new Vector3(7.5 / 16.0, 4 / 16.0, 10 / 16.0),
                  new Vector3(8.5 / 16.0, 3 / 16.0, 10 / 16.0),
                  new Vector4(0 / 16.0, 1 / 16.0, 11 / 16.0, 10 / 16.0)
              )
          }, Math.toRadians(-45), new Vector3(8 / 16.0, 0, 10 / 16.0)),
          new Quad[]{
              new Quad(
                  new Vector3(5 / 16.0, 5 / 16.0, 9 / 16.0),
                  new Vector3(7 / 16.0, 5 / 16.0, 9 / 16.0),
                  new Vector3(5 / 16.0, 5 / 16.0, 7 / 16.0),
                  new Vector4(0 / 16.0, 2 / 16.0, 8 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(5 / 16.0, 0 / 16.0, 7 / 16.0),
                  new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
                  new Vector3(5 / 16.0, 0 / 16.0, 9 / 16.0),
                  new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 2 / 16.0)
              ),
              new Quad(
                  new Vector3(5 / 16.0, 5 / 16.0, 9 / 16.0),
                  new Vector3(5 / 16.0, 5 / 16.0, 7 / 16.0),
                  new Vector3(5 / 16.0, 0 / 16.0, 9 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 3 / 16.0)
              ),
              new Quad(
                  new Vector3(7 / 16.0, 5 / 16.0, 7 / 16.0),
                  new Vector3(7 / 16.0, 5 / 16.0, 9 / 16.0),
                  new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 3 / 16.0)
              ),
              new Quad(
                  new Vector3(5 / 16.0, 5 / 16.0, 7 / 16.0),
                  new Vector3(7 / 16.0, 5 / 16.0, 7 / 16.0),
                  new Vector3(5 / 16.0, 0 / 16.0, 7 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 3 / 16.0)
              ),
              new Quad(
                  new Vector3(7 / 16.0, 5 / 16.0, 9 / 16.0),
                  new Vector3(5 / 16.0, 5 / 16.0, 9 / 16.0),
                  new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 3 / 16.0)
              )
          },
          Model.rotateY(new Quad[]{
              new Quad(
                  new Vector3(5.5 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(6.5 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(5.5 / 16.0, 5 / 16.0, 8 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(6.5 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(5.5 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(6.5 / 16.0, 5 / 16.0, 8 / 16.0),
                  new Vector4(0 / 16.0, 1 / 16.0, 11 / 16.0, 10 / 16.0)
              )
          }, Math.toRadians(45), new Vector3(6 / 16.0, 0, 8 / 16.0)),
          Model.rotateY(new Quad[]{
              new Quad(
                  new Vector3(5.5 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(6.5 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(5.5 / 16.0, 5 / 16.0, 8 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(6.5 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(5.5 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(6.5 / 16.0, 5 / 16.0, 8 / 16.0),
                  new Vector4(0 / 16.0, 1 / 16.0, 11 / 16.0, 10 / 16.0)
              )
          }, Math.toRadians(-45), new Vector3(6 / 16.0, 0, 8 / 16.0)),
          new Quad[]{
              new Quad(
                  new Vector3(8 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(10 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(8 / 16.0, 6 / 16.0, 6 / 16.0),
                  new Vector4(0 / 16.0, 2 / 16.0, 8 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(8 / 16.0, 0 / 16.0, 6 / 16.0),
                  new Vector3(10 / 16.0, 0 / 16.0, 6 / 16.0),
                  new Vector3(8 / 16.0, 0 / 16.0, 8 / 16.0),
                  new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 2 / 16.0)
              ),
              new Quad(
                  new Vector3(8 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(8 / 16.0, 6 / 16.0, 6 / 16.0),
                  new Vector3(8 / 16.0, 0 / 16.0, 8 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 2 / 16.0)
              ),
              new Quad(
                  new Vector3(10 / 16.0, 6 / 16.0, 6 / 16.0),
                  new Vector3(10 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(10 / 16.0, 0 / 16.0, 6 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 2 / 16.0)
              ),
              new Quad(
                  new Vector3(8 / 16.0, 6 / 16.0, 6 / 16.0),
                  new Vector3(10 / 16.0, 6 / 16.0, 6 / 16.0),
                  new Vector3(8 / 16.0, 0 / 16.0, 6 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 2 / 16.0)
              ),
              new Quad(
                  new Vector3(10 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(8 / 16.0, 6 / 16.0, 8 / 16.0),
                  new Vector3(10 / 16.0, 0 / 16.0, 8 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 2 / 16.0)
              )
          },
          Model.rotateY(new Quad[]{
              new Quad(
                  new Vector3(8.5 / 16.0, 7 / 16.0, 7 / 16.0),
                  new Vector3(9.5 / 16.0, 7 / 16.0, 7 / 16.0),
                  new Vector3(8.5 / 16.0, 6 / 16.0, 7 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(9.5 / 16.0, 7 / 16.0, 7 / 16.0),
                  new Vector3(8.5 / 16.0, 7 / 16.0, 7 / 16.0),
                  new Vector3(9.5 / 16.0, 6 / 16.0, 7 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              )
          }, Math.toRadians(45), new Vector3(9 / 16.0, 0, 7 / 16.0)),
          Model.rotateY(new Quad[]{
              new Quad(
                  new Vector3(8.5 / 16.0, 7 / 16.0, 7 / 16.0),
                  new Vector3(9.5 / 16.0, 7 / 16.0, 7 / 16.0),
                  new Vector3(8.5 / 16.0, 6 / 16.0, 7 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(9.5 / 16.0, 7 / 16.0, 7 / 16.0),
                  new Vector3(8.5 / 16.0, 7 / 16.0, 7 / 16.0),
                  new Vector3(9.5 / 16.0, 6 / 16.0, 7 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              )
          }, Math.toRadians(-45), new Vector3(9 / 16.0, 0, 7 / 16.0))
      ),
      Model.join(
          new Quad[]{
              new Quad(
                  new Vector3(6 / 16.0, 3 / 16.0, 10 / 16.0),
                  new Vector3(8 / 16.0, 3 / 16.0, 10 / 16.0),
                  new Vector3(6 / 16.0, 3 / 16.0, 8 / 16.0),
                  new Vector4(0 / 16.0, 2 / 16.0, 8 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(6 / 16.0, 0 / 16.0, 8 / 16.0),
                  new Vector3(8 / 16.0, 0 / 16.0, 8 / 16.0),
                  new Vector3(6 / 16.0, 0 / 16.0, 10 / 16.0),
                  new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 2 / 16.0)
              ),
              new Quad(
                  new Vector3(6 / 16.0, 3 / 16.0, 10 / 16.0),
                  new Vector3(6 / 16.0, 3 / 16.0, 8 / 16.0),
                  new Vector3(6 / 16.0, 0 / 16.0, 10 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 5 / 16.0)
              ),
              new Quad(
                  new Vector3(8 / 16.0, 3 / 16.0, 8 / 16.0),
                  new Vector3(8 / 16.0, 3 / 16.0, 10 / 16.0),
                  new Vector3(8 / 16.0, 0 / 16.0, 8 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 5 / 16.0)
              ),
              new Quad(
                  new Vector3(6 / 16.0, 3 / 16.0, 8 / 16.0),
                  new Vector3(8 / 16.0, 3 / 16.0, 8 / 16.0),
                  new Vector3(6 / 16.0, 0 / 16.0, 8 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 5 / 16.0)
              ),
              new Quad(
                  new Vector3(8 / 16.0, 3 / 16.0, 10 / 16.0),
                  new Vector3(6 / 16.0, 3 / 16.0, 10 / 16.0),
                  new Vector3(8 / 16.0, 0 / 16.0, 10 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 5 / 16.0)
              )
          },
          Model.rotateY(new Quad[]{
              new Quad(
                  new Vector3(6.5 / 16.0, 4 / 16.0, 9 / 16.0),
                  new Vector3(7.5 / 16.0, 4 / 16.0, 9 / 16.0),
                  new Vector3(6.5 / 16.0, 3 / 16.0, 9 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(7.5 / 16.0, 4 / 16.0, 9 / 16.0),
                  new Vector3(6.5 / 16.0, 4 / 16.0, 9 / 16.0),
                  new Vector3(7.5 / 16.0, 3 / 16.0, 9 / 16.0),
                  new Vector4(0 / 16.0, 1 / 16.0, 11 / 16.0, 10 / 16.0)
              )
          }, Math.toRadians(45), new Vector3(7 / 16.0, 0, 9 / 16.0)),
          Model.rotateY(new Quad[]{
              new Quad(
                  new Vector3(6.5 / 16.0, 4 / 16.0, 9 / 16.0),
                  new Vector3(7.5 / 16.0, 4 / 16.0, 9 / 16.0),
                  new Vector3(6.5 / 16.0, 3 / 16.0, 9 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(7.5 / 16.0, 4 / 16.0, 9 / 16.0),
                  new Vector3(6.5 / 16.0, 4 / 16.0, 9 / 16.0),
                  new Vector3(7.5 / 16.0, 3 / 16.0, 9 / 16.0),
                  new Vector4(0 / 16.0, 1 / 16.0, 11 / 16.0, 10 / 16.0)
              )
          }, Math.toRadians(-45), new Vector3(7 / 16.0, 0, 9 / 16.0)),
          new Quad[]{
              new Quad(
                  new Vector3(9 / 16.0, 5 / 16.0, 10 / 16.0),
                  new Vector3(11 / 16.0, 5 / 16.0, 10 / 16.0),
                  new Vector3(9 / 16.0, 5 / 16.0, 8 / 16.0),
                  new Vector4(0 / 16.0, 2 / 16.0, 8 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(9 / 16.0, 0 / 16.0, 8 / 16.0),
                  new Vector3(11 / 16.0, 0 / 16.0, 8 / 16.0),
                  new Vector3(9 / 16.0, 0 / 16.0, 10 / 16.0),
                  new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 2 / 16.0)
              ),
              new Quad(
                  new Vector3(9 / 16.0, 5 / 16.0, 10 / 16.0),
                  new Vector3(9 / 16.0, 5 / 16.0, 8 / 16.0),
                  new Vector3(9 / 16.0, 0 / 16.0, 10 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 3 / 16.0)
              ),
              new Quad(
                  new Vector3(11 / 16.0, 5 / 16.0, 8 / 16.0),
                  new Vector3(11 / 16.0, 5 / 16.0, 10 / 16.0),
                  new Vector3(11 / 16.0, 0 / 16.0, 8 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 3 / 16.0)
              ),
              new Quad(
                  new Vector3(9 / 16.0, 5 / 16.0, 8 / 16.0),
                  new Vector3(11 / 16.0, 5 / 16.0, 8 / 16.0),
                  new Vector3(9 / 16.0, 0 / 16.0, 8 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 3 / 16.0)
              ),
              new Quad(
                  new Vector3(11 / 16.0, 5 / 16.0, 10 / 16.0),
                  new Vector3(9 / 16.0, 5 / 16.0, 10 / 16.0),
                  new Vector3(11 / 16.0, 0 / 16.0, 10 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 3 / 16.0)
              )
          },
          Model.rotateY(new Quad[]{
              new Quad(
                  new Vector3(9.5 / 16.0, 6 / 16.0, 9 / 16.0),
                  new Vector3(10.5 / 16.0, 6 / 16.0, 9 / 16.0),
                  new Vector3(9.5 / 16.0, 5 / 16.0, 9 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(10.5 / 16.0, 6 / 16.0, 9 / 16.0),
                  new Vector3(9.5 / 16.0, 6 / 16.0, 9 / 16.0),
                  new Vector3(10.5 / 16.0, 5 / 16.0, 9 / 16.0),
                  new Vector4(0 / 16.0, 1 / 16.0, 11 / 16.0, 10 / 16.0)
              )
          }, Math.toRadians(45), new Vector3(10 / 16.0, 0, 9 / 16.0)),
          Model.rotateY(new Quad[]{
              new Quad(
                  new Vector3(9.5 / 16.0, 6 / 16.0, 9 / 16.0),
                  new Vector3(10.5 / 16.0, 6 / 16.0, 9 / 16.0),
                  new Vector3(9.5 / 16.0, 5 / 16.0, 9 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(10.5 / 16.0, 6 / 16.0, 9 / 16.0),
                  new Vector3(9.5 / 16.0, 6 / 16.0, 9 / 16.0),
                  new Vector3(10.5 / 16.0, 5 / 16.0, 9 / 16.0),
                  new Vector4(0 / 16.0, 1 / 16.0, 11 / 16.0, 10 / 16.0)
              )
          }, Math.toRadians(-45), new Vector3(10 / 16.0, 0, 9 / 16.0)),
          new Quad[]{
              new Quad(
                  new Vector3(5 / 16.0, 5 / 16.0, 7 / 16.0),
                  new Vector3(7 / 16.0, 5 / 16.0, 7 / 16.0),
                  new Vector3(5 / 16.0, 5 / 16.0, 5 / 16.0),
                  new Vector4(0 / 16.0, 2 / 16.0, 8 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(5 / 16.0, 0 / 16.0, 5 / 16.0),
                  new Vector3(7 / 16.0, 0 / 16.0, 5 / 16.0),
                  new Vector3(5 / 16.0, 0 / 16.0, 7 / 16.0),
                  new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 2 / 16.0)
              ),
              new Quad(
                  new Vector3(5 / 16.0, 5 / 16.0, 7 / 16.0),
                  new Vector3(5 / 16.0, 5 / 16.0, 5 / 16.0),
                  new Vector3(5 / 16.0, 0 / 16.0, 7 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 3 / 16.0)
              ),
              new Quad(
                  new Vector3(7 / 16.0, 5 / 16.0, 5 / 16.0),
                  new Vector3(7 / 16.0, 5 / 16.0, 7 / 16.0),
                  new Vector3(7 / 16.0, 0 / 16.0, 5 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 3 / 16.0)
              ),
              new Quad(
                  new Vector3(5 / 16.0, 5 / 16.0, 5 / 16.0),
                  new Vector3(7 / 16.0, 5 / 16.0, 5 / 16.0),
                  new Vector3(5 / 16.0, 0 / 16.0, 5 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 3 / 16.0)
              ),
              new Quad(
                  new Vector3(7 / 16.0, 5 / 16.0, 7 / 16.0),
                  new Vector3(5 / 16.0, 5 / 16.0, 7 / 16.0),
                  new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 3 / 16.0)
              )
          },
          Model.rotateY(new Quad[]{
              new Quad(
                  new Vector3(5.5 / 16.0, 6 / 16.0, 6 / 16.0),
                  new Vector3(6.5 / 16.0, 6 / 16.0, 6 / 16.0),
                  new Vector3(5.5 / 16.0, 5 / 16.0, 6 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(6.5 / 16.0, 6 / 16.0, 6 / 16.0),
                  new Vector3(5.5 / 16.0, 6 / 16.0, 6 / 16.0),
                  new Vector3(6.5 / 16.0, 5 / 16.0, 6 / 16.0),
                  new Vector4(0 / 16.0, 1 / 16.0, 11 / 16.0, 10 / 16.0)
              )
          }, Math.toRadians(45), new Vector3(6 / 16.0, 0, 6 / 16.0)),
          Model.rotateY(new Quad[]{
              new Quad(
                  new Vector3(5.5 / 16.0, 6 / 16.0, 6 / 16.0),
                  new Vector3(6.5 / 16.0, 6 / 16.0, 6 / 16.0),
                  new Vector3(5.5 / 16.0, 5 / 16.0, 6 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(6.5 / 16.0, 6 / 16.0, 6 / 16.0),
                  new Vector3(5.5 / 16.0, 6 / 16.0, 6 / 16.0),
                  new Vector3(6.5 / 16.0, 5 / 16.0, 6 / 16.0),
                  new Vector4(0 / 16.0, 1 / 16.0, 11 / 16.0, 10 / 16.0)
              )
          }, Math.toRadians(-45), new Vector3(6 / 16.0, 0, 6 / 16.0)),
          new Quad[]{
              new Quad(
                  new Vector3(8 / 16.0, 6 / 16.0, 7 / 16.0),
                  new Vector3(10 / 16.0, 6 / 16.0, 7 / 16.0),
                  new Vector3(8 / 16.0, 6 / 16.0, 5 / 16.0),
                  new Vector4(0 / 16.0, 2 / 16.0, 8 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(8 / 16.0, 0 / 16.0, 5 / 16.0),
                  new Vector3(10 / 16.0, 0 / 16.0, 5 / 16.0),
                  new Vector3(8 / 16.0, 0 / 16.0, 7 / 16.0),
                  new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 2 / 16.0)
              ),
              new Quad(
                  new Vector3(8 / 16.0, 6 / 16.0, 7 / 16.0),
                  new Vector3(8 / 16.0, 6 / 16.0, 5 / 16.0),
                  new Vector3(8 / 16.0, 0 / 16.0, 7 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 2 / 16.0)
              ),
              new Quad(
                  new Vector3(10 / 16.0, 6 / 16.0, 5 / 16.0),
                  new Vector3(10 / 16.0, 6 / 16.0, 7 / 16.0),
                  new Vector3(10 / 16.0, 0 / 16.0, 5 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 2 / 16.0)
              ),
              new Quad(
                  new Vector3(8 / 16.0, 6 / 16.0, 5 / 16.0),
                  new Vector3(10 / 16.0, 6 / 16.0, 5 / 16.0),
                  new Vector3(8 / 16.0, 0 / 16.0, 5 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 2 / 16.0)
              ),
              new Quad(
                  new Vector3(10 / 16.0, 6 / 16.0, 7 / 16.0),
                  new Vector3(8 / 16.0, 6 / 16.0, 7 / 16.0),
                  new Vector3(10 / 16.0, 0 / 16.0, 7 / 16.0),
                  new Vector4(2 / 16.0, 0 / 16.0, 8 / 16.0, 2 / 16.0)
              )
          },
          Model.rotateY(new Quad[]{
              new Quad(
                  new Vector3(8.5 / 16.0, 7 / 16.0, 6 / 16.0),
                  new Vector3(9.5 / 16.0, 7 / 16.0, 6 / 16.0),
                  new Vector3(8.5 / 16.0, 6 / 16.0, 6 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(9.5 / 16.0, 7 / 16.0, 6 / 16.0),
                  new Vector3(8.5 / 16.0, 7 / 16.0, 6 / 16.0),
                  new Vector3(9.5 / 16.0, 6 / 16.0, 6 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              )
          }, Math.toRadians(45), new Vector3(9 / 16.0, 0, 6 / 16.0)),
          Model.rotateY(new Quad[]{
              new Quad(
                  new Vector3(8.5 / 16.0, 7 / 16.0, 6 / 16.0),
                  new Vector3(9.5 / 16.0, 7 / 16.0, 6 / 16.0),
                  new Vector3(8.5 / 16.0, 6 / 16.0, 6 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              ),
              new Quad(
                  new Vector3(9.5 / 16.0, 7 / 16.0, 6 / 16.0),
                  new Vector3(8.5 / 16.0, 7 / 16.0, 6 / 16.0),
                  new Vector3(9.5 / 16.0, 6 / 16.0, 6 / 16.0),
                  new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
              )
          }, Math.toRadians(-45), new Vector3(9 / 16.0, 0, 6 / 16.0))
      )
  };
  //endregion

  private final Quad[] quads;
  private final Texture[] textures;

  public CandleModel(Texture candle, int candles) {
    quads = candleModel[candles-1];
    textures = new Texture[quads.length];
    Arrays.fill(textures, candle);
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public Texture[] getTextures() {
    return textures;
  }
}
