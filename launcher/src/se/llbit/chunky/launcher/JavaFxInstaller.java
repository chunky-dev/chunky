/* Copyright (c) 2022 Chunky contributors
 *
 * This file is part of Chunky.
 *
 * Chunky is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chunky is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Chunky.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.llbit.chunky.launcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JavaFxInstaller {

  private static final String HELP_LINK = "https://chunky.lemaik.de/java11";
  private static final String JAVAFX_LINK = "https://gluonhq.com/products/javafx/";
  private static final String JAVAFX_JSON = "javafx.json";

  private final JavaFxDownloads.Os[] downloads;
  private final Path target;
  private String updateSite;

  private JFrame window = null;
  private boolean complete = false;
  private boolean exiting = false;

  private static class InstallationException extends Exception {
    public InstallationException(String message) {
      super(message);
    }
  }

  private JavaFxInstaller(LauncherSettings settings) throws InstallationException {
    // Get installation target directory
    Path chunkyDir = Paths.get(System.getProperty("user.home"));
    chunkyDir = chunkyDir.resolve(".chunky");
    target = chunkyDir.resolve("javafx");
    if (target.toFile().exists()) {
      throw new InstallationException("Installation already exists.");
    }

    // Get update site
    String site;
    if (settings == null) {
      site = LauncherSettings.DEFAULT_UPDATE_SITE;
    } else {
      site = settings.updateSite;
    }
    if (site.endsWith("/")) {
      updateSite = site + JAVAFX_JSON;
    } else {
      updateSite = site + "/" + JAVAFX_JSON;
    }

    // Fetch javafx downloads
    try {
      downloads = JavaFxDownloads.fetch(new URL(updateSite));
    } catch (JavaFxDownloads.SyntaxException | IOException e) {
      throw new InstallationException("Failed to fetch download links: " + e);
    }

    // Do all the swing stuff
    showInstallDialog();
  }

  /**
   * Helper method to launch the installer.
   * @param settings  Launcher settings. May be null.
   * @param args      Launcher arguments.
   */
  public static void launch(LauncherSettings settings, String[] args) {
    try {
      JavaFxInstaller instance = new JavaFxInstaller(settings);

      // Wait until we are finished
      try {
        synchronized (instance) {
          while (!instance.isFinished()) {
            // Have a timeout in case the window closes but we are not notified
            // for some reason
            instance.wait(10000);
          }
        }
      } catch (InterruptedException ignored) {}

      // Window was closed
      if (instance.exiting) {
        return;
      }

      // Success?
      instance.close();
      JavaFxLocator.retryWithJavafx(args);
    } catch (InstallationException e) {
      showJavafxError(e);
    }
  }

  private void downloadAndInstall(URL download) {
    // Zip extraction code from
    // https://mkyong.com/java/how-to-decompress-files-from-a-zip-file/
    try (ZipInputStream zis = new ZipInputStream(download.openStream())) {
      ZipEntry entry = zis.getNextEntry();
      while (entry != null) {
        boolean isDirectory = entry.getName().endsWith("/") || entry.getName().endsWith("\\");

        // Protect against zip slip
        Path newPath = target.resolve(entry.getName());
        Path normalizedPath = newPath.normalize();
        if (!normalizedPath.startsWith(target)) {
          cleanupTarget();
          throw new InstallationException("Bad zip entry: " + entry.getName());
        }

        if (isDirectory) {
          Files.createDirectories(newPath);
        } else {
          if (newPath.getParent() != null) {
            Files.createDirectories(newPath.getParent());
          }
          Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
        }

        entry = zis.getNextEntry();

        if (exiting) {
          cleanupTarget();
          return;
        }
      }
      zis.closeEntry();
    } catch (IOException | InstallationException ex) {
      try {
        cleanupTarget();
      } catch (IOException ignored) {
        System.err.println("Could not clean up target directory.");
      }
      showJavafxError(new InstallationException(ex.getMessage()));
    }

    // Wake up listeners
    synchronized (this) {
      this.complete = true;
      this.notifyAll();
    }
  }

  /**
   * Helper method to clean up the javafx instllation directory
   * if installation failed or was canceled.
   */
  private void cleanupTarget() throws IOException {
    try (Stream<Path> walk = Files.walk(target)) {
      walk.sorted(Comparator.reverseOrder())
        .map(Path::toFile)
        .forEach(f -> {
          boolean r = f.delete();
          assert r;
        });
    }
    assert !target.toFile().exists();
  }

  private void showInstallDialog() {
    Image chunkyImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource(
      "/se/llbit/chunky/launcher/ui/chunky-cfg.png"));

    JFrame window = new JFrame("Install JavaFX");
    window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    window.setIconImage(chunkyImage);

    window.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        exiting = true;
      }
    });

    JPanel textPanel = new JPanel();
    textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

    // Title
    JLabel installLabel = new JLabel("Install JavaFX");
    installLabel.setFont(new Font(installLabel.getFont().getFontName(), Font.BOLD, 36));
    textPanel.add(installLabel);

    // Description
    JLabel[] description = new JLabel[3];
    description[0] = new JLabel("Chunky needs JavaFX to function. If you are using");
    description[1] = new JLabel("a JVM for Java 11 or later, JavaFX is no longer");
    description[2] = new JLabel("shipped alongside and must be installed separately.");
    for (JLabel label : description) {
      label.setFont(new Font(label.getFont().getFontName(), Font.PLAIN, 12));
      textPanel.add(label);
    }

    // Spacer
    textPanel.add(new JLabel(" "));

    // Computer configuration
    textPanel.add(new JLabel("Computer configuration:"));

    JPanel compPanel = new JPanel();
    compPanel.setLayout(new GridLayout(2, 3));
    textPanel.add(compPanel);

    compPanel.add(new JLabel("    "));
    compPanel.add(new JLabel("OS:"));

    JComboBox<String> osCombo = new JComboBox<>();
    compPanel.add(osCombo);

    // Populate os's and select the best match
    Arrays.stream(downloads).forEach(os -> osCombo.addItem(os.name));
    osCombo.setSelectedIndex(0);
    for (JavaFxDownloads.Os os : downloads) {
      if (os.doesMatch(System.getProperty("os.name"))) {
        osCombo.setSelectedItem(os.name);
        break;
      }
    }

    compPanel.add(new JLabel("    "));
    compPanel.add(new JLabel("Arch:"));

    JComboBox<String> archCombo = new JComboBox<>();
    compPanel.add(archCombo);

    // Automatically populate arch's and select the best match when an os is selected
    osCombo.addActionListener(e -> {
      JavaFxDownloads.Os selected = downloads[osCombo.getSelectedIndex()];

      archCombo.removeAllItems();
      Arrays.stream(selected.archs).forEach(arch -> archCombo.addItem(arch.name));

      archCombo.setSelectedIndex(0);
      for (JavaFxDownloads.Arch arch : selected.archs) {
        if (arch.doesMatch(System.getProperty("os.arch"))) {
          archCombo.setSelectedItem(arch);
          break;
        }
      }
    });
    // Force the listener to be called
    osCombo.setSelectedIndex(osCombo.getSelectedIndex());

    // Spacer
    textPanel.add(new JLabel(" "));

    // JavaFX Licensing
    JLabel licenseText = new JLabel("JavaFX is licensed under GPLv2+CE.");
    licenseText.setFont(new Font(licenseText.getFont().getFontName(), Font.PLAIN, 12));
    textPanel.add(getLinkLabel("JavaFX is licensed under GPLv2+CE.", "JavaFX is licensed under GPLv2+CE", JAVAFX_LINK));

    // Spacer
    textPanel.add(new JLabel(" "));

    textPanel.add(getLinkLabel("Click here for more information.", "For more information see", HELP_LINK));

    // Spacer
    textPanel.add(new JLabel(" "));

    // Download and Install button
    JButton downloadButton = new JButton("Download and Install");
    textPanel.add(downloadButton);
    downloadButton.addActionListener(e -> {
      JavaFxDownloads.Os os = downloads[osCombo.getSelectedIndex()];
      JavaFxDownloads.Arch arch = os.archs[archCombo.getSelectedIndex()];
      new Thread(() -> this.downloadAndInstall(arch.url)).start();

      osCombo.setEnabled(false);
      archCombo.setEnabled(false);
      downloadButton.setEnabled(false);
      downloadButton.setText("Downloading...");
    });

    // Spacer
    textPanel.add(new JLabel(" "));

    // Add icon and make top layout
    JPanel topPanel = new JPanel();
    topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
    topPanel.add(new JLabel(new ImageIcon(chunkyImage)));
    topPanel.add(textPanel);
    topPanel.add(new JLabel("  "));

    window.add(topPanel);
    window.pack();
    window.setVisible(true);
    this.window = window;
  }

  private boolean isFinished() {
    if (this.window == null) {
      return false;
    }
    if (this.complete) {
      return true;
    }
    if (this.exiting) {
      return true;
    }
    return !this.window.isVisible();
  }

  private void close() {
    window.setVisible(false);
    window.dispose();
    synchronized (this) {
      this.complete = true;
      this.notifyAll();
    }
  }

  private static JLabel getLinkLabel(String linkText, String altText, String url) {
    JLabel faqLabel;
    if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
      faqLabel = new JLabel(linkText);
      Font font = faqLabel.getFont();
      Map attributes = font.getAttributes();
      attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
      faqLabel.setFont(font.deriveFont(attributes));
      faqLabel.setForeground(Color.BLUE.darker());
      faqLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      faqLabel.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          try {
            Desktop.getDesktop().browse(new URI(url));
          } catch(IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
          }
        }
      });
    } else {
      faqLabel = new JLabel(String.format("%s: %s", altText, url));
      faqLabel.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    }
    return faqLabel;
  }

  private static void showJavafxError(InstallationException e) {
    if(!GraphicsEnvironment.isHeadless()) {
      JTextField error = new JTextField("Error installing JavaFX: " + e.getMessage());
      error.setEditable(false);
      error.setBackground(null);
      error.setBorder(null);
      error.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

      JTextField setup = new JTextField(String.format(
        "Detected computer configuration: %s %s",
        System.getProperty("os.name"),
        System.getProperty("os.arch")
      ));
      setup.setEditable(false);
      setup.setBackground(null);
      setup.setBorder(null);
      setup.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

      JLabel help = getLinkLabel("Click here for more information", "For more information, see", HELP_LINK);

      JOptionPane.showMessageDialog(null, new Object[] {
        error, setup, help
      }, "Cannot find JavaFX", JOptionPane.ERROR_MESSAGE);
    }
    System.err.println(e.getMessage());
    System.err.println("For more information see: " + HELP_LINK);
  }
}
