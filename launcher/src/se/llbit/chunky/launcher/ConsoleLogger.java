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

public class ConsoleLogger implements Logger {

	@Override
	public void appendStdout(byte[] buffer, int size) {
		System.out.write(buffer, 0, size);
	}

	@Override
	public void appendStderr(byte[] buffer, int size) {
		System.err.write(buffer, 0, size);
	}

	@Override
	public void appendLine(String line) {
		System.out.println(line);
	}

	@Override
	public void appendErrorLine(String line) {
		System.err.println(line);
	}

	@Override
	public void processExited(int exitValue) {
	}

}
