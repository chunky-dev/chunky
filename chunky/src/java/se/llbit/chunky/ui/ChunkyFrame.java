/* Copyright (c) 2012 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.ui;

import java.awt.AWTEvent;
import java.awt.Event;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;

import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.main.Messages;
import se.llbit.chunky.world.World;
import se.llbit.log.ConsoleReceiver;
import se.llbit.log.Level;
import se.llbit.log.Log;

/**
 * Main window of the Chunky application.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("serial")
public class ChunkyFrame extends JFrame {

	private ChunkMap map;
	private Minimap minimap;
	private Controls controls;
	private final Chunky chunky;

	/**
	 * Create a new instance of the application GUI.
	 * @param chunky
	 */
	public ChunkyFrame(Chunky chunky) {
		super(Chunky.getAppName());
		this.chunky = chunky;
	}

	/**
	 * Initialize the UI components.
	 */
	public void initComponents() {
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK), "Exit");
		getRootPane().getActionMap().put("Exit", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Dispatch window closing event.
				JFrame frame = ChunkyFrame.this;
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			}
		});

		minimap = new Minimap(chunky);
		map = new ChunkMap(chunky);
		controls = new Controls(chunky, minimap);
		chunky.getChunkSelection().addRegionUpdateListener(map);
		chunky.getChunkSelection().addRegionUpdateListener(minimap);

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
				.addComponent(map, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(controls, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(controls, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(map, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);

		pack();

		final UILogReceiver logReceiver = new UILogReceiver();
		Log.setReceiver(logReceiver, Level.WARNING, Level.ERROR);

		addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
			}
			@Override
			public void windowIconified(WindowEvent e) {
			}
			@Override
			public void windowDeiconified(WindowEvent e) {
			}
			@Override
			public void windowDeactivated(WindowEvent e) {
			}
			@Override
			public void windowClosing(WindowEvent e) {
				Log.setReceiver(ConsoleReceiver.INSTANCE, Level.WARNING, Level.ERROR);
				chunky.onExit();
			}
			@Override
			public void windowClosed(WindowEvent e) {
			}
			@Override
			public void windowActivated(WindowEvent e) {
			}
		});

		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationByPlatform(true);

		URL url = getClass().getResource(Messages.getString("Chunky.icon")); //$NON-NLS-1$
		if (url != null) {
			setIconImage(Toolkit.getDefaultToolkit().getImage(url));
		}

		InputListener listener = new InputListener();
		Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.KEY_EVENT_MASK);

		map.repaint();
		minimap.repaint();

		requestFocus();

		worldLoaded(chunky.getWorld());

		chunky.viewUpdated();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				minimap.redraw();
				map.redraw();
			}
		});
	}

	/**
	 * Chunky input listener.
	 */
	public class InputListener implements AWTEventListener {

		private void keyPressed(KeyEvent e) {
			if (ChunkyFrame.this.getFocusOwner() != null) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_CONTROL: {
					chunky.setCtrlModifier(true);
					break;
				}
				case KeyEvent.VK_SHIFT: {
					chunky.setShiftModifier(true);
					break;
				}
				case KeyEvent.VK_C: {
					chunky.getMap().showContextMenu();
					break;
				}
				}
			}
		}

		private void keyReleased(KeyEvent e) {
			if (ChunkyFrame.this.getFocusOwner() != null) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_CONTROL: {
					chunky.setCtrlModifier(false);
					break;
				}
				case KeyEvent.VK_SHIFT: {
					chunky.setShiftModifier(false);
					break;
				}
				}
			}
		}

		@Override
		public void eventDispatched(AWTEvent event) {
			if (event instanceof KeyEvent) {
				KeyEvent e = (KeyEvent) event;
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					keyPressed(e);
				} else if (e.getID() == KeyEvent.KEY_RELEASED) {
					keyReleased(e);
				}
			}
		}
	}

	/**
	 * @return The Controls UI element
	 */
	public Controls getControls() {
		return controls;
	}

	/**
	 * Called when a new world has been loaded
	 * @param world
	 */
	public void worldLoaded(final World world) {
		world.addChunkUpdateListener(minimap);
		world.addChunkUpdateListener(map);

		controls.setPlayerY(world.playerLocY());
		controls.enableDimension(0, world.haveDimension(0));
		controls.enableDimension(-1, world.haveDimension(-1));
		controls.enableDimension(1, world.haveDimension(1));
	}

	/**
	 * @return The main map UI element
	 */
	public ChunkMap getMap() {
		return map;
	}

	/**
	 * @return The minimap UI element
	 */
	public Minimap getMinimap() {
		return minimap;
	}
}
