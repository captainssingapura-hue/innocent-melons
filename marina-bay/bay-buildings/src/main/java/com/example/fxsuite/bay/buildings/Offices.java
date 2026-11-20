package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.OfficeTower;
import com.example.fxsuite.avatar.buildings.Placed;
import com.example.fxsuite.avatar.lifts.HighRiseBank;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * What the bay's office towers share whose owners publish no plans with their lengths written on them: each an office
 * tower ({@link OfficeTower}) on the map's outline ({@link Mapped}), stepping in where the map's parts of it do, its
 * storeys and its height as published; what no one publishes estimated, the same way for each, and said to be.
 *
 * <p>Its core the estimate, as the generic tower's is: a lift lobby for each zone, side by side across the core, a bank
 * of cars either side of it facing across; under each zone's banks, along the core's south side, a block of its
 * stairs, service lifts, toilets and risers, as high as that zone goes (the last's to the roof), so the core narrows as
 * its zones end.
 *
 * <p>A storey here is one of the model's: the lobby the first (0), the building's floor 1; floor {@code n} over it the
 * model's {@code n - 1} ({@link Homes#storey}).
 */
final class Offices {

    private Offices() {
    }

    /** The block of stairs, service lifts, toilets and risers, so deep along. */
    static final double SERVICE = 700;

    /** A zone of lifts: serving the lobby and floors {@code from} to {@code to}, {@code cars} cars, at {@code speed}. */
    record Zone(int from, int to, int cars, double speed) {
    }

    /**
     * Each floor's level, the roof's last: the lobby {@code lobby} high; then, for each pair in {@code spans}, from
     * that floor up each that high, till the next pair's floor; floors 2 to {@code top}.
     */
    static List<Double> levels(final double lobby, final int top, final double... spans) {
        final List<Double> levels = new ArrayList<>(List.of(0.0, lobby));
        for (int n = 3; n <= top + 1; n++) {
            double storey = 0;
            for (int k = 0; k < spans.length; k += 2) {
                if (n - 1 >= spans[k]) {
                    storey = spans[k + 1];
                }
            }
            levels.add(levels.getLast() + storey);
        }
        return levels;
    }

    /**
     * An office tower: on {@code mapped}'s outline, its way in at the middle of its first edge, stepping in as
     * {@code setbacks} say; its floors at {@code levels}; its top {@code height} up, its crown what is over its roof;
     * each floor offices but those {@code said} to be something else; its lifts in {@code zones}, west to east across
     * its core; its core's middle at ({@code across}, {@code along}).
     */
    static OfficeTower tower(final String name, final Mapped mapped, final List<Double> levels, final double height,
                             final Map<Integer, String> said, final List<Zone> zones, final double across,
                             final double along, final List<OfficeTower.Setback> setbacks) {
        final int top = levels.size() - 1;
        final List<String> uses = new ArrayList<>(List.of(OfficeTower.LOBBY));
        for (int n = 2; n <= top; n++) {
            uses.add(said.getOrDefault(n, OfficeTower.OFFICE));
        }
        final List<Placed> shapes = zones.stream().map(z -> new Placed(bank(levels, 1, 1, (z.cars() + 1) / 2, 400),
                0, 0, 90)).toList();
        final double longest = shapes.stream().mapToDouble(Placed::length).max().orElse(0);
        final double deep = longest + SERVICE;
        final double banksAlong = along + deep / 2 - longest / 2;
        double wide = 0;
        for (final Placed p : shapes) {
            wide += 2 * p.depth() + Homes.LIFT_LOBBY;
        }
        final List<Placed> banks = new ArrayList<>();
        final List<double[]> service = new ArrayList<>();
        double west = across - wide / 2;
        for (int k = 0; k < zones.size(); k++) {
            final Zone z = zones.get(k);
            final double depth = shapes.get(k).depth();
            final double block = 2 * depth + Homes.LIFT_LOBBY;
            final int from = Homes.storey(z.from());
            final int to = Homes.storey(z.to());
            final int one = (z.cars() + 1) / 2;
            banks.add(new Placed(bank(levels, from, to, one, z.speed()), west + depth / 2, banksAlong, 90));
            banks.add(new Placed(bank(levels, from, to, z.cars() - one, z.speed()), west + block - depth / 2,
                    banksAlong, -90));
            final double[] under = {west, west + block, along - deep / 2, along - deep / 2 + SERVICE};
            // the last zone's block up to the roof, whatever is over the zone
            service.add(k == zones.size() - 1 ? under : new double[] {under[0], under[1], under[2], under[3], 0, to});
            west += block;
        }
        return new OfficeTower(name, mapped.outline(), levels, uses, service, banks, height - levels.getLast(), 0,
                setbacks);
    }

    /**
     * As {@link #tower}, its core slim, for a floor too small or too narrow for the other: each zone's two banks
     * facing each other along, across one lift lobby, the zones side by side across; one block of stairs, service
     * lift and risers at its east end, up to the roof. So its core is as deep as one lobby and its banks, and no more.
     */
    static OfficeTower slim(final String name, final Mapped mapped, final List<Double> levels, final double height,
                            final Map<Integer, String> said, final List<Zone> zones, final double across,
                            final double along, final List<OfficeTower.Setback> setbacks) {
        final int top = levels.size() - 1;
        final List<String> uses = new ArrayList<>(List.of(OfficeTower.LOBBY));
        for (int n = 2; n <= top; n++) {
            uses.add(said.getOrDefault(n, OfficeTower.OFFICE));
        }
        final List<Placed> shapes = zones.stream().map(z -> new Placed(bank(levels, 1, 1, (z.cars() + 1) / 2, 400),
                0, 0, 0)).toList();
        final double depth = shapes.getFirst().depth();
        double wide = Homes.SERVICE;
        for (final Placed p : shapes) {
            wide += p.length();
        }
        final List<Placed> banks = new ArrayList<>();
        double west = across - wide / 2;
        for (int k = 0; k < zones.size(); k++) {
            final Zone z = zones.get(k);
            final double middle = west + shapes.get(k).length() / 2;
            final int from = Homes.storey(z.from());
            final int to = Homes.storey(z.to());
            final int one = (z.cars() + 1) / 2;
            banks.add(new Placed(bank(levels, from, to, one, z.speed()), middle,
                    along - Homes.LIFT_LOBBY / 2 - depth / 2, 0));
            banks.add(new Placed(bank(levels, from, to, z.cars() - one, z.speed()), middle,
                    along + Homes.LIFT_LOBBY / 2 + depth / 2, 180));
            west += shapes.get(k).length();
        }
        final List<double[]> service = List.of(new double[] {west, west + Homes.SERVICE,
            along - Homes.LIFT_LOBBY / 2 - depth, along + Homes.LIFT_LOBBY / 2 + depth});
        return new OfficeTower(name, mapped.outline(), levels, uses, service, banks, height - levels.getLast(), 0,
                setbacks);
    }

    /** What a hotel's floors of rooms are. */
    static final String HOTEL = "hotel rooms";

    /** The first storey of {@code levels} at or over {@code height}: where a tower steps in at that height. */
    static int over(final List<Double> levels, final double height) {
        int storey = 1;
        while (levels.get(storey) < height) {
            storey++;
        }
        return storey;
    }

    /** What each floor is, as {@code fromToUse} says it in threes: from a floor, to a floor, that use. */
    static Map<Integer, String> uses(final Object... fromToUse) {
        final Map<Integer, String> uses = new java.util.HashMap<>();
        for (int k = 0; k < fromToUse.length; k += 3) {
            for (int n = (Integer) fromToUse[k]; n <= (Integer) fromToUse[k + 1]; n++) {
                uses.put(n, (String) fromToUse[k + 2]);
            }
        }
        return uses;
    }

    /** A bank of {@code cars} office cars, as the financial centre's are, serving the lobby and floors {@code from} on. */
    private static HighRiseBank bank(final List<Double> levels, final int from, final int to, final int cars,
                                     final double speed) {
        return OfficeTower.bank(levels.subList(0, to + 1), from, to, cars, speed, 260, 210, 270, 110);
    }
}
