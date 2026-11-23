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
import com.example.fxsuite.building.spec.HouseSpec;
import com.example.fxsuite.building.spec.PointBlockSpec;
import com.example.fxsuite.building.spec.QuaySpec;
import com.example.fxsuite.building.spec.Roofing;
import com.example.fxsuite.building.spec.SlabBlockSpec;
import com.example.fxsuite.building.spec.ShopHouseSpec;
import com.example.fxsuite.building.spec.StallSpec;
import com.example.fxsuite.building.spec.WineHouseSpec;
import com.example.fxsuite.taxonomy.Leaf;
import melons.innocent.building.generic.GenericHouse;
import melons.innocent.building.generic.GenericPointBlock;
import melons.innocent.building.generic.GenericSlabBlock;

import java.util.List;
import java.util.Optional;

/**
 * What the bench offers to build, each thing by its default implementation and a specification to build it from at
 * first: anyone's house of today; the slab block and the point block of the modern era's public housing, as bare
 * structure; and the Northern Song's buildings and works, as their tests build them. One to a thing; other
 * civilizations and eras later.
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
                new Offered(new GenericHouse(), new HouseSpec<>(900, 700, 2, 300, 3)),
                new Offered(new GenericSlabBlock(), new SlabBlockSpec<>(12, 750, 1_200, 180, 6, 12)),
                new Offered(new GenericPointBlock(), new PointBlockSpec<>(20, 1_900, 800)),
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
