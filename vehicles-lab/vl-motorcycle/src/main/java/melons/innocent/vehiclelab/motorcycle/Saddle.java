package melons.innocent.vehiclelab.motorcycle;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * The boxer's seat and the cover under it, as a cruiser's are. The seat a saddle sitting on the frame's bend, thin:
 * low and narrow at its front against the tank, scooped and broad where the rider sits, rising to a hump at its back
 * where the fender begins. Under its front half the side cover, filling the frame's triangle there: broad under the
 * seat, narrowing down to a rounded point low behind the gearbox, so its front falls back from the tank and its back
 * falls forward from the seat's hump. Each round in section, its sides falling in.
 *
 * @param shapes what it is shaped with
 */
record Saddle(Shapes shapes) {

    /** How high the seat's underside is, on the frame's bend; where it begins and ends, along. */
    private static final double SEATED = 62;
    private static final double FRONT = -17;
    private static final double BACK = -66;
    /** How thick the seat is at its front, how deep its scoop and how high its hump; how wide at its nose and after. */
    private static final double THICK = 6;
    private static final double SCOOP = 2.5;
    private static final double HUMP = 8;
    private static final double NOSE = 9;
    private static final double BROAD = 15;
    /** How many sections each is skinned over. */
    private static final int SECTIONS = 16;
    /** The cover: where its top begins and ends, along; where its point is, along and how low; how far either side. */
    private static final double COVER_FRONT = -19;
    private static final double COVER_BACK = -54;
    private static final double POINT_ALONG = -38;
    private static final double POINT = 38;
    private static final double COVER_WIDE = 12;

    /** The seat. */
    Mesh seat() {
        final List<List<Vec3>> rings = new ArrayList<>();
        for (int k = 0; k <= SECTIONS; k++) {
            final double share = (double) k / SECTIONS;
            final double z = FRONT + (BACK - FRONT) * share;
            // scooped where the rider sits, a third of the way back, rising to a hump at its back
            final double top = SEATED + THICK - SCOOP * Math.sin(Math.PI * Math.min(1, share / 0.7))
                    + HUMP * Math.pow(Math.max(0, (share - 0.55) / 0.45), 2);
            final double wide = NOSE + (BROAD - NOSE) * Math.sin(Math.PI / 2 * Math.min(1, share / 0.4));
            rings.add(section(z, top, SEATED, wide, wide - 2));
        }
        return shapes.lofted(rings, new Vec3(0, SEATED + 4, (FRONT + BACK) / 2));
    }

    /** The cover under the seat's front half, broad under it and narrowing to its point below. */
    Mesh cover() {
        final List<List<Vec3>> rings = new ArrayList<>();
        final DoubleUnaryOperator foot = z -> z > POINT_ALONG
                ? SEATED - (SEATED - POINT) * (COVER_FRONT - z) / (COVER_FRONT - POINT_ALONG)
                : SEATED - (SEATED - POINT) * (z - COVER_BACK) / (POINT_ALONG - COVER_BACK);
        for (int k = 0; k <= SECTIONS; k++) {
            final double z = COVER_FRONT + (COVER_BACK - COVER_FRONT) * k / SECTIONS;
            rings.add(section(z, SEATED, Math.min(foot.applyAsDouble(z), SEATED - 2), COVER_WIDE, COVER_WIDE - 1.5));
        }
        return shapes.lofted(rings, new Vec3(0, (SEATED + POINT) / 2, POINT_ALONG));
    }

    /** A section {@code z} along, from {@code foot} to {@code top}, {@code wide} each way at top, {@code low} below. */
    private List<Vec3> section(final double z, final double top, final double foot, final double wide,
                               final double low) {
        final double round = Math.min(3, (top - foot) / 3);
        return List.of(new Vec3(-wide + round, top, z), new Vec3(wide - round, top, z),
                new Vec3(wide, top - round, z), new Vec3(low, foot + round, z), new Vec3(low - round, foot, z),
                new Vec3(-low + round, foot, z), new Vec3(-low, foot + round, z), new Vec3(-wide, top - round, z));
    }
}
