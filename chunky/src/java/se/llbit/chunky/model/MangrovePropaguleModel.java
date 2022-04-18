package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MangrovePropaguleModel extends QuadModel {
  //region Propagule model
  private static final Quad[] propaguleModel = Model.join(
    Model.rotateY(
      new Quad[]{
        // leaves
        new Quad(
          new Vector3(11.5 / 16.0, 15 / 16.0, 8 / 16.0),
          new Vector3(4.5 / 16.0, 15 / 16.0, 8 / 16.0),
          new Vector3(11.5 / 16.0, 15 / 16.0, 8 / 16.0),
          new Vector4(4 / 16.0, 11 / 16.0, 10 / 16.0, 10 / 16.0)
        ),
        new Quad(
          new Vector3(4.5 / 16.0, 9 / 16.0, 8 / 16.0),
          new Vector3(11.5 / 16.0, 9 / 16.0, 8 / 16.0),
          new Vector3(4.5 / 16.0, 9 / 16.0, 8 / 16.0),
          new Vector4(4 / 16.0, 11 / 16.0, 5 / 16.0, 5 / 16.0)
        ),
        new Quad(
          new Vector3(4.5 / 16.0, 15 / 16.0, 8 / 16.0),
          new Vector3(4.5 / 16.0, 15 / 16.0, 8 / 16.0),
          new Vector3(4.5 / 16.0, 9 / 16.0, 8 / 16.0),
          new Vector4(4 / 16.0, 4 / 16.0, 10 / 16.0, 5 / 16.0)
        ),
        new Quad(
          new Vector3(11.5 / 16.0, 15 / 16.0, 8 / 16.0),
          new Vector3(11.5 / 16.0, 15 / 16.0, 8 / 16.0),
          new Vector3(11.5 / 16.0, 9 / 16.0, 8 / 16.0),
          new Vector4(11 / 16.0, 11 / 16.0, 10 / 16.0, 6 / 16.0)
        ),
        new Quad(
          new Vector3(4.5 / 16.0, 15 / 16.0, 8 / 16.0),
          new Vector3(11.5 / 16.0, 15 / 16.0, 8 / 16.0),
          new Vector3(4.5 / 16.0, 9 / 16.0, 8 / 16.0),
          new Vector4(11 / 16.0, 4 / 16.0, 15 / 16.0, 9 / 16.0)
        ),
        new Quad(
          new Vector3(11.5 / 16.0, 15 / 16.0, 8 / 16.0),
          new Vector3(4.5 / 16.0, 15 / 16.0, 8 / 16.0),
          new Vector3(11.5 / 16.0, 9 / 16.0, 8 / 16.0),
          new Vector4(11 / 16.0, 4 / 16.0, 15 / 16.0, 9 / 16.0)
        ),
        // leaves
        new Quad(
          new Vector3(8 / 16.0, 15 / 16.0, 11.5 / 16.0),
          new Vector3(8 / 16.0, 15 / 16.0, 4.5 / 16.0),
          new Vector3(8 / 16.0, 15 / 16.0, 11.5 / 16.0),
          new Vector4(11 / 16.0, 13 / 16.0, 16 / 16.0, 16 / 16.0)
        ),
        new Quad(
          new Vector3(8 / 16.0, 9 / 16.0, 4.5 / 16.0),
          new Vector3(8 / 16.0, 9 / 16.0, 11.5 / 16.0),
          new Vector3(8 / 16.0, 9 / 16.0, 4.5 / 16.0),
          new Vector4(11 / 16.0, 13 / 16.0, 6 / 16.0, 6 / 16.0)
        ),
        new Quad(
          new Vector3(8 / 16.0, 9 / 16.0, 4.5 / 16.0),
          new Vector3(8 / 16.0, 9 / 16.0, 4.5 / 16.0),
          new Vector3(8 / 16.0, 9 / 16.0, 11.5 / 16.0),
          new Vector4(11 / 16.0, 13 / 16.0, 6 / 16.0, 6 / 16.0)
        ),
        new Quad(
          new Vector3(8 / 16.0, 15 / 16.0, 11.5 / 16.0),
          new Vector3(8 / 16.0, 15 / 16.0, 4.5 / 16.0),
          new Vector3(8 / 16.0, 9 / 16.0, 11.5 / 16.0),
          new Vector4(11 / 16.0, 4 / 16.0, 15 / 16.0, 9 / 16.0)
        ),
        new Quad(
          new Vector3(8 / 16.0, 15 / 16.0, 4.5 / 16.0),
          new Vector3(8 / 16.0, 15 / 16.0, 11.5 / 16.0),
          new Vector3(8 / 16.0, 9 / 16.0, 4.5 / 16.0),
          new Vector4(11 / 16.0, 4 / 16.0, 15 / 16.0, 9 / 16.0)
        ),
        new Quad(
          new Vector3(8 / 16.0, 15 / 16.0, 4.5 / 16.0),
          new Vector3(8 / 16.0, 15 / 16.0, 4.5 / 16.0),
          new Vector3(8 / 16.0, 9 / 16.0, 4.5 / 16.0),
          new Vector4(13 / 16.0, 13 / 16.0, 16 / 16.0, 6 / 16.0)
        ),
        new Quad(
          new Vector3(8 / 16.0, 15 / 16.0, 11.5 / 16.0),
          new Vector3(8 / 16.0, 15 / 16.0, 11.5 / 16.0),
          new Vector3(8 / 16.0, 9 / 16.0, 11.5 / 16.0),
          new Vector4(11 / 16.0, 11 / 16.0, 16 / 16.0, 6 / 16.0)
        ),
        // hypocotyl
        new Quad(
          new Vector3(9 / 16.0, 9 / 16.0, 8 / 16.0),
          new Vector3(7 / 16.0, 9 / 16.0, 8 / 16.0),
          new Vector3(9 / 16.0, 9 / 16.0, 8 / 16.0),
          new Vector4(11 / 16.0, 13 / 16.0, 16 / 16.0, 16 / 16.0)
        ),
        new Quad(
          new Vector3(7 / 16.0, 0 / 16.0, 8 / 16.0),
          new Vector3(9 / 16.0, 0 / 16.0, 8 / 16.0),
          new Vector3(7 / 16.0, 0 / 16.0, 8 / 16.0),
          new Vector4(11 / 16.0, 13 / 16.0, 6 / 16.0, 6 / 16.0)
        ),
        new Quad(
          new Vector3(7 / 16.0, 9 / 16.0, 8 / 16.0),
          new Vector3(7 / 16.0, 9 / 16.0, 8 / 16.0),
          new Vector3(7 / 16.0, 0 / 16.0, 8 / 16.0),
          new Vector4(13 / 16.0, 13 / 16.0, 16 / 16.0, 6 / 16.0)
        ),
        new Quad(
          new Vector3(9 / 16.0, 9 / 16.0, 8 / 16.0),
          new Vector3(9 / 16.0, 9 / 16.0, 8 / 16.0),
          new Vector3(9 / 16.0, 0 / 16.0, 8 / 16.0),
          new Vector4(11 / 16.0, 11 / 16.0, 16 / 16.0, 6 / 16.0)
        ),
        new Quad(
          new Vector3(7 / 16.0, 9 / 16.0, 8 / 16.0),
          new Vector3(9 / 16.0, 9 / 16.0, 8 / 16.0),
          new Vector3(7 / 16.0, 0 / 16.0, 8 / 16.0),
          new Vector4(9 / 16.0, 7 / 16.0, 9 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(9 / 16.0, 9 / 16.0, 8 / 16.0),
          new Vector3(7 / 16.0, 9 / 16.0, 8 / 16.0),
          new Vector3(9 / 16.0, 0 / 16.0, 8 / 16.0),
          new Vector4(9 / 16.0, 7 / 16.0, 9 / 16.0, 0 / 16.0)
        ),
      },
      Math.toRadians(-45)),
    // hypocotyl
    Model.rotateY(new Quad[]{
      new Quad(
        new Vector3(7 / 16.0, 9 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 9 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 9 / 16.0, 8 / 16.0),
        new Vector4(11 / 16.0, 13 / 16.0, 16 / 16.0, 16 / 16.0)
      ),
      new Quad(
        new Vector3(7 / 16.0, 0 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 0 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 0 / 16.0, 8 / 16.0),
        new Vector4(11 / 16.0, 13 / 16.0, 6 / 16.0, 6 / 16.0)
      ),
      new Quad(
        new Vector3(7 / 16.0, 9 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 9 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 0 / 16.0, 8 / 16.0),
        new Vector4(11 / 16.0, 11 / 16.0, 16 / 16.0, 6 / 16.0)
      ),
      new Quad(
        new Vector3(9 / 16.0, 9 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 9 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 0 / 16.0, 8 / 16.0),
        new Vector4(13 / 16.0, 13 / 16.0, 16 / 16.0, 6 / 16.0)
      ),
      new Quad(
        new Vector3(7 / 16.0, 9 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 9 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 0 / 16.0, 8 / 16.0),
        new Vector4(9 / 16.0, 7 / 16.0, 9 / 16.0, 0 / 16.0)
      ),
      new Quad(
        new Vector3(9 / 16.0, 9 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 9 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 0 / 16.0, 8 / 16.0),
        new Vector4(9 / 16.0, 7 / 16.0, 9 / 16.0, 0 / 16.0)
      )
    }, Math.toRadians(45))
  );
  //endregion

  //region Hanging propagule model (age=0)
  private static final Quad[] propaguleModelAge0 = Model.join(
    Model.rotateX(
      new Quad[]{
        new Quad(
          new Vector3(9 / 16.0, 13.61104 / 16.0, 10.07193 / 16.0),
          new Vector3(7 / 16.0, 13.61104 / 16.0, 10.07193 / 16.0),
          new Vector3(9 / 16.0, 13.61104 / 16.0, 12.07193 / 16.0),
          new Vector4(8 / 16.0, 10 / 16.0, 11 / 16.0, 13 / 16.0)
        ),
        new Quad(
          new Vector3(7 / 16.0, 13.61104 / 16.0, 10.07193 / 16.0),
          new Vector3(9 / 16.0, 13.61104 / 16.0, 10.07193 / 16.0),
          new Vector3(7 / 16.0, 13.61104 / 16.0, 12.07193 / 16.0),
          new Vector4(6 / 16.0, 8 / 16.0, 11 / 16.0, 13 / 16.0)
        )
      }, Math.toRadians(22.5), new Vector3(8 / 16., 16 / 16., 8 / 16.)),
    Model.rotateZ(new Quad[]{
      new Quad(
        new Vector3(10.07193 / 16.0, 13.61104 / 16.0, 7 / 16.0),
        new Vector3(10.07193 / 16.0, 13.61104 / 16.0, 9 / 16.0),
        new Vector3(12.07193 / 16.0, 13.61104 / 16.0, 7 / 16.0),
        new Vector4(8 / 16.0, 10 / 16.0, 11 / 16.0, 13 / 16.0)
      ),
      new Quad(
        new Vector3(10.07193 / 16.0, 13.61104 / 16.0, 9 / 16.0),
        new Vector3(10.07193 / 16.0, 13.61104 / 16.0, 7 / 16.0),
        new Vector3(12.07193 / 16.0, 13.61104 / 16.0, 9 / 16.0),
        new Vector4(6 / 16.0, 8 / 16.0, 11 / 16.0, 13 / 16.0)
      )
    }, Math.toRadians(-22.5), new Vector3(8 / 16., 16 / 16., 8 / 16.)),
    Model.rotateX(new Quad[]{
      new Quad(
        new Vector3(7 / 16.0, 13.61104 / 16.0, 5.92807 / 16.0),
        new Vector3(9 / 16.0, 13.61104 / 16.0, 5.92807 / 16.0),
        new Vector3(7 / 16.0, 13.61104 / 16.0, 3.92807 / 16.0),
        new Vector4(8 / 16.0, 10 / 16.0, 11 / 16.0, 13 / 16.0)
      ),
      new Quad(
        new Vector3(9 / 16.0, 13.61104 / 16.0, 5.92807 / 16.0),
        new Vector3(7 / 16.0, 13.61104 / 16.0, 5.92807 / 16.0),
        new Vector3(9 / 16.0, 13.61104 / 16.0, 3.92807 / 16.0),
        new Vector4(6 / 16.0, 8 / 16.0, 11 / 16.0, 13 / 16.0)
      )
    }, Math.toRadians(-22.5), new Vector3(8 / 16., 16 / 16., 8 / 16.)),
    Model.rotateZ(new Quad[]{
      new Quad(
        new Vector3(5.92807 / 16.0, 13.61104 / 16.0, 9 / 16.0),
        new Vector3(5.92807 / 16.0, 13.61104 / 16.0, 7 / 16.0),
        new Vector3(3.92807 / 16.0, 13.61104 / 16.0, 9 / 16.0),
        new Vector4(8 / 16.0, 10 / 16.0, 11 / 16.0, 13 / 16.0)
      ),
      new Quad(
        new Vector3(5.92807 / 16.0, 13.61104 / 16.0, 7 / 16.0),
        new Vector3(5.92807 / 16.0, 13.61104 / 16.0, 9 / 16.0),
        new Vector3(3.92807 / 16.0, 13.61104 / 16.0, 7 / 16.0),
        new Vector4(6 / 16.0, 8 / 16.0, 11 / 16.0, 13 / 16.0)
      )
    }, Math.toRadians(22.5), new Vector3(8 / 16., 16 / 16., 8 / 16.)),
    new Quad[]{
      new Quad(
        new Vector3(7 / 16.0, 14 / 16.0, 9 / 16.0),
        new Vector3(9 / 16.0, 14 / 16.0, 9 / 16.0),
        new Vector3(7 / 16.0, 14 / 16.0, 7 / 16.0),
        new Vector4(0 / 16.0, 2 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
        new Vector3(7 / 16.0, 13 / 16.0, 7 / 16.0),
        new Vector3(9 / 16.0, 13 / 16.0, 7 / 16.0),
        new Vector3(7 / 16.0, 13 / 16.0, 9 / 16.0),
        new Vector4(0 / 16.0, 2 / 16.0, 11 / 16.0, 13 / 16.0)
      ),
      new Quad(
        new Vector3(7 / 16.0, 14 / 16.0, 9 / 16.0),
        new Vector3(7 / 16.0, 14 / 16.0, 7 / 16.0),
        new Vector3(7 / 16.0, 13 / 16.0, 9 / 16.0),
        new Vector4(2 / 16.0, 0 / 16.0, 14 / 16.0, 13 / 16.0)
      ),
      new Quad(
        new Vector3(9 / 16.0, 14 / 16.0, 7 / 16.0),
        new Vector3(9 / 16.0, 14 / 16.0, 9 / 16.0),
        new Vector3(9 / 16.0, 13 / 16.0, 7 / 16.0),
        new Vector4(2 / 16.0, 0 / 16.0, 14 / 16.0, 13 / 16.0)
      ),
      new Quad(
        new Vector3(7 / 16.0, 14 / 16.0, 7 / 16.0),
        new Vector3(9 / 16.0, 14 / 16.0, 7 / 16.0),
        new Vector3(7 / 16.0, 13 / 16.0, 7 / 16.0),
        new Vector4(2 / 16.0, 0 / 16.0, 14 / 16.0, 13 / 16.0)
      ),
      new Quad(
        new Vector3(9 / 16.0, 14 / 16.0, 9 / 16.0),
        new Vector3(7 / 16.0, 14 / 16.0, 9 / 16.0),
        new Vector3(9 / 16.0, 13 / 16.0, 9 / 16.0),
        new Vector4(2 / 16.0, 0 / 16.0, 14 / 16.0, 13 / 16.0)
      )
    },
    Model.rotateY(new Quad[]{
      new Quad(
        new Vector3(7 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector4(0 / 16.0, 2 / 16.0, 16 / 16.0, 16 / 16.0)
      ),
      new Quad(
        new Vector3(7 / 16.0, 14 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 14 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 14 / 16.0, 8 / 16.0),
        new Vector4(0 / 16.0, 2 / 16.0, 16 / 16.0, 16 / 16.0)
      ),
      new Quad(
        new Vector3(7 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 14 / 16.0, 8 / 16.0),
        new Vector4(0 / 16.0, 0 / 16.0, 16 / 16.0, 14 / 16.0)
      ),
      new Quad(
        new Vector3(9 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 14 / 16.0, 8 / 16.0),
        new Vector4(0 / 16.0, 0 / 16.0, 16 / 16.0, 14 / 16.0)
      ),
      new Quad(
        new Vector3(7 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 14 / 16.0, 8 / 16.0),
        new Vector4(2 / 16.0, 0 / 16.0, 16 / 16.0, 14 / 16.0)
      ),
      new Quad(
        new Vector3(9 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 14 / 16.0, 8 / 16.0),
        new Vector4(2 / 16.0, 0 / 16.0, 16 / 16.0, 14 / 16.0)
      )
    }, Math.toRadians(-45), new Vector3(8 / 16., 16 / 16., 8 / 16.)),
    Model.rotateY(new Quad[]{
      new Quad(
        new Vector3(7 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector4(0 / 16.0, 2 / 16.0, 16 / 16.0, 16 / 16.0)
      ),
      new Quad(
        new Vector3(7 / 16.0, 14 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 14 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 14 / 16.0, 8 / 16.0),
        new Vector4(0 / 16.0, 2 / 16.0, 16 / 16.0, 16 / 16.0)
      ),
      new Quad(
        new Vector3(7 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 14 / 16.0, 8 / 16.0),
        new Vector4(0 / 16.0, 0 / 16.0, 16 / 16.0, 14 / 16.0)
      ),
      new Quad(
        new Vector3(9 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 14 / 16.0, 8 / 16.0),
        new Vector4(0 / 16.0, 0 / 16.0, 16 / 16.0, 14 / 16.0)
      ),
      new Quad(
        new Vector3(7 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 14 / 16.0, 8 / 16.0),
        new Vector4(2 / 16.0, 0 / 16.0, 16 / 16.0, 14 / 16.0)
      ),
      new Quad(
        new Vector3(9 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 14 / 16.0, 8 / 16.0),
        new Vector4(2 / 16.0, 0 / 16.0, 16 / 16.0, 14 / 16.0)
      )
    }, Math.toRadians(45), new Vector3(8 / 16., 16 / 16., 8 / 16.))
  );
  //endregion

  //region Hanging propagule model (age=1)
  private static final Quad[] propaguleModelAge1 = new Quad[]{
    new Quad(
      new Vector3(7 / 16.0, 13 / 16.0, 9 / 16.0),
      new Vector3(9 / 16.0, 13 / 16.0, 9 / 16.0),
      new Vector3(7 / 16.0, 13 / 16.0, 7 / 16.0),
      new Vector4(0 / 16.0, 2 / 16.0, 9 / 16.0, 11 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 10 / 16.0, 7 / 16.0),
      new Vector3(9 / 16.0, 10 / 16.0, 7 / 16.0),
      new Vector3(7 / 16.0, 10 / 16.0, 9 / 16.0),
      new Vector4(0 / 16.0, 2 / 16.0, 9 / 16.0, 11 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 13 / 16.0, 9 / 16.0),
      new Vector3(7 / 16.0, 13 / 16.0, 7 / 16.0),
      new Vector3(7 / 16.0, 10 / 16.0, 9 / 16.0),
      new Vector4(2 / 16.0, 0 / 16.0, 9 / 16.0, 6 / 16.0)
    ),
    new Quad(
      new Vector3(9 / 16.0, 13 / 16.0, 7 / 16.0),
      new Vector3(9 / 16.0, 13 / 16.0, 9 / 16.0),
      new Vector3(9 / 16.0, 10 / 16.0, 7 / 16.0),
      new Vector4(2 / 16.0, 0 / 16.0, 9 / 16.0, 6 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 13 / 16.0, 7 / 16.0),
      new Vector3(9 / 16.0, 13 / 16.0, 7 / 16.0),
      new Vector3(7 / 16.0, 10 / 16.0, 7 / 16.0),
      new Vector4(2 / 16.0, 0 / 16.0, 9 / 16.0, 6 / 16.0)
    ),
    new Quad(
      new Vector3(9 / 16.0, 13 / 16.0, 9 / 16.0),
      new Vector3(7 / 16.0, 13 / 16.0, 9 / 16.0),
      new Vector3(9 / 16.0, 10 / 16.0, 9 / 16.0),
      new Vector4(2 / 16.0, 0 / 16.0, 9 / 16.0, 6 / 16.0)
    )
  };
  //endregion

  //region Hanging propagule model (age=2)
  private static final Quad[] propaguleModelAge2 = Model.join(new Quad[]{
      new Quad(
        new Vector3(7 / 16.0, 13 / 16.0, 9 / 16.0),
        new Vector3(9 / 16.0, 13 / 16.0, 9 / 16.0),
        new Vector3(7 / 16.0, 13 / 16.0, 7 / 16.0),
        new Vector4(0 / 16.0, 2 / 16.0, 9 / 16.0, 11 / 16.0)
      ),
      new Quad(
        new Vector3(7 / 16.0, 10 / 16.0, 7 / 16.0),
        new Vector3(9 / 16.0, 10 / 16.0, 7 / 16.0),
        new Vector3(7 / 16.0, 10 / 16.0, 9 / 16.0),
        new Vector4(0 / 16.0, 2 / 16.0, 4 / 16.0, 6 / 16.0)
      ),
      new Quad(
        new Vector3(7 / 16.0, 13 / 16.0, 9 / 16.0),
        new Vector3(7 / 16.0, 13 / 16.0, 7 / 16.0),
        new Vector3(7 / 16.0, 10 / 16.0, 9 / 16.0),
        new Vector4(2 / 16.0, 0 / 16.0, 9 / 16.0, 6 / 16.0)
      ),
      new Quad(
        new Vector3(9 / 16.0, 13 / 16.0, 7 / 16.0),
        new Vector3(9 / 16.0, 13 / 16.0, 9 / 16.0),
        new Vector3(9 / 16.0, 10 / 16.0, 7 / 16.0),
        new Vector4(2 / 16.0, 0 / 16.0, 9 / 16.0, 6 / 16.0)
      ),
      new Quad(
        new Vector3(7 / 16.0, 13 / 16.0, 7 / 16.0),
        new Vector3(9 / 16.0, 13 / 16.0, 7 / 16.0),
        new Vector3(7 / 16.0, 10 / 16.0, 7 / 16.0),
        new Vector4(2 / 16.0, 0 / 16.0, 9 / 16.0, 6 / 16.0)
      ),
      new Quad(
        new Vector3(9 / 16.0, 13 / 16.0, 9 / 16.0),
        new Vector3(7 / 16.0, 13 / 16.0, 9 / 16.0),
        new Vector3(9 / 16.0, 10 / 16.0, 9 / 16.0),
        new Vector4(2 / 16.0, 0 / 16.0, 9 / 16.0, 6 / 16.0)
      )
    },
    Model.rotateY(new Quad[]{
      new Quad(
        new Vector3(7 / 16.0, 10 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 10 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 7 / 16.0, 8 / 16.0),
        new Vector4(5 / 16.0, 3 / 16.0, 9 / 16.0, 6 / 16.0)
      ),
      new Quad(
        new Vector3(9 / 16.0, 10 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 10 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 7 / 16.0, 8 / 16.0),
        new Vector4(5 / 16.0, 3 / 16.0, 9 / 16.0, 6 / 16.0)
      )
    }, Math.toRadians(45)),
    Model.rotateY(new Quad[]{
      new Quad(
        new Vector3(7 / 16.0, 10 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 10 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 7 / 16.0, 8 / 16.0),
        new Vector4(5 / 16.0, 3 / 16.0, 9 / 16.0, 6 / 16.0)
      ),
      new Quad(
        new Vector3(9 / 16.0, 10 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 10 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 7 / 16.0, 8 / 16.0),
        new Vector4(5 / 16.0, 3 / 16.0, 9 / 16.0, 6 / 16.0)
      )
    }, Math.toRadians(45))
  );
  //endregion

  //region Hanging propagule model (age=3)
  private static final Quad[] propaguleModelAge3 = Model.join(new Quad[]{
      new Quad(
        new Vector3(7 / 16.0, 13 / 16.0, 9 / 16.0),
        new Vector3(9 / 16.0, 13 / 16.0, 9 / 16.0),
        new Vector3(7 / 16.0, 13 / 16.0, 7 / 16.0),
        new Vector4(0 / 16.0, 2 / 16.0, 9 / 16.0, 11 / 16.0)
      ),
      new Quad(
        new Vector3(7 / 16.0, 10 / 16.0, 7 / 16.0),
        new Vector3(9 / 16.0, 10 / 16.0, 7 / 16.0),
        new Vector3(7 / 16.0, 10 / 16.0, 9 / 16.0),
        new Vector4(0 / 16.0, 2 / 16.0, 4 / 16.0, 6 / 16.0)
      ),
      new Quad(
        new Vector3(7 / 16.0, 13 / 16.0, 9 / 16.0),
        new Vector3(7 / 16.0, 13 / 16.0, 7 / 16.0),
        new Vector3(7 / 16.0, 10 / 16.0, 9 / 16.0),
        new Vector4(2 / 16.0, 0 / 16.0, 9 / 16.0, 6 / 16.0)
      ),
      new Quad(
        new Vector3(9 / 16.0, 13 / 16.0, 7 / 16.0),
        new Vector3(9 / 16.0, 13 / 16.0, 9 / 16.0),
        new Vector3(9 / 16.0, 10 / 16.0, 7 / 16.0),
        new Vector4(2 / 16.0, 0 / 16.0, 9 / 16.0, 6 / 16.0)
      ),
      new Quad(
        new Vector3(7 / 16.0, 13 / 16.0, 7 / 16.0),
        new Vector3(9 / 16.0, 13 / 16.0, 7 / 16.0),
        new Vector3(7 / 16.0, 10 / 16.0, 7 / 16.0),
        new Vector4(2 / 16.0, 0 / 16.0, 9 / 16.0, 6 / 16.0)
      ),
      new Quad(
        new Vector3(9 / 16.0, 13 / 16.0, 9 / 16.0),
        new Vector3(7 / 16.0, 13 / 16.0, 9 / 16.0),
        new Vector3(9 / 16.0, 10 / 16.0, 9 / 16.0),
        new Vector4(2 / 16.0, 0 / 16.0, 9 / 16.0, 6 / 16.0)
      )
    },
    Model.rotateY(new Quad[]{
      new Quad(
        new Vector3(7 / 16.0, 10 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 10 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 3 / 16.0, 8 / 16.0),
        new Vector4(5 / 16.0, 3 / 16.0, 13 / 16.0, 6 / 16.0)
      ),
      new Quad(
        new Vector3(9 / 16.0, 10 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 10 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 3 / 16.0, 8 / 16.0),
        new Vector4(5 / 16.0, 3 / 16.0, 13 / 16.0, 6 / 16.0)
      )}, Math.toRadians(45)),
    Model.rotateY(new Quad[]{
      new Quad(
        new Vector3(7 / 16.0, 10 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 10 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 3 / 16.0, 8 / 16.0),
        new Vector4(5 / 16.0, 3 / 16.0, 13 / 16.0, 6 / 16.0)
      ),
      new Quad(
        new Vector3(9 / 16.0, 10 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 10 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 3 / 16.0, 8 / 16.0),
        new Vector4(5 / 16.0, 3 / 16.0, 13 / 16.0, 6 / 16.0)
      )}, Math.toRadians(-45))
  );
  //endregion

  //region Hanging propagule model (age=4)
  private static final Quad[] propaguleModelAge4 = Model.join(new Quad[]{
      new Quad(
        new Vector3(7 / 16.0, 13 / 16.0, 9 / 16.0),
        new Vector3(9 / 16.0, 13 / 16.0, 9 / 16.0),
        new Vector3(7 / 16.0, 13 / 16.0, 7 / 16.0),
        new Vector4(0 / 16.0, 2 / 16.0, 9 / 16.0, 11 / 16.0)
      ),
      new Quad(
        new Vector3(7 / 16.0, 10 / 16.0, 7 / 16.0),
        new Vector3(9 / 16.0, 10 / 16.0, 7 / 16.0),
        new Vector3(7 / 16.0, 10 / 16.0, 9 / 16.0),
        new Vector4(0 / 16.0, 2 / 16.0, 4 / 16.0, 6 / 16.0)
      ),
      new Quad(
        new Vector3(7 / 16.0, 13 / 16.0, 9 / 16.0),
        new Vector3(7 / 16.0, 13 / 16.0, 7 / 16.0),
        new Vector3(7 / 16.0, 10 / 16.0, 9 / 16.0),
        new Vector4(2 / 16.0, 0 / 16.0, 9 / 16.0, 6 / 16.0)
      ),
      new Quad(
        new Vector3(9 / 16.0, 13 / 16.0, 7 / 16.0),
        new Vector3(9 / 16.0, 13 / 16.0, 9 / 16.0),
        new Vector3(9 / 16.0, 10 / 16.0, 7 / 16.0),
        new Vector4(2 / 16.0, 0 / 16.0, 9 / 16.0, 6 / 16.0)
      ),
      new Quad(
        new Vector3(7 / 16.0, 13 / 16.0, 7 / 16.0),
        new Vector3(9 / 16.0, 13 / 16.0, 7 / 16.0),
        new Vector3(7 / 16.0, 10 / 16.0, 7 / 16.0),
        new Vector4(2 / 16.0, 0 / 16.0, 9 / 16.0, 6 / 16.0)
      ),
      new Quad(
        new Vector3(9 / 16.0, 13 / 16.0, 9 / 16.0),
        new Vector3(7 / 16.0, 13 / 16.0, 9 / 16.0),
        new Vector3(9 / 16.0, 10 / 16.0, 9 / 16.0),
        new Vector4(2 / 16.0, 0 / 16.0, 9 / 16.0, 6 / 16.0)
      )
    },
    Model.rotateY(new Quad[]{
      new Quad(
        new Vector3(7 / 16.0, 10 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 10 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 0 / 16.0, 8 / 16.0),
        new Vector4(5 / 16.0, 3 / 16.0, 16 / 16.0, 6 / 16.0)
      ),
      new Quad(
        new Vector3(9 / 16.0, 10 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 10 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 0 / 16.0, 8 / 16.0),
        new Vector4(5 / 16.0, 3 / 16.0, 16 / 16.0, 6 / 16.0)
      )
    }, Math.toRadians(45)),
    Model.rotateY(new Quad[]{
      new Quad(
        new Vector3(7 / 16.0, 10 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 10 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 0 / 16.0, 8 / 16.0),
        new Vector4(5 / 16.0, 3 / 16.0, 16 / 16.0, 6 / 16.0)
      ),
      new Quad(
        new Vector3(9 / 16.0, 10 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 10 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 0 / 16.0, 8 / 16.0),
        new Vector4(5 / 16.0, 3 / 16.0, 16 / 16.0, 6 / 16.0)
      )
    }, Math.toRadians(-45))
  );
  //endregion

  private final Quad[] quads;

  private final Texture[] textures;

  public MangrovePropaguleModel(int age, boolean hanging) {
    if (hanging) {
      ArrayList<Quad> finalQuads = new ArrayList<>();
      Collections.addAll(finalQuads, propaguleModelAge0);
      if (age == 1) {
        Collections.addAll(finalQuads, propaguleModelAge1);
      } else if (age == 2) {
        Collections.addAll(finalQuads, propaguleModelAge2);
      } else if (age == 3) {
        Collections.addAll(finalQuads, propaguleModelAge3);
      } else if (age == 4) {
        Collections.addAll(finalQuads, propaguleModelAge4);
      }
      quads = finalQuads.toArray(new Quad[0]);
      textures = new Texture[quads.length];
      Arrays.fill(textures, Texture.mangrovePropaguleHanging);
    } else {
      quads = propaguleModel;
      textures = new Texture[quads.length];
      Arrays.fill(textures, Texture.mangrovePropagule);
    }
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
