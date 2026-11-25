package melons.innocent.doors.walk;

import com.example.fxsuite.places.yard.Wall;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.doors.swing.Design;
import melons.innocent.doors.swing.Fittings;
import melons.innocent.doors.swing.Hung;
import melons.innocent.doors.swing.Opens;
import melons.innocent.doors.swing.Size;
import melons.innocent.doors.swing.Swing;
import melons.innocent.doors.swing.SwingDoor;
import melons.innocent.doors.swing.Wood;

import java.util.ArrayList;
import java.util.List;

/**
 * What the walk shows: a wall across the way five metres ahead of where it begins, three metres high and as deep as
 * a door's frame, with four doorways three metres apart and in each a door of another design, hung and opening
 * another way. From the left: the traditional door with glass panels, of varnished timber, hung on the left and
 * pushed; the solid door with six raised panels, painted green, hung on the right and pulled; the modern door of one
 * sheet of glass, hung on the left and pushed; and a flush door of varnished timber, hung on the right and pushed.
 * The wall leaves each door the hole it asks for, and is carried over it.
 */
public record Showing() {

    /** Where the wall's front is, how deep and how high it is, and how far it reaches to either side. */
    private static final double FRONT = 500;
    private static final double DEEP = 12;
    private static final double HIGH = 300;
    private static final double REACH = 750;
    /** How far apart the doors' middles are. */
    private static final double APART = 300;

    /** The four doors, by name, where each stands. */
    public List<Doors.Doorway> doorways() {
        final Size usual = new Size(90, 210, 4.4);
        return List.of(
                stood("the glazed door", -1.5, new SwingDoor<>(
                        new Design.Glazed(new Wood.Varnished(), Fittings.BRONZE, 2, 3, 0.3), usual,
                        new Swing(Hung.LEFT, Opens.AWAY))),
                stood("the panelled door", -0.5, new SwingDoor<>(
                        new Design.Solid(new Wood.Painted(0x1f4a37), Fittings.BRONZE, 2, 3), usual,
                        new Swing(Hung.RIGHT, Opens.TOWARD))),
                stood("the glass door", 0.5, new SwingDoor<>(new Design.Frameless(Fittings.STEEL),
                        new Size(95, 215, 1.2), new Swing(Hung.LEFT, Opens.AWAY, 90))),
                stood("the flush door", 1.5, new SwingDoor<>(
                        new Design.Solid(new Wood.Varnished(), Fittings.STEEL, 0, 0), usual,
                        new Swing(Hung.RIGHT, Opens.AWAY, 110))));
    }

    /** A door standing in the wall, its middle so many door spacings {@code along} from the wall's middle. */
    private Doors.Doorway stood(final String name, final double along, final SwingDoor<?> door) {
        return new Doors.Doorway(name, door.stood(new Vec3(along * APART, 0, FRONT + DEEP / 2 + door.set()), 0));
    }

    /** The wall, in runs: between the doorways from the ground up, and over each from the head of its hole. */
    public List<Wall> walls() {
        final List<Wall> runs = new ArrayList<>();
        double from = -REACH;
        for (final Doors.Doorway doorway : doorways()) {
            final SwingDoor<?> door = doorway.stood().door();
            final double left = doorway.stood().at().x() - door.holeWide() / 2;
            final double right = doorway.stood().at().x() + door.holeWide() / 2;
            runs.add(run(from, left, 0));
            runs.add(run(left, right, door.holeHigh()));
            from = right;
        }
        runs.add(run(from, REACH, 0));
        return runs;
    }

    /** A run of the wall from so far across to so far, from {@code low} up to its full height. */
    private Wall run(final double from, final double to, final double low) {
        return new Wall(new Vec3(from, 0, FRONT), new Vec3(to, 0, FRONT), new Vec3(0, 0, -1), low, HIGH, DEEP,
                from + REACH);
    }

    /** The middle of the wall, for its mesh to be made about. */
    public Vec3 middle() {
        return new Vec3(0, HIGH / 2, FRONT + DEEP / 2);
    }
}
