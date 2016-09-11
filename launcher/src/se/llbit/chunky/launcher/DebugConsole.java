/* Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * Window that displays debug messages.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class DebugConsole extends JDialog implements Logger {

  static final SimpleAttributeSet stdoutAttributes = new SimpleAttributeSet();
  static final SimpleAttributeSet stderrAttributes = new SimpleAttributeSet();

  static {
    stderrAttributes.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.TRUE);
    stderrAttributes.addAttribute(StyleConstants.CharacterConstants.Foreground, Color.RED);
  }

  private final JTextPane statusText;
  private final AbstractDocument statusTextDoc;
  private final JButton closeBtn;
  private final boolean closeConsoleOnExit;

  /**
   * Create a new status window.
   */
  public DebugConsole(JFrame parent, boolean closeConsoleOnExit) {
    super(parent, "Debug Console");

    this.closeConsoleOnExit = closeConsoleOnExit;

    setModalityType(ModalityType.MODELESS);
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    statusText = new JTextPane();
    statusText.setCaretPosition(0);
    statusText.setMargin(new Insets(0, 0, 0, 0));
    statusTextDoc = (AbstractDocument) statusText.getStyledDocument();

    closeBtn = new JButton("Close");
    closeBtn.setToolTipText("Close the debug console");
    closeBtn.addActionListener(new ActionListener() {
      @Override public void actionPerformed(ActionEvent e) {
        setVisible(false);
        dispose();
      }
    });

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add(new JScrollPane(statusText), BorderLayout.CENTER);
    panel.add(closeBtn, BorderLayout.SOUTH);

    URL url = getClass().getResource("/chunky-cfg.png");
    if (url != null) {
      setIconImage(Toolkit.getDefaultToolkit().getImage(url));
    }

    setContentPane(panel);

    setPreferredSize(new Dimension(550, 300));
    pack();

    setLocationByPlatform(true);
  }

  /**
   * Clear the status text area.
   */
  public void clearStatusText() {
    statusText.setText(""); //$NON-NLS-1$
  }

  /**
   * Append text to the status text area.
   */
  public void appendStatusText(final String text, final SimpleAttributeSet attrs) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override public void run() {
        try {
          statusTextDoc.insertString(statusTextDoc.getLength(), text, attrs);
        } catch (BadLocationException e) {
          System.err.println("Error updating debug console: " + e.getMessage());
        }
      }
    });
  }

  @Override public void processExited(int exitValue) {
    if (exitValue == 0) {
      appendLine("Chunky exited normally");
      if (closeConsoleOnExit) {
        setVisible(false);
        dispose();
      }
    } else {
      appendLine("Chunky exited abnormally with exit value " + exitValue);
    }
  }

  @Override public void appendStdout(byte[] buffer, int size) {
    appendStatusText(new String(buffer, 0, size), stdoutAttributes);
  }

  @Override public void appendStderr(byte[] buffer, int size) {
    appendStatusText(new String(buffer, 0, size), stderrAttributes);
  }

  @Override public void appendLine(String line) {
    appendStatusText(line + "\n", stdoutAttributes);
  }

  @Override public void appendErrorLine(String line) {
    appendStatusText(line + "\n", stderrAttributes);
  }
}
