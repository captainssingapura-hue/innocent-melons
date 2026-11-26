package com.example.fxsuite.vulkan.afloat;

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
 * Cruises of a boat on a place's waters, for a walk to move: each run the boat {@code vessel} makes, floating where
 * {@code -Dbench.at=} says (so far across and along, in metres, and which way round, in degrees; {@code first} unless
 * said), or, if there is no water deep enough there, at the nearest water that is, searched for outward in rings.
 *
 * @param waters where the place's water lies
 * @param first  where a run starts unless told: across, along and round, as {@code -Dbench.at=} says it
 * @param vessel the boat driven, made afresh for each run
 */
public record Cruising(Waters waters, String first, Supplier<Vessel> vessel) implements Roamer {

    private static final double METRE = 100;
    /** How far apart the search for water steps, and how far out it goes, in centimetres. */
    private static final double SEARCH_STEP = 1_000;
    private static final int SEARCH_RINGS = 300;
    /** How much deeper than its draught the water must be where it starts. */
    private static final double AFLOAT = 1.5;

    @Override
    public Roaming roaming(final Source source, final Optional<Keeping> keeping, final Skies skies) {
        final String said = System.getProperty("bench.at", "").isBlank() ? first : System.getProperty("bench.at");
        final String[] numbers = said.split(",");
        final Vec3 asked = new Vec3(Double.parseDouble(numbers[0].trim()) * METRE, 0,
                Double.parseDouble(numbers[1].trim()) * METRE);
        final Vessel boat = vessel.get();
        final Vec3 wet = water(asked, boat.draught() * AFLOAT);
        return new Cruised(source, keeping, Optional.of(skies), waters, boat,
                new Afloat(new Vec3(wet.x(), waters.top(wet.x(), wet.z()), wet.z()),
                        numbers.length > 2 ? Double.parseDouble(numbers[2].trim()) : 0));
    }

    /** The nearest point to {@code asked} with water {@code deep} or deeper, searched for outward in square rings. */
    public Vec3 water(final Vec3 asked, final double deep) {
        for (int ring = 0; ring <= SEARCH_RINGS; ring++) {
            Vec3 nearest = null;
            for (int i = -ring; i <= ring; i++) {
                for (int j = -ring; j <= ring; j++) {
                    final Vec3 at = asked.plus(new Vec3(i * SEARCH_STEP, 0, j * SEARCH_STEP));
                    final boolean nearer = nearest == null || at.minus(asked).length() < nearest.minus(asked).length();
                    if (Math.max(Math.abs(i), Math.abs(j)) == ring && nearer && waters.deep(at.x(), at.z()) >= deep) {
                        nearest = at;
                    }
                }
            }
            if (nearest != null) {
                return nearest;
            }
        }
        throw new IllegalStateException("no water deep enough for a boat within " + SEARCH_RINGS * SEARCH_STEP / 100
                + " m of " + asked);
    }

    @Override
    public List<String> keys() {
        return List.of("In the card's window, the boat from the keyboard:", "  W, S: throttle open and closed",
                "  A, D: wheel to the left and right", "  Space: wheel amidships", "  C: the helm or behind",
                "  T: one, four or sixteen times as fast", "  scroll: nearer or further",
                "The mouse clicks the panel's buttons, at the foot on the right.");
    }
}
