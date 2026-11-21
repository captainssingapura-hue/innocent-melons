package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.cage.Building;
import com.example.fxsuite.life.body.cage.Cage;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * The fat's cage, every body's the same points and faces, each point found on its fat's outside ({@link FatTarget}):
 * the torso in rings of {@link #COLUMNS} points round, level, from just above the crotch to under the jaw, at heights
 * found on the bones, each point on a line out from the torso's middle at its angle round; shut over the top. A leg
 * hung from each half of the lowest ring and a point under the crotch; an arm from a loop of eight points at each side,
 * round where the arm goes out between the armpit and the shoulder; each limb in rings square to its bones, as many
 * points round as its loop. The right found, the left its mirror. What each point is on, the trunk, an arm or a leg,
 * kept with it.
 */
public record FatCage() implements StatelessFunctionalObject {

    public static final FatCage INSTANCE = new FatCage();

    /** How many points round each torso ring; the column an arm goes out at, on the right. */
    public static final int COLUMNS = 20;
    static final int ARM = 5;
    /** The torso ring at the armpits: the arm's opening from it two rings up. */
    static final int ARMPITS = 9;

    /** A cage and what each of its points is on. */
    public record Built(Cage cage, FatTarget.Region[] regions) {
    }

    /** The fat's cage on {@code target}. */
    public Built of(final FatTarget target) {
        final Rig bind = target.bind();
        final Building b = new Building();
        final Torso torso = new Torso(target);
        final int[][] grid = torso.rings(b, heights(bind, torso.crotch().y()));
        final int crotch = b.add(torso.crotch(), "under the crotch");
        b.fellows(crotch, crotch);
        final int limbs = b.size();
        final int limbFaces = b.faceCount();
        final int[] leg = new int[COLUMNS / 2 + 2];
        System.arraycopy(grid[0], 0, leg, 0, COLUMNS / 2 + 1);
        leg[COLUMNS / 2 + 1] = crotch;
        final Limb limb = new Limb(target);
        final int legs = b.size();
        limb.hang(b, leg, FatTarget.Region.LEG, new Part[] {Part.RIGHT_THIGH, Part.RIGHT_SHIN, Part.RIGHT_FOOT},
                new double[][] {{0.3, 0.45, 0.6, 0.75, 0.9, 1}, {0.15, 0.3, 0.45, 0.6, 0.75, 0.9}});
        final int arms = b.size();
        final int a = ARMPITS;
        final int[] arm = {grid[a][ARM + 1], grid[a][ARM], grid[a][ARM - 1], grid[a + 1][ARM - 1],
            grid[a + 2][ARM - 1], grid[a + 2][ARM], grid[a + 2][ARM + 1], grid[a + 1][ARM + 1]};
        limb.hang(b, arm, FatTarget.Region.ARM, new Part[] {Part.RIGHT_UPPER_ARM, Part.RIGHT_FOREARM,
            Part.RIGHT_HAND}, new double[][] {{0.12, 0.3, 0.5, 0.7, 0.88, 1}, {0.15, 0.35, 0.55, 0.75, 0.92}});
        final int right = b.size();
        b.mirror(limbs, limbFaces);
        final FatTarget.Region[] regions = new FatTarget.Region[b.size()];
        for (int i = 0; i < b.size(); i++) {
            final int r = i < right ? i : b.fellow(i);
            regions[i] = r >= arms ? FatTarget.Region.ARM : r >= legs ? FatTarget.Region.LEG
                    : FatTarget.Region.TRUNK;
        }
        return new Built(b.cage(), regions);
    }

    /**
     * The torso rings' heights, bottom to top, from the bones as {@code bind} holds them: just above the crotch; the
     * hips; the crests; the navel; up the abs to the chest; the armpits, under the shoulders and over them, where an
     * arm goes out; the neck's foot, the neck, under the jaw.
     */
    double[] heights(final Rig bind, final double crotch) {
        final double hip = bind.frame(Part.RIGHT_THIGH).at().y();
        final double abdomen = bind.frame(Part.ABDOMEN).at().y();
        final double chest = bind.frame(Part.CHEST).at().y();
        final double shoulder = bind.frame(Part.RIGHT_UPPER_ARM).at().y();
        final double neck = bind.frame(Part.NECK).at().y();
        final double head = bind.frame(Part.HEAD).at().y();
        final double armpit = shoulder - 7;
        return new double[] {crotch + 3, hip - 2, hip + 3, hip + 8, abdomen, (abdomen + chest) / 2, chest,
            chest + (armpit - chest) / 3, chest + 2 * (armpit - chest) / 3, armpit, shoulder - 2, shoulder + 3.5,
            neck, (neck + head) / 2, head - 1};
    }
}
