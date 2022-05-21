package se.llbit.chunky.ui;

import javafx.scene.control.MenuItem;

public class HyperlinkMenuItem extends MenuItem {

  private String link;

  public HyperlinkMenuItem() {
    setOnAction(e -> {
      ChunkyFx.openUrl(link);
    });
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }
}
