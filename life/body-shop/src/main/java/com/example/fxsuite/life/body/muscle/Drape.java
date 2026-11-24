package com.example.fxsuite.life.body.muscle;

import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A section of a sheet of muscle laid on its bed ({@link Bed}): its oval's points, each where it would be across the
 * sheet, taken down to the nearest of the bed in the section's own plane, not up or down the way the muscle runs, and
 * out from there as far as it is through the sheet; so that the sheet bends as what it lies on does, not running
 * straight past it. A point with nothing of the bed near, where it is drawn.
 */
public record Drape() implements StatelessFunctionalObject {

    public static final Drape INSTANCE = new Drape();

    /** How far off its bed a sheet lies, cm. */
    private static final double GAP = 0.05;
    /**
     * How far a point inside its bed is moved out at a time looking for its outside, cm, and how many times at most:
     * out of a muscle, a centimetre all told, so a point deeper in a thick one is laid on the nearest of it instead and
     * does not stand out of its sheet as a spike; out of a bone, two and a half, as far as a spine's tip, a scapula's
     * angle or its crest stands out under a sheet, which passes over it, never through it.
     */
    private static final double STEP = 0.25;
    private static final int CLIMB = 4;
    private static final int OVER_BONE = 10;

    /**
     * The ring of a section lying at {@code on}, its middle at {@code middle}, {@code wide} across, {@code across},
     * and {@code deep} out, {@code face}, the muscle running {@code along}, laid on {@code bed}:
     * {@link MuscleShape#ROUND} points round it; those with nothing of the bed near on its oval as drawn.
     */
    public List<V3> ring(final V3 on, final V3 middle, final V3 across, final V3 face, final V3 along,
            final double wide, final double deep, final Bed bed) {
        final List<V3> ring = new ArrayList<>();
        for (int q = 0; q < MuscleShape.ROUND; q++) {
            final double a = 2 * Math.PI * q / MuscleShape.ROUND;
            final double u = Math.cos(a) * wide / 2;
            final double out = deep / 2 + Math.sin(a) * deep / 2;
            final V3 line = on.plus(across.times(u));
            final Optional<Bed.Laid> laid = outer(bed, line, face);
            final V3 drawn = middle.plus(across.times(u)).plus(face.times(Math.sin(a) * deep / 2));
            ring.add(laid.map(l -> laid(l, line, along, face, out)).orElse(drawn));
        }
        return ring;
    }

    /**
     * The ring of a spread's section lying along {@code path}, its fibres' points across it, {@code deep} out the way
     * {@code face} says, the muscle running {@code along}, laid on {@code bed}: as {@link #ring}, each point across it
     * on the path, not on a straight line, and out square to the path where it is.
     */
    public List<V3> path(final List<V3> path, final V3 face, final V3 along, final double deep, final Bed bed) {
        final double[] at = new double[path.size()];
        for (int i = 1; i < path.size(); i++) {
            at[i] = at[i - 1] + path.get(i).minus(path.get(i - 1)).length();
        }
        final List<V3> ring = new ArrayList<>();
        for (int q = 0; q < MuscleShape.ROUND; q++) {
            final double a = 2 * Math.PI * q / MuscleShape.ROUND;
            final double s = (1 + Math.cos(a)) / 2 * at[at.length - 1];
            final double out = deep / 2 + Math.sin(a) * deep / 2;
            int i = 1;
            while (i < path.size() - 1 && at[i] < s) {
                i++;
            }
            final V3 from = path.get(i - 1);
            final V3 to = path.get(i);
            final double piece = at[i] - at[i - 1];
            final V3 line = piece <= 0 ? to : from.plus(to.minus(from).times((s - at[i - 1]) / piece));
            final V3 tangent = piece <= 0 ? V3.X : to.minus(from).unit();
            final V3 square = tangent.cross(along);
            final V3 out1 = square.length() < 1e-6 ? face
                    : square.unit().times(square.dot(face) < 0 ? -1 : 1);
            final Optional<Bed.Laid> laid = outer(bed, line, out1);
            ring.add(laid.map(l -> laid(l, line, along, out1, out)).orElse(line.plus(out1.times(out))));
        }
        return ring;
    }

    /**
     * What of {@code bed} a sheet at {@code line}, facing {@code face}, lies on: the nearest of it, if {@code line} is
     * outside it; if inside, the outside it comes to going out the way the sheet faces, if within a centimetre of a
     * muscle or two and a half of a bone ({@link #CLIMB}), so a sheet laid where a bone or another muscle stands out
     * lies over it, not under it on its far side; if further, the nearest of it. Nothing, if none is near.
     */
    private Optional<Bed.Laid> outer(final Bed bed, final V3 line, final V3 face) {
        final Optional<Bed.Laid> laid = bed.under(line);
        if (laid.isEmpty() || outside(laid.get(), line)) {
            return laid;
        }
        final int climb = laid.get().bone() ? OVER_BONE : CLIMB;
        for (int k = 1; k <= climb; k++) {
            final V3 p = line.plus(face.times(k * STEP));
            final Optional<Bed.Laid> there = bed.under(p);
            if (there.isPresent() && outside(there.get(), p)) {
                return there;
            }
        }
        return laid;
    }

    /** Whether {@code p} is outside the bed where {@code l} is its nearest. */
    private boolean outside(final Bed.Laid l, final V3 p) {
        return p.minus(l.at()).dot(l.out()) >= 0;
    }

    /**
     * {@code out} off {@code l}, both taken into the plane through {@code line} square to {@code along}; the way out of
     * it there, as near square to the plane as it is, {@code face} instead.
     */
    private V3 laid(final Bed.Laid l, final V3 line, final V3 along, final V3 face, final double out) {
        final V3 way = flat(l.out(), V3.ZERO, along);
        return flat(l.at(), line, along).plus((way.length() < 0.3 ? face : way.unit()).times(GAP + out));
    }

    /** {@code p} taken back into the plane through {@code o} square to {@code along}. */
    private V3 flat(final V3 p, final V3 o, final V3 along) {
        return p.minus(along.times(p.minus(o).dot(along)));
    }
}
