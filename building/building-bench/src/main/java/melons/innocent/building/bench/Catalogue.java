package melons.innocent.building.bench;

import com.example.fxsuite.building.chinese.FestiveGate;
import com.example.fxsuite.building.chinese.SongCityWall;
import com.example.fxsuite.building.chinese.SongFarmhouse;
import com.example.fxsuite.building.chinese.SongFestiveGate;
import com.example.fxsuite.building.chinese.SongGateTower;
import com.example.fxsuite.building.chinese.SongQuay;
import com.example.fxsuite.building.chinese.SongShopHouse;
import com.example.fxsuite.building.chinese.SongStall;
import com.example.fxsuite.building.chinese.SongTeaHouse;
import com.example.fxsuite.building.chinese.SongWarehouse;
import com.example.fxsuite.building.chinese.SongWineHouse;
import com.example.fxsuite.building.chinese.SongWovenArch;
import com.example.fxsuite.building.chinese.WovenArch;
import com.example.fxsuite.building.spec.CityWallSpec;
import com.example.fxsuite.building.spec.FarmhouseSpec;
import com.example.fxsuite.building.spec.GateTowerSpec;
import com.example.fxsuite.building.spec.HallSpec;
import com.example.fxsuite.building.spec.QuaySpec;
import com.example.fxsuite.building.spec.Roofing;
import com.example.fxsuite.building.spec.ShopHouseSpec;
import com.example.fxsuite.building.spec.StallSpec;
import com.example.fxsuite.building.spec.WineHouseSpec;
import com.example.fxsuite.taxonomy.Leaf;

import java.util.List;
import java.util.Optional;

/**
 * What the bench offers to build, each thing by its default implementation and a specification to build it from at
 * first: for now the Northern Song's, as their tests build them. One to a thing; other civilizations and eras later.
 *
 * @param offered each thing offered
 */
public record Catalogue(List<Offered> offered) {

    public Catalogue {
        offered = List.copyOf(offered);
    }

    /** The Northern Song's buildings and works, each from a specification as its tests have it. */
    public Catalogue() {
        this(List.of(
                new Offered(new SongFarmhouse(), new FarmhouseSpec<>(3, 330, 600, Roofing.THATCH, 800)),
                new Offered(new SongShopHouse(), new ShopHouseSpec<>(3, 360, 600, Roofing.TILE)),
                new Offered(new SongTeaHouse(), new HallSpec<>(3, 380, 600, Roofing.THATCH)),
                new Offered(new SongWarehouse(), new HallSpec<>(3, 400, 700, Roofing.TILE)),
                new Offered(new SongWineHouse(), new WineHouseSpec<>(5, 360, 720, Roofing.TILE)),
                new Offered(new SongStall(), new StallSpec<>(200, 150, StallSpec.Shade.AWNING)),
                new Offered(new SongQuay(), new QuaySpec<>(1_500, 200, 400, 200, 1)),
                new Offered(new SongCityWall(), new CityWallSpec<>(2_000, 900)),
                new Offered(new SongGateTower(), new GateTowerSpec<>(3_000, 1_930, 960, 560, 5,
                        GateTowerSpec.Ascent.RIGHT)),
                new Offered(new SongFestiveGate(), new FestiveGate(600, 160, 700)),
                new Offered(new SongWovenArch(), new WovenArch(2_000, 800, 360, 1_250, -170, 400, 21, 45, 40, 23,
                        900))));
    }

    /** What is offered to build {@code thing}, if anything is. */
    public Optional<Offered> of(final Leaf<?> thing) {
        return offered.stream().filter(one -> one.thing().equals(thing)).findFirst();
    }
}
