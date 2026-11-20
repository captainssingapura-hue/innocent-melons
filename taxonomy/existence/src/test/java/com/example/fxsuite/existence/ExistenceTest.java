package com.example.fxsuite.existence;

import com.example.fxsuite.taxonomy.Taxonomy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What exists: nothing wrong with its four trees, every node with its meaning; a use occupied or not by its branch,
 * louvred where it says; the kinds the worlds have built so far each in their tree.
 */
class ExistenceTest {

    @Test
    void nothingWrongWithItsFourTreesEveryNodeWithItsMeaning() {
        assertEquals(List.of(), Existence.problems());
        assertEquals(List.of("buildings", "uses", "spaces", "elements"), Existence.TREES.stream().map(Taxonomy::name)
                .toList());
        assertEquals(21, Buildings.TREE.leaves().size());
        assertEquals(32, Uses.TREE.leaves().size());
        assertEquals(20, Spaces.TREE.leaves().size());
        assertEquals(31, Elements.TREE.leaves().size());
        assertEquals(135, Existence.TREES.stream().mapToInt(tree -> tree.nodes().size()).sum());
        assertEquals("Rooms let by the night, served from a lobby.", Uses.TREE.meaning(new Uses.HotelRooms()));
    }

    @Test
    void aUseOccupiedOrNotByItsBranchLouvredWhereItSays() {
        // what a tower's lifts must serve: the bay's offices, homes and hotel rooms, and all else people stay in
        for (final var use : List.of(new Uses.Offices(), new Uses.Homes(), new Uses.HotelRooms(), new Uses.Restaurant(),
                new Uses.Garden(), new Uses.Courts())) {
            assertTrue(Taxonomy.answer(use, Occupied.class).isPresent(), use.title());
        }
        for (final var use : List.of(new Uses.Plant(), new Uses.Parking(), new Uses.Lobby(), new Uses.Crossing())) {
            assertFalse(Taxonomy.answer(use, Occupied.class).isPresent(), use.title());
        }
        assertEquals(List.of(new Uses.Plant(), new Uses.Parking(), new Uses.Technical()),
                Uses.TREE.leaves().stream().filter(Louvred.class::isInstance).toList());
        assertEquals(23, Uses.TREE.leaves().stream().filter(use -> Taxonomy.answer(use, Occupied.class).isPresent())
                .count());
    }

    @Test
    void theKindsTheWorldsHaveBuiltSoFarEachInTheirTree() {
        // the bay's towers; the reach's bridge, and what is to stand in it
        assertEquals(List.of(new Buildings.OfficeTower(), new Buildings.Commerce(), new Buildings.Building()),
                Taxonomy.lineage(new Buildings.OfficeTower()));
        for (final String name : List.of("homes-tower", "hotel-tower", "mixed-tower", "bridge", "shop-house",
                "wine-house", "festive-gateway", "gate-tower", "city-wall")) {
            assertTrue(Buildings.TREE.named(name).isPresent(), name);
        }
        // the bridge's parts, as elements
        for (final String name : List.of("rib", "abutment", "decking", "railing", "pole")) {
            assertTrue(Elements.TREE.named(name).isPresent(), name);
        }
        assertEquals(List.of(new Buildings.GateTower(), new Buildings.CityWall()),
                Buildings.TREE.under(new Buildings.Fortification()));
        assertTrue(Spaces.TREE.has(new Spaces.CoveredWay()) && !Spaces.TREE.has(new Uses.Homes()));
    }
}
