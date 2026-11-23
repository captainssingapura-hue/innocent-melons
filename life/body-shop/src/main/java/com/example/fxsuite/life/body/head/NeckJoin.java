package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.skull.Landmark;
import com.example.fxsuite.life.body.bone.skull.SkullForm;
import com.example.fxsuite.life.body.flesh.Flesh;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * A clay head ({@link ClayHead}) joined to a body's skin, one surface, moving as one: the clay head cut level a little
 * under its chin, {@link #CHIN}, its own short neck's foot gone; the body's skin, its head and the top of its neck
 * gone, cut on a slope from its throat, {@link #THROAT} under the chin, to low on its nape, {@link #NAPE} under the
 * back of the foramen magnum, where the body's neck, deeper than the clay head's, has room to come in to it; each cut
 * through its triangles ({@link NeckCut}), so that each ends on its cut. Each cut again {@link #IN} further in, to see
 * which way each surface runs from its edge; between the two edges a band ({@link NeckBand}), the underside of the chin
 * in front, the nape's slope behind, leaving each edge as its surface runs. The clay head following the head alone.
 * Where the skull is fitted under it ({@link SkullForm}), in the head's frame, as the body was bound.
 */
public record NeckJoin() implements StatelessFunctionalObject {

    public static final NeckJoin INSTANCE = new NeckJoin();

    /** How far under the chin the clay head is cut; the body's skin under it in front, and behind, cm. */
    private static final double CHIN = 0.6;
    private static final double THROAT = 3.5;
    private static final double NAPE = 12.5;
    /** How far in from each cut each surface is cut again, cm. */
    private static final double IN = 1;

    /** {@code skin} with the clay head {@code clay}, its points {@code k} times as far out and then at {@code at}. */
    public Flesh joined(final Flesh skin, final Mesher.Mesh clay, final V3 at, final double k) {
        final Rig.Frame f = skin.bound().get(skin.parts().indexOf(Part.HEAD));
        final V3 chin = SkullForm.INSTANCE.apply(Landmark.GNATHION.drawn());
        final V3 back = SkullForm.INSTANCE.apply(Landmark.OPISTHION.drawn());
        final V3 front = new V3(0, chin.y() - THROAT, chin.z());
        final V3 d = new V3(0, back.y() - NAPE, back.z()).minus(front);
        final V3 up = new V3(0, -d.z(), d.y()).unit();
        final V3 slope = up.y() > 0 ? up : up.times(-1);
        final double cut = chin.y() - CHIN;
        final V3[] body = new V3[skin.size()];
        for (int i = 0; i < body.length; i++) {
            body[i] = f.local(skin.point(i));
        }
        final V3[] head = new V3[clay.points().length / 3];
        for (int i = 0; i < head.length; i++) {
            head[i] = new V3(clay.points()[3 * i], clay.points()[3 * i + 1], clay.points()[3 * i + 2]).times(k)
                    .plus(at);
        }
        final NeckCut.Cut low = NeckCut.INSTANCE.cut(skin.triangles(), body, above(skin, body, front, slope, 0));
        final NeckCut.Cut high = NeckCut.INSTANCE.cut(clay.triangles(), head, below(head, cut));
        final NeckCut.Cut lower = NeckCut.INSTANCE.cut(skin.triangles(), body, above(skin, body, front, slope, IN));
        final NeckCut.Cut higher = NeckCut.INSTANCE.cut(clay.triangles(), head, below(head, cut + IN));
        final NeckEdge e = NeckEdge.INSTANCE;
        final NeckBand.Seam seam = new NeckBand.Seam(e.of(low.kept(), low.where(), p -> p.minus(front).dot(slope)),
                e.of(lower.kept(), lower.where(), p -> p.minus(front).dot(slope) + IN),
                e.of(high.kept(), high.where(), p -> p.y() - cut), e.of(higher.kept(), higher.where(),
                p -> p.y() - cut - IN));
        return NeckBand.INSTANCE.joined(skin, f, low, high, seam);
    }

    /**
     * How far above the slope less {@code in} each of {@code skin}'s points is, {@code body} in the head's frame: its
     * neck's as they are, its head's all above, the rest all below.
     */
    private double[] above(final Flesh skin, final V3[] body, final V3 front, final V3 slope, final double in) {
        final double[] off = new double[body.length];
        for (int i = 0; i < off.length; i++) {
            final Part p = skin.most(i);
            final double h = body[i].minus(front).dot(slope) + in;
            off[i] = p == Part.HEAD ? Math.max(h, 1) : p == Part.NECK ? h : Math.min(h, -1);
        }
        return off;
    }

    /** How far below {@code cut} each of the clay head's points {@code head} is. */
    private double[] below(final V3[] head, final double cut) {
        final double[] off = new double[head.length];
        for (int i = 0; i < off.length; i++) {
            off[i] = cut - head[i].y();
        }
        return off;
    }
}
