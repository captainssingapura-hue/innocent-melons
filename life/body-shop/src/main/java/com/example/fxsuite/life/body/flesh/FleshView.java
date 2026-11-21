package com.example.fxsuite.life.body.flesh;

import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import javafx.scene.Node;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;

/**
 * A body's flesh drawn ({@link Flesh}): one surface, its points and their ways out found afresh as the body is posed,
 * each frame, into a scene whose frame is the body's with each way's sign as {@code signs} has it; its triangles turned
 * round if that is a mirror's. Its colour its material's, to be set as the skin's is.
 */
public final class FleshView {

    private final Flesh flesh;
    private final V3 signs;
    private final TriangleMesh mesh = new TriangleMesh(VertexFormat.POINT_NORMAL_TEXCOORD);
    private final MeshView view = new MeshView(mesh);
    private final PhongMaterial material = new PhongMaterial();
    private final float[] at;
    private final float[] out;

    /** {@code flesh} drawn with each way's sign as {@code signs} has it, posed at first as {@code rig} holds it. */
    public FleshView(final Flesh flesh, final V3 signs, final Rig rig) {
        this.flesh = flesh;
        this.signs = signs;
        at = new float[flesh.size() * 3];
        out = new float[flesh.size() * 3];
        final int[] t = flesh.triangles();
        if (signs.x() * signs.y() * signs.z() < 0) {
            for (int i = 0; i < t.length; i += 3) {
                final int b = t[i + 1];
                t[i + 1] = t[i + 2];
                t[i + 2] = b;
            }
        }
        final int[] faces = new int[t.length * 3];
        for (int i = 0; i < t.length; i++) {
            faces[3 * i] = t[i];
            faces[3 * i + 1] = t[i];
        }
        mesh.getTexCoords().addAll(0, 0);
        show(rig);
        mesh.getFaces().setAll(faces);
        view.setCullFace(CullFace.BACK);
        material.setSpecularPower(12);
        view.setMaterial(material);
    }

    /** What to put in the scene. */
    public Node node() {
        return view;
    }

    /** Its material, to colour. */
    public PhongMaterial material() {
        return material;
    }

    /** Posed as {@code rig} holds the body. */
    public void show(final Rig rig) {
        flesh.posed(rig, at, out);
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
