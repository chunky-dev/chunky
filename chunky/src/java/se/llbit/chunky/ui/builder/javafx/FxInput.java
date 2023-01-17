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

package se.llbit.chunky.ui.builder.javafx;

import javafx.scene.Node;
import se.llbit.chunky.ui.builder.UiInput;

import java.util.ArrayList;
import java.util.function.Consumer;

public abstract class FxInput<T, N extends Node, Self extends UiInput<T, Self>> implements UiInput<T, Self> {
  public final N input;
  protected final ArrayList<Consumer<T>> callbacks = new ArrayList<>();

  protected FxInput(N input) {
    this.input = input;
  }

  protected void callCallbacks(T value) {
    for (Consumer<T> cb : callbacks) {
      cb.accept(value);
    }
  }

  @SuppressWarnings("unchecked")
  private Self self() {
    return (Self) this;
  }

  @Override
  public Self callCallbacks() {
    callCallbacks(this.get());
    return self();
  }

  @Override
  public Self addCallback(Consumer<T> callback) {
    callbacks.add(callback);
    return self();
  }

  @Override
  public Self removeCallback(Consumer<T> callback) {
    callbacks.add(callback);
    return self();
  }
}
