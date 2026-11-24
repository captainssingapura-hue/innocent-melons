package com.example.fxsuite.life.body.bone.spine;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.V3;

/**
 * Each vertebra's level, from the atlas down to the fifth lumbar, in the body's frame as the papers' figure stands at
 * rest (y up from its hip joints, z ahead): where its body's middle is, up and ahead, the spine curving forward at the
 * neck, back at the chest, forward at the small of the back; how wide, deep and high its body is; how wide and deep its
 * canal, how long its pedicles; how far its transverse processes reach out and back; how long its spinous process and
 * how steeply it slopes down, degrees.
 */
public enum Level {
    C1(60.30, -1.00, 1.60, 1.45, 1.20, 2.30, 1.40, 0.55, 2.70, 0.00, 1.30, 25.0),
    C2(58.95, -1.05, 1.60, 1.45, 1.20, 2.30, 1.40, 0.55, 2.70, 0.00, 1.30, 25.0),
    C3(57.75, -0.95, 1.60, 1.45, 1.20, 2.30, 1.40, 0.55, 2.70, 0.00, 1.30, 25.0),
    C4(56.55, -1.00, 1.69, 1.47, 1.23, 2.27, 1.39, 0.56, 2.80, 0.00, 1.38, 23.5),
    C5(55.35, -1.35, 1.90, 1.53, 1.30, 2.20, 1.38, 0.58, 3.05, 0.00, 1.53, 20.0),
    C6(54.10, -1.95, 2.11, 1.58, 1.37, 2.13, 1.36, 0.59, 3.30, 0.00, 1.60, 16.5),
    C7(52.80, -2.75, 2.20, 1.60, 1.40, 2.10, 1.35, 0.60, 3.40, 0.00, 2.60, 15.0),
    T1(51.10, -3.40, 2.40, 1.60, 1.55, 1.70, 1.40, 0.80, 3.90, 1.20, 2.80, 25.0),
    T2(49.20, -4.15, 2.44, 1.67, 1.58, 1.70, 1.40, 0.80, 3.86, 1.20, 2.86, 28.3),
    T3(47.20, -4.85, 2.54, 1.84, 1.65, 1.69, 1.40, 0.80, 3.76, 1.18, 3.01, 37.1),
    T4(45.10, -5.40, 2.66, 2.06, 1.75, 1.68, 1.41, 0.81, 3.64, 1.17, 3.19, 47.9),
    T5(43.00, -5.85, 2.76, 2.23, 1.82, 1.67, 1.41, 0.81, 3.54, 1.14, 3.34, 56.7),
    T6(40.80, -6.10, 2.80, 2.30, 1.85, 1.66, 1.42, 0.82, 3.50, 1.11, 3.40, 60.0),
    T7(38.60, -6.15, 2.86, 2.33, 1.88, 1.64, 1.43, 0.83, 3.43, 1.09, 3.34, 58.8),
    T8(36.40, -6.00, 3.03, 2.43, 1.96, 1.63, 1.44, 0.84, 3.25, 1.06, 3.20, 56.3),
    T9(34.20, -5.65, 3.25, 2.55, 2.07, 1.62, 1.44, 0.84, 3.00, 1.03, 3.06, 55.0),
    T10(32.00, -5.15, 3.48, 2.68, 2.19, 1.61, 1.45, 0.85, 2.75, 1.02, 3.00, 47.5),
    T11(29.75, -4.50, 3.64, 2.77, 2.27, 1.60, 1.45, 0.85, 2.57, 1.00, 2.70, 32.5),
    T12(27.45, -3.75, 3.70, 2.80, 2.30, 1.60, 1.45, 0.85, 2.50, 1.00, 2.40, 25.0),
    L1(24.85, -2.95, 4.00, 3.00, 2.50, 1.90, 1.55, 1.00, 3.60, 0.40, 2.50, 5.0),
    L2(21.90, -2.30, 4.15, 3.06, 2.53, 1.94, 1.56, 1.01, 4.05, 0.40, 2.44, 5.0),
    L3(18.80, -1.95, 4.50, 3.20, 2.60, 2.05, 1.58, 1.03, 4.50, 0.40, 2.30, 5.0),
    L4(15.60, -1.95, 4.85, 3.34, 2.67, 2.16, 1.59, 1.04, 4.35, 0.40, 2.16, 5.0),
    L5(12.40, -2.40, 5.00, 3.40, 2.70, 2.20, 1.60, 1.05, 4.20, 0.40, 2.10, 5.0);

