package com.example.fxsuite.qingming.structures;

import hue.captains.singapura.tao.ontology.FunctionalObject;
import com.example.fxsuite.avatar.sim.Course;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.building.Atlas;
import com.example.fxsuite.building.chinese.SongCityWall;
import com.example.fxsuite.building.chinese.SongFarmhouse;
import com.example.fxsuite.building.chinese.SongGateTower;
import com.example.fxsuite.building.chinese.SongQuay;
import com.example.fxsuite.building.chinese.SongTeaHouse;
import com.example.fxsuite.building.spec.CityWallSpec;
import com.example.fxsuite.building.spec.FarmhouseSpec;
import com.example.fxsuite.building.spec.GateTowerSpec;
import com.example.fxsuite.building.spec.HallSpec;
import com.example.fxsuite.building.spec.QuaySpec;
import com.example.fxsuite.building.spec.Roofing;
import com.example.fxsuite.historian.Chinese;
import com.example.fxsuite.historian.ChineseEras;
import com.example.fxsuite.historian.Years;
import com.example.fxsuite.qingming.land.Reach;
import com.example.fxsuite.qingming.land.Rivers;

import java.util.List;

/**
 * What stands in the reach, declared on a map ({@link Atlas}): the Chinese of the Northern Song, about 1120, as the
 * scroll shows them. Each thing is a place and a specification; how it is built is the Song's own
 * ({@code fxsuite-building-chinese}), bound once.
 *
 * <ul>
 * <li><b>The Rainbow Bridge</b> (虹桥), at the reach's origin, square across the river: 20 m between its end walls,
 * its deck 8 m wide and 3.6 m over the street at its crown, its arch of 21 groups of logs 40 cm thick 45 cm apart, as
 * its historians reckon it; its feet a little over the water.
 * <li><b>The shops along the street</b>, seven a side on its straight stretch west of the bridge, each fronting the
 * street 3 m back from its edge, the north row facing south and the south row north: two to five bays each, as the
 * scroll's are of all sizes, most tiled, some thatched.
 * <li><b>The wine house</b> (脚店), of two storeys, on the street's north side between the bridge and the shops, as
 * the scroll has one by the bridge; its <b>festive gateway</b> (彩楼欢门) before its door.
 * <li><b>A tea house</b> at each end of the bridge, beside the road, open to it; <b>two warehouses</b> on the north
 * bank east of the bridge, facing the towpath and the river.
 * <li><b>Stalls</b>, under awnings and umbrellas: two at each end of the bridge beside the road, three along the
 * street before the shops begin.
 * <li><b>The city gate</b> (城门) where the street meets the wall, its outer face east, toward the bridge: its base
 * 30 m along the wall and 9.6 m high, its passage 5.6 m wide and 19.3 m through, as the Song gate dug up at Kaifeng
 * (景龙门) is reported to measure; a tower of five bays on it, its way up a ramp inside the wall
 * south of it. <b>The wall</b> (城墙) either side of it to the reach's
 * edges, 9 m high: an estimate, the scroll's being a little lower than its gate's base.
 * <li><b>The quay</b> along the north bank before the warehouses, 30 m of it in two stretches, its face standing in
 * the water, a flight of steps down each to a landing over it.
 * <li><b>The country</b> at the reach's east end, where the scroll begins: four farmsteads either side of the road,
 * each a house, most thatched, and its yard fenced round, its gateway toward the road; a thatched tea house by the
 * road.
 * </ul>
 */
public final class Fixtures implements FunctionalObject {

    /** The first key its things are drawn by, each taking one in the order it was put. */
    public static final int KEYS = 700;
    /** The street's straight stretch: its middle so far north, its shops from so far east going west. */
    static final double STREET = 4_200;
    static final double FROM = -11_500;
    /** A shop's front so far back from the street's middle; the room between one shop's gable and the next's. */
    static final double BACK = 400 + 300;
    static final double BETWEEN = 250;
    /** The wine house's middle, so far east; a road's half width and a stall's room beside it. */
    static final double WINE_HOUSE = -9_500;
    static final double ROADSIDE = 400 + 60;
    /** The gate, as dug up; the wall so high; its ends so far short of the reach's edges. */
    static final GateTowerSpec<Chinese, ChineseEras.NorthernSong> GATE = new GateTowerSpec<>(3_000, 1_930, 960, 560,
            5, GateTowerSpec.Ascent.RIGHT);
    static final double RAMPART = 900;
    static final double SHORT = 200;
    /** The quay's line so far from the river's middle, a little behind its bank's top; its ends and joint so far east.
     */
    static final double QUAY_LINE = Rivers.TOP / 2 + 20;
    static final double QUAY_EAST = 7_300;
    static final double QUAY_JOINT = 5_800;
    static final double QUAY_WEST = 4_300;
    /** A farmstead's yard's front so far from the road's edge; the road's half width. */
    static final double FARM_BACK = 600;
    static final double ROAD = 400;

