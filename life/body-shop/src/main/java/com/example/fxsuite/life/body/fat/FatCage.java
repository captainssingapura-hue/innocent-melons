package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.cage.Building;
import com.example.fxsuite.life.body.cage.Cage;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * The fat's cage, every body's the same points and faces, each point found on its fat's outside ({@link FatTarget}):
 * the torso in rings of {@link #COLUMNS} points round, level, from just above the crotch up the neck and the head, at
 * heights found on the bones, each point on a line out from the torso's middle at its angle round; shut over the
 * crown. A leg hung from each half of the lowest ring and a point under the crotch, round the heel to the toes, shut
 * over them; an arm from a loop of eight points at each side, round where the arm goes out between the armpit and the
 * shoulder, down the hand to the fingertips, shut over them; each limb in rings of its own ({@link LimbRings}), as
 * many points round as its loop. The right found, the left its mirror. What each point is on, the trunk, an arm or a
 * leg, kept with it.
 */
public record FatCage() implements StatelessFunctionalObject {

    public static final FatCage INSTANCE = new FatCage();

    /** How many points round each torso ring; the column an arm goes out at, on the right. */
    public static final int COLUMNS = 20;
    static final int ARM = 5;
    /** The torso ring at the armpits: the arm's opening from it two rings up. */
    static final int ARMPITS = 9;
    /** The torso ring just under the head's joint, from which up the head is. */
    static final int HEAD = 14;

    /**
     * A cage, what each of its points is on, and how far it is to be held as the cage makes it, from none to wholly: a
     * hand's or a foot's wholly, their fingers and toes too thin and too near each other to be set on anything, and the
     * head's, its face to be made otherwise; from the wrist, the instep and the head's foot on, more and more, so that
     * what is held and what is not meet without a step; and the part each face is of ({@link #bands}, {@link
     * LimbRings}), so that each part is its own piece of it, the same on every body, ending on a ring.
     */
    public record Built(Cage cage, FatTarget.Region[] regions, double[] held, Part[] parts) {
    }

    /** The fat's cage on {@code target}. */
    public Built of(final FatTarget target) {
        final Rig bind = target.bind();
        final Building b = new Building();
        final Torso torso = new Torso(target);
        final double[] heights = heights(bind, torso.crotch().y(), target.highest(Part.HEAD));
        final int[][] grid = torso.rings(b, heights, bands(heights.length - 1));
        final int crotch = b.add(torso.crotch(), "under the crotch");
        b.fellows(crotch, crotch);
        final int limbs = b.size();
        final int limbFaces = b.faceCount();
        final int[] leg = new int[COLUMNS / 2 + 2];
        System.arraycopy(grid[0], 0, leg, 0, COLUMNS / 2 + 1);
        leg[COLUMNS / 2 + 1] = crotch;
        final Limb limb = new Limb(target);
        final java.util.Map<Integer, Double> ends = new java.util.HashMap<>();
        head(grid, ends);
        final int legs = b.size();
        // each of the leg's rings begun before it and in, every body's the same way, for the foot to go on from it
        final int[] instep = limb.hang(b, leg, target.counts(FatTarget.Region.LEG, 1), LimbRings.INSTANCE.leg(bind),
                false, -1.0);
        new FootCage(target).hang(b, instep, limb.ways(), ends);
        final int arms = b.size();
        final int a = ARMPITS;
        final int[] arm = {grid[a][ARM + 1], grid[a][ARM], grid[a][ARM - 1], grid[a + 1][ARM - 1],
            grid[a + 2][ARM - 1], grid[a + 2][ARM], grid[a + 2][ARM + 1], grid[a + 1][ARM + 1]};
        // each of the arm's rings begun behind and below, every body's the same way, for the hand to go on from it
        final int[] wrist = limb.hang(b, arm, target.counts(FatTarget.Region.ARM, 1), LimbRings.INSTANCE.arm(bind),
                false, -0.75 * Math.PI);
        new HandCage(target).hang(b, wrist, limb.ways(), ends);
        final int right = b.size();
        b.mirror(limbs, limbFaces);
        final FatTarget.Region[] regions = new FatTarget.Region[b.size()];
        final double[] held = new double[b.size()];
        for (int i = 0; i < b.size(); i++) {
            final int r = i < right ? i : b.fellow(i);
            regions[i] = r >= arms ? FatTarget.Region.ARM : r >= legs ? FatTarget.Region.LEG
                    : FatTarget.Region.TRUNK;
            held[i] = ends.getOrDefault(r, 0.0);
        }
        return new Built(b.cage(), regions, held, b.parts());
    }

    /**
     * The head held as its cage makes it, put in {@code held}: not pressed onto the skull, its face to be made
     * otherwise; the ring under its joint half held, so that the neck runs on into it without a step.
     */
    private void head(final int[][] grid, final java.util.Map<Integer, Double> held) {
        for (int r = HEAD; r < grid.length; r++) {
            for (final int p : grid[r]) {
                if (p >= 0) {
                    held.put(p, r == HEAD ? 0.5 : 1.0);
                }
            }
        }
    }

    /**
     * The parts of the {@code n} bands between the torso's rings, bottom to top: each part from the ring at its joint,
     * the pelvis's up to the abdomen's, the abdomen's up to the chest's, the chest's up to the neck's; the head's
     * from the ring just under its joint, up over its crown.
     */
    Part[] bands(final int n) {
        final Part[] bands = new Part[n];
        for (int r = 0; r < n; r++) {
            bands[r] = r < 4 ? Part.PELVIS : r < 6 ? Part.ABDOMEN : r < 12 ? Part.CHEST
                    : r < HEAD ? Part.NECK : Part.HEAD;
        }
        return bands;
    }

    /**
     * The torso rings' heights, bottom to top, from the bones as {@code bind} holds them: just above the crotch; the
     * hips; the crests; the navel; up the abs to the chest; the armpits, under the shoulders and over them, where an
     * arm goes out; the neck's foot, the neck, under the jaw; up the head to its crown, {@code crown}, shut over it.
     */
    double[] heights(final Rig bind, final double crotch, final double crown) {
        final double hip = bind.frame(Part.RIGHT_THIGH).at().y();
        final double abdomen = bind.frame(Part.ABDOMEN).at().y();
        final double chest = bind.frame(Part.CHEST).at().y();
        final double shoulder = bind.frame(Part.RIGHT_UPPER_ARM).at().y();
        final double neck = bind.frame(Part.NECK).at().y();
        final double head = bind.frame(Part.HEAD).at().y();
        final double armpit = shoulder - 7;
        return new double[] {crotch + 3, hip - 2, hip + 3, hip + 8, abdomen, (abdomen + chest) / 2, chest,
            chest + (armpit - chest) / 3, chest + 2 * (armpit - chest) / 3, armpit, shoulder - 2, shoulder + 3.5,
            neck, (neck + head) / 2, head - 1, head + (crown - head) * 0.15, head + (crown - head) * 0.33,
            head + (crown - head) * 0.5, head + (crown - head) * 0.67, head + (crown - head) * 0.82,
            head + (crown - head) * 0.94};
    }
}
