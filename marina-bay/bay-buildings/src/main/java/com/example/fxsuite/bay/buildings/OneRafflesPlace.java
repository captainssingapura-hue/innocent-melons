package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * One Raffles Place, its two office towers by Tange Associates, from what their architect published of them (its
 * section through both and Tower 2's plans, their lengths written on them) and their owner (OUB Centre). Tower 1 the
 * map's two triangular prisms joined, as its owner says it is: the lower to 215 m, the taller to its top; Tower 2 its
 * plans' plate, set where the map has it.
 */
public final class OneRafflesPlace {

    private OneRafflesPlace() {
    }

    /** Tower 1's two prisms as the map draws them, joined, anticlockwise from the edge its way in is in, by Chulia Street. */
    static final Mapped TOWER_1 = Mapped.of(new double[][] {
            {-1041.55, -170.24}, {-1082.30, -178.30}, {-1075.57, -215.02}, {-1074.99, -218.41}, {-1045.34, -212.46},
            {-1048.28, -197.74}, {-1051.36, -185.22}});

    /** Its taller prism, over 215 m. */
    private static final double[][] TALLER = {
            {-1041.55, -170.24}, {-1051.36, -185.22}, {-1074.99, -218.41}, {-1075.57, -215.02}, {-1082.30, -178.30}};

    /** Where the map has Tower 2: its rectangle, anticlockwise from its side on Market Street. */
    static final Mapped TOWER_2 = Mapped.of(new double[][] {
            {-1128.56, -181.02}, {-1120.78, -223.38}, {-1075.57, -215.02}, {-1083.33, -172.66}});

    /**
     * Tower 1: 63 storeys to its observation deck, 277.8 m to its top. Its storeys as its section writes them: its
     * lobby 5.2 m, its floors 4 m, those under its machine rooms 4.2 m, its machine rooms on floors 9, 22, 35 and 48
     * 7.8 m; floor 60 253.2 m up, as the Skyscraper Center has it; two floors' height to floor 61, its restaurants on 61
     * and 62, its observation deck on 63, 6.55 m high. Its shops on floors 2 to 5, its offices on 6 to 60, as its owner
     * says. Stepping in to its taller prism from the first floor over 215 m. Its lifts in two zones of six cars, floors
     * 6 to 34 and 36 to 63, three either side of a lobby, as its owner's plan draws a floor's: an estimate, the zones
     * not published.
     */
    public static OfficeTower tower1() {
        final List<Double> levels = Offices.levels(520, 63, 2, 400, 8, 420, 9, 780, 10, 400, 21, 420, 22, 780, 23, 400,
                34, 420, 35, 780, 36, 400, 47, 420, 48, 780, 49, 400, 60, 800, 61, 400, 63, 655);
        final Map<Integer, String> said = new HashMap<>();
        for (int n = 2; n <= 5; n++) {
            said.put(n, "shops");
        }
        for (final int n : new int[] {9, 22, 35, 48}) {
            said.put(n, OfficeTower.PLANT);
        }
        said.put(61, "restaurant");
        said.put(62, "restaurant");
        said.put(63, "observation deck");
        int over = 1;
        while (levels.get(over) < 21_500) {
            over++;
        }
        return Offices.tower("One Raffles Place Tower 1", TOWER_1, levels, 27_780, said,
                List.of(new Offices.Zone(6, 34, 6, 500), new Offices.Zone(36, 63, 6, 600)), 239, -652,
                List.of(new OfficeTower.Setback(over, TOWER_1.own(TALLER))));
    }

    /**
     * Tower 2: 38 storeys, 209.6 m to its top. Its plate as its plans write it: 33.36 m along Market Street, 35.16 m
     * the other way, its columns 7.74 and 8.64 m apart round its edge, a square (one face drawn a little on the slant,
     * the sloping roof over its last floors' one corner, neither drawn). Its storeys as its section writes them: its
     * lobby 5.6 m; floors 2 and 3, shops, 4 m; 4 to 7 4.92 m; its machine room on floor 8 8.62 m; 9 to 38 4.31 m, so
     * floor 38 is 166.8 m up, as the Skyscraper Center has it. Plant on 23 and 33. Its lifts as its plans draw them,
     * along its west side: a low zone (floors 4 to 21) of two cars facing three, a high zone (22 to 38) the same.
     */
    public static OfficeTower tower2() {
        final double a = 1_668;
        final double l = 1_758;
        final Mapped plate = new Mapped(List.of(new double[] {-a, -l}, new double[] {a, -l}, new double[] {a, l},
                new double[] {-a, l}), TOWER_2.x(), TOWER_2.z(), TOWER_2.yaw());
        final Map<Integer, String> said = new HashMap<>();
        said.put(2, "shops");
        said.put(3, "shops");
        for (final int n : new int[] {8, 23, 33}) {
            said.put(n, OfficeTower.PLANT);
        }
        return Offices.tower("One Raffles Place Tower 2", plate,
                Offices.levels(560, 38, 2, 400, 4, 492.31, 8, 861.54, 9, 430.77), 20_960, said,
                List.of(new Offices.Zone(4, 21, 5, 500), new Offices.Zone(22, 38, 5, 600)), -600, 0, List.of());
    }
}
