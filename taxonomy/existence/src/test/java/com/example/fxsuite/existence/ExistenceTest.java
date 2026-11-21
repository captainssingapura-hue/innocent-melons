package com.example.fxsuite.existence;

import com.example.fxsuite.taxonomy.Node;
import com.example.fxsuite.taxonomy.Taxonomy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What exists: nothing wrong with its six trees, every node with its meaning. The land in its layers: the base
 * terrain, the natural features, and the made in three parts, networks, earthworks and buildings, the bridge a
 * network's carrier and the city wall an earthwork. Ground level where it says, water wet, boats where it is
 * navigable. A use occupied or not by its branch, louvred where it says; the kinds the worlds have built so far each
 * in their tree.
 */
class ExistenceTest {

    @Test
    void nothingWrongWithItsSixTreesEveryNodeWithItsMeaning() {
        assertEquals(List.of(), Existence.INSTANCE.problems());
        assertEquals(List.of("terrains", "features", "made", "uses", "spaces", "elements"),
                Existence.INSTANCE.trees().stream().map(Taxonomy::name).toList());
        assertEquals(19, Terrains.TREE.leaves().size());
        assertEquals(10, Features.TREE.leaves().size());
        assertEquals(47, Made.TREE.leaves().size());
        assertEquals(32, Uses.TREE.leaves().size());
        assertEquals(20, Spaces.TREE.leaves().size());
        assertEquals(33, Elements.TREE.leaves().size());
        assertEquals(220, Existence.INSTANCE.trees().stream().mapToInt(tree -> tree.nodes().size()).sum());
        assertEquals("Rooms let by the night, served from a lobby.", Uses.TREE.meaning(new Uses.HotelRooms()));
        assertEquals("A wide ditch round a town or a castle, often holding water, to keep out.",
                Made.TREE.meaning(new Earthworks.Moat()));
    }

    @Test
    void theMadeInThreePartsTheBridgeANetworksCarrierTheCityWallAnEarthwork() {
        assertEquals(List.of(new Buildings.OfficeTower(), new Buildings.Commerce(), new Buildings.Building(),
                new Made()), new Buildings.OfficeTower().lineage());
        assertEquals(List.of(new Networks.Bridge(), new Networks.Carrier(), new Networks.Network(), new Made()),
                new Networks.Bridge().lineage());
        assertEquals(List.of(new Earthworks.CityWall(), new Earthworks.Raised(), new Earthworks.Earthwork(),
                new Made()), new Earthworks.CityWall().lineage());
        assertEquals(List.of(new Networks.Bridge(), new Networks.Viaduct(), new Networks.Tunnel(), new Networks.Ford()),
                Made.TREE.under(new Networks.Carrier()));
        assertEquals(List.of(new Buildings.GateTower()), Made.TREE.under(new Buildings.Fortification()));
        // each layer's things in its own tree, and no other
        assertTrue(Terrains.TREE.has(new Terrains.Floodplain()) && !Made.TREE.has(new Terrains.Floodplain()));
        assertTrue(Features.TREE.has(new Features.River()) && !Made.TREE.has(new Features.River()));
        assertTrue(Made.TREE.has(new Networks.Canal()) && !Features.TREE.has(new Networks.Canal()));
    }

    @Test
    void groundLevelWhereItSaysWaterWetBoatsWhereItIsNavigable() {
        for (final Node ground : List.<Node>of(new Terrains.Plain(), new Terrains.Floodplain(), new Earthworks.Pad(),
                new Earthworks.Terrace())) {
            assertTrue(ground.answer(Level.class).isPresent(), ground.title() + " built on");
        }
        for (final Node ground : List.<Node>of(new Terrains.Hill(), new Terrains.Cliff(), new Earthworks.Dike(),
                new Features.Bank(), new Features.River())) {
            assertFalse(ground.answer(Level.class).isPresent(), ground.title() + " not built on");
        }
        for (final Node water : List.<Node>of(new Features.River(), new Features.Pond(), new Features.Marsh(),
                new Networks.Canal())) {
            assertTrue(water.answer(Wet.class).isPresent(), water.title() + " wet");
        }
        for (final Node dry : List.<Node>of(new Terrains.Plain(), new Features.Bank(), new Earthworks.Moat())) {
            assertFalse(dry.answer(Wet.class).isPresent(), dry.title() + " dry");
        }
        assertEquals(List.of(new Features.River(), new Features.Lake(), new Features.Sea(), new Features.Bay()),
                Features.TREE.leaves().stream().filter(leaf -> leaf.answer(Navigable.class).isPresent()).toList());
        assertEquals(List.of(new Networks.Canal()),
                Made.TREE.leaves().stream().filter(leaf -> leaf.answer(Navigable.class).isPresent()).toList());
    }

    @Test
    void aUseOccupiedOrNotByItsBranchLouvredWhereItSays() {
        // what a tower's lifts must serve: the bay's offices, homes and hotel rooms, and all else people stay in
        for (final var use : List.of(new Uses.Offices(), new Uses.Homes(), new Uses.HotelRooms(), new Uses.Restaurant(),
                new Uses.Garden(), new Uses.Courts())) {
            assertTrue(use.answer(Occupied.class).isPresent(), use.title());
        }
        for (final var use : List.of(new Uses.Plant(), new Uses.Parking(), new Uses.Lobby(), new Uses.Crossing())) {
            assertFalse(use.answer(Occupied.class).isPresent(), use.title());
        }
        assertEquals(List.of(new Uses.Plant(), new Uses.Parking(), new Uses.Technical()),
                Uses.TREE.leaves().stream().filter(Louvred.class::isInstance).toList());
        assertEquals(23, Uses.TREE.leaves().stream().filter(use -> use.answer(Occupied.class).isPresent())
                .count());
    }

    @Test
    void theKindsTheWorldsHaveBuiltSoFarEachInTheirTree() {
        // the bay's towers; the reach's bridge, street, wall, quay and country
        for (final String name : List.of("homes-tower", "hotel-tower", "mixed-tower", "bridge", "shop-house",
                "wine-house", "festive-gateway", "gate-tower", "city-wall", "quay", "farmhouse", "towpath", "canal")) {
            assertTrue(Made.TREE.named(name).isPresent(), name);
        }
        // the bridge's parts, as elements
        for (final String name : List.of("rib", "abutment", "decking", "railing", "pole")) {
            assertTrue(Elements.TREE.named(name).isPresent(), name);
        }
        assertTrue(Spaces.TREE.has(new Spaces.CoveredWay()) && !Spaces.TREE.has(new Uses.Homes()));
    }
}
