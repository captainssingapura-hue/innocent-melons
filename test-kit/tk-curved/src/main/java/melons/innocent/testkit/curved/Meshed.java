package melons.innocent.testkit.curved;

import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Shell;
import melons.innocent.core.sheet.Lift;
import melons.innocent.core.sheet.Sheet;

/**
 * A shell as a mesh, for drawing: its top, a point about every quarter metre each way with the way the sheet itself
 * faces there, so that light falls on it as on a curve; its underside as far under as it is thick; and a skirt
 * round its edges. It is made from the very sheet that is asked what is under a spot, in the shell's own frame.
 * Where on the surface a point is, for a material's pattern, is how far along and across the sheet it is.
 *
 * @param shell the shell
 */
record Meshed(Shell shell) {

    /** How far apart its points are, about, in centimetres; and how few and how many each way at the least and most. */
    private static final double FINE = 25;
    private static final int FEWEST = 6;
    private static final int MOST = 640;
    /** How many pieces a way over the sheet is measured in; and how near its edge a point is asked its facing. */
    private static final int MEASURED = 64;
    private static final double INSIDE = 1e-6;

    /** The mesh. */
    Mesh formed() {
        final double far = measured(true);
        final double wide = measured(false);
        final int along = Math.max(FEWEST, Math.min(MOST, (int) Math.ceil(far / FINE)));
        final int across = Math.max(FEWEST, Math.min(MOST, (int) Math.ceil(wide / FINE)));
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        final int[][] top = new int[along + 1][across + 1];
        final int[][] under = new int[along + 1][across + 1];
        for (int a = 0; a <= along; a++) {
            for (int c = 0; c <= across; c++) {
                final Vec3 at = vec(shell.sheet().at((double) a / along, (double) c / across));
                final Vec3 faces = facing((double) a / along, (double) c / across);
                final Vec3 on = new Vec3(c * wide / across, 0, a * far / along);
                top[a][c] = forming.point(at, faces, on);
                under[a][c] = forming.point(at.plus(new Vec3(0, -shell.thick(), 0)), faces.times(-1), on);
            }
        }
        for (int a = 0; a < along; a++) {
            for (int c = 0; c < across; c++) {
                quad(forming, top[a][c], top[a + 1][c], top[a + 1][c + 1], top[a][c + 1]);
                quad(forming, under[a][c], under[a + 1][c], under[a + 1][c + 1], under[a][c + 1]);
            }
        }
        skirted(forming, along, across);
        return forming.formed();
    }

    /** A skirt down each of its four edges, from its top to its underside, facing out from the sheet. */
    private void skirted(final Forming forming, final int along, final int across) {
        for (final int edge : new int[] {0, 1}) {
            int before = -1;
            int beforeUnder = -1;
            for (int a = 0; a <= along; a++) {
                final Vec3 at = vec(shell.sheet().at((double) a / along, edge));
                final Vec3 out = out(at, vec(shell.sheet().at((double) a / along, edge == 0 ? 0.02 : 0.98)));
                final int here = forming.point(at, out);
                final int hereUnder = forming.point(at.plus(new Vec3(0, -shell.thick(), 0)), out);
                if (a > 0) {
                    quad(forming, before, here, hereUnder, beforeUnder);
                }
                before = here;
                beforeUnder = hereUnder;
            }
            for (int c = 0; c <= across; c++) {
                final Vec3 at = vec(shell.sheet().at(edge, (double) c / across));
                final Vec3 out = out(at, vec(shell.sheet().at(edge == 0 ? 0.02 : 0.98, (double) c / across)));
                final int here = forming.point(at, out);
                final int hereUnder = forming.point(at.plus(new Vec3(0, -shell.thick(), 0)), out);
                if (c > 0) {
                    quad(forming, before, here, hereUnder, beforeUnder);
                }
                before = here;
                beforeUnder = hereUnder;
            }
        }
    }

    /** The level way from {@code within} out to {@code edge}, one long: ahead, where they are one. */
    private Vec3 out(final Vec3 edge, final Vec3 within) {
        final Vec3 level = new Vec3(edge.x() - within.x(), 0, edge.z() - within.z());
        return level.length() < 1e-9 ? new Vec3(0, 0, 1) : level.unit();
    }

    /**
     * Which way the sheet faces at the point so far over it each way: asked a hair inside its edges, where it is
     * surely there, of the pass over the spot that is nearest the point's own height.
     */
    private Vec3 facing(final double far, final double over) {
        final Sheet sheet = shell.sheet();
        final Spot at = sheet.at(Math.max(INSIDE, Math.min(1 - INSIDE, far)),
                Math.max(INSIDE, Math.min(1 - INSIDE, over)));
        Vec3 faces = new Vec3(0, 1, 0);
        double nearest = Double.POSITIVE_INFINITY;
        for (final Lift lift : sheet.over(at.across(), at.along())) {
            if (Math.abs(lift.high() - at.up()) < nearest) {
                nearest = Math.abs(lift.high() - at.up());
                faces = vec(lift.faces());
            }
        }
        return faces;
    }

    /** How far it is over the sheet, down its middle: along it, or across it. */
    private double measured(final boolean along) {
        double far = 0;
        for (int piece = 0; piece < MEASURED; piece++) {
            final double from = (double) piece / MEASURED;
            final double to = (piece + 1.0) / MEASURED;
            far += (along ? shell.sheet().at(to, 0.5).minus(shell.sheet().at(from, 0.5))
                    : shell.sheet().at(0.5, to).minus(shell.sheet().at(0.5, from))).length();
        }
        return far;
    }

    /** Two triangles for the four points, each facing as its points face. */
    private void quad(final Forming forming, final int a, final int b, final int c, final int d) {
        forming.faced(a, b, c);
        forming.faced(a, c, d);
    }

    private Vec3 vec(final Spot spot) {
        return new Vec3(spot.across(), spot.up(), spot.along());
    }
}
