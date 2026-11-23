package com.example.fxsuite.vulkan.bench.play;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.TreeSet;

/**
 * A walk being kept as its steps, written to a file as it goes, in plain text a line at a time:
 * <pre>
 * walk-inputs 1                          the format
 * place Trees in winter                  the place, by the walk's title
 * 1 held=W,SHIFT pressed= turn=0.0 tilt=0.0 circling=0 scrolled=0.0
 *                                        from step 1, until the next line: what is held, what went down, the turn
 *                                        and the tilt in degrees, the swing, the wheel; and, where they happened,
 *                                        the words flies and behind
 * check 120 0.0 -0.0 412.5               where the avatar stood after step 120, in centimetres
 * end 4210                               the last step
 * </pre>
 * A step's line is written only when it differs from the step before, or something is done in it once, so a key
 * held for a minute is one line. Numbers are written so that they are read back to the bit. Where the avatar stood
 * is written every second of the run, for a replay to be checked against.
 */
public final class Keeping implements AutoCloseable {

    /** The first line of every kept walk, and the format's number. */
    static final String FORMAT = "walk-inputs 1";
    /** How many steps apart where the avatar stood is written: a second of the run. */
    static final long CHECKED = 120;

    private final BufferedWriter out;
    private Step last = new Step();
    private long steps;

    /** A walk about the place called {@code place}, kept in {@code file}, which is begun afresh. */
    public Keeping(final Path file, final String place) {
        try {
            final Path folder = file.toAbsolutePath().getParent();
            if (folder != null) {
                Files.createDirectories(folder);
            }
            this.out = Files.newBufferedWriter(file, StandardCharsets.UTF_8);
            out.write(FORMAT + "\n");
            out.write("place " + place.replace('\n', ' ') + "\n");
        } catch (final IOException failed) {
            throw new UncheckedIOException("a walk cannot be kept in " + file, failed);
        }
    }

    /** The step numbered {@code step} kept, as it was told; and, every second, where the avatar then stood. */
    public void kept(final long step, final Step told, final Where stood) {
        try {
            if (step == 1 || told.saidAfter(last)) {
                out.write(step + " " + line(told) + "\n");
            }
            if (step % CHECKED == 0) {
                out.write("check " + step + " " + stood.x() + " " + stood.y() + " " + stood.z() + "\n");
            }
        } catch (final IOException failed) {
            throw new UncheckedIOException("a walk could not be kept", failed);
        }
        last = told;
        steps = step;
    }

    /** {@code told}, as a line says it. */
    private String line(final Step told) {
        return "held=" + String.join(",", new TreeSet<>(told.held())) + " pressed=" + String.join(",", told.pressed())
                + " turn=" + told.turn() + " tilt=" + told.tilt() + " circling=" + told.circling()
                + " scrolled=" + told.scrolled() + (told.flies() ? " flies" : "") + (told.behind() ? " behind" : "");
    }

    /** The keeping ended: the last step said, and the file closed. */
    @Override
    public void close() {
        try (out) {
            out.write("end " + steps + "\n");
        } catch (final IOException failed) {
            throw new UncheckedIOException("a kept walk could not be ended", failed);
        }
    }

}
