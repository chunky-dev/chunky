package se.llbit.chunky.resources;

public interface Image {
  int getWidth();
  int getHeight();
  int getPixel(int x, int y);
  int getPixel(int index);
  BitmapImage asBitmap();
}
