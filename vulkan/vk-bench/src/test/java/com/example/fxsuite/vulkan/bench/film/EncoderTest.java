package com.example.fxsuite.vulkan.bench.film;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * A film made by FFmpeg, where this computer has it: a second of frames at 30 a second makes a film of 30 frames,
 * of that size, as FFmpeg itself says when asked. A frame of the wrong size is refused; so are sizes that are not
 * even; and an encoder that cannot be started says how to say where it is.
 */
class EncoderTest {

    private static final String FFMPEG = System.getProperty("bench.film.ffmpeg", "ffmpeg");

    @TempDir
    Path folder;

    @Test
    void aSecondOfFramesIsAFilmOfThatManyFramesOfThatSize() throws IOException, InterruptedException {
        assumeTrue(found(), "FFmpeg is on this computer");
        final Path film = folder.resolve("a second.mp4");
        try (Encoder encoder = new Encoder(film, 64, 36, 30, FFMPEG)) {
            final int[] frame = new int[64 * 36];
            for (int at = 0; at < 30; at++) {
                Arrays.fill(frame, 0xff000000 | at * 8 << 16 | 0x4080);
                encoder.frame(frame);
            }
            assertEquals(30, encoder.frames());
            assertThrows(IllegalArgumentException.class, () -> encoder.frame(new int[10]));
        }
        assertTrue(Files.size(film) > 0);
        final Process asked = new ProcessBuilder(FFMPEG.replace("ffmpeg", "ffprobe"), "-v", "error",
                "-count_frames", "-show_entries", "stream=width,height,nb_read_frames", "-of", "csv=p=0",
                film.toString()).redirectErrorStream(true).start();
        final String said = new String(asked.getInputStream().readAllBytes(), StandardCharsets.UTF_8).strip();
        asked.waitFor();
        assertEquals("64,36,30", said);
    }

    @Test
    void sizesThatAreNotEvenAndAnEncoderThatCannotBeStartedAreRefused() {
        assertThrows(IllegalArgumentException.class, () -> new Encoder(folder.resolve("odd.mp4"), 63, 36, 30,
                FFMPEG));
        final RuntimeException none = assertThrows(RuntimeException.class,
                () -> new Encoder(folder.resolve("none.mp4"), 64, 36, 30, "no-such-encoder-anywhere"));
        assertTrue(none.getMessage().contains("-Dbench.film.ffmpeg="), none.getMessage());
    }

    /** Whether FFmpeg can be started here. */
    private boolean found() {
        try {
            return new ProcessBuilder(FFMPEG, "-version").redirectErrorStream(true).start().waitFor() == 0;
        } catch (final IOException | InterruptedException absent) {
            return false;
        }
    }
}
