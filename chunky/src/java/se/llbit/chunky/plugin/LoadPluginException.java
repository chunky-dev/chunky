/* Copyright (c) 2017 Chunky contributors
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
package se.llbit.chunky.plugin;

/**
 * Exception for plugin loading failures.
 */
public class LoadPluginException extends Exception {
  /**
   * Creates a new instance of this exception.
   *
   * @param msg   message
   * @param inner exception that caused this exception
   */
  public LoadPluginException(String msg, Throwable inner) {
    super(msg, inner);
  }

  /**
   * Creates a new instance of this exception.
   *
   * @param msg message
   */
  public LoadPluginException(String msg) {
    super(msg);
  }
}
