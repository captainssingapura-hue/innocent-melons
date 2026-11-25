package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.function.UnaryOperator;

/**
 * A combustion chamber in the head, over its bore: its pent roof, two planes falling from a ridge along the bank, and
 * the band of the bore's wall carried up into the head round it, from the deck to the roof. Formed in its cylinder's
 * own frame ({@link HeadFigures}), carried where it is told. Either as the head has it, its surfaces facing into the
 * chamber, open at the deck to the bore below; or whole, closed across the deck and facing out, as the charge burning
 * in it fills it.
 *
 * @param head how the head is laid out
 */
record Chamber(HeadFigures head) {

    /** How many rings out from the bore's axis its roof is drawn in, and how many points round. */
    private static final int RINGS = 8;
    private static final int ROUND = 32;

    /** As the head has it, carried by {@code placed}: facing in, open at the deck. */
    Mesh roofed(final UnaryOperator<Vec3> placed) {
        return formed(placed, false);
    }

    /** Whole, in its cylinder's own frame: closed across the deck, facing out. */
    Mesh filled() {
        return formed(UnaryOperator.identity(), true);
    }

    private Mesh formed(final UnaryOperator<Vec3> placed, final boolean whole) {
        final Forming forming = new Forming(placed.apply(new Vec3(0, head.ridge() / 2, 0)));
        final double r = head.radius();
        final int[][] roof = new int[RINGS + 1][ROUND];
        for (int i = 0; i <= RINGS; i++) {
            for (int j = 0; j < ROUND; j++) {
                final Vec3 p = on(r * i / RINGS, j);
                roof[i][j] = forming.point(placed.apply(p), facing(placed, new Vec3(0, whole ? 1 : -1, 0)));
            }
        }
        final int[] low = new int[ROUND];
        final int[] high = new int[ROUND];
        final int[] floor = new int[ROUND];
        for (int j = 0; j < ROUND; j++) {
            final Vec3 edge = on(r, j);
            final Vec3 in = new Vec3(-edge.x(), 0, -edge.z());
            low[j] = forming.point(placed.apply(new Vec3(edge.x(), 0, edge.z())), facing(placed, whole ? in.times(-1)
                    : in));
            high[j] = forming.point(placed.apply(edge), facing(placed, whole ? in.times(-1) : in));
            floor[j] = forming.point(placed.apply(new Vec3(edge.x(), 0, edge.z())), facing(placed,
                    new Vec3(0, -1, 0)));
        }
        final int middle = forming.point(placed.apply(new Vec3(0, 0, 0)), facing(placed, new Vec3(0, -1, 0)));
        for (int j = 0; j < ROUND; j++) {
            final int n = (j + 1) % ROUND;
            for (int i = 0; i < RINGS; i++) {
                set(forming, whole, roof[i][j], roof[i + 1][j], roof[i + 1][n]);
                set(forming, whole, roof[i][j], roof[i + 1][n], roof[i][n]);
            }
            set(forming, whole, low[j], high[j], high[n]);
            set(forming, whole, low[j], high[n], low[n]);
            if (whole) {
                forming.triangle(middle, floor[j], floor[n]);
            }
        }
        return forming.formed();
    }

    /** The roof's point {@code reach} out from the bore's axis, {@code j}th of the way round. */
    private Vec3 on(final double reach, final int j) {
        final double t = 2 * Math.PI * j / ROUND;
        final double u = reach * Math.cos(t);
        return new Vec3(u, head.roof(u), reach * Math.sin(t));
    }

    private Vec3 facing(final UnaryOperator<Vec3> placed, final Vec3 way) {
        return placed.apply(way).minus(placed.apply(new Vec3(0, 0, 0)));
    }

    private void set(final Forming forming, final boolean whole, final int a, final int b, final int c) {
        if (whole) {
            forming.triangle(a, b, c);
        } else {
            forming.faced(a, b, c);
        }
    }
}
