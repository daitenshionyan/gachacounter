package com.hanyans.gachacounter.core;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A semantic version number.
 */
public class Version implements Comparable<Version> {
    public static final String MAJOR_GRP_NAME = "major";
    public static final String MINOR_GRP_NAME = "minor";
    public static final String PATCH_GRP_NAME = "patch";

    public static final Pattern VERSION_PATTERN = Pattern.compile(
            String.format("[vV]?(?<%s>\\d)(\\.(?<%s>\\d))?(\\.(?<%s>\\d))?.*",
                    MAJOR_GRP_NAME, MINOR_GRP_NAME, PATCH_GRP_NAME));

    private final int major;
    private final int minor;
    private final int patch;


    /**
     * Constructs a {@code Version}.
     *
     * @param major - major version number.
     * @param minor - minor version number.
     * @param patch - patch version number.
     * @throws IllegalArgumentException if any version numbers are negative.
     */
    public Version(int major, int minor, int patch) {
        if (!isValid(major, minor, patch)) {
            throw new IllegalArgumentException("Negative version number");
        }
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }


    /**
     * Returns {@code true} if the given set of version numbers can make a
     * valid version.
     *
     * @param major - major version number.
     * @param minor - minor version number.
     * @param patch - patch version number.
     */
    public static boolean isValid(int major, int minor, int patch) {
        return major >= 0 && minor >= 0 && patch >= 0;
    }


    /**
     * Parses the specified String to a {@code Version}.
     *
     * @param versionString - the String to parse.
     * @throws IllegalArgumentException if the given {@code versionString}
     *      cannot be parsed.
     * @throws NullPointerException if {@code versionString} is {@code null}.
     */
    public static Version parse(String versionString) {
        Objects.requireNonNull(versionString);
        if (versionString.isBlank()) {
            throw new IllegalArgumentException("Empty version string");
        }
        Matcher matcher = VERSION_PATTERN.matcher(versionString);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format("Unknown version format <%s>",
                    versionString));
        }
        int major = Optional.ofNullable(matcher.group("major"))
                .map(Integer::parseInt)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Major version number missing in <%s>",
                        versionString)));
        int minor = Optional.ofNullable(matcher.group("minor"))
                .map(Integer::parseInt)
                .orElse(0);
        int patch = Optional.ofNullable(matcher.group("patch"))
                .map(Integer::parseInt)
                .orElse(0);
        return new Version(major, minor, patch);
    }


    /**
     * Returns the major version number.
     */
    public int major() {
        return major;
    }


    /**
     * Returns the minor version number.
     */
    public int minor() {
        return minor;
    }


    /**
     * Returns the patch version number.
     */
    public int patch() {
        return patch;
    }


    /**
     * Returns {@code true} if this {@code Version} is older than the given.
     *
     * @param other - the version to compare with this.
     * @throws NullPointerException if {@code other} is {@code null}.
     */
    public boolean isBefore(Version other) {
        Objects.requireNonNull(other);
        return compareTo(other) < 0;
    }


    /**
     * Returns {@code true} if this {@code Version} is newer than the given.
     *
     * @param other - the version to compare with this.
     * @throws NullPointerException if {@code other} is {@code null}.
     */
    public boolean isAfter(Version other) {
        Objects.requireNonNull(other);
        return compareTo(other) > 0;
    }


    @Override
    public int compareTo(Version other) {
        int diff = major - other.major;
        if (diff == 0) {
            diff = minor - other.minor;
        }
        if (diff == 0) {
            diff = patch - other.patch;
        }
        return diff;
    }


    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Version)) {
            return false;
        }
        Version casted = (Version) other;
        return major == casted.major
                && minor == casted.minor
                && patch == casted.patch;
    }


    @Override
    public String toString() {
        return String.format("v%d.%d.%d",
                major, minor, patch);
    }
}
