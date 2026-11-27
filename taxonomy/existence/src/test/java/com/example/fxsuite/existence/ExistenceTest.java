package com.example.fxsuite.existence;

import com.example.fxsuite.taxonomy.Node;
import com.example.fxsuite.taxonomy.Taxonomy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What exists: nothing wrong with its nine trees, every node with its meaning. The land in its layers: the base
 * terrain, its features, the canal a river people dug among them, and the made in its departments, highways, bridges,
 * tunnels, earthworks and buildings, the city wall an earthwork. Ground level where it says, water wet, boats where it
 * is navigable; what can carry traffic, whatever layer it lies in, so that a network may join them. A use occupied or
 * not by its branch, louvred where it says; the kinds the worlds have built so far each in their tree. What is worn
 * a garment, of cloth, or armour, hard. A figure a robot, its frame bare or shelled.
 */
class ExistenceTest {

    @Test
    void nothingWrongWithItsNineTreesEveryNodeWithItsMeaning() {
        assertEquals(List.of(), Existence.INSTANCE.problems());
        assertEquals(List.of("terrains", "features", "made", "uses", "spaces", "elements", "things", "wearables",
                "figures"),
                Existence.INSTANCE.trees().stream().map(Taxonomy::name).toList());
        assertEquals(20, Terrains.TREE.leaves().size());
        assertEquals(11, Features.TREE.leaves().size());
        assertEquals(53, Made.TREE.leaves().size());
        assertEquals(32, Uses.TREE.leaves().size());
        assertEquals(20, Spaces.TREE.leaves().size());
        assertEquals(38, Elements.TREE.leaves().size());
        assertEquals(12, Things.TREE.leaves().size());
        assertEquals(15, Wearables.TREE.leaves().size());
        assertEquals(2, Figures.TREE.leaves().size());
        assertEquals(List.of(new Things.Watermelon(), new Things.Melon(), new Things.Fruit(), new Things.Thing()),
                new Things.Watermelon().lineage());
        assertEquals(277,Existence.INSTANCE.trees().stream().mapToInt(tree -> tree.nodes().size()).sum());
        assertEquals("Rooms let by the night, served from a lobby.", Uses.TREE.meaning(new Uses.HotelRooms()));
        assertEquals("A wide ditch round a town or a castle, often holding water, to keep out.",
                Made.TREE.meaning(new Earthworks.Moat()));
    }

    @Test
    void theMadeInItsDepartmentsTheCanalARiverPeopleDug() {
        assertEquals(List.of(new Buildings.OfficeTower(), new Buildings.Commerce(), new Buildings.Building(),
                new Made()), new Buildings.OfficeTower().lineage());
        assertEquals(List.of(new Bridges.Bridge(), new Bridges.Span(), new Made()), new Bridges.Bridge().lineage());
        assertEquals(List.of(new Tunnels.Tunnel(), new Tunnels.Bore(), new Made()), new Tunnels.Tunnel().lineage());
        assertEquals(List.of(new Earthworks.CityWall(), new Earthworks.Raised(), new Earthworks.Earthwork(),
                new Made()), new Earthworks.CityWall().lineage());
        assertEquals(List.of(new Highways.Road(), new Highways.Expressway(), new Highways.Street(), new Highways.Lane(),
                new Highways.Path(), new Highways.Towpath(), new Highways.Ford()), Made.TREE.under(new Highways.Way()));
        assertEquals(List.of(new Buildings.GateTower()), Made.TREE.under(new Buildings.Fortification()));
        assertEquals(List.of(new Features.Canal(), new Features.Flowing(), new Features.Water(),
                new Features.Feature()), new Features.Canal().lineage());
        // each layer's things in its own tree, and no other
        assertTrue(Terrains.TREE.has(new Terrains.Floodplain()) && !Made.TREE.has(new Terrains.Floodplain()));
        assertTrue(Features.TREE.has(new Features.Canal()) && !Made.TREE.has(new Features.Canal()));
        assertTrue(Made.TREE.has(new Highways.Towpath()) && !Features.TREE.has(new Highways.Towpath()));
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
        for (final Node water : List.<Node>of(new Features.River(), new Features.Canal(), new Features.Pond(),
                new Features.Marsh())) {
            assertTrue(water.answer(Wet.class).isPresent(), water.title() + " wet");
        }
        for (final Node dry : List.<Node>of(new Terrains.Plain(), new Features.Bank(), new Earthworks.Moat())) {
            assertFalse(dry.answer(Wet.class).isPresent(), dry.title() + " dry, but for what a moat's spec says");
        }
        assertEquals(List.of(new Features.River(), new Features.Canal(), new Features.Lake(), new Features.Sea(),
                new Features.Bay()), Features.TREE.leaves().stream()
                .filter(leaf -> leaf.answer(Navigable.class).isPresent()).toList());
    }

