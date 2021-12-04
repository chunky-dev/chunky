/* Copyright (c) 2021 Chunky Contributors
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
package se.llbit.util;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SemanticVersion implements Comparable<SemanticVersion> {
    // Official SemVer string regex from
    // https://semver.org/#is-there-a-suggested-regular-expression-regex-to-check-a-semver-string
    private static final Pattern semVerPattern = Pattern.compile(
        "(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)" +
        "(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?"
    );

    public final int major;
    public final int minor;
    public final int patch;
    public final String prerelease;
    public final String buildmetadata;
    private final String version;

    public SemanticVersion(String version) {
        this.version = version;
        Matcher match = semVerPattern.matcher(version);
        if (match.matches()) {
            major = parseIntDefault(matchDefault(match, 0, "0"), 0);
            minor = parseIntDefault(matchDefault(match, 1, "0"), 0);
            patch = parseIntDefault(matchDefault(match, 2, "0"), 0);
            prerelease = matchDefault(match, 3, "");
            buildmetadata = matchDefault(match, 4, "");
        } else {
            major = 0;
            minor = 0;
            patch = 0;
            prerelease = "";
            buildmetadata = "";
        }
    }

    private static String matchDefault(Matcher matcher, int group, String def) {
        try {
            return matcher.group(group);
        } catch (IndexOutOfBoundsException | IllegalStateException e) {
            return def;
        }
    }

    private static int parseIntDefault(String value, int def) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    @Override
    public String toString() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SemanticVersion that = (SemanticVersion) o;
        return major == that.major
            && minor == that.minor
            && patch == that.patch
            && prerelease.equals(that.prerelease)
            && buildmetadata.equals(that.buildmetadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch, prerelease, buildmetadata);
    }

    @Override
    public int compareTo(SemanticVersion o) {
        int diff;
        diff = this.major - o.major;
        if (diff != 0) return diff;
        diff = this.minor - o.minor;
        if (diff != 0) return diff;
        diff = this.patch - o.patch;
        if (diff != 0) return diff;
        if (this.prerelease.isEmpty() && !o.prerelease.isEmpty())
            return 1;
        if (!this.prerelease.isEmpty() && o.prerelease.isEmpty())
            return -1;
        return this.prerelease.compareTo(o.prerelease);
    }
}
