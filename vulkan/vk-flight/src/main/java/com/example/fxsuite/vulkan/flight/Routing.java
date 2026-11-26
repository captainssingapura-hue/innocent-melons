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
 * Flights of a tiltrotor along a route by itself ({@link Routed}), for a walk to move: no keys, the run ending with
 * the route, so it is filmed as a script is.
 *
 * @param route    the route, across and along, its first point first
 * @param speed    how fast it flies, in centimetres a second
 * @param above    how high over the highest of what lies below the next stretch, in centimetres
 * @param below    what lies below
 * @param airframe the craft flown, made afresh for each run
 */
public record Routing(List<Vec3> route, double speed, double above, Below below, Supplier<Airframe> airframe)
        implements Roamer {

    public Routing {
        route = List.copyOf(route);
    }

    @Override
    public Roaming roaming(final Source source, final Optional<Keeping> keeping, final Skies skies) {
        return new Routed(route, speed, above, below, airframe.get(), Optional.of(skies));
    }

    @Override
    public List<String> keys() {
        return List.of("The craft flies its route by itself:", String.format(java.util.Locale.ROOT,
                "  %.0f km an hour, %.0f m over what lies ahead", speed * 0.036, above / 100));
    }
}
