package com.example.fxsuite.qingming.structures;

import com.example.fxsuite.avatar.sim.Course;
import com.example.fxsuite.building.Atlas;
import com.example.fxsuite.building.chinese.FestiveGate;
import com.example.fxsuite.building.chinese.SongCityWall;
import com.example.fxsuite.building.chinese.SongFestiveGate;
import com.example.fxsuite.building.chinese.SongGateTower;
import com.example.fxsuite.building.chinese.SongShopHouse;
import com.example.fxsuite.building.chinese.SongStall;
import com.example.fxsuite.building.chinese.SongTeaHouse;
import com.example.fxsuite.building.chinese.SongWarehouse;
import com.example.fxsuite.building.chinese.SongWineHouse;
import com.example.fxsuite.building.chinese.SongWovenArch;
import com.example.fxsuite.building.chinese.WovenArch;
import com.example.fxsuite.building.spec.CityWallSpec;
import com.example.fxsuite.building.spec.GateTowerSpec;
import com.example.fxsuite.building.spec.HallSpec;
import com.example.fxsuite.building.spec.Roofing;
import com.example.fxsuite.building.spec.ShopHouseSpec;
import com.example.fxsuite.building.spec.StallSpec;
import com.example.fxsuite.building.spec.WineHouseSpec;
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
 * </ul>
 */
public final class Fixtures {

    private Fixtures() {
    }

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

    /** The shops of the north row, east to west: bays, a bay's width, depth, roofing. */
    private static final List<ShopHouseSpec<Chinese, ChineseEras.NorthernSong>> NORTH = List.of(
            new ShopHouseSpec<>(3, 360, 600, Roofing.TILE), new ShopHouseSpec<>(2, 330, 540, Roofing.TILE),
            new ShopHouseSpec<>(4, 340, 620, Roofing.TILE), new ShopHouseSpec<>(3, 350, 560, Roofing.THATCH),
            new ShopHouseSpec<>(5, 330, 640, Roofing.TILE), new ShopHouseSpec<>(2, 360, 520, Roofing.TILE),
            new ShopHouseSpec<>(3, 340, 600, Roofing.TILE));
    /** Those of the south row. */
    private static final List<ShopHouseSpec<Chinese, ChineseEras.NorthernSong>> SOUTH = List.of(
            new ShopHouseSpec<>(4, 350, 600, Roofing.TILE), new ShopHouseSpec<>(3, 330, 540, Roofing.THATCH),
            new ShopHouseSpec<>(2, 380, 560, Roofing.TILE), new ShopHouseSpec<>(3, 360, 640, Roofing.TILE),
            new ShopHouseSpec<>(3, 340, 520, Roofing.TILE), new ShopHouseSpec<>(5, 340, 600, Roofing.TILE),
            new ShopHouseSpec<>(2, 330, 560, Roofing.THATCH));

    /** The map. Declared once. */
    public static Atlas<Chinese, ChineseEras.NorthernSong> atlas() {
        return Declared.ATLAS;
    }

    private static final class Declared {
        static final Atlas<Chinese, ChineseEras.NorthernSong> ATLAS = declare();
    }

