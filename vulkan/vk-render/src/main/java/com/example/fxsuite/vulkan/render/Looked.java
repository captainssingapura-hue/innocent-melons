package com.example.fxsuite.vulkan.render;

import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Copies;
import com.example.fxsuite.vulkan.device.Drawing;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.layout.Filling;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;

/**
 * A look on the card, built once, to draw any form with: so one material is drawn on many shapes, and many shapes
 * need no look of their own each. A form is drawn with it standing wherever it is said to stand. A look whose
 * surface is seen through is built to be mixed with what is already drawn. Closed, it is gone from the card.
 */
public final class Looked implements AutoCloseable {

    private static final String PLACED = "placed";
    private static final String COARSENED = "coarsened";
    private static final String TINT = "tint";

    private final Laid laid;
    private final Drawing drawing;

    /** {@code look}, on {@code card}. */
    public Looked(final Card card, final Look look) {
        this.laid = look.laid();
        this.drawing = look.mixed() ? card.mixing(laid, look.vertex(), look.fragment())
                : card.drawing(laid, look.vertex(), look.fragment());
    }

    /**
     * Whether what is drawn with it is seen through. Such things are drawn after everything that is not, and of
     * several, the furthest from the eye first: what draws refuses them in any other order than after the rest.
     * They leave nothing in the sun's picture, so they cast no shadow.
     */
    public boolean seenThrough() {
        return drawing.mixes();
    }

    /** The layouts it was built from: what a form drawn with it has to be laid out by. */
    public Laid laid() {
        return laid;
    }

    /**
     * {@code form} drawn with this look by {@code pass}, standing as {@code stands} says: turned, stretched and
     * moved from its own frame into the world by the one matrix; seen by {@code camera}; and looking as it would
     * from {@code coarsened} times as far off, no smaller, but each pixel on it counted as covering that many times
     * as much of its surface. Where it is as the camera sees it is worked out here in doubles, so a thing far from
     * the world's middle is drawn as steadily as one at it.
     */
    public void drawn(final Pass pass, final Shaped form, final Mat4 stands, final Camera camera,
                      final double coarsened) {
        drawn(pass, form, stands, camera, coarsened, new Vec3(1, 1, 1));
    }

    /**
     * The same, the thing drawn being of the colour {@code tint}, as much red, green and blue as it has: for a look
     * whose surface takes its colour from the thing, as paint does. A look that takes none is not changed by it.
     */
    public void drawn(final Pass pass, final Shaped form, final Mat4 stands, final Camera camera,
                      final double coarsened, final Vec3 tint) {
        pass.draw(drawing, form.sent(), new Filling(laid.draw())
                .with(PLACED, camera.placed(new Vec3(0, 0, 0)).times(stands).columns())
                .with(COARSENED, coarsened)
                .with(TINT, tint.x(), tint.y(), tint.z()));
    }

    /**
     * {@code form} drawn as every one of {@code copies} in one draw, each where its copy takes it from its own frame,
     * the whole standing as {@code stands} says, otherwise as above. Copies placed relative to a point near them, and
     * that point said by {@code stands}, are drawn as steadily far from the world's middle as near it.
     */
    public void drawn(final Pass pass, final Shaped form, final Mat4 stands, final Camera camera,
                      final double coarsened, final Vec3 tint, final Copies copies) {
        pass.draw(drawing, form.sent(), new Filling(laid.draw())
                .with(PLACED, camera.placed(new Vec3(0, 0, 0)).times(stands).columns())
                .with(COARSENED, coarsened)
                .with(TINT, tint.x(), tint.y(), tint.z()), copies);
    }

    @Override
    public void close() {
        drawing.close();
    }
}
