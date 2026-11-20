package com.example.fxsuite.qingming.land;

import com.example.fxsuite.avatar.roads.Mapped;
import com.example.fxsuite.avatar.roads.Profile;
import com.example.fxsuite.avatar.roads.Roadworks;
import com.example.fxsuite.avatar.roads.Section;
import com.example.fxsuite.avatar.roads.Sections;
import com.example.fxsuite.avatar.sim.Channel;
import com.example.fxsuite.avatar.sim.Contour;
import com.example.fxsuite.avatar.sim.Course;
import com.example.fxsuite.avatar.sim.Land;
import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.avatar.sim.River;
import com.example.fxsuite.avatar.sim.RoadMap;
import com.example.fxsuite.avatar.sim.Terrain;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.avatar.sim.Vec3;
import com.example.fxsuite.avatar.sim.Volume;

import java.util.List;
import java.util.Map;

/**
 * The reach of the Bian River (汴河) from the Rainbow Bridge (虹桥) to the city gate, as Zhang Zeduan's scroll draws it:
 * its plan read from the scroll, its sizes from what is written and dug up. In centimetres, x east, z north, the
 * bridge's middle at the origin; 1,100 m east to west, 500 m north to south.
 *
 * <ul>
 * <li><b>Its plain</b>, level, as the ground round Kaifeng is, roughened a little; level pads where the bridge's ends,
 * the city's wall and its gate stand, and the quay before the warehouses.
 * <li><b>The river</b> ({@link Rivers}), coming in from the east, under the bridge, bending away south before the
 * gate, as the scroll has it turn away from the street: its water 1.86 m deep, the 6 chi the Song history (宋史) has
 * it held at for loaded boats; 22 m across between its banks' tops, as wide as the bridge's span, about 20 to 22 m by
 * the bridge's historians' reckoning (as the river within the city was 25 to 28 m where it was dug at 州桥); its banks
 * 2.1 m over its water. Its towpaths along both banks, as wide as the dike tops the Song history has kept along it
 * within the capital, 4.65 m; stopping short of the bridge, which they pass under in the scroll.
 * <li><b>Its road</b> from the country in the east, over the bridge, along the street to the gate and through it: 8 m
 * wide, as the bridge is, of earth; the bridge carrying it over the river, built on its own.
 * <li><b>The city's wall</b>, here the old city's, the gate in it the Palace Museum's reading of the scroll (东角子门,
 * the bridge then 上土桥, a short way outside it): its line 540 m west of the bridge, the scroll's distance shortened
 * as the scroll shortens it; the world's west edge 60 m inside it. Both are built on their own.
 * <li><b>Two motorcycles</b>, the engine's cruiser and boxer, parked on the street by the start, ready to ride: not of
 * the scroll's time, but to get about the reach on.
 * </ul>
 */
public final class Reach {

    private Reach() {
    }

    /** Its edges: west, east, south, north. */
    public static final double WEST = -60_000;
    public static final double EAST = 50_000;
    public static final double SOUTH = -25_000;
    public static final double NORTH = 25_000;
    /** Where the city's wall runs, north to south, and the gate in it. */
    public static final double WALL = -54_000;
    public static final Vec2 GATE = new Vec2(WALL, 3_000);
    /** Where the avatar starts: on the street north of the bridge, facing it. */
    public static final Vec3 START = new Vec3(-6_000, 0, 3_600);
    public static final double START_YAW = 111;
    /** The motorcycles parked on the street just ahead of the start, side by side, facing the bridge. */
    public static final List<Course.Chassis> BIKES = List.of(Course.CRUISER, Course.BOXER);
    private static final double BIKES_AHEAD = 400;
    private static final double BIKES_APART = 240;

    /**
     * Its level pads, each a rectangle (west, east, south, north): the wall's line, the gate's, the bridge's ends, the
     * quay's and the warehouses' ground on the north bank.
     */
    static final double[][] PADS = {
            {WALL - 1_200, WALL + 1_200, SOUTH + 100, NORTH - 100},
            {WALL - 3_000, WALL + 3_000, GATE.z() - 2_500, GATE.z() + 2_500},
            {-700, 700, -2_400, -1_200},
            {-700, 700, 1_200, 2_400},
            {4_000, 7_600, 1_000, 3_200}};

