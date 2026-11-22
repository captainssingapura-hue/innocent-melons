package com.example.fxsuite.life.body.muscle;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.cage.Fitting;
import com.example.fxsuite.life.body.cage.Subdivision;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A muscle's surface ({@link MuscleShape}), and what each of its points carries from the sections it is smoothed from,
 * smoothed alike: how far it is tendon; and how much it follows each of the parts its sections' bones move with, so
 * that, the body moving, a muscle across a joint is drawn out or gathered in between the parts either side of it.
 */
public record MuscleSurface() implements StatelessFunctionalObject {

    public static final MuscleSurface INSTANCE = new MuscleSurface();

    /**
     * A muscle's surface; how far each of its points is tendon, 0 belly, 1 tendon; the parts it follows, and how much
     * each point follows each of them, the shares adding to one.
     */
    public record Surface(Mesher.Mesh mesh, float[] tendon, List<Part> parts, float[][] shares) {
    }

    /** {@code m}'s surface, as big as {@code bulk} has it, on the body {@code rig} holds, smoothed {@code steps}. */
    public Surface surface(final Muscle m, final Muscularity bulk, final Rig rig, final boolean left,
                           final int steps) {
        return surface(m, bulk, rig, left, steps, p -> java.util.Optional.empty());
    }

    /** The same, a sheet laid on {@code bed} ({@link Drape}). */
    public Surface surface(final Muscle m, final Muscularity bulk, final Rig rig, final boolean left, final int steps,
            final Bed bed) {
        final Subdivision s = Subdivision.INSTANCE;
        final Subdivision.Level shape = s.of(Fitting.INSTANCE.fitted(MuscleShape.INSTANCE.cage(m, bulk, rig, left, bed),
                1), 1);
        final List<Part> parts = new ArrayList<>();
        final int[] partOf = new int[m.sections().size()];
        for (int i = 0; i < partOf.length; i++) {
            final Part p = m.sections().get(i).at().part(left);
            if (!parts.contains(p)) {
                parts.add(p);
            }
            partOf[i] = parts.indexOf(p);
        }
        // the channels carried: how far tendon, then each part's share, three to a pass
        final int channels = 1 + parts.size();
        final float[][] carried = new float[channels][];
        for (int from = 0; from < channels; from += 3) {
            final double[] values = new double[shape.points().length];
            for (int i = 0; i < shape.size(); i++) {
                final int section = i / MuscleShape.ROUND;
                for (int c = 0; c < 3 && from + c < channels; c++) {
                    final int channel = from + c;
                    values[3 * i + c] = channel == 0 ? (MuscleShape.INSTANCE.tendon(m, section) ? 1 : 0)
                            : partOf[section] == channel - 1 ? 1 : 0;
                }
            }
            final Subdivision.Level smooth = s.times(new Subdivision.Level(values, shape.faces(), shape.sharp()), steps);
            for (int c = 0; c < 3 && from + c < channels; c++) {
                carried[from + c] = new float[smooth.size()];
                for (int i = 0; i < smooth.size(); i++) {
                    carried[from + c][i] = (float) smooth.points()[3 * i + c];
                }
            }
        }
        final float[][] shares = new float[parts.size()][];
        System.arraycopy(carried, 1, shares, 0, shares.length);
        return new Surface(s.mesh(s.times(shape, steps)), carried[0], parts, shares);
    }
}
