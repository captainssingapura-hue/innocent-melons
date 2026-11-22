package com.example.fxsuite.vulkan.worked;

import com.example.fxsuite.vulkan.shade.Light;
import com.example.fxsuite.vulkan.sky.Sky;
import com.example.fxsuite.vulkan.space.Vec3;

/**
 * What a picture is lit by and what is behind everything in it: the light, and the sky's colour (as much red, green
 * and blue as it has). By day, by night ({@link #night}), or at any hour by where the sun and the moon stand
 * ({@link #under}).
 */
public record Daylight(Light light, Vec3 sky) {

    private static final double BRIGHTEST = 255;
    /** How far under the horizon the sun is when the day has all gone, and how far over when it has all come. */
    private static final double TWILIGHT = 6;
    /** The lowest a light is taken to shine from, in degrees above level. */
    private static final double LOWEST = 3;

    /**
     * The same place by night: a dark blue sky; the moon where the sun stood, shining the same way, a dim light
     * bluer than the sun's; and very little light on everything, so that what the moon does not reach is all but
     * black. What mirrors its surroundings has that sky to mirror from above and a ground nearly as dark from
     * below. What gives light of its own is not dimmed by any of this, and stands out.
     */
    public Daylight night() {
        final Vec3 dark = new Vec3(0x0b / BRIGHTEST, 0x10 / BRIGHTEST, 0x26 / BRIGHTEST);
        return new Daylight(new Light(light.sunTowards(),
                new Vec3(0x29 / BRIGHTEST, 0x31 / BRIGHTEST, 0x48 / BRIGHTEST),
                new Vec3(0x0c / BRIGHTEST, 0x0e / BRIGHTEST, 0x17 / BRIGHTEST),
                dark, new Vec3(0x08 / BRIGHTEST, 0x09 / BRIGHTEST, 0x0a / BRIGHTEST)), dark);
    }

    /**
     * This day's place under {@code sky}, with {@code night} for what it is like when the sun is well down: the
     * light and the sky at any hour, worked out from where the sun and the moon stand.
     *
     * <ul>
     *   <li>The sky, the light on everything and what is mirrored from below go evenly from the night's to the
     *       day's as the sun climbs from six degrees under the horizon to six over it, the sky warmed about
     *       sunrise and sunset.</li>
     *   <li>The sun, while it is up, is the one light that casts shadows: none of its light as it clears the
     *       horizon, redder the lower it is, all of it and its own colour from twelve degrees up.</li>
     *   <li>The moon is that light once the sun is down, coming in as the sun sinks to six degrees under: as
     *       bright as the night's moon when it is full and high, less for how little of its face is lit and for
     *       being low.</li>
     *   <li>With neither up there is no such light, and only the little that falls on everything.</li>
     * </ul>
     *
     * <p>The light shines from where the body stands, north being the way the world's third number grows and east
     * the way its first does; a body lower than three degrees shines as from three, so shadows end somewhere.
     */
    public Daylight under(final Sky sky, final Daylight night) {
        final double high = sky.sun().high();
        final double day = eased(-TWILIGHT, TWILIGHT, high);
        final double sunny = eased(0, 2 * TWILIGHT, high);
        final double moony = eased(0, -TWILIGHT, high) * sky.moonLit() * eased(0, 10, sky.moon().high());
        final Vec3 dusk = new Vec3(0xe0 / BRIGHTEST, 0x90 / BRIGHTEST, 0x6a / BRIGHTEST);
        final Vec3 above = mixed(mixed(night.sky, this.sky, day), dusk,
                0.55 * Math.max(0, 1 - Math.abs(high) / 8));
        final Light shining;
        if (sunny > 0) {
            final Vec3 own = light.sun();
            shining = light.sunAt(Math.max(high, LOWEST), sky.sun().round()).shining(new Vec3(own.x() * sunny,
                    own.y() * sunny * (0.55 + 0.45 * sunny), own.z() * sunny * (0.3 + 0.7 * sunny)));
        } else if (moony > 0) {
            shining = night.light.sunAt(Math.max(sky.moon().high(), LOWEST), sky.moon().round())
                    .shining(night.light.sun().times(moony));
        } else {
            shining = light.sunAt(90, 0).shining(new Vec3(0, 0, 0));
        }
        return new Daylight(new Light(shining.sunTowards(), shining.sun(),
                mixed(night.light.ambient(), light.ambient(), day), above,
                mixed(night.light.ground(), light.ground(), day)), above);
    }

    /** From nothing at {@code from} to 1 at {@code to}, gently at each end; nothing before and 1 after. */
    private double eased(final double from, final double to, final double at) {
        final double t = Math.clamp((at - from) / (to - from), 0, 1);
        return t * t * (3 - 2 * t);
    }

    /** {@code one} where {@code share} is nothing, {@code other} where it is 1, and between them between. */
    private Vec3 mixed(final Vec3 one, final Vec3 other, final double share) {
        return one.times(1 - share).plus(other.times(share));
    }

    /**
     * A bench's day, as the engine's bench kit has it for its own view: a pale sky, a soft sun from above and a
     * little to one side, and a grey light over all.
     */
    public Daylight() {
        this(new Light(new Vec3(0.45, -1, 0.35),
                        new Vec3(0x9a / BRIGHTEST, 0x97 / BRIGHTEST, 0x8f / BRIGHTEST),
                        new Vec3(0x8e / BRIGHTEST, 0x95 / BRIGHTEST, 0x9c / BRIGHTEST),
                        // what is mirrored: that sky from above, and from below a dull ground of no colour to speak of
                        new Vec3(0xc9 / BRIGHTEST, 0xdb / BRIGHTEST, 0xe8 / BRIGHTEST),
                        new Vec3(0x5c / BRIGHTEST, 0x5f / BRIGHTEST, 0x58 / BRIGHTEST)),
                new Vec3(0xc9 / BRIGHTEST, 0xdb / BRIGHTEST, 0xe8 / BRIGHTEST));
    }
}
