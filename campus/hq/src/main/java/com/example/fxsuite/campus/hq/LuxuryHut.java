package com.example.fxsuite.campus.hq;

import com.example.fxsuite.avatar.build.EarthHouse;
import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Palette;
import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.avatar.sim.Prism;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.avatar.sim.Volume;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A hut of luxury, as no homesteader built one: the earth house ({@link EarthHouse}) made big ({@code size}), finished
 * as {@code finish} says, raised on a round stone plinth 45 cm high reaching {@code apron} past its walls, steps up to
 * it before its door a step's height each; a porch of two columns under a canopy over its door; a finial on its roof's
 * point; and furnished inside as {@code interior} says, on a rug.
 *
 * <p>In its own frame: across, along (its front, its door, ahead), up; centimetres, from the ground it stands on.
 *
 * @param name     what it is called
 * @param size     how big the hut is built
 * @param finish   what it is finished in
 * @param apron    how far its plinth reaches past its walls
 * @param interior how it is furnished
 */
public record LuxuryHut(String name, EarthHouse.Size size, Finish finish, double apron, Interior interior) {

    /**
     * What it is finished in: its walls and chimney; its roof; its woodwork, its rafters and its porch's columns; its
     * floor; its plinth and steps; its canopy; its finial; its rug; what its chairs and sofa are covered in; its desk's
     * top.
     */
    public record Finish(Material walls, Material roof, Material wood, Material floor, Material plinth,
                         Material canopy, Material finial, Material rug, Material upholstery, Material deskTop) {
    }

    /** How it is furnished. */
    public sealed interface Interior permits Executive, Team {
    }

    /**
     * An executive's office: a desk, its high-backed chair behind it facing the door, two chairs before it for whoever
     * calls; a meeting table on its left with four chairs at it; a sofa along its right wall. For a hut 14 m across.
     */
    public record Executive() implements Interior {
    }

    /**
     * A department's room: its lead's desk at its back, facing the door, its chair behind it; a team table down its
     * middle, {@code seats} chairs either side of it; a sofa along one wall ({@code sofa}: -1 its left, 1 its right, 0
     * none).
     */
    public record Team(int seats, int sofa) implements Interior {
        public Team {
            if (seats < 1 || seats > 3 || Math.abs(sofa) > 1) {
                throw new IllegalArgumentException("one to three seats a side; a sofa left, right or none");
            }
        }
    }

    /** Its plinth: so high, in so many pieces round; its steps so high and so deep. */
    static final double PLINTH = 45;
    static final int ROUND = 48;
    static final double STEP = 15;
    static final double TREAD = 35;
    /** Its porch: its columns so square, so far inside its plinth's edge; its canopy so far over its door, so thick. */
    static final double COLUMN = 30;
    static final double PORCH_IN = 60;
    static final double CANOPY_OVER = 40;
    static final double CANOPY_THICK = 15;
    /** Its finial: a base so wide and so high, a spire so wide and so high on it. */
    static final double FINIAL_BASE = 50;
    static final double FINIAL_BASE_HIGH = 30;
    static final double FINIAL = 20;
    static final double FINIAL_HIGH = 90;
    /** How far its furniture stands clear of its walls, at least. */
    static final double CLEAR = 15;
    /** How high the earth house's floor of beaten earth is; its floor's top, on its plinth: what stands inside stands on that. */
    static final double EARTH = 4;
    static final double FLOOR = PLINTH + EARTH;
    /** How thick its rug is. */
    static final double RUG_THICK = 2;

    public LuxuryHut {
        if (size.walls() - size.doorHigh() < CANOPY_OVER + CANOPY_THICK + 40) {
            throw new IllegalArgumentException(name + ": room for its canopy over its door, under its eave");
        }
        if (apron < PORCH_IN + COLUMN + 100) {
            throw new IllegalArgumentException(name + ": room on its plinth for its porch");
        }
    }

    /** The hut as built. */
    EarthHouse house() {
        return EarthHouse.of(size);
    }

    /** How it is made: its finish, as the earth house's palette takes it. */
    Palette palette() {
        return EarthHouse.palette().with(Map.of(Role.WALL, finish.walls(), Role.SPIRE, finish.walls(), Role.ROOF,
                finish.roof(), Role.BASE, finish.floor(), Role.FRAME, finish.wood(), Role.STRUCTURE, finish.wood(),
                Role.GLAZING, Material.GLASS));
    }

