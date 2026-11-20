package com.example.fxsuite.qingming.structures;

import com.example.fxsuite.avatar.sim.Course;
import com.example.fxsuite.building.Atlas;
import com.example.fxsuite.building.chinese.SongShopHouse;
import com.example.fxsuite.building.chinese.SongWovenArch;
import com.example.fxsuite.building.chinese.WovenArch;
import com.example.fxsuite.building.spec.Roofing;
import com.example.fxsuite.building.spec.ShopHouseSpec;
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
