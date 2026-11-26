package melons.innocent.vehicles.boat;

import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.space.Vec3;

/**
 * A ring, as a tyre or a wheel's rim is: a tube {@code tube} thick bent round a circle {@code reach} across from its
 * middle, each point facing out from the tube's own middle line.
 *
 * @param reach how far the tube's middle line is from the ring's middle
 * @param tube  how far the tube's skin is from its middle line
 */
public record Tyre(double reach, double tube) {

    private static final int ROUND = 20;
    private static final int ACROSS = 10;

    /** The ring added to {@code forming}, its middle at {@code middle}, lying square to {@code axis}. */
    public void added(final Forming forming, final Vec3 middle, final Vec3 axis) {
        final Vec3 n = axis.unit();
        final Vec3 any = Math.abs(n.y()) < 0.9 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
        final Vec3 a = n.cross(any).unit();
        final Vec3 b = n.cross(a).unit();
        final int[][] at = new int[ROUND][ACROSS];
        for (int i = 0; i < ROUND; i++) {
            final double t = 2 * Math.PI * i / ROUND;
            final Vec3 out = a.times(Math.cos(t)).plus(b.times(Math.sin(t)));
            final Vec3 line = middle.plus(out.times(reach));
            for (int j = 0; j < ACROSS; j++) {
                final double s = 2 * Math.PI * j / ACROSS;
                final Vec3 skin = out.times(Math.cos(s)).plus(n.times(Math.sin(s)));
                at[i][j] = forming.point(line.plus(skin.times(tube)), skin);
            }
        }
        for (int i = 0; i < ROUND; i++) {
            for (int j = 0; j < ACROSS; j++) {
                final int i1 = (i + 1) % ROUND;
                final int j1 = (j + 1) % ACROSS;
                forming.faced(at[i][j], at[i1][j], at[i1][j1]);
                forming.faced(at[i][j], at[i1][j1], at[i][j1]);
            }
        }
    }
}
