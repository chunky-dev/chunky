package se.llbit.chunky.world.bedrock;

import io.github.notstirred.leveldb_ffi.LevelDBLib;
import se.llbit.chunky.world.Dimension;
import se.llbit.chunky.world.EmptyWorld;
import se.llbit.chunky.world.World;
import se.llbit.log.Log;
import se.llbit.math.Vector3i;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class BedrockWorld extends World {
  public static final boolean IS_BEDROCK_SUPPORTED = LevelDBLib.init();
  private static boolean warnedUserIfNotSupported;

  public BedrockWorld(Info info) {
    super(info);

    if (!IS_BEDROCK_SUPPORTED && !warnedUserIfNotSupported) {
      Log.warn("A bedrock world was loaded but bedrock is not supported on this OS/ARCH.\n" +
        "If you believe your platform should be supported or this is an error please report a bug on the chunky bug tracker.");
      warnedUserIfNotSupported = true;
    }
  }

  @Override
  public Set<Dimension.Identifier> getAvailableDimensions() {
    return Set.of(Dimension.Identifier.OVERWORLD);
  }

  @Override
  public Optional<Dimension.Identifier> getDefaultDimension() {
    return Optional.of(Dimension.Identifier.OVERWORLD);
  }

  @Override
  public Dimension loadDimension(Dimension.Identifier dimensionId) {
    try {
      if (this.currentDimension != EmptyWorld.INSTANCE.currentDimension()) {
        ((BedrockDimension) this.currentDimension).close(); // close early to avoid cleaner
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    BedrockDimension dimension = new BedrockDimension(this, dimensionId, this.getInfo().path(), Collections.emptySet(), new Vector3i(0, 0, 0));

    this.currentDimension = dimension;

    return dimension;
  }
}
