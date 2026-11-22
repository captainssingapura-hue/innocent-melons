package com.example.fxsuite.vulkan.form;

import com.example.fxsuite.vulkan.layout.Field;
import com.example.fxsuite.vulkan.layout.Kind;
import com.example.fxsuite.vulkan.layout.Layout;
import com.example.fxsuite.vulkan.space.Vec3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * A mesh as the bytes the card is sent: its points, one after another, each laid out as {@code layout} says; and its
 * corners. A mesh has three things to say of a point: where it is ({@code point}), the way its surface faces there
 * ({@code facing}), and where on the thing's surface it is ({@code on}); a layout asking anything else of it is
 * refused.
 */
public record Packed(Layout layout, Mesh mesh) {

    private static final String POINT = "point";
    private static final String FACING = "facing";
    private static final String ON = "on";

    public Packed {
        for (final Field field : layout.fields()) {
            final boolean known = field.name().equals(POINT) || field.name().equals(FACING)
                    || field.name().equals(ON);
            if (!known || field.kind() != Kind.TRIPLE) {
                throw new IllegalArgumentException("a mesh has no " + field.kind() + " called " + field.name()
                        + " to give " + layout.name());
            }
        }
    }

    /** Its points' bytes, packed, from the first. */
    public ByteBuffer points() {
        final int each = layout.packedBytes();
        final ByteBuffer bytes = ByteBuffer.allocate(each * mesh.points().size()).order(ByteOrder.LITTLE_ENDIAN);
        for (int point = 0; point < mesh.points().size(); point++) {
            for (final Field field : layout.fields()) {
                final Vec3 said = said(field.name(), point);
                final int at = each * point + layout.packedAt(field.name());
                bytes.putFloat(at, (float) said.x());
                bytes.putFloat(at + Float.BYTES, (float) said.y());
                bytes.putFloat(at + 2 * Float.BYTES, (float) said.z());
            }
        }
        return bytes.asReadOnlyBuffer().order(ByteOrder.LITTLE_ENDIAN).rewind();
    }

    /** What the mesh says of {@code point} under the name {@code called}. */
    private Vec3 said(final String called, final int point) {
        return switch (called) {
            case POINT -> mesh.points().get(point);
            case FACING -> mesh.facings().get(point);
            default -> mesh.ons().get(point);
        };
    }

    /** Its corners' bytes: each the number of a point, four bytes, three to a triangle. */
    public ByteBuffer corners() {
        final ByteBuffer bytes = ByteBuffer.allocate(Integer.BYTES * mesh.corners().size())
                .order(ByteOrder.LITTLE_ENDIAN);
        for (int corner = 0; corner < mesh.corners().size(); corner++) {
            bytes.putInt(Integer.BYTES * corner, mesh.corners().get(corner));
        }
        return bytes.asReadOnlyBuffer().order(ByteOrder.LITTLE_ENDIAN).rewind();
    }
}
