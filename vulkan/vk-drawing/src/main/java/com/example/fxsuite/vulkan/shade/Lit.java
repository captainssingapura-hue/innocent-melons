package com.example.fxsuite.vulkan.shade;

import com.example.fxsuite.vulkan.layout.Layout;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Constant;
import com.example.fxsuite.vulkan.term.Direction;
import com.example.fxsuite.vulkan.term.Flakes;
import com.example.fxsuite.vulkan.term.Given;
import com.example.fxsuite.vulkan.term.Point;
import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Sample;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Tint;

/**
 * A surface as the light falls on it, written as terms like the surface itself, so the shader's lighting and Java's
 * are one description: the surface's colour lit by the light on everything and by the sun as far as the surface
 * faces it, the surface's shine where the sun is mirrored toward the eye, whatever light it gives of its own, and
 * as much of the sky above and the ground below as it mirrors; and, of a paint with flakes of metal in it, the
 * flakes' own wide shine, and its colour duller seen edge on. Up is the way the world's second number grows.
 * What it reads of a point is what the vertex shader hands on ({@code passed}); what it reads of the light is the
 * frame's block ({@code frame}), reached in the shader through {@code held}.
 *
 * <p>How much of the sun reaches a point is given to it as one number, {@link #sunlit}: all of it, or none where
 * something stands between the point and the sun, or part at a shadow's edge. The shader looks it up in the sun's
 * own picture of the frame; Java, which has no such picture, takes it to be all of the sun.
 */
public record Lit(Layout passed, Layout frame, String held) {

    private static final String FACES = "faces";
    private static final String FROM = "from";
    private static final String SUN_TOWARDS = "sunTowards";
    private static final String SUN = "sun";
    private static final String AMBIENT = "ambient";
    private static final String SKY = "sky";
    private static final String GROUND = "ground";
    private static final String SUNLIT = "sunlit";
    /** A surface turned from the sun by any more than a hair has no highlight at all. */
    private static final double SHARPLY = 1000;
    /**
     * How quickly, in what is mirrored, the ground gives way to the sky about the horizon: at 1, evenly from
     * straight down to straight up; more, and it is all sky or all ground sooner, so faces turned a little apart
     * mirror visibly different light, as polished things do.
     */
    private static final double HORIZON = 3;
    /** How quickly a clear skin comes to mirror everything as it is seen more nearly edge on: the usual fifth power. */
    private static final double EDGE = 5;
    /**
     * How quickly the flakes that catch the sun give way to others as a thing is turned or the eye moves: so many
     * times over, for a mirrored view swung from one side to the other.
     */
    private static final double TWINKLE = 25;

    public Lit {
        // asked now: a layout without what the lighting reads is refused here, by name
        passed.field(FACES);
        passed.field(FROM);
        frame.field(SUN_TOWARDS);
        frame.field(SUN);
        frame.field(AMBIENT);
        frame.field(SKY);
        frame.field(GROUND);
    }

    /** {@code surface}'s colour at a point, as the light leaves it. */
    public Colour of(final Surface surface) {
        final Direction faces = new Direction(new Given(FACES, 3)).unit();
        final Direction toSun = new Direction(new Given(held + "." + SUN_TOWARDS, 3)).unit().negated();
        final Colour sun = new Colour(new Given(held + "." + SUN, 3));
        final Colour ambient = new Colour(new Given(held + "." + AMBIENT, 3));
        final Real reaches = new Real(new Given(SUNLIT, 1));
        final Real falls = faces.dot(toSun).most(new Real(0));
        final Direction toEye = new Point(new Given(FROM, 3)).back();
        final Real glint = faces.mirrored(toSun).dot(toEye).most(new Real(0)).raised(surface.tightness())
                .times(falls.times(SHARPLY).least(new Real(1))).times(reaches);
        // what a mirror would show at the point: the sky where it sends the eye upward, the ground where downward
        final Direction up = new Direction(new Constant(new double[] {0, 1, 0}));
        final Real upward = faces.mirrored(toEye).dot(up).times(HORIZON / 2).plus(new Real(0.5)).held(0, 1);
        final Colour around = new Colour(new Given(held + "." + GROUND, 3))
                .mixed(new Colour(new Given(held + "." + SKY, 3)), upward);
        final Colour mirrored = mirrored(surface, faces, toEye);
        // with flakes of metal in it, its own colour is duller seen edge on; with none, it is as it is
        final Real square = faces.dot(toEye).most(new Real(0));
        final Colour unflaked = surface.flakes().map(flakes -> surface.colour().scaled(flakes.dulled(square)))
                .orElse(surface.colour());
        // seen through, its own colour shows only as much as is not let through
        final Colour own = surface.seenThrough() ? unflaked.scaled(new Real(1 - surface.through())) : unflaked;
        final Colour whole = own.lit(ambient.plus(sun.scaled(falls.times(reaches))))
                .plus(surface.shine().lit(sun).scaled(glint))
                .plus(surface.glow()).plus(mirrored.lit(around));
        final Real sunward = falls.times(SHARPLY).least(new Real(1)).times(reaches);
        return surface.flakes().map(flakes -> whole.plus(flakes.colour().lit(sun)
                .scaled(shining(flakes, faces, toSun, toEye, sunward)))).orElse(whole);
    }

