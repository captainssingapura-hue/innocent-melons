package com.example.fxsuite.baked;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * What code is, as a word: a digest (SHA-256) of classes, each by its name and its bytes, in the order of their names,
 * then of what else is said. Of a class given, every class of its package and the packages under it, where it comes
 * from, a jar or a folder of classes: so a body shop's code is the same word whether it is read from its own jar or
 * from one it is shaded into with all it depends on. The same code and the same words, the same; any class changed,
 * added or taken away, another. When a jar was built and what else is in it make no difference.
 */
public final class Fingerprint {

    private static final String CLASS = ".class";

    /**
     * The word for the code of each of {@code from}'s packages and those under them, where each comes from, and
     * {@code said}.
     */
    public String of(final List<Class<?>> from, final String said) {
        final MessageDigest digest = digest();
        for (final Class<?> c : from) {
            try {
                final Path place = Path.of(c.getProtectionDomain().getCodeSource().getLocation().toURI());
                classes(place, c.getPackageName().replace('.', '/') + "/", digest);
            } catch (final URISyntaxException unplaced) {
                throw new IllegalArgumentException("where " + c.getName() + " comes from cannot be told", unplaced);
            }
        }
        return word(digest, said);
    }

    /** The word for the classes in {@code place} whose names begin {@code within}, and {@code said}. */
    String within(final Path place, final String within, final String said) {
        final MessageDigest digest = digest();
        classes(place, within, digest);
        return word(digest, said);
    }

    /** The word for every class in {@code places}, each a jar or a folder of classes, and {@code said}. */
    public String ofPlaces(final List<Path> places, final String said) {
        final MessageDigest digest = digest();
        places.forEach(place -> classes(place, "", digest));
        return word(digest, said);
    }

    /** Every class in {@code place} whose name begins {@code within}, by its name and its bytes, into {@code d}. */
    private void classes(final Path place, final String within, final MessageDigest d) {
        try {
            if (Files.isDirectory(place)) {
                folder(place, within, d);
            } else {
                jar(place, within, d);
            }
        } catch (final IOException unread) {
            throw new UncheckedIOException(unread);
        }
    }

    /** Every class in the folder {@code root} named {@code within} on, by its name within it, into {@code d}. */
    private void folder(final Path root, final String within, final MessageDigest d) throws IOException {
        final List<Path> classes;
        try (Stream<Path> all = Files.walk(root)) {
            classes = all.filter(p -> p.toString().endsWith(CLASS) && named(root, p).startsWith(within))
                    .sorted(Comparator.comparing(p -> named(root, p))).toList();
        }
        for (final Path c : classes) {
            d.update(named(root, c).getBytes(StandardCharsets.UTF_8));
            d.update(Files.readAllBytes(c));
        }
    }

    /** Every class in the jar {@code at} named {@code within} on, by its name and its bytes, into {@code d}. */
    private void jar(final Path at, final String within, final MessageDigest d) throws IOException {
        try (JarFile jar = new JarFile(at.toFile())) {
            final List<JarEntry> classes = jar.stream()
                    .filter(e -> e.getName().endsWith(CLASS) && e.getName().startsWith(within))
                    .sorted(Comparator.comparing(JarEntry::getName)).toList();
            for (final JarEntry e : classes) {
                d.update(e.getName().getBytes(StandardCharsets.UTF_8));
                try (InputStream in = jar.getInputStream(e)) {
                    d.update(in.readAllBytes());
                }
            }
        }
    }

    /** The name of the file {@code p} within the folder {@code root}, as a jar names it. */
    private String named(final Path root, final Path p) {
        return root.relativize(p).toString().replace('\\', '/');
    }

    /** A new digest. */
    private MessageDigest digest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (final NoSuchAlgorithmException none) {
            throw new IllegalStateException("no SHA-256 here", none);
        }
    }

    /** {@code said} put into {@code d}, and the word it all comes to. */
    private String word(final MessageDigest d, final String said) {
        d.update(said.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(d.digest());
    }
}
