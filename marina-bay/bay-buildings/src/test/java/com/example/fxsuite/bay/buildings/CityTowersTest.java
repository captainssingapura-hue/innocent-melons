package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;
import com.example.fxsuite.avatar.buildings.Placed;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The district's towers of 150 m and up: each with its storeys and its height as published, a floor of it where it is
 * published to be. And every one of the bay's refined towers: each of its floors of offices, homes or hotel rooms served
 * by one zone of lifts; on every storey, each bank going up through it and each block of its core inside that storey's
 * outline, 1.5 m or more from its glass (a corridor's width), each bank clear of the rest of its core.
 */
class CityTowersTest {

    /** A tower, its published storeys, height (cm), and a floor of it with the level it is published to be at. */
    private record Known(OfficeTower tower, int storeys, double height, int floor, double level) {
    }

    private static final List<Known> KNOWN = List.of(
            new Known(SingaporeLandTower.tower(), 49, 19_000, 1, 0),
            new Known(OcbcCentre.tower(), 52, 19_770, 1, 0),
            new Known(SixBatteryRoad.tower(), 42, 17_400, 1, 0),
            new Known(MaybankTower.tower(), 32, 17_526, 1, 0),
            new Known(BankOfChinaBuilding.tower(), 37, 16_800, 1, 0),
            new Known(ChevronHouse.tower(), 33, 15_150, 1, 0),
            new Known(HitachiTower.tower(), 37, 17_930, 1, 0),
            new Known(UobPlazaTwo.tower(), 38, 16_200, 1, 0),
            new Known(SamsungHub.tower(), 30, 17_200, 1, 0),
            new Known(NtucCentre.tower(), 32, 16_280, 1, 0),
            new Known(HongLeongBuilding.tower(), 45, 15_800, 1, 0),
            new Known(OneGeorgeStreet.tower(), 25, 15_300, 1, 0),
            new Known(OxleyTower.tower(), 32, 17_430, 1, 0),
            new Known(EighteenRobinson.tower(), 28, 18_000, 28, 15_000),
            new Known(FrasersTower.tower(), 38, 23_500, 38, 19_360),
            new Known(KeppelSouthCentral.tower(), 33, 20_000, 33, 17_080),
            new Known(OueDowntown.tower1(), 50, 20_120, 1, 0),
            new Known(OueDowntown.tower2(), 37, 15_000, 1, 0),
            new Known(SpringleafTower.tower(), 37, 16_500, 1, 0),
            new Known(LippoCentre.tower(), 34, 15_010, 1, 0),
            new Known(SbfCenter.tower(), 31, 18_700, 10, 6_000),
            new Known(RobinsonSeventySeven.tower(), 40, 17_560, 1, 0),
            new Known(SgxCentre.centre1(), 30, 18_730, 1, 0),
            new Known(SgxCentre.centre2(), 30, 18_730, 1, 0),
            new Known(SkySuitesAnson.tower(), 72, 25_000, 72, 24_600),
            new Known(Altez.tower(), 62, 25_000, 62, 24_000),
            new Known(VOnShenton.tower(), 54, 23_700, 1, 0),
            new Known(Lumiere.tower(), 45, 15_300, 1, 0),
            new Known(IconLoft.tower1(), 41, 14_720, 1, 0),
            new Known(IconLoft.tower2(), 46, 16_300, 1, 0),
            new Known(PinnacleDuxton.blockA(), 50, 15_600, 50, 14_712),
            new Known(PinnacleDuxton.blockB(), 50, 15_600, 50, 14_712),
            new Known(PinnacleDuxton.blockC(), 50, 15_600, 50, 14_712),
            new Known(PinnacleDuxton.blockD(), 50, 15_600, 50, 14_712),
            new Known(PinnacleDuxton.blockE(), 50, 15_600, 50, 14_712),
            new Known(PinnacleDuxton.blockF(), 50, 15_600, 50, 14_712),
            new Known(PinnacleDuxton.blockG(), 50, 15_600, 50, 14_712),
            new Known(OnePearlBank.westTower(), 39, 15_510, 39, 14_370),
            new Known(OnePearlBank.eastTower(), 39, 15_510, 39, 14_370),
            new Known(InternationalPlaza.tower(), 50, 19_000, 1, 0),
            new Known(MilleniaTower.tower(), 41, 22_300, 1, 0),
            new Known(CentennialTower.tower(), 37, 15_760, 1, 0),
            new Known(SuntecCity.tower1(), 45, 18_080, 1, 0),
            new Known(SuntecCity.tower2(), 45, 18_080, 1, 0),
            new Known(SuntecCity.tower3(), 45, 18_080, 1, 0),
            new Known(SuntecCity.tower4(), 45, 18_080, 1, 0),
            new Known(Duo.tower(), 39, 17_940, 39, 16_856),
            new Known(Duo.residences(), 50, 18_600, 50, 17_500),
            new Known(SwissotelStamford.tower(), 73, 22_600, 1, 0),
            new Known(RafflesCityTower.tower(), 42, 15_800, 1, 0),
            new Known(Gateway.east(), 37, 15_000, 1, 0),
            new Known(Gateway.west(), 37, 15_000, 1, 0),
            new Known(StateCourts.officeTower(), 35, 17_780, 35, 16_390),
            new Known(StateCourts.courtTower(), 35, 17_780, 35, 16_390),
            new Known(PickeringOperationsComplex.tower(), 43, 17_700, 1, 0),
            new Known(OasiaHotelDowntown.tower(), 27, 19_090, 27, 15_570),
            new Known(CanningHillPiers.tower(), 48, 18_000, 1, 0));

    /** What a floor is that a zone of lifts must serve. */
    private static final Set<String> SERVED = Set.of(OfficeTower.OFFICE, Homes.HOMES, Offices.HOTEL);

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
    void everyTowersFloorsOfOfficesHomesAndRoomsServedByOneZone() {
        final List<String> wrong = new ArrayList<>();
        for (final Refined.Tower r : Refined.TOWERS) {
            final OfficeTower t = r.model().get();
            for (int f = 1; f < t.storeys(); f++) {
                final int floor = f;
                final long zones = t.banks().stream().map(Placed::bank)
                        .filter(b -> b.zoneFrom() <= floor && floor <= b.zoneTo()).map(b -> b.zoneFrom()).distinct()
                        .count();
                if (SERVED.contains(t.uses().get(f)) && zones != 1) {
                    wrong.add(t.name() + ", storey " + f + ": " + zones + " zones");
                }
            }
        }
        assertTrue(wrong.isEmpty(), String.join("\n", wrong));
    }

    @Test
    void everyTowersCoreInsideEachStoreysOutlineClearOfItsGlassEachBankClearOfTheRest() {
        final List<String> wrong = new ArrayList<>();
        for (final Refined.Tower r : Refined.TOWERS) {
            final OfficeTower t = r.model().get();
            for (int f = 0; f < t.storeys(); f++) {
                final int storey = f;
                final List<double[]> core = new ArrayList<>();
                t.banks().stream().filter(b -> storey <= b.bank().zoneTo()).forEach(b -> core.add(b.hole()));
                final int holes = core.size();
                t.service().stream().filter(s -> s.length < 6 || s[4] <= storey && storey <= s[5]).forEach(core::add);
                final List<double[]> outline = t.outline(f);
                for (int i = 0; i < core.size(); i++) {
                    final double[] c = core.get(i);
                    for (final double[] p : List.of(new double[] {c[0], c[2]}, new double[] {c[1], c[2]},
                            new double[] {c[1], c[3]}, new double[] {c[0], c[3]})) {
                        if (!RefinedTest.inside(p, outline) || RefinedTest.clearance(p, outline) < 150) {
                            wrong.add(t.name() + ", storey " + f + ": " + RefinedTest.clearance(p, outline) / 100
                                    + " m from its glass");
                        }
                    }
                    for (int j = i + 1; j < core.size() && i < holes; j++) {
                        final double[] d = core.get(j);
                        if (!(c[1] <= d[0] + 1 || d[1] <= c[0] + 1 || c[3] <= d[2] + 1 || d[3] <= c[2] + 1)) {
                            wrong.add(t.name() + ", storey " + f + ": a bank and another part of its core");
                        }
                    }
                }
            }
        }
        assertTrue(wrong.isEmpty(), wrong.stream().distinct().limit(40).reduce("", (a, b) -> a + "\n" + b));
    }
}
