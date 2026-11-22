package com.example.fxsuite.vulkan.sky;

import java.time.Instant;

/**
 * The sun and the moon over a place, worked out from where the place is and what the moment is, and from nothing
 * else: nothing here is looked up or stored. North is north and east is east; whoever draws by this says which way
 * of their world is north.
 *
 * <p>The air's bending of light near the horizon is left out, so the sun is counted as setting some two minutes
 * before it is seen to; and the moon is counted from where the place is on the Earth, not from the Earth's middle.
 */
public record Heavens(Site site) {

    /** The sky over the place at {@code when}. */
    public Sky at(final Instant when) {
        final Among sun = new Sun().at(when);
        final Moon reckoned = new Moon();
        final Among moon = reckoned.at(when);
        final Stands middle = moon.stands(site, when);
        final Stands seen = new Stands(middle.high()
                - reckoned.lowering(when) * Math.cos(Math.toRadians(middle.high())), middle.round());
        // lit as far as the moon is round from the sun: none of its face when it is beside the sun, all when opposite
        final double lit = (1 - Math.cos(Math.toRadians(moon.from(sun)))) / 2;
        return new Sky(sun.stands(site, when), seen, lit);
    }
}
