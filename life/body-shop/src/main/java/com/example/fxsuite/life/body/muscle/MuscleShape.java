package com.example.fxsuite.life.body.muscle;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.cage.Cage;
import com.example.fxsuite.life.body.cage.Crease;
import com.example.fxsuite.life.body.cage.Fitting;
import com.example.fxsuite.life.body.cage.Subdivision;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A muscle's shape on a body ({@link Muscle}): a cage of rings, as its form has them ({@link MuscleRings}), each an
 * oval of {@link #ROUND} points, so wide across and so deep the way it faces, its middle half its depth out from where
 * it lies, square to the way the muscle runs there; four-sided faces from ring to ring, its ends shut; found so that
 * its surface, smoothed, passes through its rings ({@link Fitting}). Its belly as big as the body has it
 * ({@link Muscularity}), every section but its tendons' grown or shrunk alike, wider and deeper as one, and so lying
 * further out from what is under it; its tendons, at either end, as they are drawn. A band or a tendon all as drawn.
 * Where its bones are as {@code rig} holds them, on the right or the left.
 */
public record MuscleShape() implements StatelessFunctionalObject {

    public static final MuscleShape INSTANCE = new MuscleShape();

    /** How many points round each ring. */
    public static final int ROUND = 8;
    /** How sharp a muscle's ends are kept, as a crease is. */
    private static final double END = 2;

    /** {@code m}'s surface as drawn, on the body {@code rig} holds, on the right or the left, smoothed so often. */
    public Mesher.Mesh mesh(final Muscle m, final Rig rig, final boolean left, final int steps) {
        return mesh(m, AsDrawn.INSTANCE, rig, left, steps);
    }

    /** {@code m}'s surface, as big as {@code bulk} has it, on the body {@code rig} holds, smoothed {@code steps}. */
    public Mesher.Mesh mesh(final Muscle m, final Muscularity bulk, final Rig rig, final boolean left,
                            final int steps) {
        final Subdivision s = Subdivision.INSTANCE;
        return s.mesh(s.times(s.of(Fitting.INSTANCE.fitted(cage(m, bulk, rig, left), 1), 1), steps));
    }

    /** {@code m}'s cage as drawn on the body {@code rig} holds, on the right or the left. */
    public Cage cage(final Muscle m, final Rig rig, final boolean left) {
        return cage(m, AsDrawn.INSTANCE, rig, left);
    }

    /** {@code m}'s cage, as big as {@code bulk} has it, on the body {@code rig} holds, its points where its skin is. */
    public Cage cage(final Muscle m, final Muscularity bulk, final Rig rig, final boolean left) {
        return cage(m, bulk, rig, left, p -> Optional.empty());
    }

    /**
     * {@code m}'s cage, as big as {@code bulk} has it, on the body {@code rig} holds; a sheet laid on {@code bed}
     * ({@link Drape}).
     */
    public Cage cage(final Muscle m, final Muscularity bulk, final Rig rig, final boolean left, final Bed bed) {
        final List<Section> sections = m.sections();
        final int n = sections.size();
        final V3[] on = new V3[n];
        final V3[] way = new V3[n];
        for (int i = 0; i < n; i++) {
            final Section s = sections.get(i);
            on[i] = s.at().in(rig, left);
            way[i] = s.at().way(s.facing(), rig, left).unit();
        }
        final V3[] ends = MuscleRings.INSTANCE.ends(m, rig, left);
        return cageAt(m, on, way, ends, grown(m, bulk, on, way, ends), bed);
    }

    /**
     * {@code m}'s cage where its sections lie at {@code on}, facing {@code way}, its lines' posed {@code ends}, if a
     * spread, its belly {@code grown} times ({@link MuscleRings}); a sheet laid on {@code bed}, ring by ring.
     */
    private Cage cageAt(final Muscle m, final V3[] on, final V3[] way, final V3[] ends, final double grown,
                        final Bed bed) {
        final List<MuscleRings.Ring> rings = MuscleRings.INSTANCE.rings(m, on, way, ends, grown);
        final int n = rings.size();
        final List<V3> points = new ArrayList<>();
        final List<String> names = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            final MuscleRings.Ring r = rings.get(i);
            final V3 along = m.ring() ? rings.get((i + 1) % n).at().minus(rings.get((i + n - 1) % n).at()).unit()
                    : rings.get(Math.min(i + 1, n - 1)).at().minus(rings.get(Math.max(i - 1, 0)).at()).unit();
            final V3[] frame = frame(r, along);
            final double wide = r.span().map(V3::length).orElse(r.wide());
            if (m.sheet()) {
                points.addAll(Drape.INSTANCE.ring(r.on(), r.at(), frame[0], frame[1], frame[2], wide, r.deep(), bed));
            }
            for (int q = 0; q < ROUND; q++) {
                final double a = 2 * Math.PI * q / ROUND;
                if (!m.sheet()) {
                    points.add(r.at().plus(frame[0].times(Math.cos(a) * wide / 2))
                            .plus(frame[1].times(Math.sin(a) * r.deep() / 2)));
                }
                names.add(m.name() + ", ring " + (i + 1) + ", " + q);
            }
        }
        return new Cage(points, names, faces(n, m.ring(), outward(points, n, m.ring())), ends(m, n));
    }

    /**
     * Across {@code r}, the way it faces, and the way through it, its plane's: a strap's square to the way the muscle
     * runs, {@code along}, across it square to the way it faces; a spread's through its span, from one line to the
     * other, end to end, whichever way its fibres run, so that its ends are its lines'.
     */
    private V3[] frame(final MuscleRings.Ring r, final V3 along) {
        if (r.span().isPresent()) {
            final V3 across = r.span().get().unit();
            final V3 face = square(r.way(), across, along);
            final V3 through = across.cross(face).unit();
            return new V3[] {across, face, through.dot(along) < 0 ? through.times(-1) : through};
        }
        final V3 face = square(r.way(), along, Math.abs(along.y()) < 0.9 ? along.cross(V3.Y) : along.cross(V3.X));
        return new V3[] {along.cross(face).unit(), face, along};
    }

    /** {@code way} square to {@code to}, a unit; {@code or} square to it, should it run along it. */
    private V3 square(final V3 way, final V3 to, final V3 or) {
        final V3 v = way.minus(to.times(way.dot(to)));
        return (v.length() < 1e-6 ? or.minus(to.times(or.dot(to))) : v).unit();
    }

    /**
     * Its ends kept where their sections are: each end's ring a crease as sharp as {@link #END} says, so that,
     * smoothed, it does not round out past its attachment; a ring's, none.
     */
    private List<Crease> ends(final Muscle m, final int n) {
        if (m.ring()) {
            return List.of();
        }
        final List<Integer> first = new ArrayList<>();
        final List<Integer> last = new ArrayList<>();
        for (int q = 0; q <= ROUND; q++) {
            first.add(q % ROUND);
            last.add((n - 1) * ROUND + q % ROUND);
        }
        return List.of(new Crease("its origin", first, END, false), new Crease("its insertion", last, END, false));
    }

    /**
     * How much {@code m} is as drawn, cm3, where its sections lie at {@code on}: from each section to the next, a
     * frustum between their ovals.
     */
    public double asDrawn(final Muscle m, final V3[] on) {
        return volume(m, on, 1);
    }

    /**
     * Whether {@code m}'s {@code i}th section is kept as drawn, whatever the body: a tendon's; one the muscle keeps
     * ({@link Muscle#keptAsDrawn}); or either end of a muscle of three sections or more, where it holds to its bone,
     * one of two being a sheet all belly.
     */
    public boolean kept(final Muscle m, final int i) {
        final int n = m.sections().size();
        return tendon(m, i) || m.keptAsDrawn(i) || !m.ring() && n > 2 && (i == 0 || i == n - 1);
    }

    /**
     * Whether {@code m}'s {@code i}th section is a tendon's: all of a tendon or a band; any much thinner than its belly,
     * under a quarter of its fullest section across, or under three tenths of its deepest, as a tendon runs.
     */
    public boolean tendon(final Muscle m, final int i) {
        final List<Section> s = m.sections();
        if (m.layer() == Layer.TENDON) {
            return true;
        }
        double area = 0;
        double deep = 0;
        for (final Section x : s) {
            area = Math.max(area, x.wide() * x.deep());
            deep = Math.max(deep, x.deep());
        }
        return s.get(i).wide() * s.get(i).deep() < 0.25 * area || s.get(i).deep() < 0.3 * deep;
    }

    /**
     * How much wider and deeper {@code m}'s belly is than drawn, to be as much as {@code bulk} has it: found on its
     * sections' frustums, then put right by its surface, smoothed twice, a few times, and then by that surface alone.
     */
    private double grown(final Muscle m, final Muscularity bulk, final V3[] on, final V3[] way, final V3[] ends) {
        if (m.layer() == Layer.TENDON) {
            return 1;
        }
        final double drawn = smoothed(m, on, way, ends, 1);
        final double wanted = bulk.volume(m, drawn);
        if (Math.abs(wanted - drawn) <= 1e-9 * drawn) {
            return 1;
        }
        double target = asDrawn(m, on) * wanted / drawn;
        double k = frustums(m, on, target);
        for (int round = 0; round < 3; round++) {
            target *= wanted / smoothed(m, on, way, ends, k);
            k = frustums(m, on, target);
        }
        for (int round = 0; round < 4; round++) {
            k *= Math.sqrt(wanted / smoothed(m, on, way, ends, k));
        }
        return k;
    }

    /** How much {@code m} holds, its belly {@code k} times, its surface smoothed twice, as it is drawn. */
    private double smoothed(final Muscle m, final V3[] on, final V3[] way, final V3[] ends, final double k) {
        final Subdivision s = Subdivision.INSTANCE;
        final Cage cage = cageAt(m, on, way, ends, k, p -> Optional.empty());
        return volume(s.mesh(s.times(s.of(Fitting.INSTANCE.fitted(cage, 1), 1), 2)));
    }

    /** How much wider and deeper {@code m}'s belly is than drawn, for its frustums to come to {@code target}. */
    private double frustums(final Muscle m, final V3[] on, final double target) {
        double lo = 0.05;
        double hi = 10;
        for (int i = 0; i < 60; i++) {
            final double mid = (lo + hi) / 2;
            if (volume(m, on, mid) < target) {
                lo = mid;
            } else {
                hi = mid;
            }
        }
        return (lo + hi) / 2;
    }

    /** How much {@code mesh} holds, cm3: closed and facing out. */
    public double volume(final Mesher.Mesh mesh) {
        final float[] p = mesh.points();
        final int[] t = mesh.triangles();
        double v = 0;
        for (int i = 0; i < t.length; i += 3) {
            final V3 a = new V3(p[3 * t[i]], p[3 * t[i] + 1], p[3 * t[i] + 2]);
            final V3 b = new V3(p[3 * t[i + 1]], p[3 * t[i + 1] + 1], p[3 * t[i + 1] + 2]);
            final V3 c = new V3(p[3 * t[i + 2]], p[3 * t[i + 2] + 1], p[3 * t[i + 2] + 2]);
            v += a.dot(b.cross(c)) / 6;
        }
        return v;
    }

    /** {@code m}'s volume, its belly's sections {@code k} times as wide and deep as drawn. */
    private double volume(final Muscle m, final V3[] on, final double k) {
        final List<Section> s = m.sections();
        double v = 0;
        final int n = s.size();
        for (int i = 0; i + 1 < n || m.ring() && i < n; i++) {
            final int j = (i + 1) % n;
            final double a = area(s.get(i), kept(m, i) ? 1 : k);
            final double b = area(s.get(j), kept(m, j) ? 1 : k);
            v += on[j].minus(on[i]).length() * (a + b + Math.sqrt(a * b)) / 3;
        }
        return v;
    }

    private double area(final Section s, final double k) {
        return Math.PI / 4 * s.wide() * s.deep() * k * k;
    }

    /**
     * The faces round {@code n} rings, from each to the next, and shutting either end, or, a ring's, the last to the
     * first; turned round if not out.
     */
    private List<int[]> faces(final int n, final boolean ring, final boolean out) {
        final List<int[]> faces = new ArrayList<>();
        for (int i = 0; i + 1 < n || ring && i < n; i++) {
            final int j = (i + 1) % n;
            for (int k = 0; k < ROUND; k++) {
                final int q = (k + 1) % ROUND;
                faces.add(new int[] {i * ROUND + k, i * ROUND + q, j * ROUND + q, j * ROUND + k});
            }
        }
        if (!ring) {
            final int[] first = new int[ROUND];
            final int[] last = new int[ROUND];
            for (int k = 0; k < ROUND; k++) {
                first[k] = ROUND - 1 - k;
                last[k] = (n - 1) * ROUND + k;
            }
            faces.add(first);
            faces.add(last);
        }
        if (!out) {
            for (int f = 0; f < faces.size(); f++) {
                final int[] c = faces.get(f);
                final int[] r = new int[c.length];
                for (int k = 0; k < c.length; k++) {
                    r[k] = c[c.length - 1 - k];
                }
                faces.set(f, r);
            }
        }
        return faces;
    }

    /** Whether the faces as first laid round {@code n} rings of {@code points} face out: enclose a volume above none. */
    private boolean outward(final List<V3> points, final int n, final boolean ring) {
        double v = 0;
        final V3 o = points.getFirst();
        for (final int[] f : faces(n, ring, true)) {
            for (int i = 1; i + 1 < f.length; i++) {
                v += points.get(f[0]).minus(o).dot(points.get(f[i]).minus(o).cross(points.get(f[i + 1]).minus(o)));
            }
        }
        return v > 0;
    }
}
