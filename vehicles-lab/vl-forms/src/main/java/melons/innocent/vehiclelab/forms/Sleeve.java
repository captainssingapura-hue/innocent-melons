package melons.innocent.vehiclelab.forms;

import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

/**
 * A sleeve, a short thick-walled tube a bush is pressed into, welded where a frame is bushed: about {@code middle},
 * its bore running {@code axis}, {@code length} long, {@code outer} round outside and {@code inner} round in its bore.
 * Its outside faces out, its bore in, its ends flat.
 *
 * @param middle its middle
 * @param axis   the way its bore runs
 * @param length how long it is
 * @param outer  how far its outside is from its axis
 * @param inner  how far its bore is from its axis
 * @param round  how many points round it is drawn in
 */
public record Sleeve(Vec3 middle, Vec3 axis, double length, double outer, double inner, int round) {

    public Sleeve {
        if (inner <= 0 || outer <= inner || length <= 0 || round < 3) {
            throw new IllegalArgumentException("a sleeve is thicker outside than its bore: " + outer + ", " + inner);
        }
        axis = axis.unit();
    }

    /** It. */
    public Mesh mesh() {
        final Forming forming = new Forming(middle);
        final Vec3 any = Math.abs(axis.y()) < 0.9 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
        final Vec3 a = axis.cross(any).unit();
        final Vec3 b = axis.cross(a).unit();
        final Vec3 top = middle.plus(axis.times(length / 2));
        final Vec3 bottom = middle.minus(axis.times(length / 2));
        for (int k = 0; k < round; k++) {
            final Vec3 here = way(a, b, k);
            final Vec3 there = way(a, b, k + 1);
            quad(forming, top, bottom, here, there, outer, here, there);
            quad(forming, top, bottom, here, there, inner, here.times(-1), there.times(-1));
            end(forming, top, here, there, axis);
            end(forming, bottom, here, there, axis.times(-1));
        }
        return forming.formed();
    }

    private Vec3 way(final Vec3 a, final Vec3 b, final int k) {
        final double t = 2 * Math.PI * k / round;
        return a.times(Math.cos(t)).plus(b.times(Math.sin(t)));
    }

    /** A strip of its wall {@code reach} out, from {@code here} round to {@code there}. */
    private void quad(final Forming forming, final Vec3 top, final Vec3 bottom, final Vec3 here, final Vec3 there,
                      final double reach, final Vec3 facingHere, final Vec3 facingThere) {
        final int p = forming.point(top.plus(here.times(reach)), facingHere);
        final int q = forming.point(top.plus(there.times(reach)), facingThere);
        final int r = forming.point(bottom.plus(there.times(reach)), facingThere);
        final int s = forming.point(bottom.plus(here.times(reach)), facingHere);
        forming.faced(p, q, r);
        forming.faced(p, r, s);
    }

    /** A piece of its end at {@code at}, facing {@code out}, from its bore to its outside. */
    private void end(final Forming forming, final Vec3 at, final Vec3 here, final Vec3 there, final Vec3 out) {
        final int p = forming.point(at.plus(here.times(inner)), out);
        final int q = forming.point(at.plus(here.times(outer)), out);
        final int r = forming.point(at.plus(there.times(outer)), out);
        final int s = forming.point(at.plus(there.times(inner)), out);
        forming.faced(p, q, r);
        forming.faced(p, r, s);
    }
}
