package com.example.fxsuite.vulkan.device;

import com.example.fxsuite.vulkan.form.Packed;
import com.example.fxsuite.vulkan.layout.Layout;

import static org.lwjgl.vulkan.VK13.VK_BUFFER_USAGE_INDEX_BUFFER_BIT;
import static org.lwjgl.vulkan.VK13.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT;

/**
 * A mesh sent to the card, once: its points' bytes and its corners', there to be drawn from as often as wanted, and
 * the layout its points were packed by. Closed, it is gone from the card.
 */
public final class Sent implements AutoCloseable {

    private final Layout layout;
    private final Bytes points;
    private final Bytes corners;
    private final int count;

    Sent(final Reach reach, final Packed mesh) {
        this.layout = mesh.layout();
        this.count = mesh.mesh().corners().size();
        this.points = new Bytes(reach, "a mesh's points", mesh.points().remaining(),
                VK_BUFFER_USAGE_VERTEX_BUFFER_BIT);
        this.corners = new Bytes(reach, "a mesh's corners", mesh.corners().remaining(),
                VK_BUFFER_USAGE_INDEX_BUFFER_BIT);
        points.filled(mesh.points());
        corners.filled(mesh.corners());
    }

    /** How its points are laid out. */
    public Layout layout() {
        return layout;
    }

    /** How many corners it has: three to a triangle. */
    public int count() {
        return count;
    }

    Bytes points() {
        return points;
    }

    Bytes corners() {
        return corners;
    }

    @Override
    public void close() {
        corners.close();
        points.close();
    }
}
