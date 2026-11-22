package com.example.fxsuite.texture.lab;

import com.example.fxsuite.avatar.build.Sheet;
import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.List;

/**
 * Sheets of the engine's, as one mesh for the card: every point of every sheet, facing the way its sheet says is
 * out there, and every triangle between them. The sheets are in the engine's frame (across, along, up) and the
 * mesh in the card's (across, up, ahead), turned so that what was ahead lies along the bench's row: a thing as
 * long as a car stands side on to whoever looks at the row, its nose to their left.
 *
 * <p>A sheet has one side, the one it faces: seen from behind it is not there. So a hollow thing whose inside can
 * be seen, through a window, needs its sheets a second time, facing in ({@link #lining}).
 */
public record Sheeted(List<Sheet> sheets) {

    public Sheeted {
        sheets = List.copyOf(sheets);
    }

    /** The mesh. */
    public Mesh formed() {
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        added(forming, sheets, 1);
        return forming.formed();
    }

    /**
     * The inside of what the sheets enclose, as a mesh: each of them again, facing the other way, and
     * {@code besides} as they are, for what the inside has that the outside has not, as a floor.
     */
    public Mesh lining(final List<Sheet> besides) {
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        added(forming, sheets, -1);
        added(forming, besides, 1);
        return forming.formed();
    }

    /** {@code added}'s points and triangles put into {@code forming}, facing out where {@code way} is 1, in at -1. */
    private void added(final Forming forming, final List<Sheet> added, final double way) {
        for (final Sheet sheet : added) {
            final int first = forming.point(turned(sheet.points().getFirst()),
                    turned(sheet.normals().getFirst()).times(way));
            for (int point = 1; point < sheet.points().size(); point++) {
                forming.point(turned(sheet.points().get(point)), turned(sheet.normals().get(point)).times(way));
            }
            for (final int[] triangle : sheet.triangles()) {
                forming.faced(first + triangle[0], first + triangle[1], first + triangle[2]);
            }
        }
    }

    /** A place or a way in the engine's frame, in the card's: what was ahead to the left, what was up still up. */
    private Vec3 turned(final com.example.fxsuite.avatar.sim.Vec3 engine) {
        return new Vec3(-engine.y(), engine.z(), engine.x());
    }
}
