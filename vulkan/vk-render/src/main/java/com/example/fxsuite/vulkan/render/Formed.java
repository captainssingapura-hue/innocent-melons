package com.example.fxsuite.vulkan.render;

import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;

/**
 * A form with a look, on the card: the look built and the form sent, once, and then drawn as often as wanted, each
 * time standing wherever it is said to stand. One look and one form, for a thing that has just the one of each;
 * where many forms share a look, or many looks a form, each is kept by itself ({@link Looked}, {@link Shaped}).
 * Closed, both are gone from the card.
 */
public final class Formed implements AutoCloseable {

    private final Looked look;
    private final Shaped form;

    /** {@code form}, looking as {@code look} says, on {@code card}. */
    public Formed(final Card card, final Look look, final Mesh form) {
        this.look = new Looked(card, look);
        try {
            this.form = new Shaped(card, look.laid(), form);
        } catch (final RuntimeException refused) {
            this.look.close();
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
     * that order or any other, by the one matrix.
     */
    public void drawn(final Pass pass, final Mat4 stands, final Camera camera, final double coarsened) {
        look.drawn(pass, form, stands, camera, coarsened);
    }

    @Override
    public void close() {
        form.close();
        look.close();
    }
}