    /**
     * How much of what is behind {@code surface} it hides at a point: all of it, unless the surface is seen through.
     * Then it hides what it does not let through, and it lets through less the more it mirrors: so a pane seen
     * square on shows what is behind it, and seen edge on shows the sky in it and little else. This is what the
     * surface's colour is laid over what is already drawn by: its own light in full, and what was there as far as
     * it is not hidden.
     */
    public Real hides(final Surface surface) {
        if (!surface.seenThrough()) {
            return new Real(1);
        }
        final Direction faces = new Direction(new Given(FACES, 3)).unit();
        final Direction toEye = new Point(new Given(FROM, 3)).back();
        return new Real(1).less(new Real(1).less(mirrored(surface, faces, toEye).evened()).times(surface.through()));
    }

    /**
     * How much of its surroundings {@code surface} mirrors at a point that faces {@code faces}, seen from
     * {@code toEye}. A clear hard skin mirrors more the more nearly it is seen edge on: little square on, nearly
     * all at a glance.
     */
    private Colour mirrored(final Surface surface, final Direction faces, final Direction toEye) {
        final Real glancing = new Real(1).less(faces.dot(toEye).most(new Real(0))).raised(EDGE);
        return surface.glance().map(atGlance -> surface.mirror().mixed(atGlance, glancing))
                .orElse(surface.mirror());
    }

    /**
     * How much of {@code flakes}' own shine leaves a point that faces {@code faces}: wide about where the sun is
     * mirrored toward the eye, and nothing where the sun does not reach, which {@code sunward} says. Which flakes
     * catch the sun goes by the way a mirror there would send the eye, so they change as the thing or the eye turns.
     */
    private Real shining(final Flakes flakes, final Direction faces, final Direction toSun, final Direction toEye,
                         final Real sunward) {
        final Real wide = faces.mirrored(toSun).dot(toEye).most(new Real(0)).raised(flakes.spread());
        final Direction askew = new Direction(new Constant(new double[] {0.64, 0.48, 0.6}));
        return wide.times(sunward).times(flakes.caught(faces.mirrored(toEye).dot(askew).times(TWINKLE)));
    }

    /** What the shader calls how much of the sun reaches the point: a number it has to have worked out first. */
    public String sunlit() {
        return SUNLIT;
    }

    /**
     * {@code sample}, and what the lighting reads there: the way the surface faces, where the point is, the light.
     * The thing drawn is white, for a surface that takes its colour from the thing.
     */
    public Sample given(final Sample sample, final Vec3 faces, final Vec3 from, final Light light) {
        return given(sample, faces, from, light, new Vec3(1, 1, 1));
    }

    /** The same, the thing drawn being of the colour {@code tint}: so much red, green and blue. */
    public Sample given(final Sample sample, final Vec3 faces, final Vec3 from, final Light light, final Vec3 tint) {
        return new Tint().given(sample, tint.x(), tint.y(), tint.z())
                .with(FACES, faces.x(), faces.y(), faces.z())
                .with(FROM, from.x(), from.y(), from.z())
                .with(held + "." + SUN_TOWARDS, light.sunTowards().x(), light.sunTowards().y(), light.sunTowards().z())
                .with(held + "." + SUN, light.sun().x(), light.sun().y(), light.sun().z())
                .with(held + "." + AMBIENT, light.ambient().x(), light.ambient().y(), light.ambient().z())
                .with(held + "." + SKY, light.sky().x(), light.sky().y(), light.sky().z())
                .with(held + "." + GROUND, light.ground().x(), light.ground().y(), light.ground().z())
                .with(SUNLIT, 1);
    }
}
