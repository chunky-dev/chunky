package se.llbit.chunky.world.region;

import se.llbit.chunky.world.ChunkPosition;

import java.io.IOException;

public class ChunkReadException extends IOException {
  private final ChunkPosition chunkPosition;

  public ChunkReadException(
    ChunkPosition chunkPosition,
    String message
  ) {
    this(chunkPosition, message, null);
  }

  public ChunkReadException(
    ChunkPosition chunkPosition,
    Throwable cause
  ) {
    this(chunkPosition, null, cause);
  }

  public ChunkReadException(
    ChunkPosition chunkPosition,
    String message,
    Throwable cause
  ) {
    super(message, cause);
    this.chunkPosition = chunkPosition;
  }

  public ChunkPosition getChunkPosition() {
    return chunkPosition;
  }

  @Override
  public String toString() {
    return (getMessage() == null)
      ? String.format(
        "Failed to read chunk %s",
        chunkPosition
      )
      : String.format(
        "Failed to read chunk %s - %s",
        chunkPosition,
        getMessage()
      );
  }
}
