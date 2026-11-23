package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.vulkan.bench.film.Encoder;
import com.example.fxsuite.vulkan.bench.film.Shots;
import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Refused;
import com.example.fxsuite.vulkan.device.Target;
import com.example.fxsuite.vulkan.view.Camera;

import java.nio.file.Path;
import java.util.List;

/**
 * A run made into a film, drawn offscreen at the run's own time: {@code rate} frames to a second of the run,
 * however long each takes to draw, so the film is as smooth as its rate wherever it was made. Each frame is drawn
 * as the run stands then, between its last two steps as a window draws it, seen from wherever the shots put the
 * camera; read back; and handed to the encoder. The film is the run's from its first step to its last, and the
 * validation layer is heard after every frame.
 */
public record Filming(double rate, int width, int height, Shots shots) {

    /** How a frame is drawn: on {@code target}, the avatar where {@code shown} has it, as {@code camera} sees it. */
    public interface Drawing {
        void drawn(Target target, Avatar shown, Camera camera);
    }

    public Filming {
        if (!(rate > 0)) {
            throw new IllegalArgumentException("a film has some frames a second: " + rate);
        }
    }

    /** The encoder for a film made in {@code film} by the FFmpeg that {@code ffmpeg} names. */
    public Encoder encoder(final Path film, final String ffmpeg) {
        return new Encoder(film, width, height, rate, ffmpeg);
    }

    /** {@code run}, from where it stands to its end, drawn on {@code card} by {@code drawing} into {@code encoder}. */
    public void filmed(final Card card, final Run run, final Encoder encoder, final Drawing drawing) {
        final Shots ending = shots.endingAt(run.seconds());
        try (Canvas canvas = card.canvas(width, height)) {
            double blend = 1;
            long frame = 0;
            while (true) {
                final Avatar shown = run.shown(blend);
                final Camera camera = ending.camera(frame / rate, run.head(shown), shown.body().yaw(),
                        run.following().camera(run.inTheWay(), blend));
                drawing.drawn(canvas, shown, camera);
                encoder.frame(canvas.pixels());
                final List<String> said = card.heard();
                if (!said.isEmpty()) {
                    throw new Refused("the validation layer", String.join("\n", said));
                }
                frame++;
                if (run.over()) {
                    return;
                }
                blend = run.stepped(1 / rate);
            }
        }
    }
}
