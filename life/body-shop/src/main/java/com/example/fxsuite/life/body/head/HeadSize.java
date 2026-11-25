package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Mapped;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.skull.Landmark;
import com.example.fxsuite.life.body.bone.skull.SkullLandmarks;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.Map;

/**
 * How a reference head is to be made, region by region ({@link HeadWarp}), to go over a man's skull, as men's are
 * measured ({@link SkullLandmarks}): set on the skull by its eyes ({@link SkullUnder#at}), where its skin asks the
 * bone to lie ({@link SkullUnder#under}); its vault made as high from the nasion to the vertex as a man's, its face
 * from the nasion to the chin, its length from the brow to the back of the head, its breadth across the cheekbones,
 * each on its own. Measured again on the head as each time makes it, {@link #ROUNDS} times, the lines out to its skin
 * leaning as it changes. A head made elsewhere is not the body's: MakeHuman's vault is a fifth too high for a man's,
 * its face a tenth.
 */
public record HeadSize() implements StatelessFunctionalObject {

    public static final HeadSize INSTANCE = new HeadSize();

    /** How many times the head is measured and made again. */
    private static final int ROUNDS = 10;
    /** How little and how much a measure may be taken to follow its scale, for the next step. */
    private static final double LEAST = 0.2;
    private static final double MOST = 2;

    /**
     * How {@code head}, its right eye's middle at {@code eye}, is to be made a man's size: each round each scale
     * stepped as far as its measure is short of a man's, over how far that measure has been seen to follow the scale,
     * a line out to the skin not square to it moving the measure less than the skin moves.
     */
    public HeadWarp of(final Field.Shape head, final V3 eye) {
        final Map<Landmark, V3> men = SkullLandmarks.INSTANCE.targets();
        final double[] want = spans(men);
        final double[] k = {1, 1, 1, 1};
        final double[] follows = {1, 1, 1, 1};
        double[] lastK = null;
        double[] last = null;
        for (int i = 0; i < ROUNDS; i++) {
            final HeadWarp w = warp(eye, k);
            final double[] span = spans(SkullUnder.INSTANCE.under(made(head, w), SkullUnder.INSTANCE.at(w.apply(eye)),
                    men));
            for (int j = 0; j < k.length; j++) {
                final double moved = Math.log(k[j]) - (lastK == null ? Math.log(k[j]) : Math.log(lastK[j]));
                if (Math.abs(moved) > 1e-6) {
                    follows[j] = Math.max(LEAST, Math.min(MOST, (Math.log(span[j]) - Math.log(last[j])) / moved));
                }
            }
            lastK = k.clone();
            last = span;
            for (int j = 0; j < k.length; j++) {
                k[j] *= Math.exp(Math.log(want[j] / span[j]) / follows[j]);
            }
        }
        return warp(eye, k);
    }

    /** The warp about {@code eye} making the head {@code k} times as big: its vault, its face, ahead, across. */
    private HeadWarp warp(final V3 eye, final double[] k) {
        final Map<Landmark, V3> men = SkullLandmarks.INSTANCE.targets();
        final V3 on = SkullUnder.INSTANCE.eyeOnSkull();
        final HeadWarp.Levels levels = new HeadWarp.Levels(men.get(Landmark.NASION).y() - on.y(),
                men.get(Landmark.ZYGION).y() - on.y());
        return new HeadWarp(eye, levels, new HeadWarp.Scales(k[0], k[1], k[2], k[3]));
    }

    /** The spans the scales are made by: the vault's height, the face's, the length, across the cheekbones. */
    private double[] spans(final Map<Landmark, V3> at) {
        return new double[] {vault(at), face(at), deep(at), across(at)};
    }

    /** {@code head} as {@code w} makes it, its box its corners' as made. */
    public Field.Shape made(final Field.Shape head, final HeadWarp w) {
        final double[] b = head.box();
        final double[] box = {1e9, 1e9, 1e9, -1e9, -1e9, -1e9};
        for (int k = 0; k < 8; k++) {
            final V3 c = w.apply(new V3(b[(k & 1) == 0 ? 0 : 3], b[(k & 2) == 0 ? 1 : 4], b[(k & 4) == 0 ? 2 : 5]));
            final double[] v = {c.x(), c.y(), c.z()};
            for (int i = 0; i < 3; i++) {
                box[i] = Math.min(box[i], v[i]);
                box[i + 3] = Math.max(box[i + 3], v[i]);
            }
        }
        return new Mapped(head, w::back, box);
    }

    /** How high the vault, from the nasion to the vertex. */
    private double vault(final Map<Landmark, V3> at) {
        return found(at, Landmark.BREGMA).y() - found(at, Landmark.NASION).y();
    }

    /** How high the face, from the chin to the nasion. */
    private double face(final Map<Landmark, V3> at) {
        return found(at, Landmark.NASION).y() - found(at, Landmark.GNATHION).y();
    }

    /** How long from the brow to the back of the head. */
    private double deep(final Map<Landmark, V3> at) {
        return found(at, Landmark.GLABELLA).z() - found(at, Landmark.OPISTHOCRANION).z();
    }

    /** How broad across the cheekbones. */
    private double across(final Map<Landmark, V3> at) {
        return 2 * found(at, Landmark.ZYGION).x();
    }

    private V3 found(final Map<Landmark, V3> at, final Landmark l) {
        final V3 p = at.get(l);
        if (p == null) {
            throw new IllegalStateException("the reference head's skin not found over the " + l.name().toLowerCase()
                    + ", to size it by");
        }
        return p;
    }
}
