package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.muscle.Anchor;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleForm;
import com.example.fxsuite.life.body.muscle.Section;

import java.util.ArrayList;
import java.util.List;

/**
 * Which way a muscle's fibres run, anywhere in it, as the body is bound: a strap's along its own line, from section to
 * section, the piece of it nearest; a spread's from its origin to its insertion, from the nearest point of the one line
 * to the nearest of the other, as a fan's ribs run.
 */
public final class Fibres implements Along {

    private final List<V3> path;
    private final List<V3> origin;
    private final List<V3> insertion;

    /** The fibres of {@code m}, on the left if {@code left}, on the body {@code rig} holds. */
    public Fibres(final Muscle m, final Rig rig, final boolean left) {
        path = new ArrayList<>();
        origin = new ArrayList<>();
        insertion = new ArrayList<>();
        for (final Section s : m.sections()) {
            path.add(s.at().in(rig, left));
        }
        if (m.form() instanceof MuscleForm.Spread spread) {
            for (final Anchor a : spread.origin()) {
                origin.add(a.in(rig, left));
            }
            for (final Anchor a : spread.insertion()) {
                insertion.add(a.in(rig, left));
            }
        }
    }

    @Override
    public V3 at(final V3 p) {
        if (!origin.isEmpty() && !insertion.isEmpty()) {
            final V3 way = nearest(insertion, p).minus(nearest(origin, p));
            if (way.length() > 1e-9) {
                return way.times(1 / way.length());
            }
        }
        V3 best = new V3(0, 1, 0);
        double near = Double.MAX_VALUE;
        for (int i = 0; i + 1 < path.size(); i++) {
            final V3 a = path.get(i);
            final V3 d = path.get(i + 1).minus(a);
            final double length = d.length();
            if (length < 1e-9) {
                continue;
            }
            final double t = Math.clamp(p.minus(a).dot(d) / (length * length), 0, 1);
            final double far = a.plus(d.times(t)).minus(p).length();
            if (far < near) {
                near = far;
                best = d.times(1 / length);
            }
        }
        return best;
    }

    /** The point of the line through {@code line}'s points nearest {@code p}. */
    private V3 nearest(final List<V3> line, final V3 p) {
        if (line.size() == 1) {
            return line.getFirst();
        }
        V3 best = line.getFirst();
        double near = Double.MAX_VALUE;
        for (int i = 0; i + 1 < line.size(); i++) {
            final V3 a = line.get(i);
            final V3 d = line.get(i + 1).minus(a);
            final double l2 = d.dot(d);
            final double t = l2 < 1e-18 ? 0 : Math.clamp(p.minus(a).dot(d) / l2, 0, 1);
            final V3 q = a.plus(d.times(t));
            final double far = q.minus(p).length();
            if (far < near) {
                near = far;
                best = q;
            }
        }
        return best;
    }
}
