package com.example.fxsuite.texture.bench;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.form.Panel;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Averaged;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.view.Orbit;
import com.example.fxsuite.vulkan.worked.Daylight;
import com.example.fxsuite.vulkan.worked.Picture;
import com.example.fxsuite.vulkan.worked.Worked;

/**
 * A swatch of a texture on the lab's bench: a flat upright face of it, so far across and so high, in centimetres,
 * standing in a bench's daylight, looked at square on. Its picture is worked out by Java, from the terms its shader
 * would be written from.
 */
public record Swatch(Surface surface, double across, double up) {

    /** The squares the face is sampled in when something is averaged over it: finer than a brick, and no round size. */
    private static final double SAMPLED = 3.7;
    private static final double FIELD_OF_VIEW = 40;

    /**
     * The swatch seen from {@code from} centimetres off, {@code width} pixels by {@code height}, each pixel counted
     * as covering {@code coarsened} times what it does: 1 for the picture as it is; more, to see what the texture
     * comes to from that many times as far without the swatch shrinking to a dot.
     */
    public Picture seen(final double from, final double coarsened, final int width, final int height) {
        return picture(new Look(new Laid(), surface), from, coarsened, width, height);
    }

    /**
     * The same, in the coarser look made for where a pixel covers {@code covers} centimetres or more: whatever of the
     * texture can no longer be seen there averaged over the face and left out.
     */
    public Picture coarser(final double covers, final double from, final int width, final int height) {
        return picture(new Look(new Laid(), surface).coarser(covers, averaged()), from, 1, width, height);
    }

    /** What works out averages over the swatch's face. */
    public Averaged averaged() {
        return new Averaged(new Panel(across, up, SAMPLED).formed());
    }

    private Picture picture(final Look look, final double from, final double coarsened, final int width,
                            final int height) {
        final Daylight day = new Daylight();
        final Camera camera = new Orbit(new Vec3(0, up / 2, 0), 0, 0, from).camera(FIELD_OF_VIEW, 1);
        // the face drawn as two triangles; worked at twice the size and halved, so each pixel covers half as much
        final Mesh face = new Panel(across, up, Math.max(across, up)).formed();
        return new Worked(look, face, new Vec3(0, 0, 0), camera, day.light(), day.sky(), 2 * coarsened)
                .picture(2 * width, 2 * height).halved();
    }
}
