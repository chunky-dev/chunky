package se.llbit.chunky.world.bedrock;

import io.github.notstirred.leveldb_ffi.LevelDBLib;
import se.llbit.chunky.Plugin;
import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.world.worldformat.WorldFormats;
import se.llbit.log.Log;

public class BedrockPlugin implements Plugin {
  public static final boolean IS_BEDROCK_SUPPORTED = LevelDBLib.init();
  private static boolean warnedUserIfNotSupported;

  @Override
  public void attach(Chunky chunky) {
    if (!IS_BEDROCK_SUPPORTED && !warnedUserIfNotSupported) {
      Log.warn("A bedrock world was loaded but bedrock is not supported on this OS/ARCH.\n" +
        "If you believe your platform should be supported or this is an error please report a bug on the chunky bug tracker.");
      warnedUserIfNotSupported = true;
    }
    if (IS_BEDROCK_SUPPORTED) {
      WorldFormats.addWorldFormat(new BedrockWorldFormat());
    }
  }

  @Override
  public void shutdown(Chunky chunky) {
    if (IS_BEDROCK_SUPPORTED) {
      BedrockDB.closeAllDBs();
    }
  }
}
