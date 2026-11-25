package com.example.fxsuite.vulkan.bench;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Where a spread of windows is kept between runs: one small file of text for every bench, in the folder
 * {@code .innocent-melons} of whoever runs it, outside any repository. {@code -Dbench.spread=} names another file,
 * and {@code -Dbench.spread=none} keeps and reads nothing. A file that is not there, or cannot be read as a spread,
 * is as nothing kept: a bench then opens the usual way and says nothing of it.
 */
public final class Spreads {

    private static final String NONE = "none";

    private final Optional<Path> file;

    /** The spread kept where the run says, or in the folder of whoever runs it. */
    public Spreads() {
        this(System.getProperty("bench.spread", ""));
    }

    /** The spread kept in the file called {@code named}: the usual file where it is called nothing, none at "none". */
    public Spreads(final String named) {
        this.file = named.equals(NONE) ? Optional.empty() : Optional.of(named.isEmpty()
                ? Path.of(System.getProperty("user.home"), ".innocent-melons", "windows.spread") : Path.of(named));
    }

    /** The spread kept, where one is and reads as one. */
    public Optional<Spread> kept() {
        try {
            if (file.isEmpty() || !Files.isRegularFile(file.get())) {
                return Optional.empty();
            }
            final List<String> lines = Files.readAllLines(file.get());
            final Optional<Seat> picture = seat(lines, "picture ");
            final Optional<Seat> console = seat(lines, "console ");
            return picture.isPresent() && console.isPresent() && lines.getFirst().strip().equals("spread 1")
                    ? Optional.of(new Spread(picture.get(), console.get())) : Optional.empty();
        } catch (final IOException | RuntimeException unread) {
            return Optional.empty();
        }
    }

    /** The seat written on the line of {@code lines} that begins {@code named}; none, where there is no such line. */
    private Optional<Seat> seat(final List<String> lines, final String named) {
        return lines.stream().filter(line -> line.startsWith(named)).findFirst().map(line -> {
            final Map<String, String> said = new HashMap<>();
            for (final String part : line.substring(named.length()).strip().split("\\s+")) {
                said.put(part.substring(0, part.indexOf('=')), part.substring(part.indexOf('=') + 1));
            }
            return new Seat(Seat.Which.valueOf(said.get("on").toUpperCase(Locale.ROOT)),
                    Boolean.parseBoolean(said.get("maximized")), Integer.parseInt(said.get("x")),
                    Integer.parseInt(said.get("y")), Integer.parseInt(said.get("width")),
                    Integer.parseInt(said.get("height")));
        });
    }

    /** {@code spread} kept from now on: whether it could be written. */
    public boolean keep(final Spread spread) {
        try {
            if (file.isEmpty()) {
                return false;
            }
            Files.createDirectories(file.get().toAbsolutePath().getParent());
            Files.write(file.get(), spread.written());
            return true;
        } catch (final IOException unwritten) {
            return false;
        }
    }

    /** Nothing kept from now on: the next bench opens the usual way. */
    public void forget() {
        try {
            if (file.isPresent()) {
                Files.deleteIfExists(file.get());
            }
        } catch (final IOException undeleted) {
            // left as it is: a bench that cannot delete it reads it again, and can be told to keep another
        }
    }

    /** Where it is kept, in words. */
    public String said() {
        return file.map(Path::toString).orElse("nowhere");
    }
}
