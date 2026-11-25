package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.skull.SkullForm;
import com.example.fxsuite.life.body.flesh.Flesh;
import com.example.fxsuite.life.body.skin.HeadLine;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.function.ToDoubleFunction;

/**
 * A clay head ({@link ClayHead}) joined to a body's skin, one surface, moving as one: the clay head cut where the head
 * ends ({@link HeadLine}), under the chin and the jaw's lower border, behind the ear and along the occiput, its own
 * neck gone, its nape as well as its throat, for its neck is not the body's and lies in front of the body's spine;
 * the body's skin, its head and the top of its neck gone, cut {@link #UNDER} under the same line, so that its nape is
 * its own, over the trapezius on the neck's spines, up to under the skull, and its throat under the chin; each cut
 * through its triangles ({@link NeckCut}), so that each ends on its cut. Each cut again {@link #IN} further in, to see
 * which way each surface runs from its edge; between the two edges a band ({@link NeckBand}), the underside of the chin
 * in front, the top of the nape behind, leaving each edge as its surface runs. The clay head following the head alone.
 * Where the skull is fitted under it ({@link SkullForm}), in the head's frame, as the body was bound.
 */
public record NeckJoin() implements StatelessFunctionalObject {

    public static final NeckJoin INSTANCE = new NeckJoin();

    /** How far under the head's line the body's skin is cut: in front, 3.5 cm under the chin's bone, cm. */
    private static final double UNDER = 2.9;
    /** How far in from each cut each surface is cut again, cm. */
    private static final double IN = 1;

    /** {@code skin} with the clay head {@code clay}, its points {@code k} times as far out and then at {@code at}. */
    public Flesh joined(final Flesh skin, final Mesher.Mesh clay, final V3 at, final double k) {
        final Rig.Frame f = skin.bound().get(skin.parts().indexOf(Part.HEAD));
        final ToDoubleFunction<V3> line = HeadLine.INSTANCE.above();
        final ToDoubleFunction<V3> foot = p -> line.applyAsDouble(p) + UNDER;
        final V3[] body = new V3[skin.size()];
        for (int i = 0; i < body.length; i++) {
            body[i] = f.local(skin.point(i));
        }
        final V3[] head = new V3[clay.points().length / 3];
        for (int i = 0; i < head.length; i++) {
            head[i] = new V3(clay.points()[3 * i], clay.points()[3 * i + 1], clay.points()[3 * i + 2]).times(k)
                    .plus(at);
        }
        final NeckCut.Cut low = NeckCut.INSTANCE.cut(skin.triangles(), body, above(skin, body, foot, 0));
        final NeckCut.Cut high = NeckCut.INSTANCE.cut(clay.triangles(), head, below(head, line, 0));
        final NeckCut.Cut lower = NeckCut.INSTANCE.cut(skin.triangles(), body, above(skin, body, foot, IN));
        final NeckCut.Cut higher = NeckCut.INSTANCE.cut(clay.triangles(), head, below(head, line, IN));
        final NeckEdge e = NeckEdge.INSTANCE;
        final NeckBand.Seam seam = new NeckBand.Seam(e.of(low.kept(), low.where(), foot),
                e.of(lower.kept(), lower.where(), p -> foot.applyAsDouble(p) + IN),
                e.of(high.kept(), high.where(), line),
                e.of(higher.kept(), higher.where(), p -> line.applyAsDouble(p) - IN));
        return NeckBand.INSTANCE.joined(skin, f, low, high, seam);
    }

    /**
     * How far above the body's cut, {@code foot}, less {@code in}, each of {@code skin}'s points is, {@code body} in
     * the head's frame: its neck's as they are, its head's all above, the rest all below.
     */
    private double[] above(final Flesh skin, final V3[] body, final ToDoubleFunction<V3> foot, final double in) {
        final double[] off = new double[body.length];
        for (int i = 0; i < off.length; i++) {
            final Part p = skin.most(i);
            final double h = foot.applyAsDouble(body[i]) + in;
            off[i] = p == Part.HEAD ? Math.max(h, 1) : p == Part.NECK ? h : Math.min(h, -1);
        }
        return off;
    }

    /** How far below the head's line, {@code line}, and {@code in} above it, each of the clay head's points is. */
    private double[] below(final V3[] head, final ToDoubleFunction<V3> line, final double in) {
        final double[] off = new double[head.length];
        for (int i = 0; i < off.length; i++) {
            off[i] = in - line.applyAsDouble(head[i]);
        }
        return off;
    }
}
