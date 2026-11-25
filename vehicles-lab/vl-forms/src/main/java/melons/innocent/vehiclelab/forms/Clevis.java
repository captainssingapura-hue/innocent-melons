package melons.innocent.vehiclelab.forms;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.List;

/**
 * A clevis, the U-shaped bracket a link's bush is bolted into, pressed from sheet and welded to a frame by its foot:
 * two cheeks, either side of the bush, standing up from the foot to their rounded tips, a hole through each for the
 * bolt; and the web joining them at the foot.
 *
 * @param pivot where the bolt runs through, between the cheeks
 * @param foot  where its foot is, on what it is welded to
 * @param axis  the way the bolt runs
 * @param gap   how far apart its cheeks are, inside, as long as the bush
 * @param thick how thick its sheet is
 * @param tip   how far round its tips reach from the bolt
 * @param hole  how far round the bolt's holes are
 * @param wide  how wide its cheeks are at their foot
 * @param round how many points a whole round is drawn in
 */
public record Clevis(Vec3 pivot, Vec3 foot, Vec3 axis, double gap, double thick, double tip, double hole,
                     double wide, int round) {

    public Clevis {
        axis = axis.unit();
        if (hole >= tip || gap <= 0 || thick <= 0) {
            throw new IllegalArgumentException("a clevis's tips reach round its holes, its cheeks apart");
        }
    }

    /** It. */
    public Mesh mesh() {
        final Vec3 rise = pivot.minus(foot);
        final Vec3 standing = rise.minus(axis.times(rise.dot(axis)));
        final double reach = standing.length();
        final Vec3 up = standing.unit();
        final Vec3 along = up.cross(axis).unit();
        final Vec3 base = pivot.minus(up.times(reach));
        final Outlines outlines = new Outlines(round);
        final double apart = gap / 2 + thick / 2;
        final Shapes joining = new Shapes(round);
        final Mesh one = new Plate(outlines.tab(wide, reach, tip), List.of(outlines.circle(new Spot(0, reach), hole)),
                base.plus(axis.times(apart)), along, up, thick).mesh();
        final Mesh other = new Plate(outlines.tab(wide, reach, tip),
                List.of(outlines.circle(new Spot(0, reach), hole)), base.minus(axis.times(apart)), along, up, thick)
                .mesh();
        final double across = gap + 2 * thick;
        final Mesh web = new Plate(List.of(new Spot(-across / 2, -wide / 2), new Spot(across / 2, -wide / 2),
                new Spot(across / 2, wide / 2), new Spot(-across / 2, wide / 2)), List.of(),
                base.plus(up.times(thick / 2)), axis, along, thick).mesh();
        return joining.joined(List.of(one, other, web));
    }
}
