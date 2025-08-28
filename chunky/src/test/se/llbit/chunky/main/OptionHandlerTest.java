/*
 * Copyright (c) 2016 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2016-2021 Chunky contributors
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
import se.llbit.chunky.main.CommandLineOptions.InvalidCommandLineArgumentsException;
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

  @Test public void testOptionWithNoArgument() throws InvalidCommandLineArgumentsException {
    OptionHandler handler1 = new OptionHandler("-bart", new Range(0), arguments -> {});
    List<String> extraArgs1 = handler1.handle(Collections.emptyList());
    assertThat(extraArgs1).isEmpty();

    OptionHandler handler2 = new OptionHandler("-bart", new Range(0), arguments -> {});
    // Extra arguments are ignored.
    List<String> extraArgs2 = handler2.handle(Arrays.asList("foo", "bar", "bork", "bork", "-spork"));
    assertThat(extraArgs2).containsExactly("foo", "bar", "bork", "bork", "-spork");
  }

  @Test public void testOptionWithOneArgument() throws InvalidCommandLineArgumentsException {
    OptionHandler handler1 = new OptionHandler("-bart", new Range(1), arguments -> {});
    List<String> extraArgs1 = handler1.handle(Collections.singletonList("foo"));
    assertThat(extraArgs1).isEmpty();

    OptionHandler handler2 = new OptionHandler("-bart", new Range(1), this::expectBart);
    List<String> extraArgs2 = handler2.handle(Arrays.asList("bart", "foo"));
    assertThat(extraArgs2).containsExactly("foo");

    OptionHandler handler = new OptionHandler("-bart", new Range(1), new int[]{0}, this::expectBart);
    List<String> extraArgs = handler.handle(Arrays.asList("bart", "-42"));
    assertThat(extraArgs).containsExactly("-42");
  }

  /** Test an option that accepts 0 to 2 arguments. */
  @Test public void testOptionArgumentRanges() throws InvalidCommandLineArgumentsException {
    OptionHandler handler1 = new OptionHandler("-bart", new Range(0, 2), this::expectNone);
    List<String> extraArgs1 = handler1.handle(Collections.emptyList());
    assertThat(extraArgs1).isEmpty();

    OptionHandler handler2 = new OptionHandler("-bart", new Range(0, 2), this::expectOne);
    List<String> extraArgs2 = handler2.handle(Collections.singletonList("foo"));
    assertThat(extraArgs2).isEmpty();

    OptionHandler handler3 = new OptionHandler("-bart", new Range(0, 2), this::expectTwo);
    List<String> extraArgs3 = handler3.handle(Arrays.asList("bart", "foo"));
    assertThat(extraArgs3).isEmpty();

    OptionHandler handler4 = new OptionHandler("-bart", new Range(0, 2), this::expectOne);
    List<String> extraArgs4 = handler4.handle(Arrays.asList("foo", "-foo"));
    assertThat(extraArgs4).containsExactly("-foo");

    OptionHandler handler5 = new OptionHandler("-bart", new Range(0, 2), this::expectTwo);
    List<String> extraArgs5 = handler5.handle(Arrays.asList("baz", "foo", "-foo", "bar"));
    assertThat(extraArgs5).containsExactly("-foo", "bar");
  }

  @Test public void testArgRangeWithNumeric() throws InvalidCommandLineArgumentsException {
    OptionHandler handler1 = new OptionHandler("-bart", new Range(0, 2), new int[]{0}, this::expectNone);
    List<String> extraArgs1 = handler1.handle(Collections.emptyList());
    assertThat(extraArgs1).isEmpty();

    OptionHandler handler2 = new OptionHandler("-bart", new Range(0, 2), new int[]{0}, args -> {
      assertThat(args).containsExactly("-42", "foo");
    });
    List<String> extraArgs2 = handler2.handle(Arrays.asList("-42", "foo"));
    assertThat(extraArgs2).isEmpty();

    OptionHandler handler3 = new OptionHandler("-bart", new Range(0, 2), new int[]{1}, args -> {
      assertThat(args).containsExactly("foo", "-42");
    });
    List<String> extraArgs3 = handler3.handle(Arrays.asList("foo", "-42"));
    assertThat(extraArgs3).isEmpty();

    OptionHandler handler4 = new OptionHandler("-bart", new Range(0, 2), new int[]{1}, args -> {
      assertThat(args).containsExactly("foo");
    });
    List<String> extraArgs4 = handler4.handle(Arrays.asList("foo", "-next"));
    assertThat(extraArgs4).containsExactly("-next");

    OptionHandler handler5 = new OptionHandler("-bart", new Range(0, 2), new int[]{0}, args -> {
      assertThat(args).containsExactly("-42");
    });
    List<String> extraArgs5 = handler5.handle(Arrays.asList("-42", "-next"));
    assertThat(extraArgs5).containsExactly("-next");

    OptionHandler handler6 = new OptionHandler("-bart", new Range(0, 2), new int[]{0}, args -> {
      assertThat(args).containsExactly("-42");
    });
    List<String> extraArgs6 = handler6.handle(Arrays.asList("-42", "-42"));
    assertThat(extraArgs6).containsExactly("-42");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testError_NoOptionGiven() throws InvalidCommandLineArgumentsException {
    OptionHandler handler = new OptionHandler("-bart", new Range(1), arguments -> {});
    handler.handle(Collections.emptyList());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testError_WrongOptionGiven() throws InvalidCommandLineArgumentsException {
    OptionHandler handler = new OptionHandler("-bart", new Range(1), arguments -> {});
    // The -bort argument is treated as a separate option.
    handler.handle(Collections.singletonList("-bort"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testError_ArgumentInsteadOfOptionGiven() throws InvalidCommandLineArgumentsException {
    OptionHandler handler = new OptionHandler("-bart", new Range(1), this::expectBart);
    handler.handle(Collections.singletonList("bort"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testError_NumberInsteadOfOptionGiven() throws InvalidCommandLineArgumentsException {
    OptionHandler handler = new OptionHandler("-bart", new Range(1), this::expectBart);
    handler.handle(Collections.singletonList("-42"));
  }
}
