/* Copyright (c) 2014 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.log;

public class Log {

	public static Level level = Level.WARNING;
	private static final int INFO = Level.INFO.ordinal();
	private static final int WARNING = Level.WARNING.ordinal();
	private static final int ERROR = Level.ERROR.ordinal();
	private static final Receiver[] receiver = {
		ConsoleReceiver.INSTANCE,
		ConsoleReceiver.INSTANCE,
		ConsoleReceiver.INSTANCE
	};

	static {
		try {
			level = Level.valueOf(System.getProperty("logLevel", "WARNING"));
		} catch (IllegalArgumentException e) {
		}
	}

	public static void setReceiver(Receiver recv, Level... levels) {
		for (Level level: levels) {
			receiver[level.ordinal()] = recv;
		}
	}

	public static void infofmt(String fmt, Object... args) {
		if (level == Level.INFO) {
			receiver[INFO].logEvent(Level.INFO, String.format(fmt, args));
		}
	}

	public static void info(String message) {
		if (level == Level.INFO) {
			receiver[INFO].logEvent(Level.INFO, message);
		}
	}

	public static void info(String message, Throwable thrown) {
		if (level == Level.INFO) {
			receiver[INFO].logEvent(Level.INFO, message, thrown);
		}
	}

	public static void warningfmt(String fmt, Object... args) {
		if (level != Level.ERROR) {
			receiver[WARNING].logEvent(Level.WARNING, String.format(fmt, args));
		}
	}

	public static void warning(String message) {
		if (level != Level.ERROR) {
			receiver[WARNING].logEvent(Level.WARNING, message);
		}
	}

	public static void warning(String message, Throwable thrown) {
		if (level != Level.ERROR) {
			receiver[WARNING].logEvent(Level.WARNING, message, thrown);
		}
	}

	public static void warn(String message) {
		if (level != Level.ERROR) {
			receiver[WARNING].logEvent(Level.WARNING, message);
		}
	}

	public static void warn(String message, Throwable thrown) {
		if (level != Level.ERROR) {
			receiver[WARNING].logEvent(Level.WARNING, message, thrown);
		}
	}

	public static void errorfmt(String fmt, Object... args) {
		receiver[ERROR].logEvent(Level.ERROR, String.format(fmt, args));
	}

	public static void error(String message) {
		receiver[ERROR].logEvent(Level.ERROR, message);
	}

	public static void error(String message, Throwable thrown) {
		receiver[ERROR].logEvent(Level.ERROR, message, thrown);
	}

	public static void error(Throwable thrown) {
		receiver[ERROR].logEvent(Level.ERROR, thrown);
	}

}
