package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * The skull as men's are measured: each measure between two landmarks ({@link Landmark}), its mean and its spread
 * among men; the skull's as Howells measured 1,368 men's of 28 peoples (his data, on Auerbach's site, the University
 * of Tennessee; Howells 1973), the jaw's as Moqaddam and others measured 97 men's by CT (Med J Islam Repub Iran 2022,
 * 36:160, table 3). Where the landmarks are to be: each as near as may be to where it is drawn, every measure its mean,
 * those held where they are; found by stepping each down the slope of how far the measures are off, in spreads, and
 * how far it is from where it is drawn, in {@link #NEAR}s, many times.
 */
public record SkullLandmarks() implements StatelessFunctionalObject {

    public static final SkullLandmarks INSTANCE = new SkullLandmarks();

    /** How far from where it is drawn a landmark is let go as readily as a measure a spread off its mean, cm. */
    private static final double NEAR = 3;
    /**
     * How far from where it is asked to be a landmark is let go as readily as a measure a spread off its mean, cm: as
     * far as the soft over the bone differs from one man to the next, so that a head asks and the measures weigh in.
     */
    private static final double ASKED = 0.3;
    /** How many steps, and how long each. */
    private static final int STEPS = 40_000;
    private static final double STEP = 0.002;

    /**
     * A measure: what it is called, between landmark {@code a}, its left one if {@code aLeft}, and {@code b}, so;
     * its mean among men, and its spread, cm.
     */
    public record Measure(String said, Landmark a, boolean aLeft, Landmark b, boolean bLeft, double mean, double sd) {
    }

    /** The measures. */
    public List<Measure> measures() {
        final List<Measure> m = new ArrayList<>(vault());
        m.add(across("ZYB, across the arches", Landmark.ZYGION, 13.51, 0.65));
        m.add(new Measure("BPL", Landmark.BASION, false, Landmark.PROSTHION, false, 9.98, 0.62));
        m.add(new Measure("NPH", Landmark.NASION, false, Landmark.PROSTHION, false, 6.80, 0.52));
        m.add(new Measure("NLH", Landmark.NASION, false, Landmark.NASOSPINALE, false, 5.15, 0.38));
        m.add(across("NLB, the nasal opening", Landmark.ALARE, 2.68, 0.23));
        m.add(new Measure("OBB, the orbit", Landmark.DACRYON, false, Landmark.ECTOCONCHION, false, 4.02, 0.19));
        m.add(new Measure("OBH, the orbit", Landmark.ORBIT_TOP, false, Landmark.ORBIT_FOOT, false, 3.39, 0.23));
        m.add(across("DKB", Landmark.DACRYON, 2.19, 0.23));
        m.add(across("EKB", Landmark.ECTOCONCHION, 9.92, 0.39));
        m.add(across("MAB, the upper arch", Landmark.ECTOMOLARE, 6.53, 0.37));
        m.add(across("bigonial", Landmark.GONION, 9.66, 0.62));
        m.add(new Measure("the ramus", Landmark.CONDYLION, false, Landmark.GONION, false, 6.08, 0.46));
        m.add(new Measure("the jaw's length", Landmark.GONION, false, Landmark.GNATHION, false, 7.54, 0.43));
        m.add(new Measure("the chin", Landmark.INFRADENTALE, false, Landmark.GNATHION, false, 3.15, 0.41));
        return m;
    }

    /** The vault's: its length, breadths and height; the frontal's, the parietals' and the occipital's chords. */
    private List<Measure> vault() {
        return List.of(new Measure("GOL", Landmark.GLABELLA, false, Landmark.OPISTHOCRANION, false, 18.32, 0.75),
                new Measure("BNL", Landmark.BASION, false, Landmark.NASION, false, 10.15, 0.54),
                new Measure("BBH", Landmark.BASION, false, Landmark.BREGMA, false, 13.46, 0.68),
                new Measure("FRC", Landmark.NASION, false, Landmark.BREGMA, false, 11.17, 0.50),
                new Measure("PAC", Landmark.BREGMA, false, Landmark.LAMBDA, false, 11.28, 0.61),
                new Measure("OCC", Landmark.LAMBDA, false, Landmark.OPISTHION, false, 9.69, 0.60),
                across("XCB, the vault's breadth", Landmark.EURYON, 13.91, 0.71),
                across("ASB", Landmark.ASTERION, 10.89, 0.54));
    }

    private Measure across(final String said, final Landmark l, final double mean, final double sd) {
        return new Measure(said, l, false, l, true, mean, sd);
    }

    /** Where each landmark is to be, on the right if one of a pair. */
    public Map<Landmark, V3> targets() {
        return targets(Map.of());
    }

    /**
     * Where each landmark is to be, on the right if one of a pair, those {@code asked} as near as {@link #ASKED} to
     * where they are asked to be, a head over the skull saying where its bone lies, say ({@code SkullUnder}); the
     * measures shaping the rest.
     */
    public Map<Landmark, V3> targets(final Map<Landmark, V3> asked) {
        final Landmark[] all = Landmark.values();
        final double[][] p = new double[all.length][];
        for (final Landmark l : all) {
            p[l.ordinal()] = new double[] {l.drawn().x(), l.drawn().y(), l.drawn().z()};
        }
        final List<Measure> ms = measures();
        for (int step = 0; step < STEPS; step++) {
            final double[][] g = new double[all.length][3];
            for (final Measure m : ms) {
                pull(p, g, m);
            }
            for (final Landmark l : all) {
                if (!l.held()) {
                    move(p[l.ordinal()], g[l.ordinal()], l, asked.get(l));
                }
            }
        }
        final Map<Landmark, V3> to = new EnumMap<>(Landmark.class);
        for (final Landmark l : all) {
            to.put(l, new V3(p[l.ordinal()][0], p[l.ordinal()][1], p[l.ordinal()][2]));
        }
        return to;
    }

    /** Into {@code g}, the slope of how far {@code m} is off its mean, in spreads, squared, at {@code p}. */
    private void pull(final double[][] p, final double[][] g, final Measure m) {
        final double[] a = side(p[m.a().ordinal()], m.aLeft());
        final double[] b = side(p[m.b().ordinal()], m.bLeft());
        final double dx = a[0] - b[0];
        final double dy = a[1] - b[1];
        final double dz = a[2] - b[2];
        final double d = Math.sqrt(dx * dx + dy * dy + dz * dz);
        final double e = (d - m.mean()) / (m.sd() * m.sd()) / d;
        final double[] ga = g[m.a().ordinal()];
        final double[] gb = g[m.b().ordinal()];
        ga[0] += (m.aLeft() ? -1 : 1) * e * dx;
        ga[1] += e * dy;
        ga[2] += e * dz;
        gb[0] -= (m.bLeft() ? -1 : 1) * e * dx;
        gb[1] -= e * dy;
        gb[2] -= e * dz;
    }

    /**
     * {@code p} a step down its slope {@code g} and toward where {@code l} is drawn, or toward where it is
     * {@code asked} to be, if it is; down the middle, kept there.
     */
    private void move(final double[] p, final double[] g, final Landmark l, final V3 asked) {
        final V3 to = asked != null ? asked : l.drawn();
        final double near = asked != null ? ASKED : NEAR;
        final double[] d = {to.x(), to.y(), to.z()};
        for (int c = 0; c < 3; c++) {
            p[c] -= STEP * (g[c] + (p[c] - d[c]) / (near * near));
        }
        if (l.middle()) {
            p[0] = 0;
        }
    }

    private double[] side(final double[] p, final boolean left) {
        return left ? new double[] {-p[0], p[1], p[2]} : p;
    }
}
