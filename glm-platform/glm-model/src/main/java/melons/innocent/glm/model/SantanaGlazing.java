package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Joining;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.List;

/**
 * The retro saloon's glass, each pane a part of its own: the windscreen from the scuttle up to the front header,
 * curved ahead at its middle; the rear window from the boot lid's front up to the rear header, curved back at its
 * middle, as the drawing's outline, the car's middle, stands behind the C pillars' edges; the glass roof over the cage;
 * each bonded to its header. In each door its window and the pane beside its divider, the front door's vent ahead of
 * it, the rear door's small pane behind it, carried by the door; in each C pillar the quarter light.
 *
 * @param lines  the car's lines
 * @param panels the shell's panels, its headers among them
 */
record SantanaGlazing(SantanaLines lines, ShellPanels panels) {

    /** How far the screens stand in from their headers and up from their feet; how curved they are. */
    private static final double UNDER = 1.5;
    private static final double SCUTTLE = 100.8;
    private static final double WINDSCREEN_BULGE = 3;
    private static final double REAR_BULGE = -6;
    private static final double CROWN = 5;
    /** Where the rear window's foot is at its sides, along, in the drawing's millimetres: ahead of the boot lid. */
    private static final double REAR_FOOT = -2_935;

    /** The windscreen, the rear window and the glass roof, each bonded to its header. */
    List<Connection> screens(final Part front, final Part rear) {
        final Spot ahead = lines.frontHeader();
        final Spot behind = lines.rearHeader();
        final Vec3 frontAt = panels.glass(ahead);
        final Vec3 rearAt = panels.glass(behind);
        final double foot = REAR_FOOT * lines.scale() / 10;
        final Part windscreen = new Screen("windscreen", lines, new Spot(lines.side().getFirst().u() - UNDER, SCUTTLE),
                new Spot(ahead.u() + UNDER, ahead.v() - UNDER), WINDSCREEN_BULGE, CROWN, frontAt).part();
        final Part window = new Screen("rear window", lines, new Spot(foot, new Loop(lines.side()).top(foot)),
                new Spot(behind.u() - UNDER, behind.v() - UNDER), REAR_BULGE, CROWN, rearAt).part();
        final Part roof = new RoofGlass(lines, frontAt).part();
        return List.of(new Connection("windscreen", front, "glass", windscreen, "header", Joining.SEATED),
                new Connection("rear window", rear, "glass", window, "header", Joining.SEATED),
                new Connection("glass roof", front, "glass", roof, "header", Joining.SEATED));
    }

    /** The doors' windows and panes, each carried by its door, and the quarter lights in the body sides. */
    List<Connection> windows(final Part side, final Door frontDoor, final Part front, final Door rearDoor,
                             final Part rear) {
        final List<List<Spot>> ahead = frontDoor.panes();
        final List<List<Spot>> behind = rearDoor.panes();
        final Spot quarter = lines.quarterLight().getFirst();
        final Vec3 held = new Vec3(lines.half(quarter.u(), quarter.v()), quarter.v(), quarter.u());
        return List.of(
                carried("front door vent", ahead.get(0), front, frontDoor.hinge()),
                carried("front door window", ahead.get(1), front, frontDoor.hinge()),
                carried("rear door window", behind.get(0), rear, rearDoor.hinge()),
                carried("rear door pane", behind.get(1), rear, rearDoor.hinge()),
                new Connection("quarter light", side, "quarter light", new Pane("quarter light", lines,
                        lines.quarterLight(), "body side", held).part(), "body side", Joining.SEATED));
    }

    /** A pane called {@code name} in {@code opening}, carried by {@code door}, hinged at {@code hinge}. */
    private Connection carried(final String name, final List<Spot> opening, final Part door, final Vec3 hinge) {
        return new Connection(name, door, "hinge", new Pane(name, lines, opening, "door", hinge).part(), "door",
                Joining.SEATED);
    }
}
