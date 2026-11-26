package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.flesh.Flesh;
import com.example.fxsuite.life.body.skin.HeadLine;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;
import java.util.function.ToDoubleFunction;

/**
 * A body's own skin left open where a face is to go: none of it ahead of a line down the side of the head, from the
 * crown, {@link #CROWN}, to the throat, {@link #THROAT}, in front of the ear and the jaw's angle, leaning a little
 * forward as it goes down, as it was drawn on the bench; nor above the head's line ({@link HeadLine}) there, so that
 * the throat under it is the neck's still. Cut through its triangles ({@link NeckCut}), its edge a line, not the
 * triangles' teeth; each point made on the edge following the body as the point kept beside it does. The back and the
 * sides of the head and the neck one skin, as they were, moving as they did; the face's place open, along an edge
 * that moves with the head alone.
 */
public record FaceOpening() implements StatelessFunctionalObject {

    public static final FaceOpening INSTANCE = new FaceOpening();

    /** Where the line down the side of the head is, high and low, in the head's frame as the body was bound, cm. */
    private static final V3 CROWN = new V3(0, 14, 4.1);
    private static final V3 THROAT = new V3(0, -7.6, 6.3);

    /** {@code skin} with nothing ahead of the line and above the head's line. */
    public Flesh open(final Flesh skin) {
        final Rig.Frame f = skin.bound().get(skin.parts().indexOf(Part.HEAD));
        final ToDoubleFunction<V3> above = HeadLine.INSTANCE.above();
        final V3 down = THROAT.minus(CROWN);
        final V3 ahead = new V3(0, -down.z(), down.y()).unit().times(-1);
        final V3[] at = new V3[skin.size()];
        final double[] off = new double[skin.size()];
        for (int i = 0; i < at.length; i++) {
            at[i] = skin.point(i);
            final V3 p = f.local(at[i]);
            off[i] = Math.min(p.minus(CROWN).dot(ahead), above.applyAsDouble(p));
        }
        final NeckCut.Cut cut = NeckCut.INSTANCE.cut(skin.triangles(), at, off);
        return kept(skin, cut);
    }

    /** The surface {@code cut} keeps of {@code skin}, each made point following the body as its kept one does. */
    private Flesh kept(final Flesh skin, final NeckCut.Cut cut) {
        final int k = Flesh.PARTS;
        final int n = cut.where().length;
        final int[] which = new int[k * n];
        final float[] shares = new float[k * n];
        for (int i = 0; i < n; i++) {
            final int from = i < skin.size() ? i : cut.made()[i - skin.size()][0];
            System.arraycopy(skin.which(), k * from, which, k * i, k);
            System.arraycopy(skin.shares(), k * from, shares, k * i, k);
        }
        final List<int[]> kept = whole(cut.kept(), n);
        final int[] t = new int[3 * kept.size()];
        for (int i = 0; i < kept.size(); i++) {
            System.arraycopy(kept.get(i), 0, t, 3 * i, 3);
        }
        return new Flesh(new float[3 * n], new float[3 * n], t, skin.parts(), skin.bound(), which, shares)
                .reshaped(cut.where());
    }

    /**
     * Of {@code triangles}, over {@code n} points, only those of the largest piece, joined through their points: the
     * skin the cut leaves apart from the rest gone, as what had sunk deep into the mouth, behind the line.
     */
    private List<int[]> whole(final List<int[]> triangles, final int n) {
        final int[] root = new int[n];
        for (int i = 0; i < n; i++) {
            root[i] = i;
        }
        for (final int[] t : triangles) {
            join(root, t[0], t[1]);
            join(root, t[0], t[2]);
        }
        final int[] size = new int[n];
        int largest = 0;
        for (final int[] t : triangles) {
            final int r = find(root, t[0]);
            size[r]++;
            largest = size[r] > size[largest] ? r : largest;
        }
        final int keep = largest;
        return triangles.stream().filter(t -> find(root, t[0]) == keep).toList();
    }

    private void join(final int[] root, final int a, final int b) {
        root[find(root, a)] = find(root, b);
    }

    private int find(final int[] root, final int i) {
        int r = i;
        while (root[r] != r) {
            root[r] = root[root[r]];
            r = root[r];
        }
        return r;
    }
}
