package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;
import com.example.fxsuite.avatar.buildings.Standing;
import com.example.fxsuite.avatar.sim.Land;
import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.bay.land.MarinaBay;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * The bay's refined towers, one list: each by its name, where it stands in the bay, its model. Whatever shows or plays
 * the bay's towers takes them from here, in this order; each drawn by its own keys, given in the same order from one
 * first key, so no two share any. A tower more is a line more here.
 *
 * <p>Where each stands ({@link MarinaBay}'s frame, cm): its middle where the map has it, turned as its sides run
 * there, its feet on the bay's land.
 * <ul>
 * <li>The financial centre's three office towers ({@link Mbfc}), their plans' north along: which way each faces is the
 * map's, and agrees with each one's outline, Tower 1's notch to its north-west, Tower 2's long sides across, Tower 3's
 * step at its south-west.
 * <li>Its two homes towers ({@link MbfcHomes}) and The Sail's two ({@link Sail}): on the map's outlines, turned as
 * those are made their own ({@link Mapped}).
 * <li>One Raffles Quay's two ({@link Orq}): the North Tower's plan's north to its north-west face, where the map draws
 * the jogs its plan has at that face's corners; the South Tower's long sides along the longer pair of the map's, its
 * outline drawn small there.
 * <li>Asia Square's two ({@link AsiaSquare}): the map draws both the same, 64.6 by 56.5 m, its long sides at 106°;
 * Tower 1's plan's long sides along them, Tower 2's, nearly square, its longer sides along them. Which end of each is
 * its plan's top, the plans do not say: a guess.
 * </ul>
 */
public final class Refined {

    private Refined() {
    }

    /** The first key the bay's towers are drawn by, each taking {@link Standing#KEYS} from it in turn. */
    public static final int KEYS = 975_000;

    /**
     * One of the bay's refined towers.
     *
     * @param name  what it is called, as its model calls it
     * @param x     where its middle stands in the bay, across, cm
     * @param z     where its middle stands in the bay, along, cm
     * @param yaw   which way it is turned there, degrees
     * @param model its model, made when asked for
     */
    public record Tower(String name, double x, double z, double yaw, Supplier<OfficeTower> model) {
    }

    /** The bay's refined towers, in order. */
    public static final List<Tower> TOWERS = List.of(
            new Tower("MBFC Tower 1", -58_590, -57_440, 3.7, Mbfc::tower1),
            new Tower("MBFC Tower 2", -60_880, -64_900, 16.1, Mbfc::tower2),
            new Tower("MBFC Tower 3", -52_350, -69_450, -8.6, Mbfc::tower3),
            mapped("Marina Bay Residences", MbfcHomes.RESIDENCES, MbfcHomes::residences),
            mapped("Marina Bay Suites", MbfcHomes.SUITES, MbfcHomes::suites),
            new Tower("ORQ North Tower", -85_040, -49_850, -48.3, Orq::north),
            new Tower("ORQ South Tower", -87_950, -57_550, -74.3, Orq::south),
            mapped("The Sail, Marina Bay Tower", Sail.MARINA_BAY_TOWER, Sail::marinaBayTower),
            mapped("The Sail, Central Park Tower", Sail.CENTRAL_PARK_TOWER, Sail::centralParkTower),
            new Tower("Asia Square Tower 1", -85_590, -78_710, -74.2, AsiaSquare::tower1),
            new Tower("Asia Square Tower 2", -88_680, -89_810, 15.9, AsiaSquare::tower2));

    private static Tower mapped(final String name, final Mapped mapped, final Supplier<OfficeTower> model) {
        return new Tower(name, mapped.x(), mapped.z(), mapped.yaw(), model);
    }

    /** Each where it stands in the bay, its feet on its land, drawn by its keys: each made now. */
    public static List<Standing> standing() {
        final Land land = MarinaBay.land();
        final List<Standing> standing = new ArrayList<>();
        for (int k = 0; k < TOWERS.size(); k++) {
            final Tower t = TOWERS.get(k);
            standing.add(new Standing(t.model().get(), new Place(t.x(), t.z(), land.height(t.x(), t.z()), t.yaw()),
                    KEYS + k * Standing.KEYS));
        }
        return standing;
    }
}