    /** Where the sacrum's top is, under the fifth lumbar. */
    private static final V3 SACRUM = new V3(0, 10.4, -3.0);

    private final double up;
    private final double ahead;
    private final double wide;
    private final double deep;
    private final double high;
    private final double canalWide;
    private final double canalDeep;
    private final double pedicle;
    private final double transverse;
    private final double transverseBack;
    private final double spinous;
    private final double slope;

    Level(final double up, final double ahead, final double wide, final double deep, final double high,
          final double canalWide, final double canalDeep, final double pedicle, final double transverse,
          final double transverseBack, final double spinous, final double slope) {
        this.up = up;
        this.ahead = ahead;
        this.wide = wide;
        this.deep = deep;
        this.high = high;
        this.canalWide = canalWide;
        this.canalDeep = canalDeep;
        this.pedicle = pedicle;
        this.transverse = transverse;
        this.transverseBack = transverseBack;
        this.spinous = spinous;
        this.slope = slope;
    }

    /** Whether it is in the neck, the chest or the small of the back. */
    public boolean cervical() {
        return ordinal() <= C7.ordinal();
    }

    public boolean lumbar() {
        return ordinal() >= L1.ordinal();
    }

    /** The part it moves with. */
    public Part part() {
        return cervical() ? Part.NECK : lumbar() ? Part.ABDOMEN : Part.CHEST;
    }

    public double wide() {
        return wide;
    }

    public double deep() {
        return deep;
    }

    public double high() {
        return high;
    }

    public double canalWide() {
        return canalWide;
    }

    public double canalDeep() {
        return canalDeep;
    }

    public double pedicle() {
        return pedicle;
    }

    public double transverse() {
        return transverse;
    }

    public double transverseBack() {
        return transverseBack;
    }

    public double spinous() {
        return spinous;
    }

    public double slope() {
        return slope;
    }

    /** Its body's middle as it was drawn, in the body's frame, before the spine's curves were measured. */
    public V3 drawn() {
        return new V3(0, up, ahead);
    }

    /** Its body's middle, in the body's frame, the spine curved as men's are measured ({@link SpineCurves}). */
    public V3 middle() {
        return SpineForm.INSTANCE.middle(this);
    }

    /** Its frame: its body's middle; up along the spine, from the level below to the one above; ahead square to it. */
    public V3[] frame() {
        return frame(SpineForm.INSTANCE.middles());
    }

    /** Up along the spine at it, its vertebrae's middles {@code m}: from the one below to the one above. */
    public V3 up(final V3[] m) {
        final V3 above = ordinal() > 0 ? m[ordinal() - 1] : m[ordinal()].plus(V3.Y);
        final V3 below = ordinal() < m.length - 1 ? m[ordinal() + 1] : SACRUM;
        return above.minus(below).unit();
    }

    private V3[] frame(final V3[] m) {
        final V3 upward = up(m);
        return new V3[] {m[ordinal()], upward, V3.X.cross(upward).unit()};
    }

    /** A point given across, up and ahead in its own frame, in the body's. */
    public V3 at(final double x, final double y, final double z) {
        return among(SpineForm.INSTANCE.middles(), x, y, z);
    }

    /** The same, the spine as it was drawn. */
    public V3 drawnAt(final double x, final double y, final double z) {
        final Level[] all = values();
        final V3[] m = new V3[all.length];
        for (final Level l : all) {
            m[l.ordinal()] = l.drawn();
        }
        return among(m, x, y, z);
    }

    /** The same, its vertebrae's middles {@code m}, by their order. */
    public V3 among(final V3[] m, final double x, final double y, final double z) {
        final V3[] f = frame(m);
        return f[0].plus(V3.X.times(x)).plus(f[1].times(y)).plus(f[2].times(z));
    }

    /** Where its transverse process ends on the right, a rib's tubercle meeting it there. */
    public V3 transverseTip() {
        return at(transverse, 0.15 * high, -(deep / 2 + pedicle + transverseBack));
    }

    /** The same, the spine as it was drawn. */
    public V3 drawnTransverseTip() {
        return drawnAt(transverse, 0.15 * high, -(deep / 2 + pedicle + transverseBack));
    }

    /**
     * Where its spinous process ends, down the middle, back and down from where its laminae meet behind its canal, a
     * man's ({@link Vertebrae}), the spine as it was drawn.
     */
    public V3 drawnSpinousTip() {
        final double s = Math.toRadians(slope);
        return drawnAt(0, -spinous * Math.sin(s), -(deep / 2 + pedicle + canalDeep + spinous * Math.cos(s)));
    }
}
