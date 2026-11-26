package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.Section;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A bumper, as the original's are: a bar moulded in black plastic over its steel beam, bolted to the shell, across the
 * car's front or its tail and wrapped round its corners along each side to the wheel's arch; its outer face
 * {@code side} out at the sides and {@code face} along at the front or the tail, round its corners {@code round}; and a
 * chrome strip along its outer face, a part of its own. All in centimetres.
 *
 * @param name   what it is called
 * @param side   how far out its outer face is along the sides
 * @param end    where it ends along the sides, at the wheel's arch
 * @param face   where its outer face is across the front or the tail
 * @param round  how round its corners are, at its outer face
 * @param low    how high its foot is
 * @param high   how high its top is
 * @param chrome how high the chrome strip along it is
 */
record Bumper(String name, double side, double end, double face, double round, double low, double high,
              double chrome) {

    private static final Making MOULDED = new Making(new Made(Material.PLASTIC, true));
    private static final Making CHROMED = new Making(new Made(Material.STEEL, true));
    /** How deep the bar is; how deep and high the strip is; how finely its corners are drawn. */
    private static final double DEEP = 8;
    private static final double STRIP_DEEP = 0.6;
    private static final double STRIP_HIGH = 1.1;
    private static final int CORNER = 8;

    /** It, its points where they are on the platform. */
    Part part() {
        final Map<String, Vec3> points = new LinkedHashMap<>();
        points.put("shell", mount());
        points.put("number plate", plate());
        return MOULDED.part(name, Group.TRIM, false, points, new Sheets(0.1).member(path(-DEEP / 2, (low + high) / 2),
                new Section(DEEP, high - low, 8)));
    }

    /** Its chrome strip, along its outer face. */
    Part strip() {
        return CHROMED.part(name + " strip", Group.TRIM, false, Map.of("bumper", mount()), new Sheets(0.1).member(
                path(STRIP_DEEP / 2 - 0.1, chrome), new Section(STRIP_DEEP, STRIP_HIGH, 6)));
    }

    /** Where a number plate is held on it: the middle of its face, across the front or the tail. */
    Vec3 plate() {
        return new Vec3(0, (low + high) / 2, face);
    }

    /** Where it is bolted to the shell: the middle of its bar, across the front or the tail. */
    Vec3 mount() {
        return new Vec3(0, (low + high) / 2, face - Math.signum(face) * DEEP / 2);
    }

    /**
     * Its line, {@code out} out from its outer face, {@code up} high: along the right side from the arch, round the
     * corner, across, round the other corner and back along the left side.
     */
    private List<Vec3> path(final double out, final double up) {
        final double ahead = Math.signum(face);
        final double reach = round + out;
        final double x = side - round;
        final double z = face - ahead * round;
        final List<Vec3> right = new ArrayList<>();
        right.add(new Vec3(side + out, up, end));
        for (int k = 0; k <= CORNER; k++) {
            final double a = Math.PI / 2 * k / CORNER;
            right.add(new Vec3(x + reach * Math.cos(a), up, z + ahead * reach * Math.sin(a)));
        }
        final List<Vec3> whole = new ArrayList<>(right);
        right.reversed().forEach(p -> whole.add(new Vec3(-p.x(), p.y(), p.z())));
        return whole;
    }
}
