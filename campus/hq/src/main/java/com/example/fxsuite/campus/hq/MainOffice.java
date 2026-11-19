package com.example.fxsuite.campus.hq;

import com.example.fxsuite.avatar.build.EarthHouse;
import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Village;
import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.avatar.worlds.Meadow;

/**
 * The main office: a hut of the greatest luxury ({@link LuxuryHut}), the earth house as big as it is made, 14 m across,
 * its walls 4.2 m high and 70 cm thick, its roof rising 5.6 m to a gold finial; its walls of pale limestone, its roof
 * glazed gold, its woodwork and its rafters red lacquer, its window glazed, its floor of limestone; on its stone plinth
 * reaching 2.6 m past its walls, a porch of two lacquered columns under a gold canopy over its door. Furnished as an
 * executive's office, on a red rug.
 *
 * <p>It stands east of the green, its door to the well across it ({@link #place}).
 */
public final class MainOffice {

    /** How big it is built: as big as the earth house is made, its door and window as wide as they may be. */
    public static final EarthHouse.Size SIZE = new EarthHouse.Size(1_400, 420, 70, 560, 110, 180, 300, 160, true);

    /** It, as built and finished. */
    public static final LuxuryHut HUT = new LuxuryHut("Main office", SIZE, new LuxuryHut.Finish(Material.LIMESTONE,
            Material.GLAZED, Material.LACQUER, Material.LIMESTONE, Material.STONE, Material.GLAZED, Material.GLAZED,
            Material.LACQUER, Material.LACQUER, Material.GLAZED), 260, new LuxuryHut.Executive());

    /** Where it stands: east of the green, on the meadow's ground there, its door to the well. */
    public static final double EAST = 7_500;
    public static final double NORTH = 4_800;

    private MainOffice() {
    }

    /** What it is called. */
    public static String name() {
        return HUT.name();
    }

    /** Where it stands, facing as its door does: to the well. */
    public static Place place() {
        final double yaw = Math.toDegrees(Math.atan2(Village.WELL.x() - EAST, Village.WELL.z() - NORTH));
        return new Place(EAST, NORTH, Meadow.land().height(EAST, NORTH), yaw);
    }
}
