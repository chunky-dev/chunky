/*
 * Copyright (c) 2024 Chunky contributors
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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

public class ChunkyLauncherCliParseTest {
  @Test
  public void testAdditionalOptions() throws ParseException {
    CommandLine cmd = ChunkyLauncher.parseCli(new String[] { "--", "--help", "--help1" });
    assertThat(cmd.getOptions().length).isEqualTo(0);
    assertThat(cmd.getArgs().length).isEqualTo(2);
    assertThat(cmd.getArgs()[0]).isEqualTo("--help");
    assertThat(cmd.getArgs()[1]).isEqualTo("--help1");
  }

  @Test
  public void testOptions() throws ParseException {
    CommandLine cmd;

    // Check --help
    cmd = ChunkyLauncher.parseCli(new String[] { "--help" });
    assertThat(cmd.getOptions().length).isEqualTo(1);
    assertThat(cmd.hasOption("help")).isTrue();

    // Check -h
    cmd = ChunkyLauncher.parseCli(new String[] { "-h" });
    assertThat(cmd.getOptions().length).isEqualTo(1);
    assertThat(cmd.hasOption("help")).isTrue();

    // Check --launcher
    cmd = ChunkyLauncher.parseCli(new String[] { "--launcher" });
    assertThat(cmd.getOptions().length).isEqualTo(1);
    assertThat(cmd.hasOption("launcher")).isTrue();

    // Check --version
    cmd = ChunkyLauncher.parseCli(new String[] { "--version" });
    assertThat(cmd.getOptions().length).isEqualTo(1);
    assertThat(cmd.hasOption("version")).isTrue();

    // Check --verbose
    cmd = ChunkyLauncher.parseCli(new String[] { "--verbose" });
    assertThat(cmd.getOptions().length).isEqualTo(1);
    assertThat(cmd.hasOption("verbose")).isTrue();

    // Check --console
    cmd = ChunkyLauncher.parseCli(new String[] { "--console" });
    assertThat(cmd.getOptions().length).isEqualTo(1);
    assertThat(cmd.hasOption("console")).isTrue();

    // Check --setup
    cmd = ChunkyLauncher.parseCli(new String[] { "--setup" });
    assertThat(cmd.getOptions().length).isEqualTo(1);
    assertThat(cmd.hasOption("setup")).isTrue();

    // Check --update
    cmd = ChunkyLauncher.parseCli(new String[] { "--update" });
    assertThat(cmd.getOptions().length).isEqualTo(1);
    assertThat(cmd.hasOption("update")).isTrue();

    // Check --noRetryJavafx
    cmd = ChunkyLauncher.parseCli(new String[] { "--noRetryJavafx" });
    assertThat(cmd.getOptions().length).isEqualTo(1);
    assertThat(cmd.hasOption("noRetryJavafx")).isTrue();

    // Check --checkJvm
    cmd = ChunkyLauncher.parseCli(new String[] { "--checkJvm" });
    assertThat(cmd.getOptions().length).isEqualTo(1);
    assertThat(cmd.hasOption("checkJvm")).isTrue();

    // Check --dangerouslyDisableLibraryValidation
    cmd = ChunkyLauncher.parseCli(new String[] { "--dangerouslyDisableLibraryValidation" });
    assertThat(cmd.getOptions().length).isEqualTo(1);
    assertThat(cmd.hasOption("dangerouslyDisableLibraryValidation")).isTrue();

    // Check --javaOptions
    cmd = ChunkyLauncher.parseCli(new String[] { "--javaOptions", "test" });
    assertThat(cmd.getOptions().length).isEqualTo(1);
    assertThat(cmd.hasOption("javaOptions")).isTrue();
    assertThat(cmd.getOptionValue("javaOptions")).isEqualTo("test");
  }
}
