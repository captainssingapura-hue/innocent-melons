package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Where the fat cage's limbs' rings are ({@link Limb.Ring}), on the right, as the body is held to be bound. A leg's:
 * square to the thigh and the shin, at shares of the way down each; at the ankle, turning through a line across its
 * front, clear of it, from level to upright, round the heel, the first, level, the shin's last; then upright along the
 * foot to the toes, its top where the shin's front was. An arm's: square to the upper arm and the forearm, to the
 * wrist, the hand going on from there ({@link HandCage}). The faces up to each ring of the part it is along, but the
 * thigh's first the pelvis's, the upper arm's first two the chest's: the buttock's fold and the groin with the pelvis,
 * the shoulder with the chest, each limb's own beginning where it is round and plain.
 */
public record LimbRings() implements StatelessFunctionalObject {

    public static final LimbRings INSTANCE = new LimbRings();

    /** How far out a limb's lines start, cm. */
    private static final double OUT = 15;

    /** The right leg's rings. */
    public List<Limb.Ring> leg(final Rig bind) {
        final List<Limb.Ring> rings = new ArrayList<>();
        final V3 hip = bind.frame(Part.RIGHT_THIGH).at();
        final V3 knee = bind.frame(Part.RIGHT_SHIN).at();
        final Rig.Frame foot = bind.frame(Part.RIGHT_FOOT);
        along(rings, hip, knee, new double[] {0.3, 0.45, 0.6, 0.75, 0.9, 1}, "thigh", Part.RIGHT_THIGH);
        along(rings, knee, foot.at(), new double[] {0.15, 0.3, 0.45, 0.6, 0.75}, "shin", Part.RIGHT_SHIN);
        // the buttock's fold and the groin the pelvis's, down to where the thigh is round and plain
        rings.set(0, rings.get(0).of(Part.PELVIS));
        // round the heel: each ring through a line across the ankle's front, clear of it, from level to upright
        final V3 hinge = foot.place(new V3(0, 4, 6));
        final double far = hinge.minus(foot.at()).length();
        for (final double deg : new double[] {0, 30, 60, 90}) {
            final double a = Math.toRadians(deg);
            final V3 away = foot.way(new V3(0, -Math.sin(a), -Math.cos(a))).unit();
            rings.add(new Limb.Ring(hinge.plus(away.times(far)), foot.way(V3.X).unit(), away.times(-1), far, OUT,
                    "heel " + Math.round(deg) + "°", deg == 0 ? Part.RIGHT_SHIN : Part.RIGHT_FOOT));
        }
        for (final double z : new double[] {8}) {
            rings.add(new Limb.Ring(foot.place(new V3(0, z < 14 ? -3.5 : -5, z)), foot.way(V3.X).unit(),
                    foot.way(V3.Y).unit(), Double.POSITIVE_INFINITY, OUT, "foot " + z, Part.RIGHT_FOOT));
        }
        return rings;
    }

    /** The right arm's rings. */
    public List<Limb.Ring> arm(final Rig bind) {
        final List<Limb.Ring> rings = new ArrayList<>();
        final V3 shoulder = bind.frame(Part.RIGHT_UPPER_ARM).at();
        final V3 elbow = bind.frame(Part.RIGHT_FOREARM).at();
        final V3 wrist = bind.frame(Part.RIGHT_HAND).at();
        along(rings, shoulder, elbow, new double[] {0.12, 0.3, 0.5, 0.7, 0.88, 1}, "upper arm",
                Part.RIGHT_UPPER_ARM);
        along(rings, elbow, wrist, new double[] {0.15, 0.35, 0.55, 0.75, 0.94}, "forearm",
                Part.RIGHT_FOREARM);
        // the shoulder the chest's, all of it, down to where the arm is round and plain
        rings.set(0, rings.get(0).of(Part.CHEST));
        rings.set(1, rings.get(1).of(Part.CHEST));
        return rings;
    }

    /** Rings square to the way from {@code from} to {@code to}, at {@code shares} of it, of part {@code part}. */
    private void along(final List<Limb.Ring> rings, final V3 from, final V3 to, final double[] shares,
                       final String said, final Part part) {
        final V3 way = to.minus(from).unit();
        for (final double s : shares) {
            rings.add(ring(from.plus(to.minus(from).times(s)), way, said + " " + Math.round(s * 100) + "%", part));
        }
    }

    /** A ring round {@code middle}, square to {@code way}, its front toward the body's, of part {@code part}. */
    private Limb.Ring ring(final V3 middle, final V3 way, final String said, final Part part) {
        final V3 front = V3.Z.minus(way.times(way.z())).unit();
        return new Limb.Ring(middle, front.cross(way).unit(), front, Double.POSITIVE_INFINITY, OUT, said, part);
    }
}
