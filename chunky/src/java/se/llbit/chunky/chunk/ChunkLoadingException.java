package se.llbit.chunky.chunk;

public class ChunkLoadingException extends Exception {
  public ChunkLoadingException(String message) {
    super(message);
  }
  public ChunkLoadingException(String message, Throwable cause) {
    super(message, cause);
  }
}