package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;
import com.example.fxsuite.avatar.buildings.Placed;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The business district's towers: each with its storeys and its height as published, its top floor where it is
 * published to be; each office floor served by one zone of lifts; on every storey, each bank going up through it and
 * each block of its core inside that storey's outline, 2 m or more from its glass, clear of each other.
 */
class BusinessDistrictTest {

    /** A tower, its published storeys, height (cm), and a floor of it with the level it is published to be at. */
    private record Known(OfficeTower tower, int storeys, double height, int floor, double level) {
    }

    private static final List<Known> KNOWN = List.of(
            new Known(OneRafflesPlace.tower1(), 63, 27_780, 60, 25_320),
            new Known(OneRafflesPlace.tower2(), 38, 20_960, 38, 16_680),
            new Known(RepublicPlaza.tower(), 66, 28_000, 66, 26_290),
            new Known(UobPlaza.tower(), 66, 28_000, 1, 0),
            new Known(OceanFinancialCentre.tower(), 43, 24_500, 1, 0),
            new Known(CapitaGreen.tower(), 40, 24_200, 40, 19_700),
            new Known(IoiCentralBoulevard.westTower(), 48, 24_500, 48, 22_050),
            new Known(MarinaOne.eastTower(), 34, 22_550, 34, 18_930),
            new Known(MarinaOne.westTower(), 34, 22_550, 34, 18_930),
            new Known(OneShenton.tower1(), 50, 21_400, 50, 16_870),
            new Known(OneShenton.tower2(), 43, 17_770, 43, 14_150),
            new Known(CapitalTower.tower(), 52, 25_400, 52, 23_170),
            new Known(GuocoTower.tower(), 64, 29_000, 39, 18_000));

    @Test
    void eachWithItsStoreysAndHeightAsPublished() {
        for (final Known k : KNOWN) {
            final OfficeTower t = k.tower();
            assertEquals(k.storeys(), t.storeys(), t.name());
            assertEquals(k.height(), t.roof() + t.crown(), 1, t.name());
            assertTrue(t.crown() >= 0, t.name() + ": its roof under its top");
            assertEquals(k.level(), t.levels().get(Homes.storey(k.floor())), 100,
                    t.name() + ": floor " + k.floor() + " where it is published to be");
        }
    }

    @Test
    void eachOfficeFloorServedByOneZone() {
        for (final Known k : KNOWN) {
            final OfficeTower t = k.tower();
            for (int f = 1; f < t.storeys(); f++) {
                final int floor = f;
                final long zones = t.banks().stream().map(Placed::bank)
                        .filter(b -> b.zoneFrom() <= floor && floor <= b.zoneTo()).map(b -> b.zoneFrom()).distinct()
                        .count();
                if (OfficeTower.OFFICE.equals(t.uses().get(f)) || Homes.HOMES.equals(t.uses().get(f))) {
                    assertEquals(1, zones, t.name() + ", floor " + (f + 1));
                }
            }
        }
    }

    @Test
    void onEveryStoreyItsCoreInsideItsOutlineClearOfItsGlassAndOfItself() {
        for (final Known k : KNOWN) {
            final OfficeTower t = k.tower();
            for (int f = 0; f < t.storeys(); f++) {
                final int storey = f;
                final List<double[]> core = new ArrayList<>();
                t.banks().stream().filter(b -> storey <= b.bank().zoneTo()).forEach(b -> core.add(b.hole()));
                t.service().stream().filter(s -> s.length < 6 || s[4] <= storey && storey <= s[5]).forEach(core::add);
                final List<double[]> outline = t.outline(f);
                for (int i = 0; i < core.size(); i++) {
                    final double[] r = core.get(i);
                    for (final double[] p : List.of(new double[] {r[0], r[2]}, new double[] {r[1], r[2]},
                            new double[] {r[1], r[3]}, new double[] {r[0], r[3]})) {
                        assertTrue(RefinedTest.inside(p, outline), t.name() + ", storey " + f + ": inside");
                        assertTrue(RefinedTest.clearance(p, outline) >= 200, t.name() + ", storey " + f + ": "
                                + RefinedTest.clearance(p, outline) / 100 + " m from its glass");
                    }
                    for (int j = i + 1; j < core.size(); j++) {
                        assertTrue(apart(r, core.get(j)), t.name() + ", storey " + f + ": two parts of its core");
                    }
                }
            }
        }
    }

    private static boolean apart(final double[] a, final double[] b) {
        return a[1] <= b[0] + 1 || b[1] <= a[0] + 1 || a[3] <= b[2] + 1 || b[3] <= a[2] + 1;
    }
}
