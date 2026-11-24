package melons.innocent.building.generic;

import java.util.ArrayList;
import java.util.List;

/**
 * An executive flat as the housing board built them in point blocks, after one of its own plans: "Executive Apartment
 * (Point Block)", at 1:100, as 99.co's guide to reading the board's floor plans reproduces it. 16.7 m across and
 * 13.8 m along all told: the main bedroom over the study down its left, 3.3 m wide; bedroom three out beyond them,
 * between them; bedroom two, the kitchen and the living and dining room in the body, 10 m by 10.2 m; and on its right
 * the store and the porch, its door on the lift lobby. Its structure only pillars, the black on the board's plan,
 * measured off the drawing at 17.2 mm a pixel, to within some 5 cm, and made blades 30 cm thick: at its corners and
 * where its rooms part, along its outer walls; and two inside, where bedroom two and the kitchen end. Between them,
 * its outer walls are cover ({@link Infill}): windowed, as the plan has them, on the three sides away from the lobby;
 * solid on the lobby's side and down the main bedroom's and the study's left. Its inner walls, which may be knocked
 * down, are left out. In centimetres, its own way: across to the right, along down the drawing; the lobby's side at
 * {@link #LOBBY}.
 */
final class ExecutiveFlat {

    /** How far across its side on the lift lobby is: the porch's face. */
    static final double LOBBY = 1_670;
    /** Where along, on the lobby's side, the porch is, and the door in it. */
    static final double PORCH_FROM = 490;
    static final double PORCH_TO = 830;
    static final double DOOR_FROM = 670;
    static final double DOOR_TO = 820;
    /** What its outer walls are called. */
    private static final String OUTER = "outer wall";
    /** How thick its pillars are, and its cover. */
    static final double PILLAR = 30;
    static final double COVER = 10;

    /** What it stands over: the main bedroom and the study, the body, the store and porch, and bedroom three. */
    private static final List<FloorPlan.Slab> FLOOR = List.of(
            new FloorPlan.Slab(180, 0, 510, 1_380),
            new FloorPlan.Slab(510, 150, 1_510, 1_170),
            new FloorPlan.Slab(1_510, PORCH_FROM, LOBBY, PORCH_TO),
            new FloorPlan.Slab(0, 540, 180, 862.5));

    /** Its pillars, as the plan has them. */
    private static final List<Box> PILLARS = List.of(
            new Box(180, 0, 210, 100), new Box(480, 0, 510, 180),
            new Box(730, 150, 820, 180), new Box(1_030, 150, 1_120, 180), new Box(1_300, 150, 1_360, 180),
            new Box(1_480, 150, 1_510, 210), new Box(1_480, 440, 1_510, 540), new Box(1_480, 810, 1_510, 880),
            new Box(1_480, 1_110, 1_510, 1_170),
            new Box(760, 450, 790, 540), new Box(1_060, 450, 1_090, 540),
            new Box(0, 540, 100, 570), new Box(180, 500, 210, 540), new Box(0, 832.5, 100, 862.5),
            new Box(180, 862.5, 210, 900),
            new Box(465, 1_140, 540, 1_170), new Box(1_030, 1_140, 1_120, 1_170),
            new Box(480, 1_280, 510, 1_380), new Box(180, 1_280, 210, 1_380));

    /** Its outline, round it, but for the porch's face, which is the lobby's wall. */
    private static final List<Infill.Side> OUTLINE = List.of(
            new Infill.Side(180, 0, 510, 0, FloorPlan.Kind.WINDOWED, OUTER),
            new Infill.Side(510, 0, 510, 150, FloorPlan.Kind.WINDOWED, OUTER),
            new Infill.Side(510, 150, 1_510, 150, FloorPlan.Kind.WINDOWED, OUTER),
            new Infill.Side(1_510, 150, 1_510, PORCH_FROM, FloorPlan.Kind.SOLID, OUTER),
            new Infill.Side(1_510, PORCH_FROM, LOBBY, PORCH_FROM, FloorPlan.Kind.SOLID, OUTER),
            new Infill.Side(LOBBY, PORCH_TO, 1_510, PORCH_TO, FloorPlan.Kind.SOLID, OUTER),
            new Infill.Side(1_510, PORCH_TO, 1_510, 1_170, FloorPlan.Kind.SOLID, OUTER),
            new Infill.Side(1_510, 1_170, 510, 1_170, FloorPlan.Kind.WINDOWED, OUTER),
            new Infill.Side(510, 1_170, 510, 1_380, FloorPlan.Kind.WINDOWED, OUTER),
            new Infill.Side(510, 1_380, 180, 1_380, FloorPlan.Kind.WINDOWED, OUTER),
            new Infill.Side(180, 1_380, 180, 862.5, FloorPlan.Kind.SOLID, OUTER),
            new Infill.Side(180, 862.5, 0, 862.5, FloorPlan.Kind.SOLID, OUTER),
            new Infill.Side(0, 862.5, 0, 540, FloorPlan.Kind.WINDOWED, OUTER),
            new Infill.Side(0, 540, 180, 540, FloorPlan.Kind.SOLID, OUTER),
            new Infill.Side(180, 540, 180, 0, FloorPlan.Kind.SOLID, OUTER));

    /** Its plan: its pillars, its floor, its cover. */
    FloorPlan plan() {
        return new FloorPlan(walls(), FLOOR, covers());
    }

    /** Its pillars as shear walls. */
    List<FloorPlan.Wall> walls() {
        return PILLARS.stream().map(p -> p.wall("pillar")).toList();
    }

    /** Its cover: along each side of its outline, inside it, between the pillars on that side. */
    List<FloorPlan.Cover> covers() {
        final Infill infill = new Infill(PILLARS);
        final List<FloorPlan.Cover> covers = new ArrayList<>();
        for (final Infill.Side side : OUTLINE) {
            final double length = Math.hypot(side.x1() - side.x0(), side.z1() - side.z0());
            final double ux = (side.x1() - side.x0()) / length;
            final double uz = (side.z1() - side.z0()) / length;
            final boolean left = inside((side.x0() + side.x1()) / 2 - uz, (side.z0() + side.z1()) / 2 + ux);
            covers.addAll(infill.along(side, (left ? 1 : -1) * COVER / 2, COVER));
        }
        return covers;
    }

    /** Whether ({@code x}, {@code z}) is within its floor. */
    private boolean inside(final double x, final double z) {
        return FLOOR.stream().anyMatch(s -> x > s.x0() && x < s.x1() && z > s.z0() && z < s.z1());
    }
}
