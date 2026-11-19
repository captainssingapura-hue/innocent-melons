package com.example.fxsuite.campus.hq;

import com.example.fxsuite.avatar.sim.Land;

import java.util.List;

/**
 * The campus's quarters, by the units of the organisation (the campus plan, in the studio's architecture office): the
 * platform of the facilities round the main office, and a quarter a lab, each where its scope belongs on the meadow,
 * the loud and the dangerous far out, the tall on the rising ground, the quiet near the green. Their ground only, as
 * yet: each in a cover of its own, nothing built on it but the platform's huts that stand already.
 */
public final class Quarters {

    /** The platform, round the main office: as far out as its ring of departments reaches, and a little more. */
    static final double PLATFORM = 58;

    /** All of them: the platform first, then the labs in the organisation's order. */
    public static final List<Quarter> ALL = List.of(
            new Quarter("Platform", "the facilities, round the architecture office", Land.Cover.PAVING, List.of(
                    new Quarter.Plot("round the main office", Quarter.round(MainOffice.EAST / 100,
                            MainOffice.NORTH / 100, PLATFORM, 48)))),
            new Quarter("Landscape", "the lab of land and media, and what grows on the land", Land.Cover.MOSS, List.of(
                    new Quarter.Plot("the north bank, west of the bridge", Quarter.metres(new double[][] {
                        {-320, -80}, {-110, -50}, {-110, 10}, {-320, 10}})))),
            new Quarter("Networks", "the lab of roads and what carries them", Land.Cover.SLATE, List.of(
                    new Quarter.Plot("between the river and the highway, east of the junction",
                            Quarter.rectangle(60, -130, 380, -60)))),
            new Quarter("Structures", "the lab of buildings and what is inside them", Land.Cover.SAND, List.of(
                    new Quarter.Plot("the north-east slope", Quarter.rectangle(150, 115, 330, 260)))),
            new Quarter("Machines", "the lab of fixtures that move along fixed ways", Land.Cover.CLAY, List.of(
                    new Quarter.Plot("up the slope beside Structures", Quarter.rectangle(150, 270, 330, 380)))),
            new Quarter("Vehicles", "the lab of what carries people and goods under its own power", Land.Cover.STRAW,
                    List.of(
                            new Quarter.Plot("the proving ground, the north bank east",
                                    Quarter.rectangle(150, -10, 420, 100)),
                            new Quarter.Plot("the airfield, far west on the long meadow",
                                    Quarter.rectangle(-900, 20, -450, 140)),
                            new Quarter.Plot("the range, far east", Quarter.rectangle(700, 60, 950, 200)))),
            new Quarter("Life", "the lab of what lives and decides for itself", Land.Cover.LAWN, List.of(
                    new Quarter.Plot("along the avenue up toward the chapel, east of it", Quarter.metres(new double[][] {
                        {-40, 136}, {61, 195}, {-60, 394}, {-161, 335}})))),
            new Quarter("Objects", "the lab of loose and passing things: the commons", Land.Cover.MULCH, List.of(
                    new Quarter.Plot("the commons, on the old village's ground west of the green",
                            Quarter.metres(new double[][] {
                                {-95, -10}, {-20, -10}, {-26, 30}, {-35, 60}, {-45, 80}, {-95, 80}})))));

    private Quarters() {
    }

    /** Every plot of every quarter as the land has it: each its quarter's cover over it. */
    public static List<Land.Layer> layers() {
        return ALL.stream().flatMap(q -> q.layers().stream()).toList();
    }
}
