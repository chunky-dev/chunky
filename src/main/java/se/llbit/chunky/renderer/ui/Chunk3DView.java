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
package se.llbit.chunky.renderer.ui;

import java.awt.Dimension;
import java.awt.Event;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import se.llbit.chunky.renderer.Renderer;

/**
 * The 3D view window. It tracks mouse movement and passes them
 * to the render controls.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("serial")
public class Chunk3DView extends JDialog {
	
    private RenderCanvas canvas;
    
    private int x0, y0;

    /**
     * Create the 3D view window
     * @param listener
     * @param parentFrame 
     */
    public Chunk3DView(final ViewListener listener, JFrame parentFrame) {
    	
    	super(parentFrame, "Render Preview");
        
        canvas = new RenderCanvas();
        
        setContentPane(canvas);
        
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setModalityType(ModalityType.MODELESS);
        
        pack();
        
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_W, Event.CTRL_MASK), "Close Dialog");
		getRootPane().getActionMap().put("Close Dialog", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// close the view
            	setVisible(false);
			}
		});
		
        addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
			}
			@Override
			public void componentResized(ComponentEvent e) {
				canvas.repaint();
			}
			@Override
			public void componentMoved(ComponentEvent e) {
			}
			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});
        
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                onKeyDown(e);
            }
            @Override
            public void keyReleased(KeyEvent e) {
            }
            @Override
            public void keyPressed(KeyEvent e) {
                onKeyDown(e);
            }
            private void onKeyDown(KeyEvent e) {
                switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                case KeyEvent.VK_LEFT:
                	listener.onStrafeLeft();
                    break;
                case KeyEvent.VK_D:
                case KeyEvent.VK_RIGHT:
                	listener.onStrafeRight();
                    break;
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                	listener.onMoveForward();
                    break;
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                	listener.onMoveBackward();
                    break;
                case KeyEvent.VK_K:
                	listener.onMoveForwardFar();
                    break;
                case KeyEvent.VK_J:
                	listener.onMoveBackwardFar();
                    break;
                case KeyEvent.VK_R:
                	listener.onMoveUp();
                    break;
                case KeyEvent.VK_F:
                	listener.onMoveDown();
                    break;
                }
            }
        });
        
        canvas.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseMoved(MouseEvent e) {
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - x0;
                int dy = e.getY() - y0;
                listener.onMouseDragged(dx, dy);
                x0 = e.getX();
                y0 = e.getY();
            }
        });
        
        canvas.addMouseListener(new MouseListener() {
            @Override
            public void mouseReleased(MouseEvent e) {
            }
            @Override
            public void mousePressed(MouseEvent e) {
                x0 = e.getX();
                y0 = e.getY();
            }
            @Override
            public void mouseExited(MouseEvent e) {
            }
            @Override
            public void mouseEntered(MouseEvent e) {
            }
            @Override
            public void mouseClicked(MouseEvent e) {
            }
        });
        
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
				setVisible(false);
			}
			@Override
			public void windowClosed(WindowEvent e) {
			}
			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
        
        addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
				canvas.setBufferFinalization(true);
				listener.setViewVisible(true);
			}
			@Override
			public void componentResized(ComponentEvent e) {
			}
			@Override
			public void componentMoved(ComponentEvent e) {
			}
			@Override
			public void componentHidden(ComponentEvent e) {
				canvas.setBufferFinalization(false);
				listener.setViewVisible(false);
			}
		});
        
        canvas.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int rotation = e.getWheelRotation();
				listener.onZoom(rotation);
			}
		});
    }
    
    /**
     * Set the renderer for the canvas.
     * @param renderer
     */
	public void setRenderer(Renderer renderer) {
		canvas.setRenderer(renderer);
	}

	/**
	 * @return The 3D canvas
	 */
	public RenderCanvas getCanvas() {
		return canvas;
	}
	
	/**
	 * Displays the 3D view window and places it to the right of
	 * the parent window.
	 * @param parent
	 */
	public void displayRightOf(JDialog parent) {
		Point loc = parent.getLocation();
		setLocation(loc.x + parent.getWidth(), loc.y);
		setVisible(true);
	}

	/**
	 * Resize the render canvas to a preferred size
	 * @param width width
	 * @param height height
	 */
	public void setCanvasSize(int width, int height) {
		// NB: avoid setting the same preferred size twice
		if (width != canvas.getWidth() || height != canvas.getHeight()) {
			canvas.setPreferredSize(new Dimension(width, height));
			pack();
		}
	}
}
