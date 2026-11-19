package com.example.fxsuite.campus.hq;

import com.example.fxsuite.avatar.build.EarthHouse;
import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Palette;
import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.avatar.build.Village;
import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.avatar.sim.Prism;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.avatar.sim.Volume;
import com.example.fxsuite.avatar.worlds.Meadow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The main office: a hut as no homesteader built one, the earth house ({@link EarthHouse}) as big as it is made, 14 m
 * across, its walls 4.2 m high and 70 cm thick, its roof rising 5.6 m to a gold finial; its walls of pale limestone, its
 * roof glazed gold, its woodwork and its rafters red lacquer, its window glazed, its floor of limestone. Raised on a
 * round plinth of stone 45 cm high, reaching 2.6 m past its walls, three steps up to it before its door; a porch of two
 * lacquered columns under a gold canopy over its door.
 *
 * <p>Inside, on a red rug: an executive desk, its top gold, its high-backed chair behind it facing the door, two
 * chairs before it for whoever calls; a meeting table on its left with four chairs at it; a sofa along its right wall.
 *
 * <p>It stands east of the green, its door to the well across it ({@link #PLACE}). In its own frame: across, along
 * (its front, its door, ahead), up; centimetres, from the ground it stands on.
 */
public final class MainOffice {

    /** How big it is built: as big as the earth house is made, its door and window as wide as they may be. */
    public static final EarthHouse.Size SIZE = new EarthHouse.Size(1_400, 420, 70, 560, 110, 180, 300, 160, true);

    /** Its plinth: so high, reaching so far past its walls, in so many pieces round; its steps so high and so deep, so wide. */
    static final double PLINTH = 45;
    static final double APRON = 260;
    static final int ROUND = 48;
    static final double STEP = 15;
    static final double TREAD = 35;
    static final double STEPS_WIDE = 340;
    /** Its porch: its columns so square, so far out from its walls' foot, so far either side of its door's middle; its canopy so high, so thick. */
    static final double COLUMN = 30;
    static final double PORCH_OUT = 200;
    static final double PORCH_SIDE = 150;
    static final double CANOPY = 385;
    static final double CANOPY_THICK = 15;
    /** Its finial: a base so wide and so high, a spire so wide and so high on it. */
    static final double FINIAL_BASE = 50;
    static final double FINIAL_BASE_HIGH = 30;
    static final double FINIAL = 20;
    static final double FINIAL_HIGH = 90;
    /** How far its furniture stands clear of its walls, at least. */
    static final double CLEAR = 15;
    /** Its floor's top, on its plinth: what stands inside stands on that. */
    static final double FLOOR = PLINTH + Office.FLOOR;

    /** Where it stands: east of the green, on the meadow's ground there, its door to the well. */
    public static final double EAST = 7_500;
    public static final double NORTH = 4_800;

    private static final EarthHouse HOUSE = EarthHouse.of(SIZE);
    private static final Palette PALETTE = EarthHouse.palette().with(Map.of(Role.WALL, Material.LIMESTONE, Role.SPIRE,
            Material.LIMESTONE, Role.ROOF, Material.GLAZED, Role.BASE, Material.LIMESTONE, Role.FRAME, Material.LACQUER,
            Role.STRUCTURE, Material.LACQUER, Role.GLAZING, Material.GLASS));

    private MainOffice() {
    }

    /** What it is called. */
    public static String name() {
        return "Main office";
    }

    /** Where it stands, facing as its door does: to the well. */
    public static Place place() {
        final double yaw = Math.toDegrees(Math.atan2(Village.WELL.x() - EAST, Village.WELL.z() - NORTH));
        return new Place(EAST, NORTH, Meadow.land().height(EAST, NORTH), yaw);
    }

    /** How far its walls' outside is from its middle at their foot; their inside. */
    static double outside() {
        return SIZE.width() / 2;
    }

    static double inside() {
        return outside() - SIZE.thick();
    }

    /** How far it reaches from its middle at its widest: its plinth. */
    static double plinth() {
        return outside() + APRON;
    }

    /** How far its lowest step reaches before it, from its middle. */
    static double reach() {
        return plinth() + (PLINTH / STEP - 1) * TREAD;
    }

    /** All of it: the hut on its plinth, its steps, its porch, its finial, and all it is furnished with. */
    public static List<Solid> solids() {
        final List<Solid> all = new ArrayList<>(raised(HOUSE.solids(PALETTE)));
        all.addAll(grounds());
        all.addAll(interior());
        return all;
    }

    /** The hut as built, standing on its plinth: as high again as that. */
    private static List<Solid> raised(final List<Solid> solids) {
        return solids.stream().map(s -> new Solid(s.name(), s.role(), s.material(), s.corners().stream()
                .map(p -> new Point(p.across(), p.along(), p.up() + PLINTH)).toList())).toList();
    }

    /**
     * What stands round it: its plinth, a ring from under its walls out to its edge (its middle under its floor);
     * its steps; its porch, its columns and its canopy; and its finial.
     */
    static List<Solid> grounds() {
        final List<Solid> out = new ArrayList<>();
        final double under = outside() - 20;
        for (int k = 0; k < ROUND; k++) {
            final double a0 = 360.0 * k / ROUND;
            final double a1 = 360.0 * (k + 1) / ROUND;
            final List<Point> corners = new ArrayList<>(8);
            for (int c = 0; c < 8; c++) {
                corners.add(at((c & 2) == 0 ? under : plinth(), (c & 1) == 0 ? a0 : a1, (c & 4) == 0 ? 0 : PLINTH));
            }
            out.add(new Solid("plinth " + k, Role.BASE, Material.STONE, corners));
        }
        final double half = STEPS_WIDE / 2;
        for (int s = 1; s < PLINTH / STEP; s++) {
            // each a tread lower, a tread further out, only under its own tread: no two sides in one plane
            out.add(Furniture.box("step " + s, Material.STONE, -half, half, stepFrom(s), stepTo(s), 0,
                    PLINTH - s * STEP));
        }
        final double front = outside() + PORCH_OUT;
        for (final double side : new double[] {-1, 1}) {
            final double x = side * PORCH_SIDE;
            out.add(Furniture.box("porch, column", Material.LACQUER, x - COLUMN / 2, x + COLUMN / 2, front - COLUMN / 2,
                    front + COLUMN / 2, PLINTH, CANOPY));
        }
        out.add(Furniture.box("porch, canopy", Material.GLAZED, -PORCH_SIDE - 70, PORCH_SIDE + 70, outside() - 20,
                front + COLUMN / 2 + 40, CANOPY, CANOPY + CANOPY_THICK));
        // its finial on its roof's point, sunk a little into it
        final double top = HOUSE.top() + PLINTH - 10;
        out.add(Furniture.box("finial, base", Material.GLAZED, -FINIAL_BASE / 2, FINIAL_BASE / 2, -FINIAL_BASE / 2,
                FINIAL_BASE / 2, top, top + FINIAL_BASE_HIGH));
        out.add(Furniture.box("finial", Material.GLAZED, -FINIAL / 2, FINIAL / 2, -FINIAL / 2, FINIAL / 2,
                top + FINIAL_BASE_HIGH, top + FINIAL_BASE_HIGH + FINIAL_HIGH));
        return out;
    }

    /** Its desk: where it stands along, so wide, so deep, so high; its chair behind it. */
    static final double DESK_AT = -350;
    static final double DESK_WIDE = 220;
    static final double DESK_DEEP = 100;
    static final double DESK_HIGH = 78;
    static final double EXEC = 62;
    static final double EXEC_BACK = 140;
    static final double GUEST = 50;
    /** Its meeting table: where it stands, so wide across, so deep along. */
    static final double TABLE_X = -380;
    static final double TABLE_Y = 60;
    static final double TABLE_WIDE = 110;
    static final double TABLE_DEEP = 180;
    /** Its sofa: where it stands across, its seat so deep, so long, so high, its back so high and thick. */
    static final double SOFA_X = 420;
    static final double SOFA_Y = -40;
    static final double SOFA_DEEP = 85;
    static final double SOFA_LONG = 220;
    static final double SOFA_SEAT = 42;
    static final double SOFA_BACK = 85;
    static final double SOFA_BACK_THICK = 20;
    /** Its rug: so wide, so deep, so thick, where along. */
    static final double RUG_WIDE = 700;
    static final double RUG_DEEP = 600;
    static final double RUG_THICK = 2;
    static final double RUG_AT = -60;

    /** All it is furnished with, inside: its rug, its desk and the chairs at it, its meeting table and chairs, its sofa. */
    static List<Solid> interior() {
        final List<Solid> in = new ArrayList<>();
        in.add(Furniture.box("rug", Material.LACQUER, -RUG_WIDE / 2, RUG_WIDE / 2, RUG_AT - RUG_DEEP / 2,
                RUG_AT + RUG_DEEP / 2, FLOOR, FLOOR + RUG_THICK));
        // all of it standing on its floor, through the rug where it lies on it
        final double floor = FLOOR;
        // the desk, its top gold; its chair behind it, facing the door; two before it, facing it
        in.addAll(Furniture.table("desk", Material.GLAZED, Material.TIMBER, 0, DESK_AT, DESK_WIDE, DESK_DEEP,
                floor + DESK_HIGH, floor));
        // its front closed between its legs, a hair behind their faces: not in one plane with them
        final double face = DESK_AT + DESK_DEEP / 2 - 1;
        in.add(Furniture.box("desk, front", Material.TIMBER, -DESK_WIDE / 2 + Furniture.LEG,
                DESK_WIDE / 2 - Furniture.LEG, face - 4, face, floor + 15, floor + DESK_HIGH - Furniture.TOP));
        in.addAll(Furniture.chair("desk chair", Material.LACQUER, 0, execAt(), Furniture.Facing.AHEAD, EXEC,
                floor + 48, floor + EXEC_BACK, floor));
        for (final double side : new double[] {-1, 1}) {
            in.addAll(Furniture.chair("guest chair", Material.TIMBER, side * 70, guestAt(), Furniture.Facing.BACK,
                    GUEST, floor + 45, floor + 90, floor));
        }
        // the meeting table, two chairs either side of it along its length, facing across it
        in.addAll(Furniture.table("meeting table", Material.TIMBER, Material.TIMBER, TABLE_X, TABLE_Y, TABLE_WIDE,
                TABLE_DEEP, floor + 75, floor));
        for (final double y : new double[] {TABLE_Y - 45, TABLE_Y + 45}) {
            in.addAll(Furniture.chair("meeting chair", Material.LACQUER, TABLE_X - meetingOut(), y,
                    Furniture.Facing.RIGHT, GUEST, floor + 45, floor + 90, floor));
            in.addAll(Furniture.chair("meeting chair", Material.LACQUER, TABLE_X + meetingOut(), y,
                    Furniture.Facing.LEFT, GUEST, floor + 45, floor + 90, floor));
        }
        // the sofa along the right wall, facing the room
        in.add(Furniture.box("sofa, seat", Material.LACQUER, SOFA_X - SOFA_DEEP / 2, SOFA_X + SOFA_DEEP / 2,
                SOFA_Y - SOFA_LONG / 2, SOFA_Y + SOFA_LONG / 2, floor, floor + SOFA_SEAT));
        in.add(Furniture.box("sofa, back", Material.LACQUER, SOFA_X + SOFA_DEEP / 2, SOFA_X + SOFA_DEEP / 2
                + SOFA_BACK_THICK, SOFA_Y - SOFA_LONG / 2, SOFA_Y + SOFA_LONG / 2, floor, floor + SOFA_BACK));
        return in;
    }

    /**
     * Where step {@code s} (from 1, the top) starts along and ends: its tread a tread further out than the one over it;
     * the top one reaching back under the plinth's edge, so its round meets no gap.
     */
    static double stepFrom(final int s) {
        return s == 1 ? plinth() - 20 : plinth() + (s - 1) * TREAD;
    }

    static double stepTo(final int s) {
        return plinth() + s * TREAD;
    }

    /** Where the desk's chair is along: behind it. */
    static double execAt() {
        return DESK_AT - DESK_DEEP / 2 - 10 - EXEC / 2;
    }

    /** Where the chairs before the desk are along. */
    static double guestAt() {
        return DESK_AT + DESK_DEEP / 2 + 15 + GUEST / 2;
    }

    /** How far out from the meeting table's middle, across, its chairs stand. */
    static double meetingOut() {
        return TABLE_WIDE / 2 + 5 + GUEST / 2;
    }

    /**
     * What it is made of to the simulation: its plinth, solid all through, stood on inside and out; its steps; its
     * porch's columns; the hut's walls and roof on its plinth; its furniture, each piece in the way as its footprint is.
     */
    public static List<Volume> shape() {
        final List<Volume> shape = new ArrayList<>();
        final List<Vec2> round = new ArrayList<>(ROUND);
        for (int k = 0; k < ROUND; k++) {
            final Point p = at(plinth(), 360.0 * k / ROUND, 0);
            round.add(new Vec2(p.across(), p.along()));
        }
        shape.add(new Prism(round, 0, PLINTH));
        for (int s = 1; s < PLINTH / STEP; s++) {
            shape.add(Furniture.footprint(0, (stepFrom(s) + stepTo(s)) / 2, STEPS_WIDE, stepTo(s) - stepFrom(s), 0,
                    PLINTH - s * STEP));
        }
        final double front = outside() + PORCH_OUT;
        for (final double side : new double[] {-1, 1}) {
            shape.add(Furniture.footprint(side * PORCH_SIDE, front, COLUMN, COLUMN, PLINTH, CANOPY));
        }
        for (final Volume v : HOUSE.shape()) {
            shape.add(Volume.placed(v, new Place(0, 0, PLINTH, 0), 1));
        }
        final double floor = FLOOR;
        shape.add(Furniture.footprint(0, DESK_AT, DESK_WIDE, DESK_DEEP, floor, floor + DESK_HIGH));
        shape.add(Furniture.footprint(0, execAt(), EXEC, EXEC, floor, floor + 48));
        for (final double side : new double[] {-1, 1}) {
            shape.add(Furniture.footprint(side * 70, guestAt(), GUEST, GUEST, floor, floor + 45));
        }
        shape.add(Furniture.footprint(TABLE_X, TABLE_Y, TABLE_WIDE, TABLE_DEEP, floor, floor + 75));
        for (final double y : new double[] {TABLE_Y - 45, TABLE_Y + 45}) {
            for (final double side : new double[] {-1, 1}) {
                shape.add(Furniture.footprint(TABLE_X + side * meetingOut(), y, GUEST, GUEST, floor, floor + 45));
            }
        }
        shape.add(Furniture.footprint(SOFA_X + SOFA_BACK_THICK / 2, SOFA_Y, SOFA_DEEP + SOFA_BACK_THICK, SOFA_LONG,
                floor, floor + SOFA_SEAT));
        return shape;
    }

    /** {@code r} out from its middle at heading {@code a} (degrees, from its front to its right), {@code up} high. */
    private static Point at(final double r, final double a, final double up) {
        final double t = Math.toRadians(a);
        return new Point(r * Math.sin(t), r * Math.cos(t), up);
    }
}
