package com.example.fxsuite.vulkan.worked;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.form.Panel;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Light;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.view.Orbit;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Java's working of a picture where the thing pictured is seen through: a solid face hides the sky behind it; a
 * face that lets half through shows half of its own colour over half of the sky; and beside either, the sky.
 */
class WorkedTest {

    private static final int SIZE = 32;

    private final Laid laid = new Laid();
    private final Surface red = new Surface(new Colour(1, 0, 0), List.of(), new Colour(0, 0, 0), 8);
    private final Mesh face = new Panel(4, 4, 4).formed();
    private final Camera camera = new Orbit(new Vec3(0, 2, 0), 0, 0, 10).camera(40, 1);
    /** An even white light on everything, and no sun. */
    private final Light even = new Light(new Vec3(0, -1, 0), new Vec3(0, 0, 0), new Vec3(1, 1, 1));
    private final Vec3 sky = new Vec3(0.2, 0.4, 0.6);

    @Test
    void aSolidFaceHidesTheSkyAndOneSeenThroughShowsItsOwnColourOverAsMuchOfTheSkyAsItLetsThrough() {
        final Picture solid = new Worked(new Look(laid, red), face, new Vec3(0, 0, 0), camera, even, sky, 1)
                .picture(SIZE, SIZE);
        near(solid, SIZE / 2, SIZE / 2, 255, 0, 0, "solid: red, and nothing of the sky");
        near(solid, 1, 1, 51, 102, 153, "beside it, the sky");
        final Picture clear = new Worked(new Look(laid, red.clear(0.5)), face, new Vec3(0, 0, 0), camera, even, sky,
                1).picture(SIZE, SIZE);
        near(clear, SIZE / 2, SIZE / 2, 153, 51, 77, "seen through: half red, over half the sky");
        near(clear, 1, 1, 51, 102, 153, "beside it, the sky");
    }

    private void near(final Picture picture, final int x, final int y, final int red, final int green,
                      final int blue, final String what) {
        final int[] wanted = {red, green, blue};
        for (int part = 0; part < 3; part++) {
            assertTrue(Math.abs(picture.part(x, y, part) - wanted[part]) <= 1,
                    what + ": " + Integer.toHexString(picture.at(x, y)) + " at " + x + ", " + y);
        }
    }
}
