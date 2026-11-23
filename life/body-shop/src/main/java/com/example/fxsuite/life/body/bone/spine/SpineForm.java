package com.example.fxsuite.life.body.bone.spine;

import com.example.fxsuite.life.body.ThinPlate;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.Bend;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

import java.util.ArrayList;
import java.util.List;

/**
 * The spine curved as men's are measured ({@link SpineCurves}), from as it was drawn: each vertebra's middle; and what
 * was drawn on the vertebrae, the muscles held on them, bent smoothly ({@link ThinPlate}) from where it was to where
 * it is, through each vertebra's middle, front, back, spine, sides and transverse processes as drawn and as curved,
 * the sacrum and the joint the head nods on held. Found once.
 */
public enum SpineForm {
    INSTANCE;

    private final V3[] middles;
    private final Bend bend;

    SpineForm() {
        middles = SpineCurves.INSTANCE.middles();
        final Level[] all = Level.values();
        final V3[] drawn = new V3[all.length];
        for (final Level l : all) {
            drawn[l.ordinal()] = l.drawn();
        }
        final List<V3> was = new ArrayList<>();
        final List<V3> is = new ArrayList<>();
        for (final Level l : all) {
            for (final double[] p : points(l)) {
                was.add(l.among(drawn, p[0], p[1], p[2]));
                is.add(l.among(middles, p[0], p[1], p[2]));
            }
        }
        for (final V3 held : List.of(Discs.S1, Discs.APEX, Discs.HEAD, new V3(0, 10.4, -3.0))) {
            was.add(held);
            is.add(held);
        }
        bend = new Bend(ThinPlate.INSTANCE.through(was, is), ThinPlate.INSTANCE.through(is, was));
    }

    /**
     * Points on {@code l}, across, up and ahead in its frame: its middle, the front and top of its body, its spine's
     * root and tip, its transverse processes' tips on either side.
     */
    private List<double[]> points(final Level l) {
        final double back = -(l.deep() / 2 + l.canalDeep());
        final double tx = l.transverse();
        final double tz = -(l.deep() / 2 + l.pedicle() + l.transverseBack());
        return List.of(new double[] {0, 0, 0}, new double[] {0, 0, l.deep() / 2}, new double[] {0, l.high() / 2, 0},
                new double[] {0, 0, back}, new double[] {0, 0, back - l.spinous()}, new double[] {tx, 0, tz},
                new double[] {-tx, 0, tz});
    }

    /** Each vertebra's middle as the spine is curved, by {@link Level#ordinal}. */
    public V3[] middles() {
        return middles.clone();
    }

    /** {@code l}'s middle as the spine is curved. */
    public V3 middle(final Level l) {
        return middles[l.ordinal()];
    }

    /** Whether what is drawn on {@code bone} is bent with the spine: on a vertebra, the sacrum, the coccyx. */
    public boolean bends(final Bone bone) {
        return bone.region() == Region.SPINE;
    }

    /** Where {@code p}, drawn on the spine in the body's frame at rest, is as it is curved. */
    public V3 apply(final V3 p) {
        return bend.apply(p);
    }
}
