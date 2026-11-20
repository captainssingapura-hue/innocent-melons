package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IOI Central Boulevard Towers' West Tower: an office tower its owner (IOI Properties) publishes plans of without their
 * lengths, so on the map's outline ({@link Offices}). Published: 48 storeys, 245 m (the Skyscraper Center says 237.9),
 * its top floor occupied 220.5 m up; Central Square on floor 1, its office lobbies and drop-off on floor 2, parking on
 * 3 and 4, the podium's large offices on 5 and 6, the Central Green garden on 7, plant on 23; its offices in three
 * zones, floors 8 to 22 and 24 to 36 eight cars each, 37 to 48 nine; its core 7 to 14 m from its glass. Its East
 * Tower, 16 storeys on the podium, is not here.
 */
public final class IoiCentralBoulevard {

    private IoiCentralBoulevard() {
    }

    /** The West Tower as the map draws it, anticlockwise from the edge its way in is in, facing Commerce Street. */
    static final Mapped WEST_TOWER = Mapped.of(new double[][] {
            {-932.84, -711.76}, {-902.77, -720.70}, {-902.18, -718.95}, {-896.23, -720.63}, {-892.78, -709.43},
            {-879.73, -667.44}, {-879.38, -666.30}, {-882.25, -665.46}, {-885.52, -664.50}, {-884.91, -662.62},
            {-915.15, -653.57}, {-915.71, -655.46}, {-921.91, -653.67}, {-938.96, -707.96}, {-932.24, -709.98}});

    /**
     * The West Tower: its floor 1 9 m high, as Central Square's ceiling is, and its floors 4.6 m apart, estimates, so
     * its top floor is where it is published to be. Standing whole from the ground, where it stands on the podium's
     * columns; the podium's floors kept as their uses.
     */
    public static OfficeTower westTower() {
        final Map<Integer, String> said = new HashMap<>();
        said.put(2, "office lobbies");
        said.put(3, OfficeTower.PARKING);
        said.put(4, OfficeTower.PARKING);
        said.put(5, "podium offices");
        said.put(6, "podium offices");
        said.put(7, "sky garden");
        said.put(23, OfficeTower.PLANT);
        return Offices.tower("IOI Central Boulevard West Tower", WEST_TOWER, Offices.levels(900, 48, 2, 460), 24_500,
                said, List.of(new Offices.Zone(8, 22, 8, 400), new Offices.Zone(24, 36, 8, 600),
                        new Offices.Zone(37, 48, 9, 800)), 7, 463, List.of());
    }
}
