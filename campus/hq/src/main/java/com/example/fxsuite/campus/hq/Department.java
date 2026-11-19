package com.example.fxsuite.campus.hq;

import com.example.fxsuite.avatar.build.EarthHouse;
import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.avatar.worlds.Meadow;

import java.util.ArrayList;
import java.util.List;

/**
 * One of the main shared departments: the work every lab and world draws on, done in a hut of luxury of its own
 * ({@link LuxuryHut}), each its own size and finish, furnished as a team's room. They stand in a ring round the main
 * office ({@link #RING} out from it), from its north round its back to its south, each its door to it. The field labs
 * are not among them: theirs are to be buildings of their own, fitting what each does.
 *
 * @param number its place in the ring, from 1 at the main office's north
 * @param scope  what it does: its name
 * @param hut    the hut it works in
 */
public record Department(int number, String scope, LuxuryHut hut) {

    /** How far its ring is from the main office's middle; from what heading round it, by how much each to the next. */
    public static final double RING = 4_000;
    static final double FROM = 0;
    static final double EACH = 36;

    /** The main shared departments, in their places round the ring. */
    public static final List<Department> ALL = all();

    private static List<Department> all() {
        final List<Department> all = new ArrayList<>();
        all.add(new Department(1, "Bench UI Substrate", new LuxuryHut("Bench UI Substrate",
                new EarthHouse.Size(1_300, 400, 60, 520, 100, 150, 270, 140, true),
                new LuxuryHut.Finish(Material.PALE_CLAY, Material.COPPER, Material.PAINTED, Material.STONE,
                        Material.STONE, Material.COPPER, Material.GLAZED, Material.PAINTED, Material.TIMBER,
                        Material.COPPER), 230, new LuxuryHut.Team(3, 1))));
        all.add(new Department(2, "Generic Object Interaction", new LuxuryHut("Generic Object Interaction",
                new EarthHouse.Size(1_250, 390, 60, 500, 100, 150, 270, 140, true),
                new LuxuryHut.Finish(Material.SANDSTONE, Material.GREEN_GLAZED, Material.LACQUER, Material.LIMESTONE,
                        Material.LIMESTONE, Material.GLAZED, Material.GLAZED, Material.GREEN_GLAZED, Material.LACQUER,
                        Material.GLAZED), 230, new LuxuryHut.Team(3, -1))));
        all.add(new Department(3, "Simulation Core", new LuxuryHut("Simulation Core",
                new EarthHouse.Size(1_300, 410, 65, 540, 110, 160, 280, 150, false),
                new LuxuryHut.Finish(Material.LIMESTONE, Material.SLATE, Material.METAL, Material.LIMESTONE,
                        Material.STONE, Material.COPPER, Material.COPPER, Material.SLATE, Material.LACQUER,
                        Material.SLATE), 230, new LuxuryHut.Team(3, 0))));
        all.add(new Department(4, "Rendering", new LuxuryHut("Rendering",
                new EarthHouse.Size(1_200, 400, 60, 500, 100, 150, 270, 140, true),
                new LuxuryHut.Finish(Material.STONE, Material.GLAZED, Material.PAINTED, Material.LIMESTONE,
                        Material.LIMESTONE, Material.GLAZED, Material.GLAZED, Material.TILE, Material.PAINTED,
                        Material.GLAZED), 230, new LuxuryHut.Team(2, 1))));
        all.add(new Department(5, "Sound", new LuxuryHut("Sound",
                new EarthHouse.Size(1_150, 390, 60, 480, 100, 150, 270, 130, true),
                new LuxuryHut.Finish(Material.RAMMED_EARTH, Material.TILE, Material.TIMBER, Material.STONE,
                        Material.SANDSTONE, Material.COPPER, Material.COPPER, Material.LACQUER, Material.TIMBER,
                        Material.COPPER), 230, new LuxuryHut.Team(2, -1))));
        all.add(new Department(6, "Land and Roads", new LuxuryHut("Land and Roads",
                new EarthHouse.Size(1_200, 395, 60, 500, 100, 150, 270, 140, true),
                new LuxuryHut.Finish(Material.RED_EARTH, Material.GREY_TILE, Material.PAINTED, Material.STONE,
                        Material.STONE, Material.GREEN_GLAZED, Material.GLAZED, Material.SLATE, Material.LACQUER,
                        Material.GREEN_GLAZED), 230, new LuxuryHut.Team(2, 1))));
        return List.copyOf(all);
    }

    /** What it is called. */
    public String name() {
        return scope;
    }

    /** Which way round the main office it stands, degrees from north, to the east. */
    double heading() {
        return FROM + (number - 1) * EACH;
    }

    /** Where it stands: on its ring round the main office, on the meadow's ground there, its door to the main office. */
    public Place place() {
        final double t = Math.toRadians(heading());
        final double x = MainOffice.EAST + RING * Math.sin(t);
        final double z = MainOffice.NORTH + RING * Math.cos(t);
        final double yaw = Math.toDegrees(Math.atan2(MainOffice.EAST - x, MainOffice.NORTH - z));
        return new Place(x, z, Meadow.land(Campus.HOUSED).height(x, z), yaw);
    }
}
