package com.example.fxsuite.vulkan.device;

import com.example.fxsuite.vulkan.form.Packed;
import com.example.fxsuite.vulkan.layout.Layout;

import java.nio.ByteBuffer;
import java.util.Optional;

import static org.lwjgl.vulkan.KHRAccelerationStructure.*;
import static org.lwjgl.vulkan.VK13.*;

/**
 * A mesh sent to the card, once: its points' bytes and its corners', there to be drawn from as often as wanted, and
 * the layout its points were packed by. On a card that answers rays its triangles are kept as well for a ray to be
 * asked of, built once here, so that it can stand in a frame ({@link Standing}). Closed, it is gone from the card.
 */
public final class Sent implements AutoCloseable {

    /** Which of a point's fields says where it is: what a ray meets. */
    private static final String POINT = "point";

    private final Layout layout;
    private final Bytes points;
    private final Bytes corners;
    private final int count;
    private final Optional<Structured> solid;

    Sent(final Reach reach, final Recording recording, final Packed mesh) {
        this(reach, recording, mesh, true);
    }

    /**
     * The same; where not {@code standing}, its triangles are not kept for rays, so it stands in the way of none
     * and is spared what keeping them costs: for what is too far off for its shadow to matter.
     */
    Sent(final Reach reach, final Recording recording, final Packed mesh, final boolean standing) {
        this.layout = mesh.layout();
        this.count = mesh.mesh().corners().size();
        // read where they are by what builds the triangles for rays, on a card that answers them
        final boolean kept = reach.rays() && standing;
        final int read = kept ? VK_BUFFER_USAGE_ACCELERATION_STRUCTURE_BUILD_INPUT_READ_ONLY_BIT_KHR
                | VK_BUFFER_USAGE_SHADER_DEVICE_ADDRESS_BIT : 0;
        // in the card's own memory, which is far the faster to draw from; or, asked for, where Java reaches them
        final boolean carded = !"host".equals(System.getProperty("vulkan.meshes", "card"));
        this.points = held(reach, recording, "a mesh's points", mesh.points(),
                VK_BUFFER_USAGE_VERTEX_BUFFER_BIT | read, carded);
        this.corners = held(reach, recording, "a mesh's corners", mesh.corners(),
                VK_BUFFER_USAGE_INDEX_BUFFER_BIT | read, carded);
        this.solid = kept ? Optional.of(solid(reach, recording, mesh.mesh().points().size()))
                : Optional.empty();
    }

    /**
     * {@code bytes} kept on the card to be used as {@code use} says: where Java reaches them; or, where
     * {@code carded}, in the card's own memory, filled by way of bytes Java reaches that are let go once copied.
     */
    private Bytes held(final Reach reach, final Recording recording, final String name, final ByteBuffer bytes,
                       final int use, final boolean carded) {
        if (!carded) {
            final Bytes reached = new Bytes(reach, name, bytes.remaining(), use);
            reached.filled(bytes);
            return reached;
        }
        final Bytes staged = new Bytes(reach, name + ", on their way", bytes.remaining(),
                VK_BUFFER_USAGE_TRANSFER_SRC_BIT);
        try {
            staged.filled(bytes);
            final Bytes own = new Bytes(reach, name, bytes.remaining(), use | VK_BUFFER_USAGE_TRANSFER_DST_BIT,
                    VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);
            recording.begun().copied(staged, own);
            recording.sent();
            return own;
        } finally {
            staged.close();
        }
    }

    /** Its triangles kept for rays, built and done: of {@code many} points, three corners to a triangle. */
    private Structured solid(final Reach reach, final Recording recording, final int many) {
        final long pointsAt = points.address() + layout.packedAt(POINT);
        final long cornersAt = corners.address();
        final int each = layout.packedBytes();
        final Structured made = new Structured(reach, "a mesh's triangles",
                VK_ACCELERATION_STRUCTURE_TYPE_BOTTOM_LEVEL_KHR,
                VK_BUILD_ACCELERATION_STRUCTURE_PREFER_FAST_TRACE_BIT_KHR, count / 3, of -> {
                    of.geometryType(VK_GEOMETRY_TYPE_TRIANGLES_KHR);
                    of.geometry().triangles().sType$Default().vertexFormat(VK_FORMAT_R32G32B32_SFLOAT)
                            .vertexStride(each).maxVertex(many - 1).indexType(VK_INDEX_TYPE_UINT32);
                    of.geometry().triangles().vertexData().deviceAddress(pointsAt);
                    of.geometry().triangles().indexData().deviceAddress(cornersAt);
                });
        made.built(recording.begun(), count / 3);
        recording.sent();
        made.settled();
        return made;
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

    /** Its triangles as they are kept for rays, on a card that answers them. */
    Optional<Structured> solid() {
        return solid;
    }

    @Override
    public void close() {
        solid.ifPresent(Structured::close);
        corners.close();
        points.close();
    }
}
