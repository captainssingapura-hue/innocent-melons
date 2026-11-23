package com.example.fxsuite.vulkan.flight;

import com.example.fxsuite.vulkan.bench.Asked;
import com.example.fxsuite.vulkan.bench.Skies;
import com.example.fxsuite.vulkan.bench.play.Keeping;
import com.example.fxsuite.vulkan.bench.play.Replayed;
import com.example.fxsuite.vulkan.bench.play.Scripted;
import com.example.fxsuite.vulkan.bench.play.Source;
import com.example.fxsuite.vulkan.bench.subject.Bot;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A flight, with no card and no window. Flown at a steep hillside, it never comes within half a metre of the
 * ground, and its camera never goes into the hill. Asked to sink, it stops half a metre over the ground and no
 * lower. R doubles its speed and V halves it, and it goes about as far as its speed says. A flight kept and played
 * again is the same flight, step for step. And it is drawn as a bot on its jets.
 */
class FlownTest {

    /** A hillside rising a metre for every two along the world, and level ground across. */
    private final Below hill = (x, z) -> Math.max(0, z / 2);

    @TempDir
    Path folder;

    @Test
    void flownAtAHillsideItKeepsHalfAMetreOverItAndSoDoesItsCamera() {
        final Flown flight = flown(new Scripted("W:8"), Optional.empty(), new Aloft(new Vec3(0, 200, 0), 0, 4));
        while (!flight.over()) {
            flight.stepped(1.0 / 60);
            final Vec3 feet = flight.shown(1).feet();
            assertTrue(feet.y() >= hill.under(feet.x(), feet.z()) + 50 - 1e-9, "feet over the hill: " + feet);
            final Camera camera = flight.camera(1);
            assertTrue(camera.eye().y() >= hill.under(camera.eye().x(), camera.eye().z()) + 50 - 1e-9,
                    "the camera over the hill: " + camera.eye());
        }
        assertTrue(flight.shown(1).feet().z() > 4000, "it flew on up the hill: " + flight.shown(1).feet());
    }

    @Test
    void askedToSinkItStopsHalfAMetreOverTheGround() {
        final Flown flight = flown(new Scripted("CONTROL:6"), Optional.empty(),
                new Aloft(new Vec3(0, 3000, -500), 0, 4));
        while (!flight.over()) {
            flight.stepped(1.0 / 60);
        }
        assertEquals(50, flight.shown(1).feet().y(), 1e-6, "half a metre over the level ground");
        assertFalse(flight.figures(1).isEmpty(), "drawn as a bot on its jets");
    }

    @Test
    void rDoublesItsSpeedAndVHalvesItAndItGoesAsFarAsItsSpeedSays() {
        final Aloft first = new Aloft(new Vec3(0, 500, -100_000), 0, 4);
        assertEquals(2 * first.speed(), new Aloft(first.feet(), 0, 5).speed(), 1e-9);
        assertEquals(10_000, new Aloft(first.feet(), 0, 7).speed(), 1e-9, "the fastest, 100 m a second");
        assertEquals(4, first.gearFor(12.5), "12.5 m a second");
        final Flown faster = flown(new Scripted("R;W:4"), Optional.empty(), first);
        final Flown slower = flown(new Scripted("V;W:4"), Optional.empty(), first);
        while (!faster.over() || !slower.over()) {
            faster.stepped(1.0 / 60);
            slower.stepped(1.0 / 60);
        }
        final double far = faster.shown(1).feet().z() - first.feet().z();
        final double near = slower.shown(1).feet().z() - first.feet().z();
        assertEquals(4, far / near, 0.2, "four times as far, at four times the speed");
        assertEquals(2 * first.speed() * 4, far, 0.15 * far, "about its speed for four seconds");
    }

    @Test
    void aFlightKeptAndPlayedAgainIsTheSameFlightStepForStep() {
        final Path kept = folder.resolve("flight.walk");
        final List<Vec3> first = new ArrayList<>();
        try (Keeping keeping = new Keeping(kept, "a test")) {
            final Flown flight = flown(new Scripted("W:1;W+@40:1;SPACE+D:1;R;W+^-20:1;E:0.5;C"), Optional.of(keeping),
                    new Aloft(new Vec3(0, 300, 0), 0, 3));
            while (!flight.over()) {
                flight.stepped(1.0 / 60);
                first.add(flight.camera(0.5).eye());
            }
        }
        final List<Vec3> again = new ArrayList<>();
        final Flown replayed = flown(new Replayed(kept), Optional.empty(), new Aloft(new Vec3(0, 300, 0), 0, 3));
        while (!replayed.over()) {
            replayed.stepped(1.0 / 60);
            again.add(replayed.camera(0.5).eye());
        }
        assertEquals(first, again, "the camera, frame for frame");
    }

    private Flown flown(final Source source, final Optional<Keeping> keeping, final Aloft first) {
        return new Flown(source, keeping, Optional.of(new Skies(new Asked(1))), hill, new Bot(), first);
    }
}
