package se.llbit.chunky.launcher;

public interface UpdateListener {

  void updateError(String message);

  void updateAvailable(VersionInfo latest);

  void noUpdateAvailable();

}
