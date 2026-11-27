package com.example.fxsuite.baked;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * What code is, as a word. A folder of classes: the same as often as asked; another with a class changed, or with
 * other words said; the same with something not a class added. A jar the same whenever it was built. A package's
 * code the same in its own jar and in one shaded with others, but not with a class of its own changed.
 */
class FingerprintTest {

    private final Fingerprint print = new Fingerprint();

    @TempDir
    private Path root;

    @Test
    void aClassChangedOrOtherWordsSaidMakeAnotherWordAnythingElseAddedDoesNot() throws IOException {
        final Path classes = Files.createDirectories(root.resolve("classes/a"));
        Files.write(classes.resolve("One.class"), new byte[] {1, 2, 3});
        Files.write(classes.resolve("Two.class"), new byte[] {4, 5, 6});
        final List<Path> at = List.of(root.resolve("classes"));
        final String first = print.ofPlaces(at, "fine");
        assertEquals(first, print.ofPlaces(at, "fine"));
        assertNotEquals(first, print.ofPlaces(at, "coarse"));
        Files.writeString(classes.resolve("read-me.txt"), "nothing to do with it");
        assertEquals(first, print.ofPlaces(at, "fine"));
        Files.write(classes.resolve("Two.class"), new byte[] {4, 5, 7});
        assertNotEquals(first, print.ofPlaces(at, "fine"));
    }

    @Test
    void aJarIsTheSameWheneverItWasBuilt() throws IOException {
        final Path early = jar("early.jar", 1_000_000_000_000L, "a/One.class", "a/Two.class");
        final Path late = jar("late.jar", 1_700_000_000_000L, "a/One.class", "a/Two.class");
        assertEquals(print.ofPlaces(List.of(early), ""), print.ofPlaces(List.of(late), ""));
    }

    @Test
    void aPackagesCodeIsTheSameWhateverElseItsJarHolds() throws IOException {
        final Path own = jar("own.jar", 0, "a/One.class", "a/b/Two.class");
        final Path shaded = jar("shaded.jar", 0, "a/One.class", "a/b/Two.class", "c/Other.class", "ab/Near.class");
        final Path other = jar("other.jar", 0, "a/One.class", "a/b/Three.class");
        assertEquals(print.within(own, "a/", ""), print.within(shaded, "a/", ""));
        assertNotEquals(print.within(own, "a/", ""), print.within(other, "a/", ""));
    }

    /** A jar called {@code name} of {@code classes}, each said to be made at {@code millis}. */
    private Path jar(final String name, final long millis, final String... classes) throws IOException {
        final Path at = root.resolve(name);
        try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(at))) {
            for (final String c : classes) {
                final JarEntry e = new JarEntry(c);
                e.setLastModifiedTime(FileTime.fromMillis(millis));
                out.putNextEntry(e);
                out.write(c.getBytes());
                out.closeEntry();
            }
        }
        return at;
    }
}
