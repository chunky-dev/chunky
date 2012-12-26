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

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

/**
 * A log handler for the global Chunky logger.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ChunkyLogAppender extends AppenderSkeleton {
	
	private ChunkyErrorDialog errorDialog = new ChunkyErrorDialog();
	
	@Override
	public void close() {
		// do nothing
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	@Override
	protected void append(final LoggingEvent event) {
		Level level = event.getLevel();
		if (level == Level.FATAL || level == Level.ERROR) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					errorDialog.addRecord(event);
				}
			});
		} else if (level == Level.WARN) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null,
							event.getMessage().toString());
				}
			});
		}
	}

}