    /** Its plain, its pads level, surveyed every 2 m: a little rough elsewhere, by 20 cm or so, 30 m apart. */
    public static Terrain terrain() {
        final List<Contour> pads = java.util.Arrays.stream(PADS).map(p -> new Contour(0, List.of(new Vec2(p[0], p[2]),
                new Vec2(p[1], p[2]), new Vec2(p[1], p[3]), new Vec2(p[0], p[3])), true)).toList();
        return new Terrain("the reach", WEST, SOUTH, EAST, NORTH, 200, 0, 1_120,
                new Terrain.Noise(20, 3_000, 2), pads, List.of());
    }

    /** Its ground before its roads: its plain, the river's channel cut into it. Worked out once. */
    public static Land ground() {
        return Laid.GROUND;
    }

    /** Its land: its ground as its roads leave it. */
    public static Land land() {
        return Laid.LAND;
    }

    /** Its roads, laid over its ground. */
    public static Roadworks roads() {
        return Laid.ROADS;
    }

    /** Its river's water over its channel's bed, as wide as it is where its top meets the banks. */
    public static List<River> rivers() {
        final Channel c = Rivers.channel();
        return List.of(new River(Rivers.line(), 2 * c.across(Rivers.DEPTH - Rivers.WATER), Rivers.WATER));
    }

    /**
     * Its kinds of road across, unmarked, as a Song street is: the road, earth either side of its middle, falling 2% to
     * its edges; a towpath, earth and level, its sides steep, so that where it runs along the river's bank it throws
     * no bank of its own down into the channel.
     */
    static final String SECTIONS = """
            section "road" grade=8% vertical=30m fill=2 cut=2 ditch=0,0 bank=4m cutting=4m
              path      4m     earth        fall=2%
              |
              path      4m     earth        fall=2%

            section "towpath" grade=8% vertical=20m fill=0.5 cut=2 ditch=0,0 bank=3m cutting=3m
              path      2.3m   earth
              |
              path      2.3m   earth
            """;

    private static final class Laid {
        static final Land GROUND = new Land(terrain().survey(), List.of(Rivers.channel()));
        static final Roadworks ROADS = lay();
        static final Land LAND = GROUND.with(ROADS.land(Double.NaN));

        private static Roadworks lay() {
            final Map<String, Section> kinds = Sections.parse(SECTIONS);
            final Profile raw = (x, z, below) -> GROUND.height(x, z);
            return Roadworks.design(raw, Mapped.plans(RoadMap.parse(Streets.text()), (way, kind) -> kinds.get(kind),
                    raw, null));
        }
    }

    /**
     * Its motorcycles ({@link #BIKES}), each ready to ride: side by side on the street just ahead of the start, facing
     * the bridge as the avatar does, standing on the street, each its own colour.
     */
    public static List<Course.Piece> parked() {
        final double r = Math.toRadians(START_YAW);
        // ahead (sin, cos), to the right (cos, -sin), as the game turns a thing
        final double aheadX = START.x() + Math.sin(r) * BIKES_AHEAD;
        final double aheadZ = START.z() + Math.cos(r) * BIKES_AHEAD;
        final List<Course.Piece> parked = new java.util.ArrayList<>();
        for (int k = 0; k < BIKES.size(); k++) {
            final double side = (k - (BIKES.size() - 1) / 2.0) * BIKES_APART;
            final double x = aheadX + Math.cos(r) * side;
            final double z = aheadZ - Math.sin(r) * side;
            final Course.Chassis bike = BIKES.get(k);
            parked.add(Course.Piece.vehicle(bike, null, new Place(x, z, land().height(x, z) + bike.ride(), START_YAW),
                    k));
        }
        return parked;
    }

    /** Its ground, its river, its roads stood on, its motorcycles; the avatar on the street, facing the bridge. */
    public static void lay(final Course.Layout world) {
        world.start(START).land(land()).rivers(rivers()).pieces(parked())
                .ways(roads().roads().stream().flatMap(r -> r.pavements().stream()).map(p -> (Volume) p).toList())
                .ruled(false);
    }
}
