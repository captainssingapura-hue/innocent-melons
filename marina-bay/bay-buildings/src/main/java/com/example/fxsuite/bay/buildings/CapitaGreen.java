package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CapitaGreen, on Market Street: Toyo Ito's office tower, its owner (CapitaLand Integrated Commercial Trust) publishing
 * a typical floor's plan without its lengths, so on the map's outline ({@link Offices}). Published: 40 storeys, 242 m
 * (its owner says 245), its top floor occupied 197 m up (the Skyscraper Center); its offices on floors 3 to 36, its
 * typical floors some 21,500 sq ft, its sky terraces on floors 5, 14 and 26; its sky forest and restaurant on floor 40,
 * under its crown, a wind catcher some 45 m high; its core 10 to 16 m from its walls. Seven cars to each of three
 * zones, two more to its crown, as a listing of it has them.
 */
public final class CapitaGreen {

    private CapitaGreen() {
    }

    /** CapitaGreen as the map draws it, anticlockwise from the edge its way in is in, facing Market Street. */
    static final Mapped MAPPED = Mapped.of(new double[][] {
            {-1051.89, -530.12}, {-1061.70, -500.57}, {-1094.52, -491.15}, {-1108.10, -537.42}, {-1103.57, -553.26},
            {-1057.81, -566.38}, {-1049.39, -537.69}});

    /**
     * Its floors 4.8 m apart, as a site on it says; so its triple-height lobby some 14.6 m high, its top floor where
     * it is published to be. Its floor 2 parking (its car park lifts serving it); floors 37 to 39 plant: estimates, as
     * are its zones' floors (3 to 14, 15 to 25, 26 to 36; its crown's 38 to 40), none published.
     */
    public static OfficeTower tower() {
        final Map<Integer, String> said = new HashMap<>();
        said.put(2, OfficeTower.PARKING);
        for (int n = 37; n <= 39; n++) {
            said.put(n, OfficeTower.PLANT);
        }
        said.put(40, "sky forest and restaurant");
        return Offices.tower("CapitaGreen", MAPPED, Offices.levels(1_460, 40, 2, 480), 24_200, said,
                List.of(new Offices.Zone(3, 14, 7, 400), new Offices.Zone(15, 25, 7, 600),
                        new Offices.Zone(26, 36, 7, 800), new Offices.Zone(38, 40, 2, 400)), 95, -299, List.of());
    }
}