    /** How far its walls' outside is from its middle at their foot; their inside. */
    double outside() {
        return size.width() / 2;
    }

    double inside() {
        return outside() - size.thick();
    }

    /** How far it reaches from its middle at its widest: its plinth. */
    double plinth() {
        return outside() + apron;
    }

    /** How far its lowest step reaches before it, from its middle. */
    double reach() {
        return plinth() + (PLINTH / STEP - 1) * TREAD;
    }

    /** How wide its steps are: wider than its door either side. */
    double stepsWide() {
        return size.door() + 160;
    }

    /** How far either side of its door's middle its porch's columns stand; how far out from its middle. */
    double porchSide() {
        return size.door() / 2 + 60;
    }

    double porchOut() {
        return plinth() - PORCH_IN;
    }

    /** How high its canopy's underside is: so far over its door. */
    double canopy() {
        return PLINTH + size.doorHigh() + CANOPY_OVER;
    }

    /**
     * Where step {@code s} (from 1, the top) starts along and ends: its tread a tread further out than the one over it;
     * the top one reaching back under the plinth's edge, so its round meets no gap.
     */
    double stepFrom(final int s) {
        return s == 1 ? plinth() - 20 : plinth() + (s - 1) * TREAD;
    }

    double stepTo(final int s) {
        return plinth() + s * TREAD;
    }

    /** All of it: the hut on its plinth, its steps, its porch, its finial, and all it is furnished with. */
    public List<Solid> solids() {
        final List<Solid> all = new ArrayList<>(raised(house().solids(palette())));
        all.addAll(grounds());
        all.addAll(furnished());
        return all;
    }

    /** The hut as built, standing on its plinth: as high again as that. */
    private static List<Solid> raised(final List<Solid> solids) {
        return solids.stream().map(s -> new Solid(s.name(), s.role(), s.material(), s.corners().stream()
                .map(p -> new Point(p.across(), p.along(), p.up() + PLINTH)).toList())).toList();
    }

    /**
     * What stands round it: its plinth, a ring from under its walls out to its edge (its middle under its floor);
     * its steps; its porch, its columns and its canopy; and its finial on its roof's point.
     */
    List<Solid> grounds() {
        final List<Solid> out = new ArrayList<>();
        final double under = outside() - 20;
        for (int k = 0; k < ROUND; k++) {
            final double a0 = 360.0 * k / ROUND;
            final double a1 = 360.0 * (k + 1) / ROUND;
            final List<Point> corners = new ArrayList<>(8);
            for (int c = 0; c < 8; c++) {
                corners.add(at((c & 2) == 0 ? under : plinth(), (c & 1) == 0 ? a0 : a1, (c & 4) == 0 ? 0 : PLINTH));
            }
            out.add(new Solid("plinth " + k, Role.BASE, finish.plinth(), corners));
        }
        final double half = stepsWide() / 2;
        for (int s = 1; s < PLINTH / STEP; s++) {
            // each a tread lower, a tread further out, only under its own tread: no two sides in one plane
            out.add(Furniture.box("step " + s, finish.plinth(), -half, half, stepFrom(s), stepTo(s), 0,
                    PLINTH - s * STEP));
        }
        for (final double side : new double[] {-1, 1}) {
            final double x = side * porchSide();
            out.add(Furniture.box("porch, column", finish.wood(), x - COLUMN / 2, x + COLUMN / 2,
                    porchOut() - COLUMN / 2, porchOut() + COLUMN / 2, PLINTH, canopy()));
        }
        out.add(Furniture.box("porch, canopy", finish.canopy(), -porchSide() - 70, porchSide() + 70, outside() - 20,
                porchOut() + COLUMN / 2 + 40, canopy(), canopy() + CANOPY_THICK));
        // its finial on its roof's point, sunk a little into it
        final double top = house().top() + PLINTH - 10;
        out.add(Furniture.box("finial, base", finish.finial(), -FINIAL_BASE / 2, FINIAL_BASE / 2, -FINIAL_BASE / 2,
                FINIAL_BASE / 2, top, top + FINIAL_BASE_HIGH));
        out.add(Furniture.box("finial", finish.finial(), -FINIAL / 2, FINIAL / 2, -FINIAL / 2, FINIAL / 2,
                top + FINIAL_BASE_HIGH, top + FINIAL_BASE_HIGH + FINIAL_HIGH));
        return out;
    }

