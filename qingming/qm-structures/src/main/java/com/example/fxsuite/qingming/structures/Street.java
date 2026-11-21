package com.example.fxsuite.qingming.structures;

import com.example.fxsuite.building.Atlas;
import com.example.fxsuite.building.chinese.FestiveGate;
import com.example.fxsuite.building.chinese.SongFestiveGate;
import com.example.fxsuite.building.chinese.SongShopHouse;
import com.example.fxsuite.building.chinese.SongStall;
import com.example.fxsuite.building.chinese.SongTeaHouse;
import com.example.fxsuite.building.chinese.SongWarehouse;
import com.example.fxsuite.building.chinese.SongWineHouse;
import com.example.fxsuite.building.chinese.SongWovenArch;
import com.example.fxsuite.building.chinese.WovenArch;
import com.example.fxsuite.building.spec.HallSpec;
import com.example.fxsuite.building.spec.Roofing;
import com.example.fxsuite.building.spec.ShopHouseSpec;
import com.example.fxsuite.building.spec.StallSpec;
import com.example.fxsuite.building.spec.WineHouseSpec;
import com.example.fxsuite.historian.Chinese;
import com.example.fxsuite.historian.ChineseEras;
import com.example.fxsuite.qingming.land.Rivers;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/**
 * The reach's street as its map has it ({@link Fixtures}): the Rainbow Bridge over the river, the shops along the
 * street west of it, the wine house and its gateway between them and the bridge, a tea house at each end of the
 * bridge, the warehouses on the north bank, the stalls by the road and along the street.
 */
record Street() implements StatelessFunctionalObject {

    static final Street INSTANCE = new Street();

    /**
     * The bridge and the street: the Rainbow Bridge, the shops along the street, the wine house and its gateway, a tea
     * house at each end of the bridge, the warehouses on the north bank, the stalls.
     */
    void lay(final Atlas<Chinese, ChineseEras.NorthernSong> reach) {
        reach.with(new SongWovenArch()).at("the Rainbow Bridge", 0, 0, 0, new WovenArch(2_000, 800, 360, 1_250,
                -Rivers.DEPTH + Rivers.WATER + 44, Rivers.DEPTH, 21, 45, 40, 23, 900));
        final Atlas.Maker<ShopHouseSpec<Chinese, ChineseEras.NorthernSong>> shops = reach.with(new SongShopHouse());
        row(shops, "north", north(), 1);
        row(shops, "south", south(), -1);
        // the wine house on the street's north side, east of the shops; its gateway before its door, clear of its eaves
        final WineHouseSpec<Chinese, ChineseEras.NorthernSong> wine = new WineHouseSpec<>(5, 360, 720, Roofing.TILE);
        final double front = Fixtures.STREET + Fixtures.BACK;
        reach.with(new SongWineHouse()).at("the wine house", Fixtures.WINE_HOUSE, front + wine.depth() / 2, 180, wine);
        reach.with(new SongFestiveGate()).at("the wine house's gateway", Fixtures.WINE_HOUSE, front - 200, 180,
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
                .at("stall north 1", -Fixtures.ROADSIDE - 75, 1_750, 90, awning)
                .at("stall north 2", -Fixtures.ROADSIDE - 75, 2_050, 90, umbrella)
                .at("stall south 1", Fixtures.ROADSIDE + 75, -1_750, 270, umbrella)
                .at("stall south 2", Fixtures.ROADSIDE + 75, -2_050, 270, awning)
                .at("stall street 1", -8_600, Fixtures.STREET - Fixtures.ROADSIDE - 180, 0, umbrella)
                .at("stall street 2", -9_000, Fixtures.STREET - Fixtures.ROADSIDE - 180, 0, awning)
                .at("stall street 3", -9_400, Fixtures.STREET - Fixtures.ROADSIDE - 180, 0, umbrella);
    }

    /** The shops of the north row, east to west: bays, a bay's width, depth, roofing. */
    private List<ShopHouseSpec<Chinese, ChineseEras.NorthernSong>> north() {
        return List.of(
                new ShopHouseSpec<>(3, 360, 600, Roofing.TILE), new ShopHouseSpec<>(2, 330, 540, Roofing.TILE),
                new ShopHouseSpec<>(4, 340, 620, Roofing.TILE), new ShopHouseSpec<>(3, 350, 560, Roofing.THATCH),
                new ShopHouseSpec<>(5, 330, 640, Roofing.TILE), new ShopHouseSpec<>(2, 360, 520, Roofing.TILE),
                new ShopHouseSpec<>(3, 340, 600, Roofing.TILE));
    }

    /** Those of the south row. */
    private List<ShopHouseSpec<Chinese, ChineseEras.NorthernSong>> south() {
        return List.of(
                new ShopHouseSpec<>(4, 350, 600, Roofing.TILE), new ShopHouseSpec<>(3, 330, 540, Roofing.THATCH),
                new ShopHouseSpec<>(2, 380, 560, Roofing.TILE), new ShopHouseSpec<>(3, 360, 640, Roofing.TILE),
                new ShopHouseSpec<>(3, 340, 520, Roofing.TILE), new ShopHouseSpec<>(5, 340, 600, Roofing.TILE),
                new ShopHouseSpec<>(2, 330, 560, Roofing.THATCH));
    }

    /**
     * A row of shops along the street's {@code side} (1 its north, -1 its south), east to west from
     * {@link Fixtures#FROM}, each fronting the street: the north row's facing south, the south row's north.
     */
    private void row(final Atlas.Maker<ShopHouseSpec<Chinese, ChineseEras.NorthernSong>> shops, final String name,
                     final List<ShopHouseSpec<Chinese, ChineseEras.NorthernSong>> specs, final int side) {
        double east = Fixtures.FROM;
        for (int k = 0; k < specs.size(); k++) {
            final ShopHouseSpec<Chinese, ChineseEras.NorthernSong> spec = specs.get(k);
            shops.at("shop " + name + " " + (k + 1), east - spec.width() / 2,
                    Fixtures.STREET + side * (Fixtures.BACK + spec.depth() / 2), side > 0 ? 180 : 0, spec);
            east -= spec.width() + Fixtures.BETWEEN;
        }
    }
}
