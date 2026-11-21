package com.example.fxsuite.building;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.StandIns;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.building.spec.Roofing;
import com.example.fxsuite.building.spec.ShopHouseSpec;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.existence.Spaces;
import com.example.fxsuite.existence.Uses;
import com.example.fxsuite.historian.Chinese;
import com.example.fxsuite.historian.ChineseEras;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;
import com.example.fxsuite.historian.StraitsChinese;
import com.example.fxsuite.historian.StraitsChineseEras;
import com.example.fxsuite.historian.Years;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A map declared: an implementation bound once, then things put by a place and a specification; what is older and
 * standing still bound apart, and refused if its era had not begun; a structure held to the rules of what it is for.
 */
class AtlasTest {

    /** A shed for a shop, built from the shared specification by whoever: a floor, a way in at its front. */
    private record Shed<C extends Civilization, E extends Era<? super C>>(C civilization, E era, boolean door)
            implements ThingImpl<Buildings.ShopHouse, C, E, ShopHouseSpec<C, E>> {

        public Buildings.ShopHouse thing() {
            return new Buildings.ShopHouse();
        }

        public Structure build(final ShopHouseSpec<C, E> spec) {
            final double a = spec.width() / 2;
            final double d = spec.depth() / 2;
            final List<Structure.Part> parts = List.of(new Structure.Part(new Elements.Floor(),
                    Timbers.block("floor", Role.BASE, Material.STONE, -a, a, -d, d, 0, 18)));
            return new Structure(parts, StandIns.prisms(parts.stream().map(Structure.Part::solid).toList()),
                    List.of(new Structure.Room(new Spaces.ShopFloor(), new Uses.Shops(), List.of(new Vec2(-a, -d),
                            new Vec2(a, -d), new Vec2(a, d), new Vec2(-a, d)), 18)),
                    door ? List.of(new Structure.WayIn(0, d, 18)) : List.of());
        }
    }

    private static Atlas<Chinese, ChineseEras.NorthernSong> reach() {
        return new Atlas<>("the reach", Chinese.IT, new ChineseEras.NorthernSong(), Years.ce(1120));
    }

    @Test
    void anImplementationBoundOnceThenThingsPutByAPlaceAndASpecification() {
        final Atlas<Chinese, ChineseEras.NorthernSong> reach = reach();
        reach.with(new Shed<>(Chinese.IT, new ChineseEras.NorthernSong(), true))
                .at("shop 1", 0, 500, 180, new ShopHouseSpec<>(3, 360, 600, Roofing.TILE))
                .at("shop 2", 1_400, 500, 180, new ShopHouseSpec<>(2, 330, 540, Roofing.THATCH));
        assertEquals(List.of(), reach.problems());
        assertEquals(2, reach.fixtures().size());
        final Atlas.Fixture first = reach.fixtures().getFirst();
        assertEquals(new Buildings.ShopHouse(), first.thing());
        assertEquals(new ChineseEras.NorthernSong(), first.era());
        assertEquals(1, first.structure().solids(new Elements.Floor()).size());
    }

    @Test
    void whatIsOlderAndStandingStillBoundApartRefusedIfItsEraHadNotBegun() {
        final Atlas<Chinese, ChineseEras.NorthernSong> reach = reach();
        reach.standing(new Shed<>(Chinese.IT, new ChineseEras.Tang(), true))
                .at("an old shop", 0, 0, 0, new ShopHouseSpec<>(3, 360, 600, Roofing.TILE));
        assertEquals(List.of(), reach.problems(), "a Tang shop still standing in 1120");
        reach.standing(new Shed<>(StraitsChinese.IT, new StraitsChineseEras.StraitsSettlements(), true))
                .at("a shop to come", 2_000, 0, 0, new ShopHouseSpec<>(3, 360, 600, Roofing.TILE))
                .at("a shop to come", 4_000, 0, 0, new ShopHouseSpec<>(3, 360, 600, Roofing.TILE));
        final List<String> problems = reach.problems();
        assertTrue(problems.contains("a shop to come: the Straits Settlements had not begun by 1120"),
                problems.toString());
        assertTrue(problems.contains("a shop to come: Straits Chinese (Peranakan) was not there by 1120"),
                problems.toString());
        assertTrue(problems.contains("two things called a shop to come"), problems.toString());
        // and a map whose year is not in its era
        final List<String> wrong = new Atlas<>("wrong", Chinese.IT, new ChineseEras.NorthernSong(), Years.ce(1200))
                .problems();
        assertEquals(List.of("wrong: 1200 is not in the Northern Song"), wrong);
    }

    @Test
    void aStructureHeldToTheRulesOfWhatItIsFor() {
        final Atlas<Chinese, ChineseEras.NorthernSong> reach = reach();
        reach.with(new Shed<>(Chinese.IT, new ChineseEras.NorthernSong(), false))
                .at("a shop with no door", 0, 0, 0, new ShopHouseSpec<>(3, 360, 600, Roofing.TILE));
        assertEquals(List.of("a shop with no door: ShopFloor for Shops: people spend their time there, and there is no"
                + " way in"), reach.problems());
        // a crossing has two ends; a part is an element, a space a kind of space for a use
        final List<Vec2> square = List.of(new Vec2(-100, -100), new Vec2(100, -100), new Vec2(100, 100),
                new Vec2(-100, 100));
        final Structure bridge = new Structure(List.of(new Structure.Part(new Uses.Shops(),
                Timbers.block("deck", Role.STRUCTURE, Material.TIMBER, -100, 100, -100, 100, 0, 10))), List.of(),
                List.of(new Structure.Room(new Spaces.Deck(), new Uses.Crossing(), square, 10),
                        new Structure.Room(new Elements.Roof(), new Spaces.Hall(), square, 10)),
                List.of(new Structure.WayIn(0, 100, 10)));
        assertEquals(List.of("Shops is no element", "Deck for Crossing: gone through, and it has 1 of its two ends",
                "Roof is no kind of space", "Hall is no use"), bridge.problems());
        assertEquals(List.of("made of nothing"), new Structure(List.of(), List.of(), List.of(), List.of()).problems());
    }
}