    /** The reach's map, declared once: what is on it, and what is wrong with it, the same every time. */
    public static final Fixtures INSTANCE = new Fixtures();

    /** The map: declared here, in the constructor, and never changed after. */
    private final Atlas<Chinese, ChineseEras.NorthernSong> atlas;

    private Fixtures() {
        atlas = new Atlas<>("the reach of the Bian", Chinese.IT, new ChineseEras.NorthernSong(),
                Years.INSTANCE.ce(1120));
        Street.INSTANCE.lay(atlas);
        gate(atlas);
        quay(atlas);
        country(atlas);
    }

    /** Everything on the map, in the order it was put. */
    public List<Atlas.Fixture> fixtures() {
        return atlas.fixtures();
    }

    /** Everything wrong with the map, all of it; none if nothing is. */
    public List<String> problems() {
        return atlas.problems();
    }

    /** Everything on the map as pieces of the world, each standing on the reach's land. */
    public List<Course.Piece> pieces() {
        return atlas.pieces(Reach.INSTANCE.land(), KEYS);
    }

    /**
     * The gate where the street meets the wall, its outer face east; the wall north and south of it, each run so that
     * its outer face is east too.
     */
    private void gate(final Atlas<Chinese, ChineseEras.NorthernSong> reach) {
        reach.with(new SongGateTower()).at("the city gate", Reach.WALL, Reach.GATE_Z, 90, GATE);
        reach.with(new SongCityWall())
                .between("the wall north of the gate", Reach.WALL, Reach.NORTH - SHORT, Reach.WALL,
                        Reach.GATE_Z + GATE.wide() / 2, length -> new CityWallSpec<>(length, RAMPART))
                .between("the wall south of the gate", Reach.WALL, Reach.GATE_Z - GATE.wide() / 2, Reach.WALL,
                        Reach.SOUTH + SHORT, length -> new CityWallSpec<>(length, RAMPART));
    }

    /** The quay along the north bank, each stretch run west so that its face is to the river. */
    private void quay(final Atlas<Chinese, ChineseEras.NorthernSong> reach) {
        final List<Vec2> bank = Rivers.INSTANCE.beside(true, QUAY_LINE);
        final Atlas.Maker<QuaySpec<Chinese, ChineseEras.NorthernSong>> quay = reach.with(new SongQuay());
        final double[] along = {QUAY_EAST, QUAY_JOINT, QUAY_WEST};
        for (int k = 0; k + 1 < along.length; k++) {
            final Vec2 east = at(bank, along[k]);
            final Vec2 west = at(bank, along[k + 1]);
            quay.between("the quay " + (k + 1), east.x(), east.z(), west.x(), west.z(), length -> new QuaySpec<>(
                    length, 200, Rivers.DEPTH, Rivers.DEPTH - Rivers.WATER - 14, 1));
        }
    }

    /** The farmsteads either side of the road in the country, each facing it; a tea house by it. */
    private void country(final Atlas<Chinese, ChineseEras.NorthernSong> reach) {
        // each so far east, the road's middle there so far north, which side of it (1 north, -1 south)
        final double[][] farms = {{45_000, -5_700, -1}, {41_000, -5_460, 1}, {37_000, -5_220, -1}, {31_000, -4_860, 1}};
        final List<FarmhouseSpec<Chinese, ChineseEras.NorthernSong>> houses = List.of(
                new FarmhouseSpec<>(3, 330, 600, Roofing.THATCH, 800), new FarmhouseSpec<>(2, 340, 560, Roofing.THATCH,
                        700), new FarmhouseSpec<>(4, 320, 620, Roofing.THATCH, 900),
                new FarmhouseSpec<>(3, 350, 580, Roofing.TILE, 800));
        final Atlas.Maker<FarmhouseSpec<Chinese, ChineseEras.NorthernSong>> makers = reach.with(new SongFarmhouse());
        for (int k = 0; k < farms.length; k++) {
            final FarmhouseSpec<Chinese, ChineseEras.NorthernSong> house = houses.get(k);
            final double side = farms[k][2];
            makers.at("farmstead " + (k + 1), farms[k][0], farms[k][1] + side * (ROAD + FARM_BACK + house.yard()
                    + house.depth() / 2), side > 0 ? 180 : 0, house);
        }
        reach.with(new SongTeaHouse()).at("the roadside tea house", 24_000, -4_480 + BACK + 300, 180,
                new HallSpec<>(3, 380, 600, Roofing.THATCH));
    }

    /** Where {@code line}, running east to west, is so far east: between the two of its points either side. */
    private Vec2 at(final List<Vec2> line, final double x) {
        for (int k = 0; k + 1 < line.size(); k++) {
            final Vec2 a = line.get(k);
            final Vec2 b = line.get(k + 1);
            if (a.x() >= x && b.x() <= x) {
                final double t = (a.x() - x) / (a.x() - b.x());
                return new Vec2(x, a.z() + t * (b.z() - a.z()));
            }
        }
        throw new IllegalArgumentException("the line does not reach " + x);
    }
}