    /** All it is furnished with, inside: its rug, and what stands on its floor, through the rug where it lies on it. */
    List<Solid> furnished() {
        return switch (interior) {
            case Executive e -> executive();
            case Team t -> team(t);
        };
    }

    /** What stands in its way inside: each piece of its furniture as its footprint is, from its floor up. */
    List<Prism> footprints() {
        return switch (interior) {
            case Executive e -> executiveFootprints();
            case Team t -> teamFootprints(t);
        };
    }

    // ---------------------------------------------------------------- an executive's office

    /** Its desk: where it stands along, so wide, so deep, so high; its chair behind it; the chairs before it. */
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
    /** Its rug: so wide, so deep, where along. */
    static final double RUG_WIDE = 700;
    static final double RUG_DEEP = 600;
    static final double RUG_AT = -60;

    private List<Solid> executive() {
        final List<Solid> in = new ArrayList<>();
        in.add(rug(RUG_WIDE, RUG_DEEP, RUG_AT));
        in.addAll(desk(DESK_AT, DESK_WIDE, DESK_DEEP, DESK_HIGH));
        in.addAll(Furniture.chair("desk chair", finish.upholstery(), 0, execAt(), Furniture.Facing.AHEAD, EXEC,
                FLOOR + 48, FLOOR + EXEC_BACK, FLOOR));
        for (final double side : new double[] {-1, 1}) {
            in.addAll(Furniture.chair("guest chair", Material.TIMBER, side * 70, guestAt(), Furniture.Facing.BACK,
                    GUEST, FLOOR + 45, FLOOR + 90, FLOOR));
        }
        // the meeting table, two chairs either side of it along its length, facing across it
        in.addAll(Furniture.table("meeting table", Material.TIMBER, Material.TIMBER, TABLE_X, TABLE_Y, TABLE_WIDE,
                TABLE_DEEP, FLOOR + 75, FLOOR));
        for (final double y : new double[] {TABLE_Y - 45, TABLE_Y + 45}) {
            in.addAll(Furniture.chair("meeting chair", finish.upholstery(), TABLE_X - meetingOut(), y,
                    Furniture.Facing.RIGHT, GUEST, FLOOR + 45, FLOOR + 90, FLOOR));
            in.addAll(Furniture.chair("meeting chair", finish.upholstery(), TABLE_X + meetingOut(), y,
                    Furniture.Facing.LEFT, GUEST, FLOOR + 45, FLOOR + 90, FLOOR));
        }
        in.addAll(sofa(1, SOFA_X, SOFA_Y));
        return in;
    }

    private List<Prism> executiveFootprints() {
        final List<Prism> in = new ArrayList<>();
        in.add(Furniture.footprint(0, DESK_AT, DESK_WIDE, DESK_DEEP, FLOOR, FLOOR + DESK_HIGH));
        in.add(Furniture.footprint(0, execAt(), EXEC, EXEC, FLOOR, FLOOR + 48));
        for (final double side : new double[] {-1, 1}) {
            in.add(Furniture.footprint(side * 70, guestAt(), GUEST, GUEST, FLOOR, FLOOR + 45));
        }
        in.add(Furniture.footprint(TABLE_X, TABLE_Y, TABLE_WIDE, TABLE_DEEP, FLOOR, FLOOR + 75));
        for (final double y : new double[] {TABLE_Y - 45, TABLE_Y + 45}) {
            for (final double side : new double[] {-1, 1}) {
                in.add(Furniture.footprint(TABLE_X + side * meetingOut(), y, GUEST, GUEST, FLOOR, FLOOR + 45));
            }
        }
        in.add(sofaFootprint(1, SOFA_X, SOFA_Y));
        return in;
    }

    /** Where the executive's chair is along: behind its desk. */
    static double execAt() {
        return DESK_AT - DESK_DEEP / 2 - 10 - EXEC / 2;
    }

