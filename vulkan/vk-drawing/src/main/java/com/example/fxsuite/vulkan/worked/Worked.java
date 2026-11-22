package com.example.fxsuite.vulkan.worked;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.shade.Light;
import com.example.fxsuite.vulkan.shade.Lit;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Clip;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Reckoning;
import com.example.fxsuite.vulkan.term.Sample;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.view.Camera;

/**
 * Java's working of a subject's picture, pixel by pixel, with no card: the subject's form standing at a place, seen
 * by a camera, lit by a light, the sky behind it. It does what the two shaders do, from the same description: places
 * each corner as the vertex shader would, and works the look's expression out at each pixel as the fragment shader
 * would, with how much of the surface the pixel covers reckoned as the card reckons it, from the pixels beside it.
 * Slow, and for looking at and checking against, not for playing.
 *
 * <p>{@code coarsened} makes each pixel count for so many times as much of the surface as it covers: 1 for the
 * picture as it is; more, to see what the terms come to from that many times as far off without the subject
 * shrinking to a dot. {@code tint} is the thing's own colour, for a look whose surface takes its colour from the
 * thing.
 *
 * <p>A subject whose surface is seen through shows the sky through it, and only the sky: the nearest of its faces
 * is laid over the sky as the card would lay it, and nothing of the subject is seen through itself.
 */
public record Worked(Look look, Mesh form, Vec3 placed, Camera camera, Light light, Vec3 sky, double coarsened,
                     Vec3 tint) {

    /** A white thing: for a look whose surface takes no colour from the thing, which is nearly every look. */
    public Worked(final Look look, final Mesh form, final Vec3 placed, final Camera camera, final Light light,
                  final Vec3 sky, final double coarsened) {
        this(look, form, placed, camera, light, sky, coarsened, new Vec3(1, 1, 1));
    }

    /** The picture, {@code width} pixels by {@code height}. */
    public Picture picture(final int width, final int height) {
        final Drawing drawing = new Drawing(width, height, sky);
        final Mat4 seen = camera.seen((double) width / height);
        final Reckoning lit = new Reckoning(look.lit());
        final Reckoning hides = new Reckoning(look.hides());
        for (int triangle = 0; triangle < form.triangles(); triangle++) {
            if (ahead(triangle, seen)) {
                final Flat flat = new Flat(corner(triangle, 0, seen, width, height),
                        corner(triangle, 1, seen, width, height), corner(triangle, 2, seen, width, height));
                if (flat.facesCamera()) {
                    draw(flat, lit, hides, drawing, width, height);
                }
            }
        }
        return drawing.picture();
    }

    /** Whether all of {@code triangle} is ahead of where the camera begins to see; one that is not is left out. */
    private boolean ahead(final int triangle, final Mat4 seen) {
        for (int which = 0; which < 3; which++) {
            if (seen.clip(form.corner(triangle, which).plus(camera.from(placed))).w() <= camera.near()) {
                return false;
            }
        }
        return true;
    }

    /** Corner {@code which} of {@code triangle}, placed as the vertex shader places it, and where it falls. */
    private Corner corner(final int triangle, final int which, final Mat4 seen, final int width, final int height) {
        final Vec3 at = form.corner(triangle, which);
        final Vec3 from = at.plus(camera.from(placed));
        final Clip clip = seen.clip(from);
        final Vec3 shown = clip.shown();
        return new Corner(form.on(triangle, which), form.facing(triangle, which), from, (shown.x() + 1) / 2 * width,
                (shown.y() + 1) / 2 * height, shown.z(), clip.w());
    }

    /** Every pixel whose middle {@code flat} holds, drawn where nothing nearer is. */
    private void draw(final Flat flat, final Reckoning lit, final Reckoning hides, final Drawing drawing,
                      final int width, final int height) {
        final int left = (int) Math.max(0, Math.floor(flat.left()));
        final int right = (int) Math.min(width - 1, Math.ceil(flat.right()));
        final int top = (int) Math.max(0, Math.floor(flat.top()));
        final int bottom = (int) Math.min(height - 1, Math.ceil(flat.bottom()));
        for (int y = top; y <= bottom; y++) {
            for (int x = left; x <= right; x++) {
                final double across = x + 0.5;
                final double down = y + 0.5;
                if (flat.holds(across, down) && drawing.shows(x, y, flat.depth(across, down))) {
                    drawing.draw(x, y, flat.depth(across, down), colour(flat, lit, hides, across, down));
                }
            }
        }
    }

    /** The look's colour at the point ({@code x}, {@code y}) of the picture, as the fragment shader would have it. */
    private Vec3 colour(final Flat flat, final Reckoning lit, final Reckoning hides, final double x,
                        final double y) {
        final Corner here = flat.at(x, y);
        final Vec3 beside = spread(flat.at(x + 1, y).at().minus(here.at()));
        final Vec3 below = spread(flat.at(x, y + 1).at().minus(here.at()));
        final double covers = beside.plus(below).length() * coarsened;
        final Lit lighting = look.lighting();
        final Sample sample = lighting.given(new Seen().sample(here.at(), covers), here.faces(), here.from(), light,
                tint);
        final double[] colour = lit.at(sample);
        if (!look.mixed()) {
            return new Vec3(colour[0], colour[1], colour[2]);
        }
        // seen through: its own light, no brighter than the brightest, over as much of the sky as it does not hide
        final double shows = 1 - Math.clamp(hides.at(sample)[0], 0, 1);
        return new Vec3(Math.clamp(colour[0], 0, 1), Math.clamp(colour[1], 0, 1), Math.clamp(colour[2], 0, 1))
                .plus(sky.times(shows));
    }

    /** How far apart two neighbouring pixels' points are, each way, whichever way round. */
    private Vec3 spread(final Vec3 apart) {
        return new Vec3(Math.abs(apart.x()), Math.abs(apart.y()), Math.abs(apart.z()));
    }
}
