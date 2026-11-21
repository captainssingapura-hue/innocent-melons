package com.example.fxsuite.vulkan.shade;

import com.example.fxsuite.vulkan.layout.Layout;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Direction;
import com.example.fxsuite.vulkan.term.Given;
import com.example.fxsuite.vulkan.term.Point;
import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Sample;
import com.example.fxsuite.vulkan.term.Surface;

/**
 * A surface as the light falls on it, written as terms like the surface itself, so the shader's lighting and Java's
 * are one description: the surface's colour lit by the light on everything and by the sun as far as the surface
 * faces it, the surface's shine where the sun is mirrored toward the eye, and whatever light it gives of its own.
 * What it reads of a point is what the vertex shader hands on ({@code passed}); what it reads of the light is the
 * frame's block ({@code frame}), reached in the shader through {@code held}.
 */
public record Lit(Layout passed, Layout frame, String held) {

    private static final String FACES = "faces";
    private static final String FROM = "from";
    private static final String SUN_TOWARDS = "sunTowards";
    private static final String SUN = "sun";
    private static final String AMBIENT = "ambient";
    /** A surface turned from the sun by any more than a hair has no highlight at all. */
    private static final double SHARPLY = 1000;

    public Lit {
        // asked now: a layout without what the lighting reads is refused here, by name
        passed.field(FACES);
        passed.field(FROM);
        frame.field(SUN_TOWARDS);
        frame.field(SUN);
        frame.field(AMBIENT);
    }

    /** {@code surface}'s colour at a point, as the light leaves it. */
    public Colour of(final Surface surface) {
        final Direction faces = new Direction(new Given(FACES, 3)).unit();
        final Direction toSun = new Direction(new Given(held + "." + SUN_TOWARDS, 3)).unit().negated();
        final Colour sun = new Colour(new Given(held + "." + SUN, 3));
        final Colour ambient = new Colour(new Given(held + "." + AMBIENT, 3));
        final Real falls = faces.dot(toSun).most(new Real(0));
        final Direction toEye = new Point(new Given(FROM, 3)).back();
        final Real glint = faces.mirrored(toSun).dot(toEye).most(new Real(0)).raised(surface.tightness())
                .times(falls.times(SHARPLY).least(new Real(1)));
        return surface.colour().lit(ambient.plus(sun.scaled(falls))).plus(surface.shine().lit(sun).scaled(glint))
                .plus(surface.glow());
    }

    /** {@code sample}, and what the lighting reads there: the way the surface faces, where the point is, the light. */
    public Sample given(final Sample sample, final Vec3 faces, final Vec3 from, final Light light) {
        return sample.with(FACES, faces.x(), faces.y(), faces.z())
                .with(FROM, from.x(), from.y(), from.z())
                .with(held + "." + SUN_TOWARDS, light.sunTowards().x(), light.sunTowards().y(), light.sunTowards().z())
                .with(held + "." + SUN, light.sun().x(), light.sun().y(), light.sun().z())
                .with(held + "." + AMBIENT, light.ambient().x(), light.ambient().y(), light.ambient().z());
    }
}