    private static Atlas<Chinese, ChineseEras.NorthernSong> declare() {
        final Atlas<Chinese, ChineseEras.NorthernSong> reach = new Atlas<>("the reach of the Bian", Chinese.IT,
                new ChineseEras.NorthernSong(), Years.ce(1120));
        reach.with(new SongWovenArch()).at("the Rainbow Bridge", 0, 0, 0, new WovenArch(2_000, 800, 360, 1_250,
                -Rivers.DEPTH + Rivers.WATER + 44, Rivers.DEPTH, 21, 45, 40, 23, 900));
        final Atlas.Maker<ShopHouseSpec<Chinese, ChineseEras.NorthernSong>> shops = reach.with(new SongShopHouse());
        row(shops, "north", NORTH, 1);
        row(shops, "south", SOUTH, -1);
        // the wine house on the street's north side, east of the shops; its gateway before its door, clear of its eaves
        final WineHouseSpec<Chinese, ChineseEras.NorthernSong> wine = new WineHouseSpec<>(5, 360, 720, Roofing.TILE);
        reach.with(new SongWineHouse()).at("the wine house", WINE_HOUSE, STREET + BACK + wine.depth() / 2, 180, wine);
        reach.with(new SongFestiveGate()).at("the wine house's gateway", WINE_HOUSE, STREET + BACK - 200, 180,
                new FestiveGate(600, 140, 700));
        // a tea house at each end of the bridge, beside the road and facing it: west of it south, east of it north
        reach.with(new SongTeaHouse())
                .at("tea house south", -1_000, -2_400, 90, new HallSpec<>(3, 380, 600, Roofing.THATCH))
                .at("tea house north", 1_000, 2_300, 270, new HallSpec<>(3, 380, 600, Roofing.TILE));
        // two warehouses on the north bank east of the bridge, facing the towpath and the river
        reach.with(new SongWarehouse())
                .at("warehouse 1", 5_000, 2_550, 180, new HallSpec<>(3, 400, 700, Roofing.TILE))
                .at("warehouse 2", 6_600, 2_550, 180, new HallSpec<>(3, 400, 700, Roofing.TILE));
        // stalls beside the road at each end of the bridge, and along the street before the shops begin
        final StallSpec<Chinese, ChineseEras.NorthernSong> awning = new StallSpec<>(200, 150, StallSpec.Shade.AWNING);
        final StallSpec<Chinese, ChineseEras.NorthernSong> umbrella = new StallSpec<>(200, 150,
                StallSpec.Shade.UMBRELLA);
        reach.with(new SongStall())
                .at("stall north 1", -ROADSIDE - 75, 1_750, 90, awning)
                .at("stall north 2", -ROADSIDE - 75, 2_050, 90, umbrella)
                .at("stall south 1", ROADSIDE + 75, -1_750, 270, umbrella)
                .at("stall south 2", ROADSIDE + 75, -2_050, 270, awning)
                .at("stall street 1", -8_600, STREET - ROADSIDE - 180, 0, umbrella)
                .at("stall street 2", -9_000, STREET - ROADSIDE - 180, 0, awning)
                .at("stall street 3", -9_400, STREET - ROADSIDE - 180, 0, umbrella);
        // the gate where the street meets the wall, its outer face east; the wall north and south of it, each run so
        // that its outer face is east too
        final double gate = Reach.GATE.z();
        reach.with(new SongGateTower()).at("the city gate", Reach.WALL, gate, 90, GATE);
        reach.with(new SongCityWall())
                .between("the wall north of the gate", Reach.WALL, Reach.NORTH - SHORT, Reach.WALL,
                        gate + GATE.wide() / 2, length -> new CityWallSpec<>(length, RAMPART))
                .between("the wall south of the gate", Reach.WALL, gate - GATE.wide() / 2, Reach.WALL,
                        Reach.SOUTH + SHORT, length -> new CityWallSpec<>(length, RAMPART));
        return reach;
    }

    /**
     * A row of shops along the street's {@code side} (1 its north, -1 its south), east to west from {@link #FROM},
     * each fronting the street: the north row's facing south, the south row's north.
     */
    private static void row(final Atlas.Maker<ShopHouseSpec<Chinese, ChineseEras.NorthernSong>> shops,
                            final String name, final List<ShopHouseSpec<Chinese, ChineseEras.NorthernSong>> specs,
                            final int side) {
        double east = FROM;
        for (int k = 0; k < specs.size(); k++) {
            final ShopHouseSpec<Chinese, ChineseEras.NorthernSong> spec = specs.get(k);
            shops.at("shop " + name + " " + (k + 1), east - spec.width() / 2,
                    STREET + side * (BACK + spec.depth() / 2), side > 0 ? 180 : 0, spec);
            east -= spec.width() + BETWEEN;
        }
    }

    /** Everything on the map as pieces of the world, each standing on the reach's land. */
    public static List<Course.Piece> pieces() {
        return atlas().pieces(Reach.land(), KEYS);
    }
}
