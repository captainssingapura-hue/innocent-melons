package com.example.fxsuite.life.body.muscle;

import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A muscle's rings ({@link MuscleShape}), as its form has them ({@link MuscleForm}): a strap's, one at each of its
 * sections, a sheet's with more between, none further from the next than {@link #APART}, each laid on what the sheet
 * lies on ({@link Drape}), so that it lies on it all along, not spanning straight from one section to the next over
 * what dips between; a spread's, from across its origin's line to across its insertion's, as many.
 */
public record MuscleRings() implements StatelessFunctionalObject {

    public static final MuscleRings INSTANCE = new MuscleRings();

    /** How far apart a sheet's rings are at most, cm. */
    private static final double APART = 2;

    /**
     * A ring: where it lies, where its middle is, the way it faces; from end to end across it, if its form says
     * (a spread's, from one line to the other), else across it square to the way it runs; how wide and deep it is.
     *
     * @param on   where it lies
     * @param at   its middle
     * @param way  the way it faces
     * @param span from end to end across it, if said
     * @param wide how wide, cm
     * @param deep how deep, cm
     */
    public record Ring(V3 on, V3 at, V3 way, Optional<V3> span, double wide, double deep) {
    }

    /**
     * The posed ends of {@code m}'s lines, if a spread, on the body {@code rig} holds, on the right or the left: its
     * origin's first and last, its insertion's first and last, the way each line faces; a strap's, none.
     */
    public V3[] ends(final Muscle m, final Rig rig, final boolean left) {
        return switch (m.form()) {
            case MuscleForm.Strap s -> new V3[0];
            case MuscleForm.Spread s -> new V3[] {s.origin().getFirst().in(rig, left),
                    s.origin().getLast().in(rig, left), s.insertion().getFirst().in(rig, left),
                    s.insertion().getLast().in(rig, left), s.origin().getFirst().way(s.facing(), rig, left).unit(),
                    s.insertion().getFirst().way(s.facing(), rig, left).unit()};
        };
    }

    /**
     * {@code m}'s rings, its sections lying at {@code on}, facing {@code way}, its lines' posed {@code ends}, its belly
     * {@code grown} times: a strap's sections wider and deeper as one, its tendons as drawn; a spread deeper, its
     * lines where they are.
     */
    public List<Ring> rings(final Muscle m, final V3[] on, final V3[] way, final V3[] ends, final double grown) {
        return switch (m.form()) {
            case MuscleForm.Strap s -> strap(m, on, way, grown);
            case MuscleForm.Spread s -> spread(ends, s.deep(), MuscleShape.INSTANCE.kept(m, 0) ? 1 : grown,
                    MuscleShape.INSTANCE.kept(m, 1) ? 1 : grown, grown);
        };
    }

    /** Which of {@code m}'s sections each of its rings, on the body {@code rig} holds, is nearer. */
    public int[] sectionOf(final Muscle m, final Rig rig, final boolean left) {
        final List<Integer> of = new ArrayList<>();
        if (m.form() instanceof MuscleForm.Spread) {
            final V3[] e = ends(m, rig, left);
            final int n = count(e[0].plus(e[1]).times(0.5), e[2].plus(e[3]).times(0.5));
            for (int i = 0; i < n; i++) {
                of.add(2 * i < n ? 0 : 1);
            }
        } else {
            final List<Section> sections = m.sections();
            for (int i = 0; i < sections.size(); i++) {
                of.add(i);
                if (m.sheet() && i + 1 < sections.size()) {
                    final int more = count(sections.get(i).at().in(rig, left), sections.get(i + 1).at().in(rig, left))
                            - 2;
                    for (int k = 1; k <= more; k++) {
                        of.add(2 * k <= more + 1 ? i : i + 1);
                    }
                }
            }
        }
        return of.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Whether each of {@code m}'s rings, on the body {@code rig} holds, is tendon: a strap's, as the section it is
     * nearer is; a spread's, only its first and last, at its lines, as their sections are, its fibres flesh between.
     */
    public boolean[] tendonOf(final Muscle m, final Rig rig, final boolean left) {
        final int[] of = sectionOf(m, rig, left);
        final boolean[] tendon = new boolean[of.length];
        final boolean spread = m.form() instanceof MuscleForm.Spread;
        for (int i = 0; i < of.length; i++) {
            final boolean end = i == 0 || i == of.length - 1;
            tendon[i] = (!spread || end) && MuscleShape.INSTANCE.tendon(m, of[i]);
        }
        return tendon;
    }

    /** A strap's rings: one at each section; a sheet's with more between. */
    private List<Ring> strap(final Muscle m, final V3[] on, final V3[] way, final double grown) {
        final List<Section> sections = m.sections();
        final List<Ring> drawn = new ArrayList<>();
        for (int i = 0; i < sections.size(); i++) {
            final Section s = sections.get(i);
            final double k = MuscleShape.INSTANCE.kept(m, i) ? 1 : grown;
            drawn.add(new Ring(on[i], on[i].plus(way[i].times(s.deep() * k / 2)), way[i], Optional.empty(),
                    s.wide() * k, s.deep() * k));
        }
        if (!m.sheet()) {
            return drawn;
        }
        final List<Ring> all = new ArrayList<>();
        for (int i = 0; i < drawn.size(); i++) {
            all.add(drawn.get(i));
            if (i + 1 < drawn.size()) {
                final int more = count(drawn.get(i).on(), drawn.get(i + 1).on()) - 2;
                for (int k = 1; k <= more; k++) {
                    all.add(share(drawn.get(i), drawn.get(i + 1), (double) k / (more + 1)));
                }
            }
        }
        return all;
    }

    /**
     * A spread's rings, from across its origin to across its insertion, each from the one line to the other; as deep
     * as drawn, {@code origin} times at its origin, {@code insertion} times at its insertion, {@code belly} times
     * midway, rounding between.
     */
    private List<Ring> spread(final V3[] e, final double drawn, final double origin, final double insertion,
                              final double belly) {
        final int n = count(e[0].plus(e[1]).times(0.5), e[2].plus(e[3]).times(0.5));
        final List<Ring> rings = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            final double t = (double) i / (n - 1);
            final double end = origin + (insertion - origin) * t;
            final double deep = drawn * (end + (belly - end) * Math.sin(Math.PI * t));
            final V3 first = mix(e[0], e[2], t);
            final V3 last = mix(e[1], e[3], t);
            final V3 on = first.plus(last).times(0.5);
            final V3 way = mix(e[4], e[5], t).unit();
            final V3 span = last.minus(first);
            rings.add(new Ring(on, on.plus(way.times(deep / 2)), way, Optional.of(span), span.length(), deep));
        }
        return rings;
    }

    /** How many rings from {@code a} to {@code b}, both ends among them, none further apart than {@link #APART}. */
    private int count(final V3 a, final V3 b) {
        return Math.max(2, (int) Math.ceil(b.minus(a).length() / APART) + 1);
    }

    private Ring share(final Ring a, final Ring b, final double t) {
        return new Ring(mix(a.on(), b.on(), t), mix(a.at(), b.at(), t), mix(a.way(), b.way(), t).unit(),
                Optional.empty(), a.wide() + (b.wide() - a.wide()) * t, a.deep() + (b.deep() - a.deep()) * t);
    }

    private V3 mix(final V3 a, final V3 b, final double t) {
        return a.times(1 - t).plus(b.times(t));
    }
}
