package com.example.fxsuite.vulkan.flight;

import com.example.fxsuite.vulkan.bench.Roamer;
import com.example.fxsuite.vulkan.bench.Roaming;
import com.example.fxsuite.vulkan.bench.Skies;
import com.example.fxsuite.vulkan.bench.play.Keeping;
import com.example.fxsuite.vulkan.bench.play.Source;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Flights of a tiltrotor over a place, for a walk to move: each run the craft {@code airframe} makes, standing on
 * what is below where {@code -Dbench.at=} says (so far across and along, in metres, and which way round, in degrees;
 * {@code first} unless said), its nacelles up and its power idle, to be flown from the keyboard alone.
 *
 * @param below    what lies below the place, ground or water
 * @param first    where a run starts unless told: across, along and round, as {@code -Dbench.at=} says it
 * @param airframe the craft flown, made afresh for each run
 */
public record Piloting(Below below, String first, Supplier<Airframe> airframe) implements Roamer {

    private static final double METRE = 100;

    @Override
    public Roaming roaming(final Source source, final Optional<Keeping> keeping, final Skies skies) {
        final String said = System.getProperty("bench.at", "").isBlank() ? first : System.getProperty("bench.at");
        final String[] numbers = said.split(",");
        if (numbers.length < 3) {
            throw new IllegalArgumentException("a craft starts so far across and along, in metres, and so far round,"
                    + " in degrees: " + said);
        }
        final double x = Double.parseDouble(numbers[0].trim()) * METRE;
        final double z = Double.parseDouble(numbers[1].trim()) * METRE;
        return new Piloted(source, keeping, Optional.of(skies), below, airframe.get(),
                new Tiltrotor(new Vec3(x, below.under(x, z) + 50, z), Double.parseDouble(numbers[2].trim())));
    }

    @Override
    public List<String> keys() {
        return List.of("In the card's window, the craft from the keyboard:", "  W, S: more and less power",
                "  up, down: nose down and up", "  left, right: roll (level again when let go)",
                "  A, D: heading", "  Q, E: nacelles up and down", "  1, 2, 3: nacelles up, half way, down",
                "  C: cockpit or behind", "  T: one, four or sixteen times as fast", "  scroll: nearer or further",
                "The mouse clicks the panel's buttons, at the foot on the right.");
    }
}
