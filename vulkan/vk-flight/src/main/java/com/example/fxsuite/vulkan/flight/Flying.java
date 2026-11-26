package com.example.fxsuite.vulkan.flight;

import com.example.fxsuite.vulkan.bench.Afoot;
import com.example.fxsuite.vulkan.bench.Roamer;
import com.example.fxsuite.vulkan.bench.Roaming;
import com.example.fxsuite.vulkan.bench.Skies;
import com.example.fxsuite.vulkan.bench.play.Keeping;
import com.example.fxsuite.vulkan.bench.play.Source;
import com.example.fxsuite.vulkan.bench.subject.Bot;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.List;
import java.util.Optional;

/**
 * Flights over a place, for a walk to move: each run a figure on its jets over {@code below}, starting where
 * {@code -Dbench.at=} says (so far across and along, in metres, which way round, in degrees, and how high over what
 * is below, in metres; {@code first} unless said), drawn as the bot {@code -Dbench.bot=} says (the man unless said),
 * flying at the speed nearest {@code -Dbench.flight.speed=} metres a second (12.5 unless said).
 *
 * @param below what lies below the place, ground or water
 * @param first where a run starts unless told: across, along, round and how high, as {@code -Dbench.at=} says it
 */
public record Flying(Below below, String first) implements Roamer {

    private static final double METRE = 100;

    @Override
    public Roaming roaming(final Source source, final Optional<Keeping> keeping, final Skies skies) {
        final String said = System.getProperty("bench.at", "").isBlank() ? first : System.getProperty("bench.at");
        final String[] numbers = said.split(",");
        if (numbers.length != 4) {
            throw new IllegalArgumentException("a flight starts so far across and along, in metres, so far round, in"
                    + " degrees, and so high over what is below, in metres: " + said);
        }
        final double x = Double.parseDouble(numbers[0].trim()) * METRE;
        final double z = Double.parseDouble(numbers[1].trim()) * METRE;
        final double high = below.under(x, z) + Double.parseDouble(numbers[3].trim()) * METRE;
        final Aloft any = new Aloft(new Vec3(x, high, z), Double.parseDouble(numbers[2].trim()), 0);
        final int gear = any.gearFor(Double.parseDouble(System.getProperty("bench.flight.speed", "12.5")));
        return new Flown(source, keeping, Optional.of(skies), below,
                new Bot(new Afoot.Said(System.getProperty("bench.bot", "man")).build()),
                new Aloft(any.feet(), any.yaw(), gear));
    }

    @Override
    public List<String> keys() {
        return List.of("In the card's window, flying only:", "  mouse: turn, and look up or down (Esc lets it go)",
                "  W, S: ahead and back", "  A, D: sideways", "  Space: climb; Ctrl: sink",
                "  Shift: a quarter speed", "  R, V: twice or half as fast (80 cm to 100 m a second)",
                "  Q, E: swing the view round; C: back behind", "  scroll: nearer or further",
                "  kept half a metre over the ground and the water");
    }
}
