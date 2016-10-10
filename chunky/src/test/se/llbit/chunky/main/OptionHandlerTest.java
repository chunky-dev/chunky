/* Copyright (c) 2016 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.main;

import org.junit.Test;
import se.llbit.chunky.main.CommandLineOptions.ArgumentError;
import se.llbit.chunky.main.CommandLineOptions.OptionHandler;
import se.llbit.chunky.main.CommandLineOptions.Range;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class OptionHandlerTest {
  private void expectBart(List<String> arguments) {
    if (!arguments.get(0).equals("bart")) {
      throw new IllegalArgumentException();
    }
  }

  private void expectNone(List<String> arguments) {
    assertThat(arguments).isEmpty();
  }

  private void expectOne(List<String> arguments) {
    assertThat(arguments).hasSize(1);
  }

  private void expectTwo(List<String> arguments) {
    assertThat(arguments).hasSize(2);
  }

  @Test public void testNoArg1() throws ArgumentError {
    OptionHandler handler = new OptionHandler("-bart", new Range(0), arguments -> {});
    List<String> extraArgs = handler.handle(Collections.emptyList());
    assertThat(extraArgs).isEmpty();
  }

  @Test public void testNoArg2() throws ArgumentError {
    OptionHandler handler = new OptionHandler("-bart", new Range(0), arguments -> {});
    // Extra arguments are ignored.
    List<String> extraArgs = handler.handle(Arrays.asList("foo", "bar", "bork", "bork", "-spork"));
    assertThat(extraArgs).containsExactly("foo", "bar", "bork", "bork", "-spork");
  }

  @Test public void testOneArg1() throws ArgumentError {
    OptionHandler handler = new OptionHandler("-bart", new Range(1), arguments -> {});
    List<String> extraArgs = handler.handle(Collections.singletonList("foo"));
    assertThat(extraArgs).isEmpty();
  }

  @Test public void testOneArg2() throws ArgumentError {
    OptionHandler handler = new OptionHandler("-bart", new Range(1), this::expectBart);
    List<String> extraArgs = handler.handle(Arrays.asList("bart", "foo"));
    assertThat(extraArgs).containsExactly("foo");
  }

  /** Test an option that accepts 0 to 2 arguments. */
  @Test public void testArgRange1() throws ArgumentError {
    OptionHandler handler = new OptionHandler("-bart", new Range(0, 2), this::expectNone);
    List<String> extraArgs = handler.handle(Collections.emptyList());
    assertThat(extraArgs).isEmpty();
  }

  /** Test an option that accepts 0 to 2 arguments. */
  @Test public void testArgRange2() throws ArgumentError {
    OptionHandler handler = new OptionHandler("-bart", new Range(0, 2), this::expectOne);
    List<String> extraArgs = handler.handle(Collections.singletonList("foo"));
    assertThat(extraArgs).isEmpty();
  }

  /** Test an option that accepts 0 to 2 arguments. */
  @Test public void testArgRange3() throws ArgumentError {
    OptionHandler handler = new OptionHandler("-bart", new Range(0, 2), this::expectTwo);
    List<String> extraArgs = handler.handle(Arrays.asList("bart", "foo"));
    assertThat(extraArgs).isEmpty();
  }

  /** Test an option that accepts 0 to 2 arguments. */
  @Test public void testArgRange4() throws ArgumentError {
    OptionHandler handler = new OptionHandler("-bart", new Range(0, 2), this::expectOne);
    List<String> extraArgs = handler.handle(Arrays.asList("foo", "-foo"));
    assertThat(extraArgs).containsExactly("-foo");
  }

  /** Test an option that accepts 0 to 2 arguments. */
  @Test public void testArgRange5() throws ArgumentError {
    OptionHandler handler = new OptionHandler("-bart", new Range(0, 2), this::expectTwo);
    List<String> extraArgs = handler.handle(Arrays.asList("baz", "foo", "-foo", "bar"));
    assertThat(extraArgs).containsExactly("-foo", "bar");
  }

  /** Test missing option argument. */
  @Test(expected = ArgumentError.class)
  public void testError01() throws ArgumentError {
    OptionHandler handler = new OptionHandler("-bart", new Range(1), arguments -> {});
    handler.handle(Collections.emptyList());
  }

  /** Test missing option argument. */
  @Test(expected = ArgumentError.class)
  public void testError1() throws ArgumentError {
    OptionHandler handler = new OptionHandler("-bart", new Range(1), arguments -> {});
    // The -bort argument is treated as a separate option.
    handler.handle(Collections.singletonList("-bort"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testError02() throws ArgumentError {
    OptionHandler handler = new OptionHandler("-bart", new Range(1), this::expectBart);
    handler.handle(Collections.singletonList("bort"));
  }
}
