package com.example.fxsuite.vulkan.bench.play;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

/**
 * Steps told by a walk kept earlier ({@link Keeping}), read back: each step told exactly what it was told then.
 * It knows where the avatar stood after a step, every second of the run, so that whatever replays it can say
 * whether the replay is still the same walk, and where it first went astray if not: a rule changed since it was
 * kept, say. A file that is not a kept walk, or says what cannot be read, is refused, saying which line.
 */
public final class Replayed implements Source {

    private final String place;
    private final NavigableMap<Long, Step> lines = new TreeMap<>();
    private final Map<Long, Where> checks = new TreeMap<>();
    private final long end;

    /** The walk kept in {@code file}. */
    public Replayed(final Path file) {
        final List<String> read;
        try {
            read = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (final IOException failed) {
            throw new UncheckedIOException("no kept walk can be read from " + file, failed);
        }
        if (read.isEmpty() || !read.getFirst().equals(Keeping.FORMAT)) {
            throw new IllegalArgumentException(file + " is not a kept walk: it does not begin " + Keeping.FORMAT);
        }
        String named = "";
        long last = -1;
        for (int at = 1; at < read.size(); at++) {
            final String line = read.get(at).strip();
            final String[] words = line.split(" ");
            try {
                switch (words[0]) {
                    case "place" -> named = line.substring("place ".length());
                    case "check" -> checks.put(Long.parseLong(words[1]), new Where(Double.parseDouble(words[2]),
                            Double.parseDouble(words[3]), Double.parseDouble(words[4])));
                    case "end" -> last = Long.parseLong(words[1]);
                    default -> lines.put(Long.parseLong(words[0]), step(Arrays.asList(words).subList(1,
                            words.length)));
                }
            } catch (final RuntimeException unread) {
                throw new IllegalArgumentException(file + ", line " + (at + 1) + ", cannot be read: " + line, unread);
            }
        }
        if (last < 0) {
            throw new IllegalArgumentException(file + " was never ended: it has no last step");
        }
        this.place = named;
        this.end = last;
    }

    /** One step's line, after its number, read. */
    private Step step(final List<String> words) {
        Set<String> held = Set.of();
        List<String> pressed = List.of();
        double turn = 0;
        double tilt = 0;
        int circling = 0;
        double scrolled = 0;
        for (final String word : words) {
            final String[] said = word.split("=", -1);
            final String value = said.length > 1 ? said[1] : "";
            switch (said[0]) {
                case "held" -> held = value.isEmpty() ? Set.of() : Set.of(value.split(","));
                case "pressed" -> pressed = value.isEmpty() ? List.of() : List.of(value.split(","));
                case "turn" -> turn = Double.parseDouble(value);
                case "tilt" -> tilt = Double.parseDouble(value);
                case "circling" -> circling = Integer.parseInt(value);
                case "scrolled" -> scrolled = Double.parseDouble(value);
                case "flies", "behind" -> { }
                default -> throw new IllegalArgumentException("no step is told " + word);
            }
        }
        return new Step(held, pressed, turn, tilt, words.contains("flies"), circling, scrolled,
                words.contains("behind"));
    }

    /** The place it was kept in, by the walk's title. */
    public String place() {
        return place;
    }

    /** Where the avatar stood after the step numbered {@code step}, where the kept walk says. */
    @Override
    public Optional<Where> check(final long step) {
        return Optional.ofNullable(checks.get(step));
    }

    @Override
    public Step step(final long step, final int left) {
        final Map.Entry<Long, Step> said = lines.floorEntry(step);
        if (said == null) {
            return new Step();
        }
        return said.getKey() == step ? said.getValue() : said.getValue().goingOn();
    }

    @Override
    public long length() {
        return end;
    }

    @Override
    public boolean over(final long steps) {
        return steps >= end;
    }

    @Override
    public boolean ends() {
        return true;
    }
}