    /** Where the chairs before the executive's desk are along. */
    static double guestAt() {
        return DESK_AT + DESK_DEEP / 2 + 15 + GUEST / 2;
    }

    /** How far out from the meeting table's middle, across, its chairs stand. */
    static double meetingOut() {
        return TABLE_WIDE / 2 + 5 + GUEST / 2;
    }

    // ---------------------------------------------------------------- a department's room

    /** Its lead's desk: so wide, so deep; its team table so wide, so much longer a seat; a seat's chair so square. */
    static final double LEAD_WIDE = 180;
    static final double LEAD_DEEP = 90;
    static final double TEAM_WIDE = 100;
    static final double SEAT_ALONG = 80;
    static final double SEAT = 45;
    /** How far apart the lead's desk and the team table are. */
    static final double APART = 90;

    /** Where the lead's desk stands along: so far back toward the back wall, as far as the hut is big. */
    double leadAt() {
        return -0.55 * inside();
    }

    /** Where the lead's chair stands along: behind the desk. */
    double leadChairAt() {
        return leadAt() - LEAD_DEEP / 2 - 10 - EXEC / 2;
    }

    /** How long the team table is, along, for {@code seats} a side; where its middle is. */
    static double teamLong(final int seats) {
        return SEAT_ALONG * seats + 40;
    }

    double teamAt(final int seats) {
        return leadAt() + LEAD_DEEP / 2 + APART + teamLong(seats) / 2;
    }

    /** Where the team's chairs stand along, a seat apart about the table's middle; how far out across. */
    List<Double> seatsAt(final int seats) {
        final List<Double> at = new ArrayList<>();
        for (int k = 0; k < seats; k++) {
            at.add(teamAt(seats) + (k - (seats - 1) / 2.0) * SEAT_ALONG);
        }
        return at;
    }

    static double seatOut() {
        return TEAM_WIDE / 2 + 5 + SEAT / 2;
    }

    /** Where its sofa's seat stands across: its back's outer corners clear of its walls. */
    double sofaX() {
        final double far = Math.abs(SOFA_Y) + SOFA_LONG / 2;
        final double r = inside() - CLEAR;
        return Math.sqrt(r * r - far * far) - SOFA_BACK_THICK - SOFA_DEEP / 2 - 1;
    }

    private List<Solid> team(final Team t) {
        final List<Solid> in = new ArrayList<>();
        final double r = inside();
        in.add(rug(1.1 * r, 1.2 * r, -40));
        in.addAll(desk(leadAt(), LEAD_WIDE, LEAD_DEEP, DESK_HIGH));
        in.addAll(Furniture.chair("lead's chair", finish.upholstery(), 0, leadChairAt(), Furniture.Facing.AHEAD, EXEC,
                FLOOR + 48, FLOOR + EXEC_BACK, FLOOR));
        in.addAll(Furniture.table("team table", Material.TIMBER, Material.TIMBER, 0, teamAt(t.seats()), TEAM_WIDE,
                teamLong(t.seats()), FLOOR + 75, FLOOR));
        for (final double y : seatsAt(t.seats())) {
            in.addAll(Furniture.chair("team chair", finish.upholstery(), -seatOut(), y, Furniture.Facing.RIGHT, SEAT,
                    FLOOR + 45, FLOOR + 90, FLOOR));
            in.addAll(Furniture.chair("team chair", finish.upholstery(), seatOut(), y, Furniture.Facing.LEFT, SEAT,
                    FLOOR + 45, FLOOR + 90, FLOOR));
        }
        if (t.sofa() != 0) {
            in.addAll(sofa(t.sofa(), sofaX(), SOFA_Y));
        }
        return in;
    }

    private List<Prism> teamFootprints(final Team t) {
        final List<Prism> in = new ArrayList<>();
        in.add(Furniture.footprint(0, leadAt(), LEAD_WIDE, LEAD_DEEP, FLOOR, FLOOR + DESK_HIGH));
        in.add(Furniture.footprint(0, leadChairAt(), EXEC, EXEC, FLOOR, FLOOR + 48));
        in.add(Furniture.footprint(0, teamAt(t.seats()), TEAM_WIDE, teamLong(t.seats()), FLOOR, FLOOR + 75));
        for (final double y : seatsAt(t.seats())) {
            for (final double side : new double[] {-1, 1}) {
                in.add(Furniture.footprint(side * seatOut(), y, SEAT, SEAT, FLOOR, FLOOR + 45));
            }
        }
        if (t.sofa() != 0) {
            in.add(sofaFootprint(t.sofa(), sofaX(), SOFA_Y));
        }
        return in;
    }

