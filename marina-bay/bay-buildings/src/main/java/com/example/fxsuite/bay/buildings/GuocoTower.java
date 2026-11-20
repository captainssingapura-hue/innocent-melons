package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;
import com.example.fxsuite.avatar.buildings.Placed;
import com.example.fxsuite.avatar.lifts.HighRiseBank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Guoco Tower, at Tanjong Pagar: SOM's office tower with homes over it, Wallich Residence, its owner (GuocoLand)
 * publishing no plans of it, so on the map's outlines: its offices' plate whole up to its residence, the residence's
 * slab over the south half of it. Published: 64 storeys, 290 m (SOM; the Skyscraper Center says 65 and 283.7 m); a
 * podium of shops on floors 1 to 6, a gym on 6; its offices on floors 7 to 37; its residence on floors 39 to 64, some
 * 180 m up, its pool and gym on 39, its residents' club on 52, its observation deck on 62, its penthouse at its top.
 * Three zones of eight cars to its offices (7 to 16, 17 to 27, 28 to 37), as a lift fans' database has them.
 *
 * <p>Its lifts and core laid out here, as no other tower's are like it: its offices' core in the north half of its
 * plate, its zones' lobbies side by side across it, their stairs, service lifts and toilets south of them, as high as
 * its offices go; its residence's core in the south half, under the slab: four cars either side of one lobby, going
 * up from the lobby without stopping to the residence, its stairs and service lift beside them. Estimates.
 */
public final class GuocoTower {

    private GuocoTower() {
    }

    /** Guoco Tower's offices as the map draws them, anticlockwise from the edge its way in is in, by Wallich Street. */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-1381.56, -1136.22}, {-1438.71, -1159.42}, {-1428.96, -1183.38}, {-1418.78, -1208.34},
            {-1361.62, -1185.14}, {-1372.03, -1159.62}});

    /** Wallich Residence over it, from floor 39, as the map draws it. */
    private static final double[][] RESIDENCE = {
            {-1428.96, -1183.38}, {-1372.03, -1159.62}, {-1381.56, -1136.22}, {-1438.71, -1159.42}};

    /**
     * Its lobby 6 m high and its podium's floors 5 m, so its podium is the 30 m the map has; its offices' floors 4.66 m
     * apart, so its residence starts 180 m up; the residence's 3.85 m: estimates. Floor 38, between its offices and
     * its residence, plant: an estimate.
     */
    public static OfficeTower tower() {
        final List<Double> levels = Offices.levels(600, 64, 2, 500, 7, 466, 39, 385);
        final Map<Integer, String> said = new HashMap<>();
        for (int n = 2; n <= 5; n++) {
            said.put(n, "shops");
        }
        said.put(6, "gym");
        for (int n = 7; n <= 37; n++) {
            said.put(n, OfficeTower.OFFICE);
        }
        said.put(38, OfficeTower.PLANT);
        said.put(39, "pool and gym");
        said.put(52, "residents' club");
        said.put(62, "observation deck");
        said.put(63, Homes.PENTHOUSES);
        said.put(64, Homes.PENTHOUSES);
        final List<String> uses = new ArrayList<>(List.of(OfficeTower.LOBBY));
        for (int n = 2; n <= 64; n++) {
            uses.add(said.getOrDefault(n, Homes.HOMES));
        }
        final List<Placed> banks = new ArrayList<>();
        final int[][] zones = {{7, 16}, {17, 27}, {28, 37}};
        for (int k = 0; k < zones.length; k++) {
            final int from = Homes.storey(zones[k][0]);
            final int to = Homes.storey(zones[k][1]);
            final double middle = -1_275 + 425 + k * 850;
            banks.add(new Placed(office(levels, from, to, 400 + 200 * k), middle - 295, 1_300, 90));
            banks.add(new Placed(office(levels, from, to, 400 + 200 * k), middle + 295, 1_300, -90));
        }
        final int homesFrom = Homes.storey(39);
        final int homesTo = Homes.storey(64);
        final HighRiseBank homes = OfficeTower.bank(levels, homesFrom, homesTo, 4, 600);
        banks.add(new Placed(homes, -275, -1_300, 90));
        banks.add(new Placed(OfficeTower.bank(levels, homesFrom, homesTo, 4, 600), 275, -1_300, -90));
        final List<double[]> service = List.of(
                // the offices' stairs, service lifts and toilets, as high as they go
                new double[] {-1_275, 1_275, 0, 700, 0, Homes.storey(37)},
                // the residence's stairs and service lift, beside its lifts, to the roof
                new double[] {440, 1_040, -1_800, -800});
        return new OfficeTower("Guoco Tower", MAPPED.outline(), levels, uses, service, banks,
                29_000 - levels.getLast(), 0, List.of(new OfficeTower.Setback(homesFrom, MAPPED.own(RESIDENCE))));
    }

    /** Four of its office cars, as the financial centre's are, serving the lobby and floors {@code from} to {@code to}. */
    private static HighRiseBank office(final List<Double> levels, final int from, final int to, final double speed) {
        return OfficeTower.bank(levels.subList(0, to + 1), from, to, 4, speed, 260, 210, 270, 110);
    }
}
