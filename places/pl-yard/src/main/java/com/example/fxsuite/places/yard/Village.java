package com.example.fxsuite.places.yard;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Num;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Turned;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * A village along a lane: so many homes, by turns on the lane's left and its right, each a yard walled round with
 * a gateway onto the lane, and in the yard a house that is half a giant watermelon. The lane runs on from where the
 * avatar starts, the way the avatar faces. The same village every time: how big each yard and each melon is, and
 * how each melon is turned, comes from the engine's own scatter, which repeats. Before the village, behind
 * where the avatar starts, is its small lake.
 */
public record Village(int homes) {

    /** How wide the lane is from its middle to a yard's front, and how far along it the first yard's middle is. */
    private static final double LANE = 350;
    private static final double BEGINS = 1100;
    /** How far along the lane from one yard's middle to the next on the same side. */
    private static final double APART = 2000;
    private static final double WALL_HIGH = 190;
    private static final double WALL_THICK = 30;
    private static final double GATE = 300;
    /** How many kinds of wall the yards are walled with, by turns. */
    private static final int WALLINGS = 3;

    /**
     * One home: its yard's wall, the house in the yard, and which kind of wall the yard has (0, 1 or 2).
     */
    public record Home(Walled yard, MelonHouse house, int walling) {
    }

    /** Every home of the village, the first nearest where the avatar starts. */
    public List<Home> homesteads() {
        final List<Home> all = new ArrayList<>();
        for (int home = 0; home < homes; home++) {
            final boolean onLeft = home % 2 == 0;
            final double frontage = 1500 + 300 * Num.hash(home, 1);
            final double deep = 1300 + 200 * Num.hash(home, 2);
            // a yard faces the lane: one on the left faces right, and one on the right, left
            final double turned = onLeft ? -90 : 90;
            final Vec3 middle = new Vec3((onLeft ? -1 : 1) * (LANE + deep / 2), 0, BEGINS + APART * (home / 2));
            final Walled yard = new Walled(middle, turned, frontage, deep, WALL_HIGH, WALL_THICK, GATE, WALL_HIGH);
            // the house a little toward the back of its yard, its length along the yard's front, give or take
            final Vec3 back = new Turned(new Vec3(0, 1, 0), turned).matrix()
                    .clip(new Vec3(0, 0, 0.1 * deep)).shown();
            all.add(new Home(yard, new MelonHouse(middle.plus(back), 20 + 6 * Num.hash(home, 3),
                    turned + 30 * (Num.hash(home, 4) - 0.5)), home % WALLINGS));
        }
        return all;
    }

    /**
     * The small lake before the village: some 120 m by 62 m, about twice the ground the village itself stands on
     * (eight homes: 78 m along the lane by 37 m across it). It lies across the lane's line behind where the avatar
     * starts, its near shore five or six metres off, so the avatar starts between the lake and the village.
     */
    public VillageLake lake() {
        return new VillageLake(new Vec3(0, 0, -3400), 12_000, 6_200);
    }

    /** Where each house stands, for the one half melon that is drawn once for each. */
    public List<Mat4> houses() {
        return homesteads().stream().map(home -> home.house().placed()).toList();
    }

    /** Everything of the village that is in the way: every yard's wall, every house, and the lake's kerb. */
    public Obstacles inTheWay() {
        final List<Block> blocks = new ArrayList<>();
        for (final Home home : homesteads()) {
            blocks.addAll(home.yard().inTheWay());
            blocks.addAll(home.house().inTheWay());
        }
        blocks.addAll(lake().inTheWay());
        return new Obstacles(blocks);
    }
}
