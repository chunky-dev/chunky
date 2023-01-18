/*
 * Copyright (c) 2023 Chunky contributors
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

import javafx.scene.control.Tooltip;
import se.llbit.fxutil.CustomizedListCellFactory;
import se.llbit.util.Registerable;

public class RegisterableCellAdapter implements CustomizedListCellFactory.Adapter<Registerable> {
  @Override
  public String getLabel(Registerable item) {
    return item.getName();
  }

  @Override
  public Tooltip getTooltip(Registerable item) {
    String description = item.getDescription();
    if (description != null && !description.isEmpty()) {
      return new Tooltip(item.getDescription());
    }
    return null;
  }
}
