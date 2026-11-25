package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Reshaped;
import com.example.fxsuite.life.body.Rot;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.skull.Landmark;
import com.example.fxsuite.life.body.bone.skull.SkullLandmarks;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.Map;

/**
 * How many times as big along each way a reference head is to be made to go over a man's skull, as men's are measured
 * ({@link SkullLandmarks}): set on the skull by its eyes ({@link SkullUnder#at}), where its skin asks the bone to lie
 * over the vertex and the chin, the brow and the back of the head, and either cheekbone ({@link SkullUnder#under});
 * made as high from the one to the other, as long, and as broad as a man's, each way on its own. Measured again on
 * the head as each time makes it, {@link #ROUNDS} times, the lines out to its skin leaning as it changes. A head made
 * elsewhere is not the body's size: MakeHuman's is about a tenth too high for it.
 */
public record HeadSize() implements StatelessFunctionalObject {

    public static final HeadSize INSTANCE = new HeadSize();

    /** How many times the head is measured and made again. */
    private static final int ROUNDS = 4;

    /** How many times as big across, up and ahead {@code head}, its right eye's middle at {@code eye}, is to be. */
    public V3 of(final Field.Shape head, final V3 eye) {
        final Map<Landmark, V3> men = SkullLandmarks.INSTANCE.targets();
        V3 k = new V3(1, 1, 1);
        for (int i = 0; i < ROUNDS; i++) {
            final Field.Shape made = new Reshaped(head, V3.ZERO, V3.ZERO, Rot.NONE, k, 0);
            final Map<Landmark, V3> asked = SkullUnder.INSTANCE.under(made, SkullUnder.INSTANCE.at(eye.times(k)), men);
            k = k.times(new V3(across(men) / across(asked), high(men) / high(asked), deep(men) / deep(asked)));
        }
        return k;
    }

    /** How broad across the cheekbones. */
    private double across(final Map<Landmark, V3> at) {
        return 2 * found(at, Landmark.ZYGION).x();
    }

    /** How high from the chin to the vertex. */
    private double high(final Map<Landmark, V3> at) {
        return found(at, Landmark.BREGMA).y() - found(at, Landmark.GNATHION).y();
    }

    /** How long from the brow to the back of the head. */
    private double deep(final Map<Landmark, V3> at) {
        return found(at, Landmark.GLABELLA).z() - found(at, Landmark.OPISTHOCRANION).z();
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
