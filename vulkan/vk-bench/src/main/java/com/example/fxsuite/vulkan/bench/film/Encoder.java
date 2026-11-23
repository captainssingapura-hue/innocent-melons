package com.example.fxsuite.vulkan.bench.film;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * A film being made: frames handed one after another to FFmpeg, a program outside the game, through a pipe, as
 * raw pixels, with no file of frames in between; it makes them into a video in {@code film}. What FFmpeg says is
 * kept beside the film, in a file of the same name with {@code .log} after it, and a film it fails to make is an
 * exception that says so and ends with the last of what it said.
 *
 * <p>Each frame is so many pixels wide and tall, each pixel as the card's pictures are read back: solid, then red,
 * green and blue, a byte each. Both sizes are even, as the video's colours are kept at half the size each way.
 */
public final class Encoder implements AutoCloseable {

    private static final int PIXEL = 4;
    /** How faithful the video is, from 0 for exact to 51 for poorest: FFmpeg's own scale; 18 looks as drawn. */
    private static final int FAITHFUL = 18;
    /** How many of the last lines FFmpeg said are told, where it failed. */
    private static final int TOLD = 12;

    private final Path film;
    private final Path log;
    private final int width;
    private final int height;
    private final Process making;
    private final OutputStream into;
    private final ByteBuffer frame;
    private long frames;

    /**
     * A film made in {@code film} by the FFmpeg at {@code ffmpeg} (a path, or a name it is found by), frames
     * {@code width} pixels by {@code height}, {@code rate} of them a second.
     */
    public Encoder(final Path film, final int width, final int height, final double rate, final String ffmpeg) {
        if (width <= 0 || height <= 0 || width % 2 != 0 || height % 2 != 0 || !(rate > 0)) {
            throw new IllegalArgumentException("a film's frames are some even number of pixels each way, some a"
                    + " second: " + width + " by " + height + ", " + rate + " a second");
        }
        this.film = film.toAbsolutePath();
        this.log = Path.of(this.film + ".log");
        this.width = width;
        this.height = height;
        this.frame = ByteBuffer.allocate(PIXEL * width * height).order(ByteOrder.LITTLE_ENDIAN);
        try {
            Files.createDirectories(this.film.getParent());
            this.making = new ProcessBuilder(ffmpeg, "-y", "-loglevel", "error", "-f", "rawvideo",
                    "-pix_fmt", "bgra", "-s", width + "x" + height, "-r", String.valueOf(rate), "-i", "-",
                    "-c:v", "libx264", "-pix_fmt", "yuv420p", "-crf", String.valueOf(FAITHFUL),
                    "-movflags", "+faststart", this.film.toString())
                    .redirectOutput(log.toFile()).redirectErrorStream(true).start();
        } catch (final IOException failed) {
            throw new UncheckedIOException("FFmpeg could not be started as " + ffmpeg
                    + "; say where it is with -Dbench.film.ffmpeg=", failed);
        }
        this.into = making.getOutputStream();
    }

    /** {@code pixels}, a frame as the card's pictures are read back, row by row from the top, added to the film. */
    public void frame(final int[] pixels) {
        if (pixels.length != width * height) {
            throw new IllegalArgumentException("a frame of this film is " + width + " by " + height + " pixels, not "
                    + pixels.length);
        }
        frame.clear();
        frame.asIntBuffer().put(pixels);
        try {
            into.write(frame.array());
        } catch (final IOException failed) {
            throw new IllegalStateException("FFmpeg stopped taking frames, after " + frames + ": " + said(), failed);
        }
        frames++;
    }

    /** How many frames have been added. */
    public long frames() {
        return frames;
    }

    /** The film finished: FFmpeg told there are no more frames, and waited for until it has made the film. */
    @Override
    public void close() {
        try {
            into.close();
            final int ended = making.waitFor();
            if (ended != 0) {
                throw new IllegalStateException("FFmpeg could not make " + film + " (it ended " + ended + "): "
                        + said());
            }
        } catch (final IOException failed) {
            throw new UncheckedIOException("the film could not be finished", failed);
        } catch (final InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("the film was left unfinished", interrupted);
        }
    }

    /** The last of what FFmpeg said, in a line. */
    private String said() {
        try {
            final List<String> lines = Files.readAllLines(log, StandardCharsets.UTF_8);
            return String.join(" / ", lines.subList(Math.max(0, lines.size() - TOLD), lines.size()));
        } catch (final IOException unread) {
            return "(what it said could not be read from " + log + ")";
        }
    }
}
