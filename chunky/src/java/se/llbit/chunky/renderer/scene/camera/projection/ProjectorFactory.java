package se.llbit.chunky.renderer.scene.camera.projection;

import se.llbit.chunky.renderer.scene.camera.ApertureShape;
import se.llbit.chunky.renderer.scene.camera.Camera;
import se.llbit.log.Log;

public class ProjectorFactory {
  private ProjectorFactory() {
  }

  /**
   * Creates projector based on the current camera settings.
   */
  public static Projector create(Camera camera) {
    switch (camera.getProjectionMode()) {
      default:
        Log.errorf("Unknown projection mode: %s, using standard mode", camera.getProjectionMode());
      case PINHOLE:
        return applyShift(
          camera,
          applyDoF(
            camera,
            new PinholeProjector(
              camera.getFov()
            ),
            camera.getSubjectDistance()
          )
        );
      case PARALLEL:
        return applyShift(
          camera,
          applyDoF(
            camera,
            new ForwardDisplacementProjector(
              new ParallelProjector(
                camera.getWorldDiagonalSize(),
                camera.getFov()
              ),
              -camera.getWorldDiagonalSize()
            ),
            camera.getSubjectDistance() + camera.getWorldDiagonalSize()
          )
        );
      case FISHEYE:
        return applySphericalDoF(
          camera,
          new FisheyeProjector(
            camera.getFov()
          )
        );
      case PANORAMIC_SLOT:
        return applySphericalDoF(
          camera,
          new PanoramicSlotProjector(
            camera.getFov()
          )
        );
      case PANORAMIC:
        return applySphericalDoF(
          camera,
          new PanoramicProjector(
            camera.getFov()
          )
        );
      case STEREOGRAPHIC:
        return new StereographicProjector(
          camera.getFov()
        );
      case ODS_LEFT:
        return new OmniDirectionalStereoProjector(
          OmniDirectionalStereoProjector.Eye.LEFT
        );
      case ODS_RIGHT:
        return new OmniDirectionalStereoProjector(
          OmniDirectionalStereoProjector.Eye.RIGHT
        );
    }
  }

  private static Projector applyDoF(
    Camera camera,
    Projector projector,
    double subjectDistance
  ) {
    if (camera.isInfiniteDoF()) {
      return projector;
    }

    if (camera.getApertureShape() == ApertureShape.CUSTOM) {
      return new ApertureProjector(
        projector,
        subjectDistance / camera.getDof(),
        subjectDistance,
        camera.getApertureMaskFilename()
      );
    } else if (camera.getApertureShape() == ApertureShape.CIRCLE) {
      return new ApertureProjector(
        projector,
        subjectDistance / camera.getDof(),
        subjectDistance
      );
    } else
      return new ApertureProjector(
        projector,
        subjectDistance / camera.getDof(),
        subjectDistance,
        camera.getApertureShape()
      );
  }

  private static Projector applySphericalDoF(
    Camera camera,
    Projector projector
  ) {
    return camera.isInfiniteDoF() ?
      projector :
      new SphericalApertureProjector(
        projector,
        camera.getSubjectDistance() / camera.getDof(),
        camera.getSubjectDistance()
      );
  }

  private static Projector applyShift(
    Camera camera,
    Projector projector
  ) {
    if (Math.abs(camera.getShiftX()) > 0 || Math.abs(camera.getShiftY()) > 0) {
      return new ShiftProjector(
        projector,
        camera.getShiftX(),
        camera.getShiftY()
      );
    }
    return projector;
  }
}
