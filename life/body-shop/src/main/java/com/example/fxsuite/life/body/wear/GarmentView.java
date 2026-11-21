package com.example.fxsuite.life.body.wear;

import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.flesh.Flesh;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;

/**
 * A garment drawn as the body moves ({@link Garment}): its cloth's points and their ways out found afresh as the body
 * is posed, into a scene whose frame is the body's with each way's sign as {@code signs} has it, its triangles turned
 * round if that is a mirror's; in its colour, matte, a knit's faint sheen; both its faces, its inside seen through an
 * opening.
 */
public final class GarmentView {

    private final Flesh cloth;
    private final V3 signs;
    private final TriangleMesh mesh = new TriangleMesh(VertexFormat.POINT_NORMAL_TEXCOORD);
    private final MeshView view = new MeshView(mesh);
    private final float[] at;
    private final float[] out;

    /** {@code g} drawn with each way's sign as {@code signs} has it, posed at first as {@code rig} holds it. */
    public GarmentView(final Garment g, final V3 signs, final Rig rig) {
        this.cloth = g.cloth();
        this.signs = signs;
        at = new float[cloth.size() * 3];
        out = new float[cloth.size() * 3];
        final int[] t = cloth.triangles();
        final boolean mirror = signs.x() * signs.y() * signs.z() < 0;
        final int[] faces = new int[t.length * 3];
        for (int i = 0; i < t.length; i += 3) {
            for (int k = 0; k < 3; k++) {
                final int c = t[i + (mirror && k > 0 ? 3 - k : k)];
                faces[3 * (i + k)] = c;
                faces[3 * (i + k) + 1] = c;
                faces[3 * (i + k) + 2] = 0;
            }
        }
        mesh.getTexCoords().setAll(0.5f, 0.5f);
        show(rig);
        mesh.getFaces().setAll(faces);
        view.setCullFace(CullFace.NONE);
        final Color c = Color.web(g.colour());
        final PhongMaterial m = new PhongMaterial(c);
        m.setSpecularColor(c.interpolate(Color.WHITE, 0.25).deriveColor(0, 1, 0.35, 1));
        m.setSpecularPower(12);
        view.setMaterial(m);
    }

    /** What to put in the scene. */
    public Node node() {
        return view;
    }

    /** Posed as {@code rig} holds the body. */
    public void show(final Rig rig) {
        cloth.posed(rig, at, out);
        final float sx = (float) signs.x();
        final float sy = (float) signs.y();
        final float sz = (float) signs.z();
        for (int i = 0; i < at.length; i += 3) {
            at[i] *= sx;
            at[i + 1] *= sy;
            at[i + 2] *= sz;
            out[i] *= sx;
            out[i + 1] *= sy;
            out[i + 2] *= sz;
        }
        if (mesh.getPoints().size() == at.length) {
            mesh.getPoints().set(0, at, 0, at.length);
            mesh.getNormals().set(0, out, 0, out.length);
        } else {
            mesh.getPoints().setAll(at);
            mesh.getNormals().setAll(out);
        }
    }
}