    // ---------------------------------------------------------------- what both have

    /** A rug so wide and so deep, its middle so far along, on its floor. */
    private Solid rug(final double wide, final double deep, final double at) {
        return Furniture.box("rug", finish.rug(), -wide / 2, wide / 2, at - deep / 2, at + deep / 2, FLOOR,
                FLOOR + RUG_THICK);
    }

    /** A desk, its middle so far along, so wide, so deep, so high: its top as its finish says, its front closed. */
    private List<Solid> desk(final double at, final double wide, final double deep, final double high) {
        final List<Solid> d = new ArrayList<>(Furniture.table("desk", finish.deskTop(), Material.TIMBER, 0, at, wide,
                deep, FLOOR + high, FLOOR));
        // its front closed between its legs, a hair behind their faces: not in one plane with them
        final double face = at + deep / 2 - 1;
        d.add(Furniture.box("desk, front", Material.TIMBER, -wide / 2 + Furniture.LEG, wide / 2 - Furniture.LEG,
                face - 4, face, FLOOR + 15, FLOOR + high - Furniture.TOP));
        return d;
    }

    /** A sofa along its {@code side} wall (1 its right, -1 its left), its seat's middle so far out, facing the room. */
    private List<Solid> sofa(final int side, final double x, final double y) {
        final double seat0 = side * x - SOFA_DEEP / 2;
        final double seat1 = side * x + SOFA_DEEP / 2;
        final double back0 = side > 0 ? seat1 : seat0 - SOFA_BACK_THICK;
        final double back1 = side > 0 ? seat1 + SOFA_BACK_THICK : seat0;
        return List.of(
                Furniture.box("sofa, seat", finish.upholstery(), seat0, seat1, y - SOFA_LONG / 2, y + SOFA_LONG / 2,
                        FLOOR, FLOOR + SOFA_SEAT),
                Furniture.box("sofa, back", finish.upholstery(), back0, back1, y - SOFA_LONG / 2, y + SOFA_LONG / 2,
                        FLOOR, FLOOR + SOFA_BACK));
    }

    private static Prism sofaFootprint(final int side, final double x, final double y) {
        return Furniture.footprint(side * (x + SOFA_BACK_THICK / 2), y, SOFA_DEEP + SOFA_BACK_THICK, SOFA_LONG, FLOOR,
                FLOOR + SOFA_SEAT);
    }

    /**
     * What it is made of to the simulation: its plinth, solid all through, stood on inside and out; its steps; its
     * porch's columns; the hut's walls and roof on its plinth; its furniture, each piece in the way as its footprint is.
     */
    public List<Volume> shape() {
        final List<Volume> shape = new ArrayList<>();
        final List<Vec2> round = new ArrayList<>(ROUND);
        for (int k = 0; k < ROUND; k++) {
            final Point p = at(plinth(), 360.0 * k / ROUND, 0);
            round.add(new Vec2(p.across(), p.along()));
        }
        shape.add(new Prism(round, 0, PLINTH));
        for (int s = 1; s < PLINTH / STEP; s++) {
            shape.add(Furniture.footprint(0, (stepFrom(s) + stepTo(s)) / 2, stepsWide(), stepTo(s) - stepFrom(s), 0,
                    PLINTH - s * STEP));
        }
        for (final double side : new double[] {-1, 1}) {
            shape.add(Furniture.footprint(side * porchSide(), porchOut(), COLUMN, COLUMN, PLINTH, canopy()));
        }
        for (final Volume v : house().shape()) {
            shape.add(Volume.placed(v, new Place(0, 0, PLINTH, 0), 1));
        }
        shape.addAll(footprints());
        return shape;
    }

    /** {@code r} out from its middle at heading {@code a} (degrees, from its front to its right), {@code up} high. */
    private static Point at(final double r, final double a, final double up) {
        final double t = Math.toRadians(a);
        return new Point(r * Math.sin(t), r * Math.cos(t), up);
    }
}
