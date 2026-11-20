package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;
import com.example.fxsuite.avatar.buildings.Placed;
import com.example.fxsuite.avatar.buildings.Standing;
import com.example.fxsuite.avatar.lifts.HighRiseBank;
import com.example.fxsuite.avatar.sim.Place;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The bay's four homes towers: each on the map's outline, standing where the map draws it; its storeys and its height
 * as published; its lifts in two zones, each floor over its lobby served by one; its core clear of its glass.
 */
class HomesTest {

    private static final OfficeTower RESIDENCES = MbfcHomes.residences();
    private static final OfficeTower SUITES = MbfcHomes.suites();
    private static final OfficeTower MARINA_BAY = Sail.marinaBayTower();
    private static final OfficeTower CENTRAL_PARK = Sail.centralParkTower();

    @Test
    void eachOnTheMapsOutlineStandingWhereTheMapDrawsIt() {
        // the map's first points, back in the bay's frame, within a centimetre and a half
        standsOn(MbfcHomes.RESIDENCES, -452.35, -603.81);
        standsOn(MbfcHomes.SUITES, -776.74, -634.05);
        standsOn(Sail.MARINA_BAY_TOWER, -748.68, -490.98);
        standsOn(Sail.CENTRAL_PARK_TOWER, -737.09, -573.72);
        // their floors as large as the map draws them
        near(1_263, RESIDENCES);
        near(997, SUITES);
        near(1_460, MARINA_BAY);
        near(880, CENTRAL_PARK);
    }

    @Test
    void theirStoreysAndHeightsAsPublished() {
        published(RESIDENCES, 55, 22_190);
        published(SUITES, 66, 22_690);
        published(MARINA_BAY, 70, 24_500);
        published(CENTRAL_PARK, 63, 21_500);
        assertEquals(Homes.PENTHOUSES, RESIDENCES.uses().get(Homes.storey(55)));
        assertEquals(Homes.PENTHOUSES, SUITES.uses().get(Homes.storey(66)));
        assertEquals("club", MARINA_BAY.uses().get(Homes.storey(44)));
        assertEquals("recreation", CENTRAL_PARK.uses().get(Homes.storey(34)));
        assertEquals(Homes.HOMES, CENTRAL_PARK.uses().get(Homes.storey(2)));
    }

    @Test
    void theirLiftsInTwoZonesEachFloorServedByOneTheirCoresClearOfTheirGlass() {
        lifts(RESIDENCES, 6);
        lifts(SUITES, 6);
        lifts(MARINA_BAY, 8);
        lifts(CENTRAL_PARK, 6);
    }

    private static void standsOn(final Mapped mapped, final double x, final double z) {
        final Standing s = new Standing(RESIDENCES, new Place(mapped.x(), mapped.z(), 0, mapped.yaw()), 0);
        final double[] first = s.world(mapped.outline().getFirst()[0], mapped.outline().getFirst()[1]);
        assertEquals(x * 100, first[0], 1.5);
        assertEquals(z * 100, first[1], 1.5);
        assertEquals(mapped.outline().get(0)[1], mapped.outline().get(1)[1], "its way in level");
    }

    private static void near(final double map, final OfficeTower tower) {
        // its floor and the holes its lifts' shafts cut in it
        final double area = tower.area(Homes.storey(10)) + tower.banks().stream().map(Placed::hole)
                .mapToDouble(h -> (h[1] - h[0]) * (h[3] - h[2]) / 10_000).sum();
        assertTrue(Math.abs(area - map) / map < 0.02, tower.name() + ": " + area + " m² against the map's " + map);
    }

    private static void published(final OfficeTower tower, final int floors, final double height) {
        assertEquals(floors, tower.storeys(), tower.name() + ": its lobby, floors 2 up");
        assertEquals(height, tower.roof() + tower.crown(), 1, tower.name());
        assertTrue(tower.crown() >= 0, tower.name() + ": its roof under its top");
    }

    /** {@code tower}'s lifts: so many, in two zones, each floor over the lobby served by one; its core clear of its glass. */
    private static void lifts(final OfficeTower tower, final int cars) {
        assertEquals(cars, tower.banks().stream().mapToInt(p -> p.bank().cars()).sum(), tower.name());
        assertEquals(2, tower.banks().stream().map(p -> p.bank().zoneFrom()).distinct().count(), tower.name());
        for (int f = 1; f < tower.storeys(); f++) {
            final int floor = f;
            assertEquals(1, tower.banks().stream().map(Placed::bank)
                    .filter(b -> b.zoneFrom() <= floor && floor <= b.zoneTo()).count(), tower.name() + ", storey " + f);
        }
        final List<double[]> core = new ArrayList<>();
        tower.banks().forEach(b -> core.add(b.hole()));
        core.addAll(tower.service());
        for (final double[] r : core) {
            for (final double[] p : List.of(new double[] {r[0], r[2]}, new double[] {r[1], r[2]},
                    new double[] {r[1], r[3]}, new double[] {r[0], r[3]})) {
                assertTrue(RefinedTest.inside(p, tower.outline()), tower.name() + ": its core inside its outline");
                assertTrue(RefinedTest.clearance(p, tower.outline()) >= 600,
                        tower.name() + ": its core 6 m or more from its glass, "
                                + RefinedTest.clearance(p, tower.outline()) / 100 + " m");
            }
        }
        // the two banks apart, the core's block clear of both
        final double[] low = tower.banks().get(0).hole();
        final double[] high = tower.banks().get(1).hole();
        assertTrue(low[3] <= high[2], tower.name() + ": its lift lobby between its banks");
        for (final double[] h : List.of(low, high)) {
            assertTrue(h[1] <= tower.service().getFirst()[0] + 1, tower.name() + ": its banks west of its block");
        }
        assertTrue(tower.banks().stream().map(Placed::bank).mapToInt(HighRiseBank::zoneTo).max().orElse(0)
                == tower.storeys() - 1, tower.name() + ": its top floor served");
    }
}