    @Test
    void whatCarriesTrafficWhateverLayerItLiesInSoANetworkMayJoinIt() {
        // the reach's street, the bridge it lands on, the towpath along the canal and the canal itself
        for (final Node member : List.<Node>of(new Highways.Street(), new Highways.Towpath(), new Highways.Ford(),
                new Bridges.Bridge(), new Bridges.Viaduct(), new Bridges.Flyover(), new Bridges.Ramp(),
                new Bridges.SpiralRamp(), new Bridges.ElevatedRoad(), new Tunnels.Tunnel(),
                new Features.Canal(), new Features.River())) {
            assertTrue(member.answer(Transit.class).isPresent(), member.title() + " carries traffic");
        }
        for (final Node not : List.<Node>of(new Highways.Paving(), new Buildings.Quay(), new Earthworks.Pad(),
                new Features.Pond(), new Features.Bank(), new Terrains.Plain())) {
            assertFalse(not.answer(Transit.class).isPresent(), not.title() + " carries none");
        }
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
                "wine-house", "festive-gateway", "gate-tower", "city-wall", "quay", "farmhouse", "towpath")) {
            assertTrue(Made.TREE.named(name).isPresent(), name);
        }
        assertTrue(Features.TREE.named("canal").isPresent());
        // the bridge's parts, as elements
        for (final String name : List.of("rib", "abutment", "decking", "railing", "pole")) {
            assertTrue(Elements.TREE.named(name).isPresent(), name);
        }
        assertTrue(Spaces.TREE.has(new Spaces.CoveredWay()) && !Spaces.TREE.has(new Uses.Homes()));
    }

    @Test
    void whatIsWornAGarmentOfClothOrArmourHard() {
        assertEquals(List.of(new Wearables.Cuirass(), new Wearables.Armour(), new Wearables.Wearable()),
                new Wearables.Cuirass().lineage());
        assertEquals(List.of(new Wearables.Briefs(), new Wearables.Tights()),
                Wearables.TREE.under(new Wearables.Garment()));
        assertEquals(List.of(new Wearables.Helmet(), new Wearables.Gorget(), new Wearables.Cuirass(),
                new Wearables.Pauldron(), new Wearables.Couter(), new Wearables.Vambrace(), new Wearables.Gauntlet(),
                new Wearables.Fauld(), new Wearables.Cuisse(), new Wearables.Poleyn(), new Wearables.Greave(),
                new Wearables.Sabaton()), Wearables.TREE.under(new Wearables.Armour()));
        assertTrue(Wearables.TREE.has(new Wearables.Helmet()) && !Things.TREE.has(new Wearables.Helmet()));
    }

    @Test
    void aFigureMovesItselfARobotItsFrameBareOrShelled() {
        assertEquals(List.of(new Figures.ShelledRobot(), new Figures.Robot(), new Figures.Figure()),
                new Figures.ShelledRobot().lineage());
        assertEquals(List.of(new Figures.FramedRobot(), new Figures.ShelledRobot()),
                Figures.TREE.under(new Figures.Robot()));
        assertTrue(Figures.TREE.has(new Figures.FramedRobot()) && !Things.TREE.has(new Figures.FramedRobot()));
    }
}
