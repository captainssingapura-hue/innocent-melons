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
 * its outer walls are cover: windowed, as the plan has them, on the three sides away from the lobby; solid on the
 * lobby's side and down the main bedroom's and the study's left. Its inner walls, which may be knocked down, are left
 * out. In centimetres, its own way: across to the right, along down the drawing; the lobby's side at {@link #LOBBY}.
 */
final class ExecutiveFlat {

    /** How far across its side on the lift lobby is: the porch's face. */
    static final double LOBBY = 1_670;
    /** Where along, on the lobby's side, the porch is, and the door in it. */
    static final double PORCH_FROM = 490;
    static final double PORCH_TO = 830;
    static final double DOOR_FROM = 670;
    static final double DOOR_TO = 820;
    /** How thick its pillars are, and its cover. */
    static final double PILLAR = 30;
    static final double COVER = 10;
    /** How short a run of cover between two pillars is let be. */
    private static final double SHORTEST = 20;

    /** A rectangle in plan, square to it. */
    private record Box(double x0, double z0, double x1, double z1) {
    }

    /** A side of its outline, from one corner to the next, round it, and the kind of cover along it. */
    private record Side(double x0, double z0, double x1, double z1, FloorPlan.Kind kind) {
    }

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
    private static final List<Side> OUTLINE = List.of(
            new Side(180, 0, 510, 0, FloorPlan.Kind.WINDOWED),
            new Side(510, 0, 510, 150, FloorPlan.Kind.WINDOWED),
            new Side(510, 150, 1_510, 150, FloorPlan.Kind.WINDOWED),
            new Side(1_510, 150, 1_510, PORCH_FROM, FloorPlan.Kind.SOLID),
            new Side(1_510, PORCH_FROM, LOBBY, PORCH_FROM, FloorPlan.Kind.SOLID),
            new Side(LOBBY, PORCH_TO, 1_510, PORCH_TO, FloorPlan.Kind.SOLID),
            new Side(1_510, PORCH_TO, 1_510, 1_170, FloorPlan.Kind.SOLID),
            new Side(1_510, 1_170, 510, 1_170, FloorPlan.Kind.WINDOWED),
            new Side(510, 1_170, 510, 1_380, FloorPlan.Kind.WINDOWED),
            new Side(510, 1_380, 180, 1_380, FloorPlan.Kind.WINDOWED),
            new Side(180, 1_380, 180, 862.5, FloorPlan.Kind.SOLID),
            new Side(180, 862.5, 0, 862.5, FloorPlan.Kind.SOLID),
            new Side(0, 862.5, 0, 540, FloorPlan.Kind.WINDOWED),
            new Side(0, 540, 180, 540, FloorPlan.Kind.SOLID),
            new Side(180, 540, 180, 0, FloorPlan.Kind.SOLID));

    /** Its plan: its pillars, its floor, its cover. */
    FloorPlan plan() {
        return new FloorPlan(walls(), FLOOR, covers());
    }

    /** Its pillars as shear walls: each along its longer way, as thick as its shorter. */
    List<FloorPlan.Wall> walls() {
        final List<FloorPlan.Wall> walls = new ArrayList<>();
        for (final Box p : PILLARS) {
            final double mx = (p.x0() + p.x1()) / 2;
            final double mz = (p.z0() + p.z1()) / 2;
            walls.add(p.x1() - p.x0() >= p.z1() - p.z0()
                    ? new FloorPlan.Wall(p.x0(), mz, p.x1(), mz, p.z1() - p.z0(), "pillar")
                    : new FloorPlan.Wall(mx, p.z0(), mx, p.z1(), p.x1() - p.x0(), "pillar"));
        }
        return walls;
    }

    /** Its cover: along each side of its outline, inside it, between the pillars on that side. */
    List<FloorPlan.Cover> covers() {
        final List<FloorPlan.Cover> covers = new ArrayList<>();
        for (final Side side : OUTLINE) {
            final double length = Math.hypot(side.x1() - side.x0(), side.z1() - side.z0());
            final double ux = (side.x1() - side.x0()) / length;
            final double uz = (side.z1() - side.z0()) / length;
            final double mx = (side.x0() + side.x1()) / 2;
            final double mz = (side.z0() + side.z1()) / 2;
            final double turn = inside(mx - uz, mz + ux) ? 1 : -1;
            final double nx = -uz * turn;
            final double nz = ux * turn;
            double from = 0;
            for (final double[] span : spans(side, ux, uz, nx, nz, length)) {
                run(covers, side, ux, uz, nx, nz, from, span[0]);
                from = Math.max(from, span[1]);
            }
            run(covers, side, ux, uz, nx, nz, from, length);
        }
        return covers;
    }

    /** How far along {@code side} each pillar on it reaches, from and to, in order: those within its cover. */
    private List<double[]> spans(final Side side, final double ux, final double uz, final double nx, final double nz,
                                 final double length) {
        final List<double[]> spans = new ArrayList<>();
        for (final Box p : PILLARS) {
            double t0 = Double.MAX_VALUE;
            double t1 = -Double.MAX_VALUE;
            double s0 = Double.MAX_VALUE;
            double s1 = -Double.MAX_VALUE;
            for (final double x : new double[] {p.x0(), p.x1()}) {
                for (final double z : new double[] {p.z0(), p.z1()}) {
                    final double t = (x - side.x0()) * ux + (z - side.z0()) * uz;
                    final double s = (x - side.x0()) * nx + (z - side.z0()) * nz;
                    t0 = Math.min(t0, t);
                    t1 = Math.max(t1, t);
                    s0 = Math.min(s0, s);
                    s1 = Math.max(s1, s);
                }
            }
            if (s0 < COVER && s1 > 0 && t1 > 0 && t0 < length) {
                spans.add(new double[] {Math.max(0, t0), Math.min(length, t1)});
            }
        }
        spans.sort((a, b) -> Double.compare(a[0], b[0]));
        return spans;
    }

    /** A run of {@code side}'s cover from {@code from} to {@code to} along it, inside it: if it is long enough. */
    private void run(final List<FloorPlan.Cover> covers, final Side side, final double ux, final double uz,
                     final double nx, final double nz, final double from, final double to) {
        if (to - from < SHORTEST) {
            return;
        }
        final double ox = side.x0() + nx * COVER / 2;
        final double oz = side.z0() + nz * COVER / 2;
        covers.add(new FloorPlan.Cover(new FloorPlan.Wall(ox + ux * from, oz + uz * from, ox + ux * to,
                oz + uz * to, COVER, "outer wall"), side.kind()));
    }

    /** Whether ({@code x}, {@code z}) is within its floor. */
    private boolean inside(final double x, final double z) {
        return FLOOR.stream().anyMatch(s -> x > s.x0() && x < s.x1() && z > s.z0() && z < s.z1());
    }
}
