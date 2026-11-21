package com.example.fxsuite.vulkan.render;

import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Drawing;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.device.Sent;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.form.Packed;
import com.example.fxsuite.vulkan.layout.Filling;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;

/**
 * A form with a look, on the card: the look built and the form sent, once, and then drawn as often as wanted, each
 * time standing wherever it is said to stand. Closed, both are gone from the card.
 */
public final class Formed implements AutoCloseable {

    private static final String PLACED = "placed";
    private static final String COARSENED = "coarsened";

    private final Laid laid;
    private final Drawing drawing;
    private final Sent mesh;

    /** {@code form}, looking as {@code look} says, on {@code card}. */
    public Formed(final Card card, final Look look, final Mesh form) {
        this.laid = look.laid();
        this.drawing = card.drawing(laid, look.vertex(), look.fragment());
        try {
            this.mesh = card.sent(new Packed(laid.point(), form));
        } catch (final RuntimeException refused) {
            drawing.close();
            throw refused;
        }
    }

    /** Drawn by {@code pass}, standing at {@code placed} in the world and seen by {@code camera}, as it is. */
    public void drawn(final Pass pass, final Vec3 placed, final Camera camera) {
        drawn(pass, placed, camera, 1);
    }

    /**
     * The same, looking as it would from {@code coarsened} times as far off: no smaller, but each pixel on it
     * counted as covering that many times as much of its surface, so what would have faded by then has faded.
     */
    public void drawn(final Pass pass, final Vec3 placed, final Camera camera, final double coarsened) {
        drawn(pass, new Mat4(placed), camera, coarsened);
    }

    /**
     * The same, standing as {@code stands} says: turned, stretched and moved from its own frame into the world, in
     * that order or any other, by the one matrix. Where it is as the camera sees it is worked out here in doubles,
     * so a thing far from the world's middle is drawn as steadily as one at it.
     */
    public void drawn(final Pass pass, final Mat4 stands, final Camera camera, final double coarsened) {
        pass.draw(drawing, mesh, new Filling(laid.draw())
                .with(PLACED, camera.placed(new Vec3(0, 0, 0)).times(stands).columns())
                .with(COARSENED, coarsened));
    }

    @Override
    public void close() {
        mesh.close();
        drawing.close();
    }
}
