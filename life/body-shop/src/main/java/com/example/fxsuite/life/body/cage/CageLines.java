package com.example.fxsuite.life.body.cage;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.fxsuite.life.body.cage.TorsoRing.ABOVE_HIPS;
import static com.example.fxsuite.life.body.cage.TorsoRing.BELOW_NAVEL;
import static com.example.fxsuite.life.body.cage.TorsoRing.CRESTS;
import static com.example.fxsuite.life.body.cage.TorsoRing.HIGH_ABS;
import static com.example.fxsuite.life.body.cage.TorsoRing.HIPS_RING;
import static com.example.fxsuite.life.body.cage.TorsoRing.LOW_HIPS;
import static com.example.fxsuite.life.body.cage.TorsoRing.MID_ABS;
import static com.example.fxsuite.life.body.cage.TorsoRing.NAVEL_RING;
import static com.example.fxsuite.life.body.cage.TorsoRing.OVER_SHOULDERS;
import static com.example.fxsuite.life.body.cage.TorsoRing.XIPHOID_RING;

/**
 * How a cage's shaping is written down ({@link Shaping}), and what any body's has: the abs' lines, the rectus's edges
 * running in to the groin as the V; the groove down the spine; the folds under the buttocks.
 */
public record CageLines() implements StatelessFunctionalObject {

    public static final CageLines INSTANCE = new CageLines();

    /** The point on ring {@code r} in column {@code column}. */
    public Shaping.Spot at(final TorsoRing r, final int column) {
        return new Shaping.Spot(r, column);
    }

    /** The point on ring {@code r} in column {@code column} moved: further round, up, out, ahead. */
    public Shaping.Push push(final TorsoRing r, final int column, final double turn, final double up,
                             final double out, final double ahead) {
        return new Shaping.Push(r.said(), at(r, column), turn, up, out, ahead);
    }

    /** Down column {@code column} from ring {@code from} to ring {@code to}. */
    public List<Shaping.Spot> down(final int column, final TorsoRing from, final TorsoRing to) {
        final List<Shaping.Spot> s = new ArrayList<>();
        for (int r = from.ordinal(); r <= to.ordinal(); r++) {
            s.add(at(TorsoRing.values()[r], column));
        }
        return s;
    }

    /** Round ring {@code r} from column {@code from} to column {@code to}. */
    public List<Shaping.Spot> round(final TorsoRing r, final int from, final int to) {
        final List<Shaping.Spot> s = new ArrayList<>();
        for (int j = from; j <= to; j++) {
            s.add(at(r, j));
        }
        return s;
    }

    /**
     * The abs: the linea alba down the middle; the rectus's edges, the linea semilunaris, and on down to the groin, the
     * V; the three tendinous intersections across, if a six-pack; pressed in {@code depth}, creased {@code sharp}.
     */
    public List<Shaping.Line> abs(final boolean sixPack, final double depth, final double sharp) {
        final List<Shaping.Line> l = new ArrayList<>();
        l.add(new Shaping.Line("the linea alba", down(0, BELOW_NAVEL, XIPHOID_RING), sharp, depth, true, false));
        l.add(new Shaping.Line("the linea semilunaris", down(2, BELOW_NAVEL, XIPHOID_RING), sharp * 0.8,
                depth * 0.7, true, true));
        l.add(new Shaping.Line("the V", down(3, LOW_HIPS, CRESTS), sharp * 0.8, depth * 0.8, true, true));
        if (sixPack) {
            for (final TorsoRing r : List.of(NAVEL_RING, MID_ABS, HIGH_ABS)) {
                l.add(new Shaping.Line("the tendinous intersection at " + r.said(), round(r, 0, 2), sharp * 0.8,
                        depth * 0.7, true, true));
            }
        }
        return l;
    }

    /** The V's points turned in toward the groin: the rectus's edge and the columns inside it, low on the belly. */
    public List<Shaping.Push> groin() {
        return List.of(push(ABOVE_HIPS, 3, -8, 0, 0, 0), push(HIPS_RING, 3, -16, 0, 0, 0),
                push(LOW_HIPS, 3, -24, 0, 0, 0), push(HIPS_RING, 2, -9, 0, 0, 0), push(LOW_HIPS, 2, -15, 0, 0, 0),
                push(LOW_HIPS, 1, -7, 0, 0, 0));
    }

    /** The back's lines: the spine's groove, the folds under the buttocks. */
    public List<Shaping.Line> back() {
        return List.of(new Shaping.Line("the spine's groove", down(10, ABOVE_HIPS, OVER_SHOULDERS), 0.6, 0.45, false,
                        false),
                new Shaping.Line("the fold under the buttock", round(LOW_HIPS, 6, 10), 1.2, 0, false, true));
    }

    /** The buttocks, swelling {@code out} at their fullest. */
    public List<Shaping.Push> buttocks(final double out) {
        return List.of(push(LOW_HIPS, 7, 0, 0, out * 0.3, 0), push(LOW_HIPS, 8, 0, 0, out * 0.4, 0),
                push(LOW_HIPS, 9, 0, 0, out * 0.3, 0), push(HIPS_RING, 7, 0, 0, out * 0.8, 0),
                push(HIPS_RING, 8, 0, 0, out, 0), push(HIPS_RING, 9, 0, 0, out * 0.8, 0),
                push(ABOVE_HIPS, 8, 0, 0, out * 0.4, 0));
    }
}
